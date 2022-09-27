package fhir.controller;



import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import fhir.models.MetadataForUser;
import fhir.models.Resource;
import fhir.service.DatumSorter;
import fhir.service.ErstellerSorter;
import fhir.service.GroesseSorter;
import fhir.service.ResourceProvider;
import fhir.service.TitelSorter;

@Controller


public class ResourceController {
	
	@Autowired 
	private ResourceProvider resourceProvider;
	
	List<MetadataForUser>metadata;
	

	/**
	 * PostMapping Methode, um die Datei oder eine Liste von Dateien von dem Benutzer in der Datenbank zu speichern.
	 * Bekommt 2 Parameter übergeben (Liste von Dateien und einen RedirectAttribute für den Erfolgnachricht nach dem 
	 * Upload)
	 * @param file
	 * @param ra
	 * @return Eine "Redirection" auf den Homepage.
	 */
	
	@PostMapping("/uploadFile")
	public String  uploadFiles( @RequestParam ("upload-file-name") MultipartFile[] file, Model model) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		List<String> display = new ArrayList<String>();
		
		try {
			int isInserted=0;
			String user=authentication.getName();

			for (MultipartFile file1:file) {

				String jsonString=new String(file1.getBytes(), StandardCharsets.UTF_8);
				String fileName=new String(file1.getOriginalFilename().substring(0,file1.getOriginalFilename().length()-5));
				byte [] datei = file1.getBytes();
				
				int dateiGroesse=(int) file1.getSize();
				
				if (dateiGroesse>20971520) {
					display.add(" Datei <" + fileName + "> ist zu Groß");
				}
				
				if(!resourceProvider.validierung(jsonString)) {
					System.out.println(resourceProvider.validierung(jsonString));
					display.add(" Datei <" + fileName + "> entspricht nicht dem FHIR Format");

				} else {
					isInserted=resourceProvider.create(datei,fileName,jsonString,user);
					System.out.println(isInserted);	
					
					
					switch(isInserted){
					case 0:
						display.add("Datei <" + fileName+ "> ist schon vorhanden");
						break;
					case 1:
						display.add("Datei <" + fileName+ "> mit einem exixtierenden Patientreferenz wurde erfolgreich hochgeladen");
						break;
					case 2:
						display.add("Datei <" + fileName+ "> ohne existierende Patientreferenz wurde erfolgreich hochgeladen");
						break;
					case 3:
						display.add("Eine neue Version der Datei <" + fileName+ "> wurde erfolgreich hochgeladen");
						break;
					}
					
				}
				
			}

		}catch (Exception e){
			System.out.println(e.getMessage());
		}
		
		model.addAttribute("answerToDisplay", display);
			
		return("/uploadMessage");
		}

	
	
	// Download methood
		
	/**
	 * 
	 * @param fileId
	 * @return
	 */
		@GetMapping("/downloadFile/{fileId}")
		public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable int fileId){
			Resource resourc = resourceProvider.getFile(fileId).get();
			
			System.out.println("testDownload");
			System.out.println(resourc.getDatei_Name());
			return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType("application/octet-stream"))
					.header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=\""+resourc.getDatei_Name()+".json\"")
					.body(new ByteArrayResource(resourc.getDatei()));
		}
	
		/**
		 * Methode für die Suche nach allen Dateien in der Datenbank.
		 * Bekommt einen Spring Model als Parameter übergeben, 
		 * um die gefundenen Dateien auf dem Frontend anzuzeigen.
		 * @param model
		 * @return Die Seite "archivedfiles"
		 */
		
	@GetMapping("/archivedfiles")
	public String searchAllArchivedfiles(Model model) {
		if (metadata != null) {
		metadata.clear();
		}
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication!=null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
			 metadata = resourceProvider.searchAllFiles();
			model.addAttribute("metadaten", metadata);
		}
		else {
		
		String user=authentication.getName();
		
		 metadata = resourceProvider.searchByUsername(user);
		model.addAttribute("metadaten", metadata);
		}
		return("/archivedfiles");
		
	}
	
	/**
	 * Methode bekommt einen Model als Parameter übergeben, um die resultierende Liste 
	 * der vorhandene Patienten zum User anzuzeigen. 
	 * @param model
	 * @return Eine Liste der Metadaten von den in der Datenbank vorhandene Patienten
	 */
	
	@GetMapping("/patient")
	public String patientSearch(Model model) {
		
		metadata.clear();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication!=null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
			
		    metadata =  resourceProvider.searchByResourceType("Patient");
			model.addAttribute("metadaten", metadata );
		}
		else {
			String user=authentication.getName();
			
			  metadata =  resourceProvider.searchByResourceType_ForUser("Patient",user);
				model.addAttribute("metadaten", metadata );
			
		}
			
			return("/archivedfiles");
	}
	
	/**
	 * Methode bekommt einen Model als Parameter übergeben, um die resultierende Liste 
	 * der vorhandene Observation zum User anzuzeigen. 
	 * @param model
	 * @return Eine Liste der Metadaten von den in der Datenbank vorhandene Observation.
	 */
	
	@GetMapping("/observation")
	public String observationSearch(Model model) {
		metadata.clear();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication!=null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
		
		  metadata =  resourceProvider.searchByResourceType("Observation");
			model.addAttribute("metadaten", metadata );
		}
		else {
			String user=authentication.getName();
			
			 metadata =  resourceProvider.searchByResourceType_ForUser("Observation",user);
			model.addAttribute("metadaten", metadata );
			
		}
			
			return("/archivedfiles");
	}
	
	/**
	 * Methode bekommt einen Model als Parameter übergeben, um die resultierende Liste 
	 * der vorhandene DiagnosticReport zum User anzuzeigen. 
	 * @param model
	 * @return Eine Liste der Metadaten von den in der Datenbank vorhandene DiagnosticReport.
	 */
	@GetMapping("/diagnostic")
	public String diagnosticSearch(Model model) {
		metadata.clear();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication!=null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
		
		 metadata =  resourceProvider.searchByResourceType("DiagnosticReport");
			model.addAttribute("metadaten", metadata );
		}
		
		else {
			String user=authentication.getName();
			
			 metadata =  resourceProvider.searchByResourceType_ForUser("DiagnosticReport",user);
			model.addAttribute("metadaten", metadata );
		}
			
			return("/archivedfiles");
	}
	
	/**
	 * Methode bekommt 2 Parameter übergeben (der Suchtext und das Model), um eine
	 * Suche nach Username,Resourcentyp oder Dateiname(für den Admin) und Resourcentyp,Dateiname
	 * (für einen einfacher User) in der Datenbank durchzuführen und zum User anzuzeigen.
	 * @param searchInput
	 * @param model
	 * @return Eine Liste der Metadaten der gefundenen Resourcen.
	 */
	
	@GetMapping("/search")
	public String searchALLByFileNameUserResourceTyp(@RequestParam("search_DB") String searchInput,Model model) {

		String firstLetter=searchInput.substring(0, 1);
		String restOfWord=searchInput.substring(1);
		searchInput=firstLetter.toUpperCase().concat(restOfWord);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication!=null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
			if(!resourceProvider.searchByResourceType(searchInput).isEmpty())
				model.addAttribute("metadaten", resourceProvider.searchByResourceType(searchInput) );
			else if(!resourceProvider.searchByUsername(searchInput).isEmpty())
				model.addAttribute("metadaten", resourceProvider.searchByUsername(searchInput) );
			else
				model.addAttribute("metadaten", resourceProvider.searchByFileName(searchInput) );
		}
		else {
			String user=authentication.getName();
			
			if(!resourceProvider.searchByResourceType_ForUser(searchInput,user).isEmpty())
				model.addAttribute("metadaten", resourceProvider.searchByResourceType_ForUser(searchInput,user) );
			else
				model.addAttribute("metadaten", resourceProvider.searchByFileName_ForUser(searchInput,user) );
			
		}
		
		return("/archivedfiles");
	}
	
	/**
	 * Methode bekommt einen Model für die Darstellung des Ergebnis übergeben und sortiert
	 * die aktuelle angezeigte Liste nach dem Datum. 
	 * @param model
	 * @return Eine nach dem Datum sortierte Liste. 
	 */
	
	@GetMapping("/datum")
	public String filterNachDatum(Model model) {
		

		 Collections.sort(metadata, new DatumSorter());
		
			model.addAttribute("metadaten", metadata );
			
			
			return("/archivedfiles");
	}
	
	/**
	 * Methode bekommt einen Model für die Darstellung des Ergebnis übergeben und sortiert
	 * die aktuelle angezeigte Liste nach der Größe. 
	 * @param model
	 * @return Eine nach der Größe sortierte Liste.
	 */
	@GetMapping("/groesse")
	public String filterNachGroesse(Model model) {
		
		 Collections.sort(metadata, new GroesseSorter());
		// List<MetadataForUser> metadata =  resourceProvider.FilterNachGroesse();
			model.addAttribute("metadaten", metadata );
			
			
			return("/archivedfiles");
	}
	
	/**
	 * Methode bekommt einen Model für die Darstellung des Ergebnis übergeben und sortiert
	 * die aktuelle angezeigte Liste nach dem Dateiname. 
	 * @param model
	 * @return Eine nach dem Dateiname sortierte Liste.
	 */
	@GetMapping("/dateiName")
	public String filterNachDateiName(Model model) {
		
		Collections.sort(metadata, new TitelSorter());
		// List<MetadataForUser> metadata =  resourceProvider.FilterNachTitel();
			model.addAttribute("metadaten", metadata );
			
			
			return("/archivedfiles");
	}
	
	/**
	 * Methode bekommt einen Model für die Darstellung des Ergebnis übergeben und sortiert
	 * die aktuelle angezeigte Liste nach dem User. 
	 * @param model
	 * @return Eine nach dem User sortierte Liste.
	 */
	@GetMapping("/username")
	public String filterNachUsername(Model model) {
		
		Collections.sort(metadata, new ErstellerSorter());
		// List<MetadataForUser> metadata =  resourceProvider.FilterNachUser();
			model.addAttribute("metadaten", metadata );
			
			
			return("/archivedfiles");
	}
	
}


