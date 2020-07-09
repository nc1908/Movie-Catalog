package com.example.demo.transformer;

import org.apache.commons.collections4.CollectionUtils;

import com.example.demo.domain.MovieRequest;
import com.example.demo.domain.MovieResponse;
import com.example.demo.entity.Movie;

public final class MovieTransformer {

	public static Movie transformTo(MovieRequest request) {
		Movie movie = new Movie();
		if (request.getId() > 0) {
			movie.setId(request.getId());
		}
		movie.setName(request.getName());
		movie.setRating(request.getRating());
		return movie;
	}

	public static MovieResponse transformFrom(Movie entity) {
		MovieResponse movieRequest = new MovieResponse();
		movieRequest.setDirector(CollectionUtils.isNotEmpty(entity.getDirectors()) ? entity.getDirectors().iterator().next().getName() : null);
		movieRequest.setName(entity.getName());
		movieRequest.setId(entity.getId());
		movieRequest.setRating(entity.getRating());
		return movieRequest;
	}
}
