package io.onshipping.demo.events;

import java.net.URI;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sun.source.doctree.LinkTree;

import io.onshipping.demo.common.ErrorsResource;



@Controller
@RequestMapping(value="/api/events",produces= MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {
	
	
	private final EventRepository eventRepository;

	private final EventValidator eventValidator;
	
	public EventController(EventRepository eventRepository,EventValidator eventValidator) {
		this.eventRepository = eventRepository;
		this.eventValidator=eventValidator;
	}
	public ResponseEntity badRequest(Errors errors) {
		return ResponseEntity.badRequest().body(new ErrorsResource(errors));
	}
	
	@PostMapping
	public ResponseEntity createEvent(@RequestBody @Valid EventDTO eventDTO,Errors errors) {

		if(errors.hasErrors())
			return badRequest(errors);
		
		eventValidator.validate(eventDTO, errors);
		if(errors.hasErrors()) {
			return badRequest(errors);
		}
		ModelMapper modelMapper = new ModelMapper();
		Event event = modelMapper.map(eventDTO, Event.class);
		event.update();
		//event.setId(50);
		eventRepository.save(event);
		ControllerLinkBuilder selfLinkBuilder = ControllerLinkBuilder.linkTo(EventController.class).slash("{id}");
		URI createUri = selfLinkBuilder.toUri();
		EventResource eventResource = new EventResource(event);
		eventResource.add(ControllerLinkBuilder.linkTo(EventController.class).withRel("query-events"));
		eventResource.add(selfLinkBuilder.withRel("update-event"));
		eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
		return ResponseEntity.created(createUri).body(eventResource);
	}
}
