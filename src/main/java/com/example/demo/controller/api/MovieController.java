package com.example.demo.controller.api;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.MovieRequest;
import com.example.demo.domain.MovieResponse;
import com.example.demo.repository.specification.MovieSpecification;
import com.example.demo.service.MovieService;

@RestController
@RequestMapping(value = "/api/movie")
public class MovieController {

	@Autowired
	private MovieService movieService;

	@GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MovieResponse>> fetchAll() {
		List<MovieResponse> movieResponses = movieService.findAll();
		return new ResponseEntity<>(movieResponses, CollectionUtils.isNotEmpty(movieResponses) ? HttpStatus.OK : HttpStatus.NO_CONTENT);
	}

	@PostMapping(value = "/save")
	public ResponseEntity save(@Valid @RequestBody MovieRequest request) {
		return new ResponseEntity(movieService.save(request), HttpStatus.CREATED);
	}

	@PutMapping(value = "/update")
	public ResponseEntity update(@Valid @RequestBody MovieRequest request) {
		MovieResponse updatedMovie = movieService.update(request);
		return new ResponseEntity(updatedMovie, updatedMovie == null ? HttpStatus.NOT_FOUND : HttpStatus.OK);
	}

	@PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MovieResponse>> search(@Valid @RequestBody MovieSpecification.Filter filter) {
		List<MovieResponse> movieResponses = movieService.search(filter);
		return new ResponseEntity<>(movieResponses, CollectionUtils.isNotEmpty(movieResponses) ? HttpStatus.OK : HttpStatus.NO_CONTENT);
	}

	@DeleteMapping(value = "/delete/{id}")
	public ResponseEntity delete(@PathVariable int id) {
		movieService.delete(id);
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
}
