package com.example.demo.service;

import java.util.List;

import com.example.demo.domain.MovieRequest;
import com.example.demo.domain.MovieResponse;
import com.example.demo.repository.specification.MovieSpecification.Filter;

public interface MovieService {

	MovieResponse save(MovieRequest movieRequest);
	MovieResponse update(MovieRequest movieRequest);
	void delete(int id);
	List<MovieResponse> findAll();
	List<MovieResponse> search(Filter movieFilter);
}
