package com.supportTicket.supportTicket.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Place {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String bestTime;
	private String Location;
	private String description;
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

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "user_place", joinColumns = @JoinColumn(name = "place_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private Set<User> users = new HashSet<>();

	public Place() {
		super();
	}

	public Place(Long id, String name, String bestTime, String location, String description,
			List<PicturesPlace> picturesPlace, Category category, List<Comments> comms, Set<User> users) {
		super();
		this.id = id;
		this.name = name;
		this.bestTime = bestTime;
		Location = location;
		this.description = description;
		this.picturesPlace = picturesPlace;
		this.category = category;
		this.comms = comms;
		this.users = users;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

}
