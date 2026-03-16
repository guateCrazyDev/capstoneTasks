package com.supportTicket.supportTicket.records;

import java.util.List;


public record PlaceUniqueRecord(String name, String bestTime, String location, List<PicturesPlaceRecord> picturesPlace,String categoryName,List<CommentRecord> comments,Double rateAve) {}