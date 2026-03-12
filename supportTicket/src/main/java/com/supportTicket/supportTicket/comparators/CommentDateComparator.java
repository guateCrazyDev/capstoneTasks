package com.supportTicket.supportTicket.comparators;

import java.util.Comparator;

import com.supportTicket.supportTicket.model.Comments;

public class CommentDateComparator implements Comparator<Comments>{
	public int compare(Comments c1, Comments c2) {
		return c1.getDate().compareTo(c2.getDate());
	}
}
