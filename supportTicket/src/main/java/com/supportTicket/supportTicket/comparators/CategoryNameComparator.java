package com.supportTicket.supportTicket.comparators;

import java.util.Comparator;

import com.supportTicket.supportTicket.records.CategoryRecord;

public class CategoryNameComparator implements Comparator<CategoryRecord>{
	public int compare(CategoryRecord r1, CategoryRecord r2) {
		return r1.categoryName().compareTo(r2.categoryName());
	}
}
