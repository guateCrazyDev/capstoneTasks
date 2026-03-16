package com.supportTicket.supportTicket.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.records.CommentRecord;
import com.supportTicket.supportTicket.service.CommentsService;

@RestController
@RequestMapping("/api/comments")
public class CommentsController {

	private final CommentsService commService;

	public CommentsController(CommentsService commService) {
		this.commService = commService;
	}

	@PostMapping(consumes = { "multipart/form-data" })
	public ResponseEntity<CommentRecord> addComment(
			@RequestPart("commentData") CommentRecord commRecord,
			@RequestPart(value = "files", required = false) List<MultipartFile> files,
			@RequestPart("userName") String userName,
			@RequestPart("placeName") String placeName) {

		CommentRecord response = commService.createComm(commRecord, files, userName, placeName);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}