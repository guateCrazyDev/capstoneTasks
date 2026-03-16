package com.supportTicket.supportTicket.records;

import java.sql.Date;
import java.util.List;

public record CommentRecord(
String text, Integer rate, Date date
, List<PictureCommentsRecord> picComms,String userName,String profilePath) {}