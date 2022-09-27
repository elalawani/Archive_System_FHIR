package fhir.service;

import java.util.Comparator;

import fhir.models.MetadataForUser;

public class ErstellerSorter implements Comparator<MetadataForUser> {

	@Override
	public int compare(MetadataForUser o1, MetadataForUser o2) {
		
		
		return o2.getErstellt_von().compareTo(o1.getErstellt_von());	
	}

}
