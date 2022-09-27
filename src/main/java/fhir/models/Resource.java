package fhir.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;





/*@NamedStoredProcedureQuery(
	    name = "insertionNew", 
	    procedureName = "insertionNew", 
	    parameters = {
	        @StoredProcedureParameter(mode = ParameterMode.IN, type = String.class, name="json_txt"), 
	        @StoredProcedureParameter(mode = ParameterMode.IN, type = String.class, name="datei_N"),
	        @StoredProcedureParameter(mode = ParameterMode.IN, type = Integer.class, name="herkunft")
	    }
	)*/
@Entity
@Table(name="resources")
public class Resource {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer datei_Id;
	
	private String datei_Name;
	private String resourcentyp;
	private String resourcen_Id;
	private byte [] datei;
	private String resourceinhalt ;
	private  Integer patientreferenz;


	/*Constructors to Send jsonTxt to the Database*/
	
	
	public Resource(String datei_Name,byte[] datei,String resourceinhalt,String dbUser) {
		super();
		this.datei_Name = datei_Name;
		this.datei = datei;
		this.resourceinhalt = resourceinhalt;
	}
	

	public Resource(Integer datei_Id, String datei_Name, String resourcentyp, String resourcen_Id, byte[] datei,
			String resourceinhalt, int patientreferenz) {
		super();
		this.datei_Id = datei_Id;
		this.datei_Name = datei_Name;
		this.resourcentyp = resourcentyp;
		this.resourcen_Id = resourcen_Id;
		this.datei = datei;
		this.resourceinhalt = resourceinhalt;
		this.patientreferenz = patientreferenz;
	}


	public Resource() {
		super();
	}
	

	/**
	 * Methode zur Bereitstellung des Dateiname.
	 * @return Die Dateiname.
	 */
	public String getDatei_Name() {
		return datei_Name;
	}

	/**
	 * Methode für die Übergabe eines neuen Dateiname
	 * @param datei_Name
	 */
	public void setDatei_Name(String datei_Name) {
		this.datei_Name = datei_Name;
	}

	/**
	 * Methode zur Bereitstellung der Datei.
	 * @return Die Datei
	 */
	public byte[] getDatei() {
		return datei;
	}
	
	/**
	 * Methode für die Übergabe einer Datei
	 * @param datei
	 */
	public void setDatei(byte[] datei) {
		this.datei = datei;
	}

	/**
	 * Methode zur Bereitstellung der Datei_id.
	 * @return Die Datei_id.
	 */
	public int getDatei_Id() {
		return datei_Id;
	}
	
	/**
	 * Methode für die Übergabe einer Datei_id
	 * @param datei_Id
	 */
	public void setDatei_Id(int datei_Id) {
		this.datei_Id = datei_Id;
	}
	
	/**
	 * Methode zur Bereitstellung der Resourcen_id
	 * @return Die Resourcen_id
	 */
	public String getResourcen_Id() {
		return resourcen_Id;
	}
	
	/**
	 * Methode für die Übergabe einer Resourcen_id
	 * @param resourcen_Id
	 */
	public void setResourcen_Id(String resourcen_Id) {
		this.resourcen_Id = resourcen_Id;
	}
	
	/**
	 * Methode zur Bereitstellung des Resourcentyps.
	 * @return Der Resourcentyp
	 */
	public String getResourcentyp() {
		return resourcentyp;
	}
	
	/**
	 * Methode für die Übergabe des Resourcentyps.
	 * @param resourcentyp
	 */
	public void setResourcentyp(String resourcentyp) {
		this.resourcentyp = resourcentyp;
	}
	
	/**
	 * Methode zur Bereitstellung der Patientreferenz.
	 * @return Die Patientreferenz
	 */
	public Integer getPatientreferenz() {
		return patientreferenz;
	}
	
	/**
	 * Methode für die Übergabe einer Patientreferenz
	 * @param patientreferenz
	 */
	public void setPatientreferenz(int patientreferenz) {
		this.patientreferenz = patientreferenz;
	}
	
	/**
	 * Methode zur Bereitstellung des Resourceninhalt.
	 * @return Der Inhalt der Resource. 
	 */
	public String getResourceinhalt() {
		return resourceinhalt;
	}
	
	/**
	 * Methode für die Übergabe des Resurceninhalts
	 * @param resourceinhalt
	 */
	public void setResourceinhalt(String resourceinhalt) {
		this.resourceinhalt = resourceinhalt;
	}
	

}

