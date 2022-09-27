package fhir;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import fhir.repository.UserRepository;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = UserRepository.class)

public class FhirApplication /*implements CommandLineRunner*/{

    @Autowired
    private ApplicationContext context;
  /*  @Autowired
   JdbcTemplate jdbcTemplate;*/

    public static void main(String[] args) {

        SpringApplication.run(FhirApplication.class, args);
    }

    /*@Bean
    public ServletRegistrationBean ServletRegistrationBean() {
        ServletRegistrationBean registration= new ServletRegistrationBean(new FhirRestfulServer(context),"/*");
        registration.setName("FhirServlet");
        return registration;
    }*/
    //Test-Kommentar

}
