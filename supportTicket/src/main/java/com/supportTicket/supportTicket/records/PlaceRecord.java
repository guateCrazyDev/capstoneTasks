package com.supportTicket.supportTicket.records;

import java.util.List;


public record PlaceRecord(String name, String bestTime, String location,String description, List<PicturesPlaceRecord> picturesPlace,String categoryName,List<CommentRecord> comments) {}