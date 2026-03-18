package com.supportTicket.supportTicket.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.records.CommentRecord;

public interface CommentsService {
	List<CommentRecord> getCommentsByPlace(String placeName);

	CommentRecord createComm(
			CommentRecord commRecord,
			List<MultipartFile> files,
			String userName,
			String placeName);

}