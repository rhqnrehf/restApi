package io.onshipping.demo.events;

import java.net.URI;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping(value="/api/events",produces= MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {
	
	
	EventRepository eventRepository;

	
	
	public EventController(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}
	@PostMapping
	public ResponseEntity createEvent(@RequestBody EventDTO eventDTO) {
		ModelMapper modelMapper = new ModelMapper();
		Event event = modelMapper.map(eventDTO, Event.class);
		event.setId(50);
		Event newEvent = eventRepository.save(event);

		URI createUri = ControllerLinkBuilder.linkTo(EventController.class).slash("{id}").toUri();
		return ResponseEntity.created(createUri).body(event);
	}
}
