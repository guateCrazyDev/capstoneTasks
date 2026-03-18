package com.supportTicket.supportTicket.comparators;

import java.util.Comparator;

import com.supportTicket.supportTicket.records.CategoryRecord;

public class CategoryNameComparator implements Comparator<CategoryRecord>{
	public int compare(CategoryRecord c1, CategoryRecord c2) {

		String name1 = c1.categoryName();
		String name2 = c2.categoryName();

		if (name1 == null && name2 == null)
			return 0;
		if (name1 == null)
			return 1;
		if (name2 == null)
			return -1;

		return name1.compareToIgnoreCase(name2);
	}
}
