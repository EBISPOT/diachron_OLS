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
        cal.set(2016, 3, 15);
        Date date = cal.getTime(); // get back a Date object

//        changeSummaryRepository.save(new ChangeSummary("Add_label", date, "efo", "2.67", 12));
//        changeSummaryRepository.save(new ChangeSummary("Add_class", date, "efo", "2.67", 23));
//        changeSummaryRepository.save(new ChangeSummary("Add_synonym", date, "efo", "2.67", 31));
//        changeSummaryRepository.save(new ChangeSummary("Add_definition", date, "efo", "2.67", 3));
//        changeSummaryRepository.save(new ChangeSummary("Delete_label", date, "efo", "2.67", 18));
//        changeSummaryRepository.save(new ChangeSummary("Delete_synonoym", date, "efo", "2.67", 35));
//        changeSummaryRepository.save(new ChangeSummary("Delete_class", date, "efo", "2.67", 42))    ;
//        changeSummaryRepository.save(new ChangeSummary("Delete_definition", date, "efo", "2.67", 75));

        Map<String, Collection<String>> propep = new HashMap<>();
        propep.put("predicate", Collections.singleton("label"));
        propep.put("label", Collections.singleton("new label 1"));

        changeRepository.save(new Change(date, "efo", "Add_label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_class", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete_label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete_synonoym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete_synonoym", "http://www.ebi.ac.uk/efo/EFO_00000002", propep));

        cal.setTimeInMillis(0);
        cal.set(2016, 4, 15);
        date = cal.getTime(); // get back a Date object

        changeRepository.save(new Change(date, "efo", "Add_label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_class", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_class", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete_label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete_synonoym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete_synonoym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete_synonoym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete_synonoym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete_synonoym", "http://www.ebi.ac.uk/efo/EFO_00000002", propep));

        cal.setTimeInMillis(0);
        cal.set(2016, 5, 15);
        date = cal.getTime(); // get back a Date object

        changeRepository.save(new Change(date, "efo", "Add_label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_class", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_class", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Add_synonym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete_label", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete_synonoym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete_synonoym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete_synonoym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete_synonoym", "http://www.ebi.ac.uk/efo/EFO_00000001", propep));
        changeRepository.save(new Change(date, "efo", "Delete_synonoym", "http://www.ebi.ac.uk/efo/EFO_00000002", propep));

//
//        changeSummaryRepository.save(new ChangeSummary("Add_label", date, "efo", "2.67", 12));
//        changeSummaryRepository.save(new ChangeSummary("Add_class", date, "efo", "2.67", 23));
//        changeSummaryRepository.save(new ChangeSummary("Add_synonym", date, "efo", "2.67", 31));
//        changeSummaryRepository.save(new ChangeSummary("Add_definition", date, "efo", "2.67", 3));
//        changeSummaryRepository.save(new ChangeSummary("Delete_label", date, "efo", "2.67", 18));
//        changeSummaryRepository.save(new ChangeSummary("Delete_synonoym", date, "efo", "2.67", 35));
//        changeSummaryRepository.save(new ChangeSummary("Delete_class", date, "efo", "2.67", 42))    ;
//        changeSummaryRepository.save(new ChangeSummary("Delete_definition", date, "efo", "2.67", 75));

        System.out.println("By ontology name EFO");
        System.out.println("--------------------");


        for (ChangeSummary changeSummary : changeSummaryService.findByOntologyNameAndChangeDateBefore("efo", new Date())) {
            System.out.println(changeSummary.toString());
        }
        System.out.println("");

    }
}
