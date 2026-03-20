package com.supportTicket.supportTicket.records;

import java.util.List;


public record PlaceRecord(String name, String bestTime, String location, List<PicturesPlaceRecord> picturesPlace,String categoryName,Boolean relation,Integer numeroComments,List<CommentRecord> comments) {}