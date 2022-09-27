package fhir.service;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Search;
import fhir.models.MetadataForUser;
import fhir.models.Resource;
import fhir.repository.ResourceRepository;

//import für die Validierung

import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
@Service
public class ResourceProvider {

	@Autowired
	private ResourceRepository resourceRepository;

		Connection conn;

		public Optional<Resource> getFile(Integer fileId){
			return resourceRepository.findById(fileId);
		}


		/**
		 * Die Methode zur Speicherung einer neuen Datei. Bekommt 4 Parameter übergeben.
		 * @param datei
		 * @param dbDateiName
		 * @param jsonText
		 * @param dbUser
		 * @return procedureResult: Der Output Parameter der Datenbank-Stored Procedure; 0,1,2,3 jenach 
		 * Antwort der Stored Procedure.
		 * 0: Wenn die Datei schon existiert.
		 * 1: Wenn eine neue Datei mit einer Patientreferenz hochgeladen wurde.
		 * 2: Wenn eine neue Datei ohne Patientreferenz hochgeladen wurde.
		 * 3: Wenn eine neue Version einer Datei hochgeladen wurde.   
		 */
		
	@Create
	public int create(byte[] datei,String dbDateiName, String jsonText,String dbUser) {
	
		Resource theResourceToUpdate=new Resource(dbDateiName,datei,jsonText,dbUser);
		
		//Connection to the Database and Calling the stored procedure "insertionNew" 
		//to fill up the Tables.
		int procedureResult=0;

		try {
			
			conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/archivsystem_swtp", "postgres", "1234");
			 System.out.println("Connected to the PostgreSQL server successfully.");
		
			CallableStatement q = conn.prepareCall("CALL insertionnew(?::json,?,?,?,?)");
			q.setString(1,theResourceToUpdate.getResourceinhalt());
			q.setString(2,theResourceToUpdate.getDatei_Name() );
			q.setString(3, dbUser);
			q.setBytes(4,theResourceToUpdate.getDatei());
			q.setInt(5, procedureResult);
			q.registerOutParameter(5, Types.INTEGER);
			q.executeUpdate();
			
			procedureResult=q.getInt(5);
				
			conn.close();
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
			System.out.println("Stored Procedure excecuted successfully.");
	
		return procedureResult;
	}
	
	//ADMINISTRATOR SEARCH METHODES
    
    /**
     * Methode sucht in der Datenbank nach alle vorhandene Dateien.
     * Methode nur für Admin.
     * @return Eine Liste mit den anzuzeigenen Metadaten.
     */
	
	@Search
    public List<MetadataForUser> searchAllFiles(){
				
		List <MetadataForUser> result=new ArrayList<MetadataForUser>();
			
		try {
			conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/archivsystem_swtp", "postgres", "1234");
			System.out.println("Connected to the PostgreSQL server successfully.");
		
				PreparedStatement q = conn.prepareStatement("select datei_id,datei_name,aenderungsdatum,username,groesse/1024 AS Groesse,version \r\n"
						+ "from metadaten m LEFT JOIN resources r ON m.datei_resource=r.datei_ID LEFT JOIN users u ON m.herkunft=u.user_id");
			
				ResultSet rs=q.executeQuery();
				
				while (rs.next()) {
					 String id = rs.getString(1);
					 String titel=rs.getString(2);
					 String erstellt_am=(String)rs.getString(3);
					 String erstellt_von=rs.getString(4);
					 int dateiGroesse=rs.getInt(5);
					 String version=(String)rs.getString(6);
					 
						 
					 
					 MetadataForUser a=new MetadataForUser(titel,erstellt_am,erstellt_von,dateiGroesse,version,Integer.parseInt(id));
					 result.add(a);
					 System.out.println("metadata: "+ titel+" " +erstellt_am+" " +erstellt_von+" " +dateiGroesse+" " +version+" " +Integer.parseInt(id));
				}
				
				 conn.close();
				 
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				
			}
		
		        return result;
		        		
    }
	
	/**
	 * Methode bekommt einen Parameter übergeben und sucht in der Datenbank nach einem bestimmten Resourcentyp.
	 * Methode nur für Admins.
	 * @param rTyp
	 * @return Eine Liste mit den anzuzeigenen Metadaten eines bestimmten Resourcentyps.
	 */
	
	@Search
    public List<MetadataForUser> searchByResourceType(String rTyp){
		
		List <MetadataForUser> result=new ArrayList<MetadataForUser>();
	
		try {
			conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/archivsystem_swtp", "postgres", "1234");
			System.out.println("Connected to the PostgreSQL server successfully.");
		
				PreparedStatement q = conn.prepareStatement("select datei_id,datei_name,aenderungsdatum,username,groesse/1024 AS Groesse ,version\r\n"
						+ "from metadaten m LEFT JOIN resources r ON m.datei_resource=r.datei_ID LEFT JOIN users u ON m.herkunft=u.user_id\r\n"
						+ "where r.resourcentyp LIKE ?");
				q.setString(1,rTyp);
				
				ResultSet rs=q.executeQuery();
				
			
				while (rs.next()) {
					String id = rs.getString(1);
					 String titel=rs.getString(2);
					 String erstellt_am=(String)rs.getString(3);
					 String erstellt_von=rs.getString(4);
					 int dateiGroesse=rs.getInt(5);
					 String version=(String)rs.getString(6);
				 
				 MetadataForUser a=new MetadataForUser(titel,erstellt_am,erstellt_von,dateiGroesse,version,Integer.parseInt(id));
				 result.add(a);
					 
				}
				 conn.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		
		return result;
}
	
	
	
	/**
	 * Methode bekommt einen Parameter übergeben und sucht in der Datenbank nach einem bestimmten Dateiname.
	 * Methode nur für Admins.
	 * @param Fname
	 * @return Eine Liste mit den anzuzeigenen Metadaten einer bestimmten Datei.
	 */
	
	@Search
    public List<MetadataForUser>searchByFileName(String Fname){
		
		List <MetadataForUser> result=new ArrayList<MetadataForUser>();
	
		try {
			conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/archivsystem_swtp", "postgres", "1234");
			System.out.println("Connected to the PostgreSQL server successfully.");
		
				PreparedStatement q = conn.prepareStatement("select datei_id,datei_name,aenderungsdatum,username,groesse/1024 AS Groesse ,version \r\n"
						+ "from metadaten m LEFT JOIN resources r ON m.datei_resource=r.datei_ID LEFT JOIN users u ON m.herkunft=u.user_id\r\n"
						+ "where r.datei_name LIKE ?");
				q.setString(1,Fname);
				ResultSet rs=q.executeQuery();
				
				while (rs.next()) {
					String id = rs.getString(1);
					 String titel=rs.getString(2);
					 String erstellt_am=(String)rs.getString(3);
					 String erstellt_von=rs.getString(4);
					 int dateiGroesse=rs.getInt(5);
					 String version=(String)rs.getString(6);
					 
				 
				 MetadataForUser a=new MetadataForUser(titel,erstellt_am,erstellt_von,dateiGroesse,version,Integer.parseInt(id));
				 result.add(a);
					 
					 
				}
				
				 conn.close();
				 
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				
			}
		
		return result;
}
	
	/**
	 * Methode bekommt einen Parameter übergeben und sucht in der Datenbank nach Datein, die von
	 * einem bestimmten User hochgeladen wurde.
	 * Methode für Admins and User.
	 * @param userNa
	 * @return Eine Liste mit den anzuzeigenen Metadaten von den von einem bestimmten User hochgeladenen Dateien.
	 */
	
	@Search
    public List<MetadataForUser> searchByUsername(String userNa){
		
		List <MetadataForUser> result=new ArrayList<MetadataForUser>();
	
		try {
			conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/archivsystem_swtp", "postgres", "1234");
			System.out.println("Connected to the PostgreSQL server successfully.");
		
				PreparedStatement q = conn.prepareStatement("select datei_name,aenderungsdatum,username,groesse/1024 AS Groesse ,version \r\n"
						+ "from metadaten m LEFT JOIN resources r ON m.datei_resource=r.datei_ID LEFT JOIN users u ON m.herkunft=u.user_id\r\n"
						+ "where u.username=?");
				q.setString(1,userNa);
				ResultSet rs=q.executeQuery();
				
				while (rs.next()) {
						 String title=rs.getString(1);
						 String erstellt_am=rs.getString(2);
						 String erstellt_von=rs.getString(3);
						 int dateiGroesse=rs.getInt(5);
						 String version=rs.getString(5);
					
						
						 
					 MetadataForUser a=new MetadataForUser(title,erstellt_am,erstellt_von,dateiGroesse,version);
					 result.add(a);
					 
					 
				}
				
				 conn.close();
				 
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				
			}
		
		return result;
	}

	/*USER_METHODES(NO ADMIN)*/
	
	/**
	 * Methode bekommt einen Parameter übergeben und sucht in der Datenbank nach Datein, die von
	 * einem bestimmten User hochgeladen wurde.
	 * Methode nur für Admins.
	 * @param username
	 * @return Eine Liste mit den anzuzeigenen Metadaten von den von einem bestimmten User hochgeladenen Dateien.
	 */
/*	
	@Search
    public List<MetadataForUser> searchAllFiles_ForUser(String username){
				
		List <MetadataForUser> result=new ArrayList<MetadataForUser>();
			
		try {
			conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/archivsystem_swtp", "postgres", "1234");
			System.out.println("Connected to the PostgreSQL server successfully.");
		
				PreparedStatement q = conn.prepareStatement("select datei_id,datei_name,aenderungsdatum,username,groesse/1024 AS Groesse,version \r\n"
						+ "from metadaten m LEFT JOIN resources r ON m.datei_resource=r.datei_ID LEFT JOIN users u ON m.herkunft=u.user_id where username=?");
				q.setString(1, username);
				ResultSet rs=q.executeQuery();
				
				while (rs.next()) {
					 String id = rs.getString(1);
					 String titel=rs.getString(2);
					 String erstellt_am=(String)rs.getString(3);
					 String erstellt_von=rs.getString(4);
					 int dateiGroesse=rs.getInt(5);
					 String version=(String)rs.getString(6);
					 
						 
					 
					 MetadataForUser a=new MetadataForUser(titel,erstellt_am,erstellt_von,dateiGroesse,version,Integer.parseInt(id));
					 result.add(a);
					 System.out.println("metadata: "+ titel+" " +erstellt_am+" " +erstellt_von+" " +dateiGroesse+" " +version+" " +Integer.parseInt(id));
				}
				 conn.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		
		        return result;
		        		
    }*/
	
	/**
	 * Methode bekommt 2 Parameter(Resourcentyp und username) übergeben und sucht in der Datenbank nach einem
	 * bestimmten Resourcentyp, hochgeladen von einem bestimmten User.
	 * @param rTyp
	 * @param username
	 * @return Eine Liste mit den anzuzeigenen Metadaten eines bestimmten Resourcentyps, die von einem bestimmten
	 * User hochgeladen wurde.
	 */
	
	@Search
    public List<MetadataForUser> searchByResourceType_ForUser(String rTyp,String username){
		
		List <MetadataForUser> result=new ArrayList<MetadataForUser>();
	
		try {
			conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/archivsystem_swtp", "postgres", "1234");
			System.out.println("Connected to the PostgreSQL server successfully.");
		
				PreparedStatement q = conn.prepareStatement("select datei_id,datei_name,aenderungsdatum,username,groesse/1024 AS Groesse ,version\r\n"
						+ "from metadaten m LEFT JOIN resources r ON m.datei_resource=r.datei_ID LEFT JOIN users u ON m.herkunft=u.user_id\r\n"
						+ "where r.resourcentyp LIKE ? AND u.username=?");
				q.setString(1,rTyp);
				q.setString(2, username);
				
				ResultSet rs=q.executeQuery();
				
			
				while (rs.next()) {
					String id = rs.getString(1);
					 String titel=rs.getString(2);
					 String erstellt_am=(String)rs.getString(3);
					 String erstellt_von=rs.getString(4);
					 int dateiGroesse=rs.getInt(5);
					 String version=(String)rs.getString(6);
				 
				 MetadataForUser a=new MetadataForUser(titel,erstellt_am,erstellt_von,dateiGroesse,version,Integer.parseInt(id));
				 result.add(a);
					 
				}
				
				 conn.close();
				 
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				
			}
		
		return result;
}
	
	/**
	 * Methode bekommt 2 Parameter übergeben(Dateiname,username) und sucht in der Datenbank 
	 * nach einem bestimmten Dateiname, die von einem bestimmten User hochgeladen wurde.
	 * Methode nur für User.
	 * @param Fname
	 * @param username
	 * @return EEine Liste mit den anzuzeigenen Metadaten eines bestimmten Datei, die von einem bestimmten
	 * User hochgeladen wurde.
	 */
	
	@Search
    public List<MetadataForUser>searchByFileName_ForUser(String Fname,String username){
		
		List <MetadataForUser> result=new ArrayList<MetadataForUser>();
	
		try {
			conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/archivsystem_swtp", "postgres", "1234");
			System.out.println("Connected to the PostgreSQL server successfully.");
		
				PreparedStatement q = conn.prepareStatement("select datei_id,datei_name,aenderungsdatum,username,groesse/1024 AS Groesse ,version \r\n"
						+ "from metadaten m LEFT JOIN resources r ON m.datei_resource=r.datei_ID LEFT JOIN users u ON m.herkunft=u.user_id\r\n"
						+ "where r.datei_name LIKE ? AND u.username=?");
				q.setString(1,Fname);
				q.setString(2, username);
				ResultSet rs=q.executeQuery();
				
				while (rs.next()) {
					String id = rs.getString(1);
					 String titel=rs.getString(2);
					 String erstellt_am=(String)rs.getString(3);
					 String erstellt_von=rs.getString(4);
					 int dateiGroesse=rs.getInt(5);
					 String version=(String)rs.getString(6);
					 
				 
				 MetadataForUser a=new MetadataForUser(titel,erstellt_am,erstellt_von,dateiGroesse,version,Integer.parseInt(id));
				 result.add(a);
					 
					 
				}
				
				 conn.close();
				 
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				
			}
		
		return result;
}
	
	
	//METHODE ZUM SORTIEREN(FILTER)
	
	/**
	 * Methode zum sortieren der anzuzeigenen Metadaten nach dem Titel(Dateiname).
	 * @return Eine Liste der anzuzeigenen Metadaten nach dem Titel(Dateiname) sortiert.
	 */
	
	@Search
    public List<MetadataForUser>FilterNachTitel(){
		
		List <MetadataForUser> result=new ArrayList<MetadataForUser>();
	
		try {
			conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/archivsystem_swtp", "postgres", "1234");
			System.out.println("Connected to the PostgreSQL server successfully.");
		
				PreparedStatement q = conn.prepareStatement("select datei_id,datei_name,aenderungsdatum,username,groesse/1024 AS Groesse ,version \r\n"
						+ "from metadaten m LEFT JOIN resources r ON m.datei_resource=r.datei_ID LEFT JOIN users u ON m.herkunft=u.user_id\r\n"
						+ "ORDER BY datei_name");
				
				ResultSet rs=q.executeQuery();
				
				while (rs.next()) {
					String id = rs.getString(1);
					 String titel=rs.getString(2);
					 String erstellt_am=(String)rs.getString(3);
					 String erstellt_von=rs.getString(4);
					 int dateiGroesse=rs.getInt(5);
					 String version=(String)rs.getString(6);
				 
				 MetadataForUser a=new MetadataForUser(titel,erstellt_am,erstellt_von,dateiGroesse,version,Integer.parseInt(id));
				 result.add(a);
					 
					 
				}
				
				 conn.close();
				 
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				
			}
		
		return result;
}
	
	/**
	 * Methode zum sortieren der anzuzeigenen Metadaten nach dem Datum.
	 * @return Eine Liste der anzuzeigenen Metadaten nach dem Datum sortiert.
	 */
	
	@Search	
	public List<MetadataForUser>FilterNachDatum(){
		
		List <MetadataForUser> result=new ArrayList<MetadataForUser>();
	
		try {
			conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/archivsystem_swtp", "postgres", "1234");
			System.out.println("Connected to the PostgreSQL server successfully.");
		
				PreparedStatement q = conn.prepareStatement("select datei_id,datei_name,aenderungsdatum,username,groesse/1024 AS Groesse ,version \r\n"
						+ "from metadaten m LEFT JOIN resources r ON m.datei_resource=r.datei_ID LEFT JOIN users u ON m.herkunft=u.user_id\r\n"
						+ "ORDER BY aenderungsdatum");
				
				ResultSet rs=q.executeQuery();
				
				while (rs.next()) {
					String id = rs.getString(1);
					 String titel=rs.getString(2);
					 String erstellt_am=(String)rs.getString(3);
					 String erstellt_von=rs.getString(4);
					 int dateiGroesse=rs.getInt(5);
					 String version=(String)rs.getString(6);
					 
				 
				 MetadataForUser a=new MetadataForUser(titel,erstellt_am,erstellt_von,dateiGroesse,version,Integer.parseInt(id));
				 result.add(a);
					 
					 
				}
				
				 conn.close();
				 
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				
			}
		
		return result;
}

	/**
	 * Methode zum sortieren der anzuzeigenen Metadaten nach dem Username.
	 * @return Eine Liste der anzuzeigenen Metadaten nach dem Username sortiert.
	 */
	
	@Search
	public List<MetadataForUser>FilterNachUser(){
	
	List <MetadataForUser> result=new ArrayList<MetadataForUser>();

	try {
		conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/archivsystem_swtp", "postgres", "1234");
		System.out.println("Connected to the PostgreSQL server successfully.");
	
			PreparedStatement q = conn.prepareStatement("select datei_id,datei_name,aenderungsdatum,username,groesse/1024 AS Groesse ,version\r\n"
					+ "from metadaten m LEFT JOIN resources r ON m.datei_resource=r.datei_ID LEFT JOIN users u ON m.herkunft=u.user_id\r\n"
					+ "ORDER BY username");
			
			ResultSet rs=q.executeQuery();
			
			while (rs.next()) {
				String id = rs.getString(1);
				 String titel=rs.getString(2);
				 String erstellt_am=(String)rs.getString(3);
				 String erstellt_von=rs.getString(4);
				 int dateiGroesse=rs.getInt(5);
				 String version=(String)rs.getString(6);
				 
			 
			 MetadataForUser a=new MetadataForUser(titel,erstellt_am,erstellt_von,dateiGroesse,version,Integer.parseInt(id));
			 result.add(a);
				 
				 
			}
			
			 conn.close();
			 
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			
		}
	
	return result;
}

	/**
	 * Methode zum sortieren der anzuzeigenen Metadaten nach dem Dateigröße.
	 * @return Eine Liste der anzuzeigenen Metadaten nach dem Dateigröße sortiert.
	 */
	
	@Search
	public List<MetadataForUser>FilterNachGroesse(){
	
	List <MetadataForUser> result=new ArrayList<MetadataForUser>();

	try {
		
		
		conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/archivsystem_swtp", "postgres", "1234");
		System.out.println("Connected to the PostgreSQL server successfully.");
	
			PreparedStatement q = conn.prepareStatement("select datei_id, datei_name,aenderungsdatum,username,groesse/1024 AS Groesse ,version \r\n"
					+ "from metadaten m LEFT JOIN resources r ON m.datei_resource=r.datei_ID LEFT JOIN users u ON m.herkunft=u.user_id\r\n"
					+ "ORDER BY groesse");
			
			ResultSet rs=q.executeQuery();
			
			while (rs.next()) {
				String id = rs.getString(1);
				 String titel=rs.getString(2);
				 String erstellt_am=(String)rs.getString(3);
				 String erstellt_von=rs.getString(4);
				 int dateiGroesse=rs.getInt(5);
				 String version=(String)rs.getString(6);
				 
			 
			 MetadataForUser a=new MetadataForUser(titel,erstellt_am,erstellt_von,dateiGroesse,version,Integer.parseInt(id));
			 result.add(a);
				  
				 
			}
			
			 conn.close();
			 
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			
		}
	
	return result;
}
	
	
	
		//validierungsmethod
		/**
		 * Methode bekommt einen Resourcentext als Parameter übergeben und validiert 
		 * die Resource nach dem FHIR Standard.
		 * @param resource
		 * @return true or false je nach dem Ergebnis der valiedierung. 
		 */
		public boolean validierung (String resource) {
			boolean b =true;
			
			 // Create a new validator
		      FhirContext ctx = FhirContext.forR4();
		      
		      /*Adding validation Support Modules:
		      	The  DefaultProfileValidationSupport is enought when we just
		      	need to validate against the core fhir specification.
		      	This loads the StructureDefinitions,ValueSet and CodeSystem for validation.
		      */
		      ValidationSupportChain validationSupportChain = new ValidationSupportChain(
		    		   new DefaultProfileValidationSupport(ctx),
		    		   new InMemoryTerminologyServerValidationSupport(ctx),
		    		   new CommonCodeSystemsTerminologyService(ctx)
		    		  
		    		);
		      
		      FhirValidator validator = ctx.newValidator();
		      FhirInstanceValidator instanceValidator = new FhirInstanceValidator(validationSupportChain);
		      validator.registerValidatorModule(instanceValidator);
		      
		      // Did we succeed?
		      try {
		      ValidationResult result = validator.validateWithResult(resource);
		      
		      b = result.isSuccessful();
		      }catch (Exception e) {
		    	  b=false;
		    	  System.out.println(e.getMessage());
		    	  
		      }
			return b;
		}
	
		
		
		
		
		
}
