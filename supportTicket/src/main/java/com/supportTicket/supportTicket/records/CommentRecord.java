package com.supportTicket.supportTicket.records;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

public record CommentRecord(
        String text,
        Integer rate,
        Date date,
        List<PictureCommentsRecord> picComms,
        UserRecord user) {
}