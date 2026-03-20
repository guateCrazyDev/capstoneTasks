package com.supportTicket.supportTicket.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class PicturesComments {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String path;
	@ManyToOne
	@JoinColumn(name = "comment_id", nullable = false)
	@com.fasterxml.jackson.annotation.JsonBackReference("comment-pictures")
	private Comments comment;
	
	public PicturesComments() {
		super();
	}
	
	public PicturesComments(Long id, String path, Comments comment) {
		super();
		this.id = id;
		this.path = path;
		this.comment = comment;
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
	public Comments getComment() {
		return comment;
	}
	public void setComment(Comments comment) {
		this.comment = comment;
	}
}
