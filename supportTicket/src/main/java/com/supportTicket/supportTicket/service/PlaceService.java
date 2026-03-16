package com.supportTicket.supportTicket.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.records.PlaceLigthRecord;
import com.supportTicket.supportTicket.records.PlaceRecord;
import com.supportTicket.supportTicket.records.PlaceUniqueRecord;

public interface PlaceService {
	PlaceRecord createPlace(PlaceRecord place,List<MultipartFile> files,String catName);
	List<PlaceRecord> getAllByName();
	List<PlaceRecord> getAllByNameCat(String categoryName);
	PlaceRecord updatePlace(PlaceRecord place,List<MultipartFile> files,String catName,String originalName);
	void deletePlace(String placeName);
	List<PlaceLigthRecord> getAllByNameCatLigth(String categoryName);
	PlaceUniqueRecord getPlaceByName(String placeName);
}
