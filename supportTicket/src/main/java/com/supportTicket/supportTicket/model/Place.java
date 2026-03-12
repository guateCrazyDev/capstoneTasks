package com.supportTicket.supportTicket.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Place {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String bestTime;
	private String Location;
	@OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
	@com.fasterxml.jackson.annotation.JsonManagedReference("place-pictures")
	private List<PicturesPlace> picturesPlace;

	@ManyToOne
	@JoinColumn(name = "category_id", nullable = false)
	@com.fasterxml.jackson.annotation.JsonBackReference("category-places")
	private Category category;

	@OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
	@com.fasterxml.jackson.annotation.JsonManagedReference("place-comments")
	private List<Comments> comms;
	
	public Place() {
		super();
	}
	
	public Place(Long id, String name, String bestTime, String location, List<PicturesPlace> picturesPlace,
			Category category, List<Comments> comms) {
		super();
		this.id = id;
		this.name = name;
		this.bestTime = bestTime;
		Location = location;
		this.picturesPlace = picturesPlace;
		this.category = category;
		this.comms = comms;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBestTime() {
		return bestTime;
	}
	public void setBestTime(String bestTime) {
		this.bestTime = bestTime;
	}
	public String getLocation() {
		return Location;
	}
	public void setLocation(String location) {
		Location = location;
	}
	public List<PicturesPlace> getPicturesPlace() {
		return picturesPlace;
	}
	public void setPicturesPlace(List<PicturesPlace> picturesPlace) {
		this.picturesPlace = picturesPlace;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public List<Comments> getComms() {
		return comms;
	}
	public void setComms(List<Comments> comms) {
		this.comms = comms;
	}
}
