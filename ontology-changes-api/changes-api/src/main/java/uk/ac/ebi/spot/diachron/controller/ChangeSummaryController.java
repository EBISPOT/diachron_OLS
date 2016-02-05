package uk.ac.ebi.spot.diachron.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.*;
import org.springframework.hateoas.core.EmbeddedWrapper;
import org.springframework.hateoas.core.EmbeddedWrappers;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import uk.ac.ebi.spot.diachron.model.ChangeSummary;
import uk.ac.ebi.spot.diachron.model.DateSummary;
import uk.ac.ebi.spot.diachron.service.ChangeSummaryService;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Simon Jupp
 * @date 05/02/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Controller
@RequestMapping("/changesummaries")
@ExposesResourceFor(ChangeSummary.class)
public class ChangeSummaryController implements
        ResourceProcessor<RepositoryLinksResource> {


    @Autowired
    ChangeSummaryService changeSummaryService;

    @Autowired
    EntityLinks entityLinks;



    @RequestMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<Resources<ChangeSummary>> root
            (
    ) throws ResourceNotFoundException {


        Resources<ChangeSummary> resources = new Resources<ChangeSummary>(new ArrayList<>());

        resources.add(ControllerLinkBuilder.linkTo(ChangeSummarySearchController.class, "size", "ontologyName").slash("dates").withRel("dates")  );
        resources.add(ControllerLinkBuilder.linkTo(ChangeSummarySearchController.class).slash("findByOntologyNameAndChangeDateAfter").withRel("findByOntologyNameAndChangeDateAfter")  );
        resources.add(ControllerLinkBuilder.linkTo(ChangeSummarySearchController.class).slash("findByOntologyNameAndChangeDateBefore").withRel("findByOntologyNameAndChangeDateBefore")  );
        resources.add(ControllerLinkBuilder.linkTo(ChangeSummarySearchController.class).slash("findByOntologyNameAndChangeDateBetween").withRel("findByOntologyNameAndChangeDateBetween") );
        resources.add(ControllerLinkBuilder.linkTo(ChangeSummarySearchController.class).slash("findByOntologyNameAndChangeNameAndChangeDateBetween").withRel("findByOntologyNameAndChangeNameAndChangeDateBetween") );
        return new ResponseEntity<>( resources , HttpStatus.OK);

    }

    @Override
    public RepositoryLinksResource process(RepositoryLinksResource resource) {
        resource.add(ControllerLinkBuilder.linkTo(ChangeSummaryController.class).withRel("changesummaries"));
               return resource;
    }
}
