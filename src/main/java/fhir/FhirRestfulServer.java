package fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.RestfulServer;
import fhir.service.ResourceProvider;

import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;


@WebServlet("/*")
public class FhirRestfulServer extends RestfulServer {

    
	private ApplicationContext applicationContext;

    FhirRestfulServer(ApplicationContext context) {

        this.applicationContext = context;
    }

    @Override
    protected void initialize() throws ServletException{
        super.initialize();
        setFhirContext(FhirContext.forR4());
        registerProviders(ResourceProvider.class);
    }
}
