package io.onshipping.demo.events;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class EventValidator {

	public void validate(EventDTO eventDTO,Errors errors) {
		if(eventDTO.getBasePrice()> eventDTO.getMaxPrice() && eventDTO.getMaxPrice() != 0) {
			errors.reject("wrongPrices","Values fo prices are wrong");
		}
		LocalDateTime endEventDateTime = eventDTO.getEndEventDateTime();
		if(endEventDateTime.isBefore(eventDTO.getBeginEventDateTime()) ||
				endEventDateTime.isBefore(eventDTO.getCloseEnrollmentDateTime())||
				endEventDateTime.isBefore(eventDTO.getBeginEnrollmentDateTime())) {
			errors.rejectValue("endEventDateTime", "wrongValue","EndEventDateTime is Wrong");
		}
	}
}
