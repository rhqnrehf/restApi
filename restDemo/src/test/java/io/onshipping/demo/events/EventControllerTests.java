package io.onshipping.demo.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
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
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.restdocs.RestDocsAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.config.SnippetConfigurer;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.headers.RequestHeadersSnippet;
import org.springframework.restdocs.hypermedia.HypermediaDocumentation;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.onshipping.demo.common.RestDocsConfiguration;
import io.onshipping.demo.common.TestDescription;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
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
			.andExpect(jsonPath("offline").value(true))
			.andExpect(jsonPath("eventStatus").value(Matchers.not(EventStatus.PUBLISHED)))
			.andExpect(jsonPath("_links.self").exists())
			.andExpect(jsonPath("_links.query-events").exists())
			.andExpect(jsonPath("_links.update-event").exists())
			.andDo(document("create-event",
					HypermediaDocumentation.links(
							HypermediaDocumentation.linkWithRel("self").description("link to self"),
							HypermediaDocumentation.linkWithRel("query-events").description("link to query events"),
							HypermediaDocumentation.linkWithRel("update-event").description("link to update an existing event")
							),
					HeaderDocumentation.requestHeaders(
							HeaderDocumentation.headerWithName(HttpHeaders.ACCEPT).description("accept header"),
							HeaderDocumentation.headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
							),
					PayloadDocumentation.requestFields(
							PayloadDocumentation.fieldWithPath("name").description("Name of new event"),
							PayloadDocumentation.fieldWithPath("description").description("description of new event"),
							PayloadDocumentation.fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
							PayloadDocumentation.fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
							PayloadDocumentation.fieldWithPath("beginEventDateTime").description("date time of begion of new event"),
							PayloadDocumentation.fieldWithPath("endEventDateTime").description("date time of end of new event"),
							PayloadDocumentation.fieldWithPath("location").description("location of new event"),
							PayloadDocumentation.fieldWithPath("basePrice").description("base price of new event"),
							PayloadDocumentation.fieldWithPath("maxPrice").description("max price of new event"),
							PayloadDocumentation.fieldWithPath("limitOfEnrollment").description("limit of new event")
							),
					HeaderDocumentation.responseHeaders(
							HeaderDocumentation.headerWithName(HttpHeaders.LOCATION).description("location type header"),
							HeaderDocumentation.headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
							),
					PayloadDocumentation.responseFields(
							PayloadDocumentation.fieldWithPath("id").description("identifier of new event"),
							PayloadDocumentation.fieldWithPath("name").description("Name of new event"),
							PayloadDocumentation.fieldWithPath("description").description("description of new event"),
							PayloadDocumentation.fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
							PayloadDocumentation.fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
							PayloadDocumentation.fieldWithPath("beginEventDateTime").description("date time of begion of new event"),
							PayloadDocumentation.fieldWithPath("endEventDateTime").description("date time of end of new event"),
							PayloadDocumentation.fieldWithPath("location").description("location of new event"),
							PayloadDocumentation.fieldWithPath("basePrice").description("base price of new event"),
							PayloadDocumentation.fieldWithPath("maxPrice").description("max price of new event"),
							PayloadDocumentation.fieldWithPath("limitOfEnrollment").description("limit of new event"),
							PayloadDocumentation.fieldWithPath("free").description("it tells if this event is free or not"),
							PayloadDocumentation.fieldWithPath("offline").description("it tells if this event is offline event or not"),
							PayloadDocumentation.fieldWithPath("eventStatus").description("event Status"),
							PayloadDocumentation.fieldWithPath("_links.self.href").description("link"),
							PayloadDocumentation.fieldWithPath("_links.query-events.href").description("link"),
							PayloadDocumentation.fieldWithPath("_links.update-event.href").description("link")
							)
					));
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
				.offline(false)
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
		
		assertThat(event.isOffline()).isTrue();
		
		event = Event.builder()
				.build();
		
		event.update();
		
		assertThat(event.isOffline()).isFalse();
	}
}
