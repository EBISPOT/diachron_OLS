package uk.ac.ebi.spot.diachron;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ebi.spot.diachron.model.ChangeSummary;

import java.util.Calendar;
import java.util.Date;

@SpringBootApplication
@EnableAutoConfiguration
public class ChangesWebApplication extends SpringBootServletInitializer {

   public static void main(String[] args) {

       SpringApplication.run(ChangesWebApplication.class, args);
    }
}
