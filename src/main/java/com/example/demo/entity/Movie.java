package com.example.demo.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Movie")
public class Movie implements Serializable {

	private static final long serialVersionUID = -2022057965668210449L;

	@Id
	@Column(name = "Id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "Name")
	private String name;

	@ManyToMany(targetEntity = Director.class, cascade = CascadeType.ALL)
	@JoinTable(
			name = "Movie_Directors",
			joinColumns = { @JoinColumn(name = "Movie_Id")},
			inverseJoinColumns = { @JoinColumn(name = "Director_Id")})
	private Set<Director> directors;

	@Column(name = "Rating")
	private int rating;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Director> getDirectors() {
		return directors;
	}

	public void setDirectors(Set<Director> directors) {
		this.directors = directors;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("Id: ").append(id).append(", ").append("Name: ").append(name).toString();
	}
}
