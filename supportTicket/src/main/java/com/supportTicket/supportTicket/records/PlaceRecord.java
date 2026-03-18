package com.supportTicket.supportTicket.records;

import java.util.List;


public record PlaceRecord(String name, String bestTime, String location, List<PicturesPlaceRecord> picturesPlace,String categoryName,Boolean relation,List<CommentRecord> comments) {}