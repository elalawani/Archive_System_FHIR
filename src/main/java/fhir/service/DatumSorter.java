package fhir.service;

import java.util.Comparator;

import fhir.models.MetadataForUser;

public class DatumSorter implements Comparator<MetadataForUser> {

	@Override
	public int compare(MetadataForUser o1, MetadataForUser o2) {
		
		return o1.getErstellt_am().compareTo(o2.getErstellt_am());	
	}

}
