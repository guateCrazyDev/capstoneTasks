package com.supportTicket.supportTicket.model;

import java.sql.Date;
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
public class Comments {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String text;
	private Integer rate;
	private Date date;
	@OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
	@com.fasterxml.jackson.annotation.JsonManagedReference("comment-pictures")
	private List<PicturesComments> picturesComms;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	@com.fasterxml.jackson.annotation.JsonBackReference("user-comments")
	private User user;

	@ManyToOne
	@JoinColumn(name = "place_id", nullable = false)
	@com.fasterxml.jackson.annotation.JsonBackReference("place-comments")
	private Place place;

	public Comments() {
		super();
	}

	public Comments(Long id, String text, Integer rate, Date date, List<PicturesComments> picturesComms, User user,
			Place place) {
		super();
		this.id = id;
		this.text = text;
		this.rate = rate;
		this.date = date;
		this.picturesComms = picturesComms;
		this.user = user;
		this.place = place;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getRate() {
		return rate;
	}

	public void setRate(Integer rate) {
		this.rate = rate;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<PicturesComments> getPicturesComms() {
		return picturesComms;
	}

	public void setPicturesComms(List<PicturesComments> picturesComms) {
		this.picturesComms = picturesComms;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}
}
