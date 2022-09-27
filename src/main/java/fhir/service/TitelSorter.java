package fhir.service;

import java.util.Comparator;

import fhir.models.MetadataForUser;

public class TitelSorter implements Comparator<MetadataForUser> {

	@Override
	public int compare(MetadataForUser o1, MetadataForUser o2) {
		
		
		return o1.getTitle().compareTo(o2.getTitle());	
	}
}
