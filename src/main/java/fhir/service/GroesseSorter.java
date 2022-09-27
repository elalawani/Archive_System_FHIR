package fhir.service;

import java.util.Comparator;

import fhir.models.MetadataForUser;

public class GroesseSorter implements Comparator<MetadataForUser> {

	@Override
	public int compare(MetadataForUser o1, MetadataForUser o2) {
		
		
		return o1.getDateiGroesse() - (o2.getDateiGroesse());	
	}

}
