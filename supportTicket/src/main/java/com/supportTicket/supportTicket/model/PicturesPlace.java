package com.supportTicket.supportTicket.model;

import jakarta.persistence.*;

@Entity
public class PicturesPlace {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String path;

	@ManyToOne
	@JoinColumn(name = "place_id", nullable = false)
	@com.fasterxml.jackson.annotation.JsonBackReference("place-pictures")
	private Place place;

	public PicturesPlace() {
	}

	public PicturesPlace(Long id, String path, Place place) {
		this.id = id;
		this.path = path;
		this.place = place;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}
}