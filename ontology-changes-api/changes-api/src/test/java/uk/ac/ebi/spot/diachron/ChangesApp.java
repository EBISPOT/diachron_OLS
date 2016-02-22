package uk.ac.ebi.spot.diachron;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import uk.ac.ebi.spot.diachron.model.Change;
import uk.ac.ebi.spot.diachron.model.ChangeSummary;
import uk.ac.ebi.spot.diachron.respository.ChangeRepository;
import uk.ac.ebi.spot.diachron.service.ChangeSummaryService;
//import uk.ac.ebi.spot.diachron.respository.ChangeSummaryRepository;

import java.util.*;

/**
 * @author Simon Jupp
 * @date 28/01/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 *
 * This loads some dummy data into the mongo DB, not really a test
 *
 */

@SpringBootApplication
@EnableAutoConfiguration
public class ChangesApp  implements CommandLineRunner {

//
//    @Autowired
//    ChangeSummaryRepository changeSummaryRepository;

    @Autowired
    ChangeSummaryService changeSummaryService;

    @Autowired
    ChangeRepository changeRepository;

    public static void main(String[] args) {

        SpringApplication.run(ChangesWebApplication.class, args);


    }

    @Override
    public void run(String... strings) throws Exception {
//        changeSummaryRepository.deleteAll();
        changeRepository.deleteAll();


        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(2015, 3, 15);
        Date date = cal.getTime(); // get back a Date object

//        changeSummaryRepository.save(new ChangeSummary("Addlabel", date, "efo", "2.67", 12));
//        changeSummaryRepository.save(new ChangeSummary("Addclass", date, "efo", "2.67", 23));
//        changeSummaryRepository.save(new ChangeSummary("Addsynonym", date, "efo", "2.67", 31));
//        changeSummaryRepository.save(new ChangeSummary("Adddefinition", date, "efo", "2.67", 3));
//        changeSummaryRepository.save(new ChangeSummary("Deletelabel", date, "efo", "2.67", 18));
//        changeSummaryRepository.save(new ChangeSummary("Deletesynonoym", date, "efo", "2.67", 35));
//        changeSummaryRepository.save(new ChangeSummary("Deleteclass", date, "efo", "2.67", 42))    ;
//        changeSummaryRepository.save(new ChangeSummary("Deletedefinition", date, "efo", "2.67", 75));

        Map<String, Collection<String>> propep = new HashMap<>();
        propep.put("predicate", Collections.singleton("label"));
        propep.put("label", Collections.singleton("new label 1"));

        changeRepository.save(new Change(date, "efo", "Add label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add class", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete synonoym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete synonoym", "http://www.ebi.ac.uk/efo/EFO_00000002", propep));

        cal.setTimeInMillis(0);
        cal.set(2015, 4, 15);
        date = cal.getTime(); // get back a Date object

        changeRepository.save(new Change(date, "efo", "Add label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add labelclass", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add labelclass", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete synonoym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete synonoym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete synonoym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete synonoym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete synonoym", "http://www.ebi.ac.uk/efo/EFO_00000002", propep));

        cal.setTimeInMillis(0);
        cal.set(2015, 5, 15);
        date = cal.getTime(); // get back a Date object

        changeRepository.save(new Change(date, "efo", "Add label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add labelclass", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add labelclass", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete synonoym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete synonoym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete synonoym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete synonoym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete synonoym", "http://www.ebi.ac.uk/efo/EFO_00000002", propep));

//
//        changeSummaryRepository.save(new ChangeSummary("Add label", date, "efo", "2.67", 12));
//        changeSummaryRepository.save(new ChangeSummary("Add labelclass", date, "efo", "2.67", 23));
//        changeSummaryRepository.save(new ChangeSummary("Add labelsynonym", date, "efo", "2.67", 31));
//        changeSummaryRepository.save(new ChangeSummary("Add labeldefinition", date, "efo", "2.67", 3));
//        changeSummaryRepository.save(new ChangeSummary("Delete label", date, "efo", "2.67", 18));
//        changeSummaryRepository.save(new ChangeSummary("Delete synonoym", date, "efo", "2.67", 35));
//        changeSummaryRepository.save(new ChangeSummary("Delete class", date, "efo", "2.67", 42))    ;
//        changeSummaryRepository.save(new ChangeSummary("Delete definition", date, "efo", "2.67", 75));

        System.out.println("Get changes before today");
        System.out.println("--------------------");

        for (ChangeSummary changeSummary : changeSummaryService.findByOntologyNameAndChangeDateBefore("efo", new Date())) {
            System.out.println(changeSummary.toString());
        }
        System.out.println("");

    }
}
