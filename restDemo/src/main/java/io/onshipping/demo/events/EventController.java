package io.onshipping.demo.events;

import java.net.URI;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sun.source.doctree.LinkTree;



@Controller
@RequestMapping(value="/api/events",produces= MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {
	
	
	private final EventRepository eventRepository;

	private final EventValidator eventValidator;
	
	public EventController(EventRepository eventRepository,EventValidator eventValidator) {
		this.eventRepository = eventRepository;
		this.eventValidator=eventValidator;
	}
	
	@PostMapping
	public ResponseEntity createEvent(@RequestBody @Valid EventDTO eventDTO,Errors errors) {
	
		if(errors.hasErrors())
			return ResponseEntity.badRequest().build();
		eventValidator.validate(eventDTO, errors);
		if(errors.hasErrors()) {
			return ResponseEntity.badRequest().body(errors);
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
		return ResponseEntity.created(createUri).body(eventResource);
	}
}
