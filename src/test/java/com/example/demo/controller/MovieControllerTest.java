package com.example.demo.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.demo.controller.api.MovieController;
import com.example.demo.domain.MovieRequest;
import com.example.demo.domain.MovieResponse;
import com.example.demo.entity.Director;
import com.example.demo.entity.Movie;
import com.example.demo.service.MovieService;
import com.example.demo.transformer.MovieTransformer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

@WebMvcTest(MovieController.class)
public class MovieControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private MovieService movieService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void givenTwoMoviesInDB_whenFetchAll_shouldReturnTwoMovies() throws Exception {

		Movie movie1 = createMovie("Test Movie 1", 1, "Director 1", 1);
		Movie movie2 = createMovie("Test Movie 2", 1, "Director 2", 2);
		given(movieService.findAll()).willReturn(
				Arrays.asList(
						MovieTransformer.transformFrom(movie1),
						MovieTransformer.transformFrom(movie2)
				)
		);

		mvc.perform(get("/api/movie/all")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].name", is(movie1.getName())));
	}

	@Test
	public void givenValidMovieRequest_shouldSaveMovieRequestToDbAndReturnHttpStatusCreated() throws Exception {
		MovieRequest movieRequest = new MovieRequest();
		movieRequest.setName("Test Movie");
		movieRequest.setRating(2);
		movieRequest.setDirector("Test Director");

		when(movieService.save(any(MovieRequest.class))).thenReturn(MovieTransformer.transformFrom(MovieTransformer.transformTo(movieRequest)));

		MvcResult mvcResult = mvc.perform(post("/api/movie/save")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(movieRequest)))
				.andExpect(status().isCreated())
				.andReturn();

		MovieResponse returnedResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), MovieResponse.class);
		assertThat(returnedResponse.getName(), equalTo(movieRequest.getName()));
	}

	@Test
	void givenValidMovieId_shouldDeleteMovieRecordFromDbAndReturnHttpStatusNoContent() throws Exception {
		mvc.perform(delete("/api/movie/delete/" + 1)).andExpect(status().isNoContent());
	}

	@Test
	void givenValidUpdateMovieRequest_shouldUpdateMovieRecordAndReturnHttpStatusOk() throws Exception {
		MovieRequest movieRequest = new MovieRequest();
		movieRequest.setId(1);
		movieRequest.setName("Test Movie");
		movieRequest.setRating(2);
		movieRequest.setDirector("Test Director");

		when(movieService.update(any(MovieRequest.class))).thenReturn(MovieTransformer.transformFrom(MovieTransformer.transformTo(movieRequest)));

		MvcResult mvcResult = mvc.perform(put("/api/movie/update")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(movieRequest)))
				.andExpect(status().isOk())
				.andReturn();

		MovieResponse returnedResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), MovieResponse.class);
		assertThat(returnedResponse.getName(), equalTo(movieRequest.getName()));
	}

	private Movie createMovie(String movieName, int movieId, String directorName, int rating) {
		Movie movie = new Movie();
		movie.setId(movieId);
		movie.setName(movieName);
		movie.setRating(rating);
		movie.setDirectors(Sets.newHashSet(new Director(directorName)));
		return movie;
	}
}