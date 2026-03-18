package com.supportTicket.supportTicket.service;

import java.util.List;

import com.supportTicket.supportTicket.records.PlaceLigthRecord;

public interface UserPlaceService {
	void createRelationship(String user,String place);
	List<PlaceLigthRecord> getAllByUserLigth(String userName);
}
