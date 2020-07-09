package com.example.demo.repository.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.example.demo.entity.Director;
import com.example.demo.entity.Movie;

public class MovieSpecification implements Specification<Movie> {

	private final Filter filter;

	public MovieSpecification(Filter filter) {
		this.filter = filter;
	}

	@Override
	public Predicate toPredicate(Root<Movie> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		final List<Predicate> predicates = new ArrayList<>();

		if (StringUtils.isNotBlank(filter.getName())) {
			predicates.add(criteriaBuilder.equal(root.get("name"), filter.getName()));
		}

		if (filter.getRating() > 0) {
			predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), filter.getRating()));
		}

		if (CollectionUtils.isNotEmpty(filter.getDirectorNames())) {
			Join<Movie, Director> director = root.join("directors");
			predicates.add(criteriaBuilder.in(director.get("name")).value(filter.getDirectorNames()));
		}

		return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
	}

	public static class Filter extends Movie {
		List<String> directorNames;

		public List<String> getDirectorNames() {
			return directorNames;
		}

		public void setDirectorNames(List<String> directorNames) {
			this.directorNames = directorNames;
		}
	}
}
