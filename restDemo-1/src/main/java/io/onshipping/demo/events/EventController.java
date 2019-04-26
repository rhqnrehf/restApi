package io.onshipping.demo.events;

import java.net.URI;
import java.util.Optional;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.onshipping.demo.common.ErrorsResource;



@Controller
@RequestMapping(value="/api/events",produces= MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {
	
	
	private final EventRepository eventRepository;

	private final EventValidator eventValidator;
	@Autowired
	ModelMapper modelMapper;
	
	public EventController(EventRepository eventRepository,EventValidator eventValidator) {
		this.eventRepository = eventRepository;
		this.eventValidator=eventValidator;
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
		eventRepository.save(event);
		ControllerLinkBuilder selfLinkBuilder = ControllerLinkBuilder.linkTo(EventController.class).slash("{id}");
		URI createUri = selfLinkBuilder.toUri();
		EventResource eventResource = new EventResource(event);
		eventResource.add(ControllerLinkBuilder.linkTo(EventController.class).withRel("query-events"));
		eventResource.add(selfLinkBuilder.withRel("update-event"));
		eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
		return ResponseEntity.created(createUri).body(eventResource);
	}
	
	@GetMapping
	public ResponseEntity queryEvents(Pageable pageable,PagedResourcesAssembler<Event> assembler) throws JsonProcessingException {
		Page<Event> page =eventRepository.findAll(pageable);
		var pagedResources = assembler.toResource(page,e->new EventResource(e));
		pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
		return ResponseEntity.ok(pagedResources);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity getEvent(@PathVariable("id") Integer id) {
		Optional<Event> optional = this.eventRepository.findById(id);
		
		if(optional.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		Event event = optional.get();
		EventResource eventResource = new EventResource(event);
		eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
		return ResponseEntity.ok(eventResource);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity updateEvent(@PathVariable("id") Integer id,@RequestBody @Valid EventDTO eventDTO, Errors errors) {
		Optional<Event> optional=eventRepository.findById(id);
		if(optional.isEmpty())
			return ResponseEntity.notFound().build();
		if(errors.hasErrors())
			return ResponseEntity.badRequest().build();
		eventValidator.validate(eventDTO, errors);
		if(errors.hasErrors())
			return ResponseEntity.badRequest().build();
			
		Event event = optional.get();
		modelMapper.map(eventDTO, event);
		eventRepository.saveAndFlush(event);

		EventResource eventResource = new EventResource(event);
		eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));
		return ResponseEntity.ok(eventResource);
		
	}
	public ResponseEntity badRequest(Errors errors) {
		return ResponseEntity.badRequest().body(new ErrorsResource(errors));
	}
}
