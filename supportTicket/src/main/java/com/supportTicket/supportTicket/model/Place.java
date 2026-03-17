package com.supportTicket.supportTicket.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class Place {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String bestTime;
	private String location;

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

	@ManyToMany(mappedBy = "favoritePlaces")
	@com.fasterxml.jackson.annotation.JsonIgnore
	private List<User> usersWhoFavorited = new ArrayList<>();

	public Place() {
	}

	public Place(Long id, String name, String bestTime, String location, List<PicturesPlace> picturesPlace,
			Category category, List<Comments> comms) {
		this.id = id;
		this.name = name;
		this.bestTime = bestTime;
		this.location = location;
		this.picturesPlace = picturesPlace;
		this.category = category;
		this.comms = comms;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getBestTime() {
		return bestTime;
	}

	public String getLocation() {
		return location;
	}

	public List<PicturesPlace> getPicturesPlace() {
		return picturesPlace;
	}

	public Category getCategory() {
		return category;
	}

	public List<Comments> getComms() {
		return comms;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setBestTime(String bestTime) {
		this.bestTime = bestTime;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setPicturesPlace(List<PicturesPlace> picturesPlace) {
		this.picturesPlace = picturesPlace;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public void setComms(List<Comments> comms) {
		this.comms = comms;
	}

	public List<User> getUsersWhoFavorited() {
		return usersWhoFavorited;
	}

	public void setUsersWhoFavorited(List<User> usersWhoFavorited) {
		this.usersWhoFavorited = usersWhoFavorited;
	}
	
}