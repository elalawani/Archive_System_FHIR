package fhir.models;

import java.io.File;

public class MetadataForUser {
	
	private String title;
	private String erstellt_am;
	private String erstellt_von;
	private int dateiGroesse;
	private String version;
	private File datei;
	private int id;
	
	public MetadataForUser(String titel,String erstellt_am,String erstellt_von,
			int dateiGröße, String version, int id) {
		this.setTitle(titel);
		this.setErstellt_am(erstellt_am);
		this.setErstellt_von(erstellt_von);
		this.setDateiGroesse(dateiGröße);
		this.setVersion(version);
		this.setId(id);
		
	}

	public MetadataForUser(String titel,String erstellt_am,String erstellt_von,
			int dateiGröße, String version) {
		this.setTitle(titel);
		this.setErstellt_am(erstellt_am);
		this.setErstellt_von(erstellt_von);
		this.setDateiGroesse(dateiGröße);
		this.setVersion(version);
		
		
	}
	
	public MetadataForUser(String titel,String erstellt_am,String erstellt_von,
			int dateiGröße, String version,File datei,int id) {
		this.setTitle(titel);
		this.setErstellt_am(erstellt_am);
		this.setErstellt_von(erstellt_von);
		this.setDateiGroesse(dateiGröße);
		this.setVersion(version);
		this.setDatei(datei);
		this.setId(id);
		
	}
	
/**
 * Methode zur Bereitstellung der Id.
 * @return Die Datei_id von der Datenbank.
 */
	public int getId() {
		return id;
	}
	
/**
 * 	Methode für die Übergabe einen neuen Id.
 * @param id
 */
	public void setId(int id) {
		this.id = id;
	}
	
/**
 * 	Methode zur Bereitstellung des Titels(Dateiname).
 * @return der Dateiname
 */
	public String getTitle() {
		return title;
	}
	
/**
 * 	Methode für die Übergabe einen neuen Titel(Dateiname).
 * @param titel
 */
	public void setTitle(String titel) {
		this.title = titel;
	}
	
/**
 * Methode zur Bereitstellung des Erstellungsdatum
 * @return Die Erstellungsdatum.
 */
	public String getErstellt_am() {
		return erstellt_am;
	}
	
/**
 * Methode für die Übergabe ein neues ErstellungsDatum .
 * @param erstellt_am
 */
	public void setErstellt_am(String erstellt_am) {
		this.erstellt_am = erstellt_am;
	}
	
/**
 * Methode zur Bereitstellung des Users, der eine bestimmten Datei hochgeladet hat.
 * @return User der eine bestimmten Datei hochgeladet hat.
 */
	public String getErstellt_von() {
		return erstellt_von;
	}
	
/**
 * Methode für die Übergabe einen neuen Users, der eine bestimmten Datei hochgeladet hat.
 * @param erstellt_von
 */
	public void setErstellt_von(String erstellt_von) {
		this.erstellt_von = erstellt_von;
	}
	
/**
 * Methode zur Bereitstellung der Dateigröße.
 * @return Die Dateigröße.
 */
	public int getDateiGroesse() {
		return dateiGroesse;
	}
	
/**
 * Methode für die Übergabe der Dateigröße.
 * @param dateiGröße
 */
	public void setDateiGroesse(int dateiGröße) {
		this.dateiGroesse = dateiGröße;
	}
	
/**
 * Methode zur Bereitstellung der Version.
 * @return die Version.
 */
	public String getVersion() {
		return version;
	}
	
/**
 * Methode für die Übergabe eine neue Version.
 * @param version
 */
	public void setVersion(String version) {
		this.version = version;
	}
	
/**
 * Methode zur Bereitstellung der Datei(File).
 * @return Die Datei
 */
	public File getDatei() {
		return datei;
	}
	
/**
 * Methode für die Übergabe eine neue Datei.
 * @param datei
 */
	public void setDatei(File datei) {
		this.datei = datei;
	}

}
