package uk.ac.ebi.spot.diachron.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.ebi.spot.diachron.model.Change;
import uk.ac.ebi.spot.diachron.model.ChangeSummary;
import uk.ac.ebi.spot.diachron.respository.ChangeRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Simon Jupp
 * @date 28/04/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Controller
@RequestMapping("/changes/search")
@ExposesResourceFor(Change.class)
public class ChangeSearchController {

    @Autowired
    ChangeRepository repository;

    @RequestMapping(path = "/findByOntologyNameAndChangeDate", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<PagedResources<Change>> findByOntologyNameAndChangeDate(
            @RequestParam("ontologyName") String ontologyName,
            @RequestParam("date")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date,
            Pageable pageable,
            PagedResourcesAssembler assembler
    ) throws ResourceNotFoundException {

        Date after = getStartOfDayDate(date);
        Date before = getEndOfDayDate(date);
        Page<Change> summaries = repository.findByOntologyNameAndChangeDateBetween(ontologyName, after, before, pageable);
        return new ResponseEntity<>(assembler.toResource(summaries), HttpStatus.OK);

    }

    @RequestMapping(path = "/findByOntologyNameAndChangeNameAndChangeDate", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<PagedResources<Change>> findByOntologyNameAndChangeDate(
            @RequestParam("ontologyName") String ontologyName,
            @RequestParam("changeName") String changeName,
            @RequestParam("date")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date,
            Pageable pageable,
            PagedResourcesAssembler assembler
    ) throws ResourceNotFoundException {

        Date after = getStartOfDayDate(date);
        Date before = getEndOfDayDate(date);
        Page<Change> summaries = repository.findByOntologyNameAndChangeNameAndChangeDateBetween(ontologyName, changeName, after, before, pageable);
        return new ResponseEntity<>(assembler.toResource(summaries), HttpStatus.OK);

    }

    public Date getStartOfDayDate (Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return getEndOfDayDate(calendar.getTime());
    }

    public Date getEndOfDayDate (Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        return calendar.getTime();
    }
}
