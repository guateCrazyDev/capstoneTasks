package com.supportTicket.supportTicket.comparators;

import java.util.Comparator;

import com.supportTicket.supportTicket.records.PlaceLigthRecord;

public class PlaceLigthNameComparator implements Comparator<PlaceLigthRecord> {
    public int compare(PlaceLigthRecord plR1, PlaceLigthRecord plR2) {
        return plR1.name().compareTo(plR2.name());
    }
}