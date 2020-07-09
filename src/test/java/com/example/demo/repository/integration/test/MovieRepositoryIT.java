package com.example.demo.repository.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.entity.Director;
import com.example.demo.entity.Movie;
import com.example.demo.repository.DirectorRepository;
import com.example.demo.repository.MovieRepository;
import com.example.demo.repository.specification.MovieSpecification;
import com.example.demo.repository.specification.MovieSpecification.Filter;
import com.google.common.collect.Sets;

@DataJpaTest
@RunWith(SpringRunner.class)
public class MovieRepositoryIT {

	private static final String MOVIE_NAME_1 = "Test Movie 1";
	private static final String MOVIE_NAME_2 = "Test Movie 2";
	private static final String DIRECTOR_NAME_1 = "Director 1";
	private static final String DIRECTOR_NAME_2 = "Director 2";
	private static final String DIRECTOR_NAME_3 = "Director 3";
	private static final String NEW_MOVIE_NAME = "New Movie Name";
	private static final int RATING_1 = 1;
	private static final int RATING_2 = 2;
	private static final int RATING_3 = 3;

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private DirectorRepository directorRepository;

	@Test
	public void testAddNewMovie() {
		Movie movie = createMovie(MOVIE_NAME_1, DIRECTOR_NAME_1, RATING_1);

		Movie save = movieRepository.save(movie);

		assertNotNull(save);
		assertThat(save.getId(), greaterThan(0));
		assertThat(save.getName(), equalTo(movie.getName()));
		assertThat(save.getRating(), equalTo(movie.getRating()));
		assertThat(save.getDirectors(), hasSize(RATING_1));
		assertThat(save.getDirectors().iterator().next().getName(), equalTo(DIRECTOR_NAME_1));
	}

	@Test
	public void testUpdateMovie() {
		Movie savedMovie = movieRepository.save(createMovie(MOVIE_NAME_1, DIRECTOR_NAME_1, RATING_1));

		assertThat(savedMovie.getName(), equalTo(MOVIE_NAME_1));

		savedMovie.setName(NEW_MOVIE_NAME);

		assertThat(movieRepository.save(savedMovie).getName(), equalTo(NEW_MOVIE_NAME));
	}

	@Test
	public void testDeleteMovie() {
		Movie savedMovie = movieRepository.save(createMovie(MOVIE_NAME_1, DIRECTOR_NAME_1, RATING_1));

		assertThat(savedMovie.getName(), equalTo(MOVIE_NAME_1));

		movieRepository.delete(savedMovie);

		assertThat(movieRepository.findById(savedMovie.getId()).isPresent(), is(false));
	}

	@Test
	public void givenThereAre3MoviesWithDifferentNames_whenSearchMovieWithNameNewMovieName_shouldOnlyReturnNewMovieName() {
		movieRepository.save(createMovie(MOVIE_NAME_1, DIRECTOR_NAME_1, RATING_1));
		movieRepository.save(createMovie(MOVIE_NAME_2, DIRECTOR_NAME_1, RATING_1));
		movieRepository.save(createMovie(NEW_MOVIE_NAME, DIRECTOR_NAME_1, RATING_1));

		Filter filter = new Filter();
		filter.setName(NEW_MOVIE_NAME);

		List<Movie> movieList = movieRepository.findAll(new MovieSpecification(filter));

		assertThat(movieList, hasSize(RATING_1));
		assertThat(movieList.get(0).getName(), equalTo(NEW_MOVIE_NAME));
	}

	@Test
	public void givenThereAre3MoviesWithRating1_2And3_whenSearchAllMovieWithRating2AndAbove_shouldReturn2Movies() {
		movieRepository.save(createMovie(MOVIE_NAME_1, DIRECTOR_NAME_1, RATING_1));
		movieRepository.save(createMovie(MOVIE_NAME_2, DIRECTOR_NAME_1, RATING_2));
		movieRepository.save(createMovie(NEW_MOVIE_NAME, DIRECTOR_NAME_1, RATING_3));

		Filter filter = new Filter();
		filter.setRating(RATING_2);

		List<Movie> movieList = movieRepository.findAll(new MovieSpecification(filter));

		assertThat(movieList, hasSize(2));
		assertThat(movieList.stream().map(Movie::getRating).collect(Collectors.toList()), containsInAnyOrder(RATING_2, RATING_3));
	}

	@Test
	public void givenThereAre3MoviesWithTwoOfThemAreFromDirector1_whenSearchAllMovieDirectedByDirector1_shouldReturn2Movies() {
		movieRepository.save(createMovie(MOVIE_NAME_1, DIRECTOR_NAME_1, RATING_1));
		movieRepository.save(createMovie(MOVIE_NAME_2, DIRECTOR_NAME_1, RATING_1));
		movieRepository.save(createMovie(NEW_MOVIE_NAME, DIRECTOR_NAME_2, RATING_1));

		Filter filter = new Filter();
		filter.setDirectorNames(Arrays.asList(DIRECTOR_NAME_1));

		List<Movie> movieList = movieRepository.findAll(new MovieSpecification(filter));

		assertThat(movieList, hasSize(2));
		assertThat(movieList.get(0).getDirectors().iterator().next().getName(), equalTo(DIRECTOR_NAME_1));
		assertThat(movieList.get(1).getDirectors().iterator().next().getName(), equalTo(DIRECTOR_NAME_1));
	}

	@Test
	public void givenThereAre3MoviesWithOneFromDirector1AndOneFromDirector2_whenSearchAllMovieDirectedByDirector1AndDirector2_shouldReturn2Movies() {
		movieRepository.save(createMovie(MOVIE_NAME_1, DIRECTOR_NAME_1, RATING_1));
		movieRepository.save(createMovie(MOVIE_NAME_2, DIRECTOR_NAME_2, RATING_1));
		movieRepository.save(createMovie(NEW_MOVIE_NAME, DIRECTOR_NAME_3, RATING_1));

		Filter filter = new Filter();
		filter.setDirectorNames(Arrays.asList(DIRECTOR_NAME_1, DIRECTOR_NAME_2));

		List<Movie> movieList = movieRepository.findAll(new MovieSpecification(filter));
		assertThat(movieList, hasSize(2));
		assertThat(
			movieList
				.stream()
				.map(movie -> movie.getDirectors())
				.flatMap(directors -> directors.stream())
				.map(director -> director.getName())
				.collect(Collectors.toList()),
			containsInAnyOrder(DIRECTOR_NAME_1, DIRECTOR_NAME_2));
	}

	private Movie createMovie(String movieName, String directorName, int rating) {
		Movie movie = new Movie();
		movie.setName(movieName);
		movie.setRating(rating);
		Director director = new Director(directorName);
		Director save = directorRepository.save(director);
		movie.setDirectors(Sets.newHashSet(save));
		return movie;
	}
}