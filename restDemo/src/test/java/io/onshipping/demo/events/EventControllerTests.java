package io.onshipping.demo.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.time.LocalDateTime;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.onshipping.demo.common.TestDescription;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;

	
	
	@Test
	public void createEvent() throws Exception{
		EventDTO event = EventDTO.builder()
				.name("Spring")
				.description("REST API Development with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
				.closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
				.beginEventDateTime(LocalDateTime.of(2018,11,25,14,21))
				.endEventDateTime(LocalDateTime.of(2018,11,26,14,21))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남역 D2 스타텁 팩토리")
				.build();

		mockMvc.perform(post("/api/events/")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaTypes.HAL_JSON)
				.content(objectMapper.writeValueAsString(event))
				)
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("id").exists())
			.andExpect(header().exists(HttpHeaders.LOCATION))
			.andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
			.andExpect(jsonPath("id").value(Matchers.not(100)))
			.andExpect(jsonPath("free").value(false))
			.andExpect(jsonPath("offLine").value(true))
			.andExpect(jsonPath("eventStatus").value(Matchers.not(EventStatus.PUBLISHED)));
			
	}
	
	
	
	//@Test
	public void badRequest() throws JsonProcessingException, Exception {
		Event event = Event.builder()
				.id(100)
				.name("Spring")
				.description("REST API Development with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
				.closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
				.beginEventDateTime(LocalDateTime.of(2018,11,25,14,21))
				.endEventDateTime(LocalDateTime.of(2018,11,26,14,21))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남역 D2 스타텁 팩토리")
				.free(true)
				.offLine(false)
				.eventStatus(EventStatus.PUBLISHED)
				.build();
		//Mockito.when(eventRepository.save(event)).thenReturn(event);
		mockMvc.perform(post("/api/events")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaTypes.HAL_JSON)
				.content(objectMapper.writeValueAsString(event)))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}
	
	//@Test
	public void createEvent_Bad_Request_Empty_Input() throws Exception {
		EventDTO eventDTO = EventDTO.builder()
				.name("Spring")
				//.description("REST API Development with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
				.closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
				.beginEventDateTime(LocalDateTime.of(2018,11,25,14,21))
				.endEventDateTime(LocalDateTime.of(2018,11,26,14,21))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남역 D2 스타텁 팩토리")
				.build();
		
		mockMvc.perform(post("/api/events")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaTypes.HAL_JSON_UTF8)
				.content(objectMapper.writeValueAsString(eventDTO)))
		.andDo(print())
		.andExpect(status().isBadRequest());
	}
	
	//@Test
	@TestDescription("정상적으로 이벤트를 생성하는 이벤트")
	public void createEvent_Bad_Request_Empty_Wrong_Input() throws Exception {
		EventDTO eventDTO = EventDTO.builder()
				.name("Spring")
				.description("REST API Development with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
				.closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
				.beginEventDateTime(LocalDateTime.of(2018,11,25,14,21))
				.endEventDateTime(LocalDateTime.of(2018,11,23,14,21))
				.basePrice(10000)
				.maxPrice(1000)
				.limitOfEnrollment(100)
				.location("강남역 D2 스타텁 팩토리")
				.build();
		
		mockMvc.perform(post("/api/events")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaTypes.HAL_JSON_UTF8)
				.content(objectMapper.writeValueAsString(eventDTO)))
		.andDo(print())
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$[0].objectName").exists())
		.andExpect(jsonPath("$[0].field").exists())
		.andExpect(jsonPath("$[0].defaultMessage").exists())
		.andExpect(jsonPath("$[0].code").exists())
		.andExpect(jsonPath("$[0].rejectedValue").exists());
	}
	
	//@Test
	public void testFree() {
		Event event = Event.builder()
				.basePrice(0)
				.maxPrice(0)
				.build();
		event.update();
		
		assertThat(event.isFree()).isTrue();
		
		event = Event.builder()
				.basePrice(100)
				.maxPrice(0)
				.build();
		event.update();
		
		assertThat(event.isFree()).isFalse();
		
		event = Event.builder()
				.basePrice(0)
				.maxPrice(100)
				.build();
		event.update();
		
		assertThat(event.isFree()).isFalse();
	}
	
	//@Test
	public void testOffline() {
		Event event = Event.builder()
				.location("강남역 네이버 D2 스타텁 팩토리")
				.build();
		
		event.update();
		
		assertThat(event.isOffLine()).isTrue();
		
		event = Event.builder()
				.build();
		
		event.update();
		
		assertThat(event.isOffLine()).isFalse();
	}
}
