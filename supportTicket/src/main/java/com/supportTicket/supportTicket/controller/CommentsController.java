package com.supportTicket.supportTicket.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.records.CommentRecord;
import com.supportTicket.supportTicket.records.PlaceRecord;
import com.supportTicket.supportTicket.service.CommentsService;

@RestController
@RequestMapping("/api")
public class CommentsController {
	@Autowired
	CommentsService commService;
	
	@PostMapping("/comment")
	public ResponseEntity<List<String>> addComment(
			@RequestPart("commentData") CommentRecord commRecord,
			@RequestPart(value="img", required=false) List<MultipartFile> files,
			@RequestPart("userName") String userName,
			@RequestPart("placeName") String placeName){
		List<String> paths = commService.createComm(commRecord,files,userName,placeName);
		return new ResponseEntity<>(paths,HttpStatus.CREATED);
	}
}
