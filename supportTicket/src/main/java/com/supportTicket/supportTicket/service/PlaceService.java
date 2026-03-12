package com.supportTicket.supportTicket.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.records.PlaceRecord;

public interface PlaceService {
	PlaceRecord createPlace(PlaceRecord place,List<MultipartFile> files,String catName);
	List<PlaceRecord> getAllByName();
}
