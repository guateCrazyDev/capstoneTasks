package com.supportTicket.supportTicket.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.records.CommentRecord;
import com.supportTicket.supportTicket.records.CommentStatsRecord;

public interface CommentsService {

	CommentRecord createComm(CommentRecord commR, List<MultipartFile> files, String userName, String placeName);

	List<CommentRecord> getCommentsByPlace(String placeName);

	CommentStatsRecord getCommentsStats(String placeName);
	
	void updateComm(CommentRecord comment,CommentRecord commentNew,
			String placeName,String userName,List<MultipartFile> images);
	
	void delete(CommentRecord comment,String placeName,String userName);
}