package com.example.demo.service;

import java.awt.print.Pageable;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.example.demo.domain.MovieRequest;
import com.example.demo.domain.MovieResponse;
import com.example.demo.entity.Director;
import com.example.demo.entity.Movie;
import com.example.demo.repository.DirectorRepository;
import com.example.demo.repository.MovieRepository;
import com.example.demo.repository.specification.MovieSpecification;
import com.example.demo.repository.specification.MovieSpecification.Filter;
import com.example.demo.transformer.MovieTransformer;

@Service("MovieCatalogService")
@Transactional
public class MovieCatalogService implements MovieService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MovieCatalogService.class);

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private DirectorRepository directorRepository;

	/**
	 * Creates a new movie record in the database based on the details in movie request
	 * @param movieRequest contains the info need to be added to movie record
	 * @return created movie record
	 */
	@Override
	public MovieResponse save(MovieRequest movieRequest) {
		Assert.notNull(movieRequest, "movie cannot be null");

		Movie movie = MovieTransformer.transformTo(movieRequest);
		movie.setDirectors(getDirectors(movieRequest));

		LOGGER.info("Adding movie: {}", movie);
		return MovieTransformer.transformFrom(movieRepository.save(movie));
	}

	/**
	 * If a valid movie request is passed in and the movie record does exist in the DB, the movie record details will be
	 * updated according to the details passed through via movie request otherwise no movie record will be updated and a
	 * null will be returned.
	 * @param movieRequest contains the updated info about the movie
	 * @return an updated movie record or null
	 */
	@Override
	public MovieResponse update(MovieRequest movieRequest) {
		Assert.notNull(movieRequest, "movie cannot be null");
		Assert.notNull(movieRequest.getId(), "Movie Id cannot be null");

		Optional<Movie> movieOptional = movieRepository.findById(movieRequest.getId());
		if (movieOptional.isPresent()) {
			Movie movie = movieOptional.get();
			movie.setName(movieRequest.getName());
			movie.setRating(movieRequest.getRating());
			movie.setDirectors(getDirectors(movieRequest));

			LOGGER.info("Updating movie: {}", movie);
			return MovieTransformer.transformFrom(movieRepository.save(movie));
		}
		return null;
	}

	/**
	 * Delete a movie record from database based on the id, if id doesn't exist do nothing
	 * @param id Movie id to delete from DB
	 */
	@Override
	public void delete(int id) {
		Assert.isTrue(id > 0, "invalid movie id");

		Optional<Movie> optional = movieRepository.findById(id);
		if (optional.isPresent()) {
			Movie movieToDelete = optional.get();
			LOGGER.info("Deleting movie: {}", movieToDelete);
			movieRepository.delete(movieToDelete);
		}
	}

	/**
	 * @return all movies from DB
	 */
	@Override
	public List<MovieResponse> findAll() {
		List<Movie> all = (List<Movie>) movieRepository.findAll();
		return all.stream().map(movie -> MovieTransformer.transformFrom(movie)).collect(Collectors.toList());
	}

	/**
	 * This method will return movie responses based on details passed in through movieFilter and only movie details matches
	 * the filter will be returned.
	 * @param movieFilter contains filter details
	 * @return a list of MovieResponse
	 */
	@Override
	public List<MovieResponse> search(Filter movieFilter) {
		Assert.notNull(movieFilter, "movieFilter cannot be null");

		List<Movie> movieResults = movieRepository.findAll(new MovieSpecification(movieFilter));
		return movieResults.stream().map(movie -> MovieTransformer.transformFrom(movie)).collect(Collectors.toList());
	}


	/**
	 * This method will create a director and add it to the set if is not in the database, if the director is already exist
	 * and movieRequest has a valid movieId it will update director's name and add it to the set.
	 * @param movieRequest contains details of the request
	 * @return new set of directors
	 */
	private Set<Director> getDirectors(MovieRequest movieRequest) {
		Set<Director> directorSet = new HashSet<>();

		String director = movieRequest.getDirector();
		Director directorByName = directorRepository.findByName(director);

		if (directorByName == null) {
			directorByName = directorRepository.save(new Director(director));
		}
		else if (movieRequest.hasValidMovieId()) {
			directorByName.setName(director);
			directorRepository.save(directorByName);
		}

		directorSet.add(directorByName);
		return directorSet;
	}
}
