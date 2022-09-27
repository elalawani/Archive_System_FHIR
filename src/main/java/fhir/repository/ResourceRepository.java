package fhir.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fhir.models.Resource;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Integer> {
	   // Optional<Resource> findByDateiName(String DateiName);
	
}
