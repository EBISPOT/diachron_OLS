package uk.ac.ebi.spot.diachron.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.ebi.spot.diachron.model.ChangeSummary;
import uk.ac.ebi.spot.diachron.model.DateSummary;
import uk.ac.ebi.spot.diachron.service.ChangeSummaryService;

import java.util.Date;
import java.util.List;

/**
 * @author Simon Jupp
 * @date 05/02/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Controller
@RequestMapping("/changesummaries/search")
@ExposesResourceFor(ChangeSummary.class)
public class ChangeSummarySearchController implements
        ResourceProcessor<RepositoryLinksResource> {


    @Autowired
    ChangeSummaryService changeSummaryService;

    @RequestMapping(path = "/dates", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<List<DateSummary>> findByOntologyName
            (
                    @RequestParam("size") long size,
                    @RequestParam("ontologyName") String ontologyName
    ) throws ResourceNotFoundException {

        List<DateSummary> dates = changeSummaryService.getChangeDates(ontologyName, size);
        return new ResponseEntity<>( dates , HttpStatus.OK);

    }

    @RequestMapping(path = "/findByOntologyNameAndChangeDateAfter", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<List<ChangeSummary>> findByOntologyNameAndChangeDateAfter(
            @RequestParam("ontologyName") String ontologyName,
            @RequestParam("after")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date after
    ) throws ResourceNotFoundException {

        List<ChangeSummary> summaries = changeSummaryService.findByOntologyNameAndChangeDateAfter(ontologyName, after);
        return new ResponseEntity<>( summaries , HttpStatus.OK);

    }

    @RequestMapping(path = "/findByOntologyNameAndChangeDateBefore", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<List<ChangeSummary>> findByOntologyNameAndChangeDateBefore(
            @RequestParam("ontologyName") String ontologyName,
            @RequestParam("before")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date before
    ) throws ResourceNotFoundException {

        List<ChangeSummary> summaries = changeSummaryService.findByOntologyNameAndChangeDateBefore(ontologyName, before);
        return new ResponseEntity<>( summaries , HttpStatus.OK);

    }

    @RequestMapping(path = "/findByOntologyNameAndChangeDateBetween", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<List<ChangeSummary>> findByOntologyNameAndChangeDateBetween(
            @RequestParam("ontologyName") String ontologyName,
            @RequestParam("after")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date after,
            @RequestParam("before")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date before
    ) throws ResourceNotFoundException {
        List<ChangeSummary> summaries = changeSummaryService.findByOntologyNameAndChangeDateBetween(ontologyName, after, before);
        return new ResponseEntity<>( summaries , HttpStatus.OK);

    }

    @RequestMapping(path = "/findByOntologyNameAndChangeNameAndChangeDateBetween", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<List<ChangeSummary>> findByOntologyNameAndChangeNameAndChangeDateBetween(
            @RequestParam("ontologyName") String ontologyName,
            @RequestParam("changeName") String changeName,
            @RequestParam("after")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date after,
            @RequestParam("before")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date before
    ) throws ResourceNotFoundException {
        List<ChangeSummary> summaries = changeSummaryService.findByOntologyNameAndChangeNameAndChangeDateBetween(ontologyName,changeName, after, before);
        return new ResponseEntity<>( summaries , HttpStatus.OK);

    }

    @Override
    public RepositoryLinksResource process(RepositoryLinksResource resource) {
//        resource.add(ControllerLinkBuilder.methodOn(ChangeSummarySearchController.class).);
               return resource;
    }

}
