package com.example.demo.repository.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.entity.Director;
import com.example.demo.repository.DirectorRepository;

@DataJpaTest
@RunWith(SpringRunner.class)
public class DirectorRepositoryIT {

	private static final String DIRECTOR_NAME = "Director 1";
	private static final String NEW_DIRECTOR_NAME = "New Director Name";

	@Autowired
	private DirectorRepository directorRepository;

	Director director;

	@Before
	public void setup() {
		director = new Director(DIRECTOR_NAME);
	}

	@Test
	public void testAddNewDirector() {
		Director savedDirector = directorRepository.save(director);

		assertNotNull(savedDirector);
		assertThat(savedDirector.getId(), greaterThan(0));
		assertThat(savedDirector.getName(), equalTo(director.getName()));
	}

	@Test
	public void testUpdateDirector() {
		Director savedDirector = directorRepository.save(director);

		assertThat(savedDirector.getName(), equalTo(director.getName()));

		savedDirector.setName(NEW_DIRECTOR_NAME);

		assertThat(directorRepository.save(savedDirector).getName(), equalTo(NEW_DIRECTOR_NAME));
	}

	@Test
	public void testDeleteDirector() {
		Director savedDirector = directorRepository.save(director);

		assertThat(savedDirector.getName(), equalTo(director.getName()));

		directorRepository.delete(savedDirector);

		assertThat(directorRepository.findById(savedDirector.getId()).isPresent(), is(false));
	}

	@Test
	public void givenDirectorWithNameDirector1_whenSearchByName_shouldReturnDirectorWithNameDirector1() {
		directorRepository.save(director);

		assertThat(directorRepository.findByName(DIRECTOR_NAME).getName(), equalTo(DIRECTOR_NAME));
	}
}