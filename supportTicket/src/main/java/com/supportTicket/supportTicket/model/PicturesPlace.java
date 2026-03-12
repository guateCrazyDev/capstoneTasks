package com.supportTicket.supportTicket.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class PicturesPlace {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String path;
	@ManyToOne
	@JoinColumn(name = "place_id", nullable = false)
	@com.fasterxml.jackson.annotation.JsonBackReference("place-pictures")
	private Place place;
	
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
	
	public PicturesPlace(Long id, String path, Place place) {
		super();
		this.id = id;
		this.path = path;
		this.place = place;
	}
	public PicturesPlace() {
	}
}
