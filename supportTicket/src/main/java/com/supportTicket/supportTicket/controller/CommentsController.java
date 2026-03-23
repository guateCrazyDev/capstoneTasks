package com.supportTicket.supportTicket.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.records.CommentRecord;
import com.supportTicket.supportTicket.records.CommentStatsRecord;
import com.supportTicket.supportTicket.service.CommentsService;

@RestController
@RequestMapping("/api/comments")
public class CommentsController {

	@Autowired
	private CommentsService commentsService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CommentRecord> addComment(
			@RequestPart("commentData") CommentRecord commRecord,
			@RequestPart(value = "files", required = false) List<MultipartFile> files,
			@RequestParam String userName,
			@RequestParam String placeName) {

		CommentRecord response = commentsService.createComm(commRecord, files, userName, placeName);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/place/{placeName}")
	public ResponseEntity<List<CommentRecord>> getCommentsByPlace(
			@PathVariable String placeName) {
		return ResponseEntity.ok(
				commentsService.getCommentsByPlace(placeName));
	}

	@GetMapping("/stats/{placeName}")
	public CommentStatsRecord getStats(@PathVariable String placeName) {
		return commentsService.getCommentsStats(placeName);
	}

	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> updateComm(
			@RequestPart("commentData") CommentRecord oldComment,
			@RequestPart("newCommentData") CommentRecord newComment,
			@RequestParam String placeName,
			@RequestPart(value = "images", required = false) List<MultipartFile> images) {

		commentsService.updateComm(oldComment, newComment, placeName, images);

		return ResponseEntity.ok("Successfully updated");
	}

	@DeleteMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> delete(
			@RequestPart("commentData") CommentRecord commRecord,
			@RequestParam String placeName) {

		commentsService.delete(commRecord, placeName);

		return ResponseEntity.ok("Successfully deleted");
	}
}
