package com.supportTicket.supportTicket.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.records.CommentRecord;

public interface CommentsService {
	void createComm(CommentRecord commR,List<MultipartFile> files,String userName,String placeName);
}
