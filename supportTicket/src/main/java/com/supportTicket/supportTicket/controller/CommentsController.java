package com.supportTicket.supportTicket.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.records.CommentRecord;
import com.supportTicket.supportTicket.service.CommentsService;

@RestController
@RequestMapping("/api/comments")
public class CommentsController {

	@Autowired
	private CommentsService commentsService;

	@PostMapping(consumes = { "multipart/form-data" })
	public ResponseEntity<CommentRecord> addComment(
			@RequestPart("commentData") CommentRecord commRecord,
			@RequestPart(value = "files", required = false) List<MultipartFile> files,
			@RequestPart("userName") String userName,
			@RequestPart("placeName") String placeName) {

		CommentRecord response = commentsService.createComm(commRecord, files, userName, placeName);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/place/{placeName}")
	public ResponseEntity<List<CommentRecord>> getCommentsByPlace(@PathVariable String placeName) {

		List<CommentRecord> comments = commentsService.getCommentsByPlace(placeName);

		return ResponseEntity.ok(comments);
	}
}