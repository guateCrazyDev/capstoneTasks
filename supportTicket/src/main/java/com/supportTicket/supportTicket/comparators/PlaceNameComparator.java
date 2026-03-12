package com.supportTicket.supportTicket.comparators;
 
import java.util.Comparator;
 
import com.supportTicket.supportTicket.records.PlaceRecord;
 
public class PlaceNameComparator implements Comparator<PlaceRecord>{
	public int compare(PlaceRecord plR1,PlaceRecord plR2) {
		return plR1.name().compareTo(plR2.name());
	}
}