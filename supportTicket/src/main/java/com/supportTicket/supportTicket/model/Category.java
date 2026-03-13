package com.supportTicket.supportTicket.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;

@Entity
public class Category {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String categoryName;
	private String description;
	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
	@com.fasterxml.jackson.annotation.JsonManagedReference("category-places")
	private List<Place> places;
	@Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] img;
	
	public Category() {
		super();
	}
	
	public Category(Long id, String categoryName, String description, List<Place> places, byte[] img) {
		super();
		this.id = id;
		this.categoryName = categoryName;
		this.description = description;
		this.places = places;
		this.img = img;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public List<Place> getPlaces() {
		return places;
	}
	public void setPlaces(List<Place> places) {
		this.places = places;
	}
	public byte[] getImg() {
		return img;
	}
	public void setImg(byte[] img) {
		this.img = img;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
