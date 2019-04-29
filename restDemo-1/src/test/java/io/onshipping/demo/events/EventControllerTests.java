package io.onshipping.demo.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
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
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.onshipping.demo.accounts.Account;
import io.onshipping.demo.accounts.AccountRole;
import io.onshipping.demo.accounts.AccountService;
import io.onshipping.demo.common.AppProperties;
import io.onshipping.demo.common.BaseControllerTest;
import io.onshipping.demo.common.RestDocsConfiguration;
import io.onshipping.demo.common.TestDescription;

public class EventControllerTests extends BaseControllerTest{
	
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private AccountService accountService;
	
	@Autowired
	AppProperties appProperties;

	//@Test
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
				.header(HttpHeaders.AUTHORIZATION,"Bearer "/*+getAccessToken()*/)
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
			.andDo(document("create-event",
					HypermediaDocumentation.links(
							HypermediaDocumentation.linkWithRel("self").description("link to self"),
							HypermediaDocumentation.linkWithRel("query-events").description("link to query events"),
							HypermediaDocumentation.linkWithRel("update-event").description("link to update an existing event"),
							HypermediaDocumentation.linkWithRel("profile").description("link to profile an existing event")
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
							PayloadDocumentation.fieldWithPath("manager").description("identifier of new event"),
							PayloadDocumentation.fieldWithPath("maxPrice").description("max price of new event"),
							PayloadDocumentation.fieldWithPath("limitOfEnrollment").description("limit of new event"),
							PayloadDocumentation.fieldWithPath("free").description("it tells if this event is free or not"),
							PayloadDocumentation.fieldWithPath("offline").description("it tells if this event is offline event or not"),
							PayloadDocumentation.fieldWithPath("eventStatus").description("event Status"),
							PayloadDocumentation.fieldWithPath("_links.self.href").description("link"),
							PayloadDocumentation.fieldWithPath("_links.query-events.href").description("link"),
							PayloadDocumentation.fieldWithPath("_links.update-event.href").description("link"),
							PayloadDocumentation.fieldWithPath("_links.profile.href").description("link")
							)
					));
	}
	
	
	
	@Test
	public void getAccessToken() throws Exception {
		String username = "keesun@email.commm";
		String password = "keesun123";


		
		ResultActions perform = mockMvc.perform(post("/oauth/token")
				.with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
				.param("username", username)
				.param("password", password)
                .param("grant_type", "password"));
		var responseBody = perform.andReturn().getResponse().getContentAsString();
		Jackson2JsonParser parse = new Jackson2JsonParser();
		String jsonString  = parse.parseMap(responseBody).get("access_token").toString();
		System.out.println(jsonString);
		//return parse.parseMap(responseBody).get("access_token").toString();
		
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
		.andExpect(jsonPath("content[0].objectName").exists())
		.andExpect(jsonPath("content[0].field").exists())
		.andExpect(jsonPath("content[0].defaultMessage").exists())
		.andExpect(jsonPath("content[0].code").exists())
		.andExpect(jsonPath("content[0].rejectedValue").exists())
		.andExpect(jsonPath("_links.index").exists());
	}
	
	//@Test
	@TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
	public void queryEvents()throws Exception {
		IntStream.range(0, 30).forEach(this::generateEvent);
		
		mockMvc.perform(get("/api/events")
				.param("page", "1")
				.param("size", "10")
				.param("sort", "name,DESC")
				.accept(MediaTypes.HAL_JSON_UTF8))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("page").exists())
			.andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
			.andDo(document("query-events"));
	}
	
	//@Test
	@TestDescription("기존에 이벤트 하나 조회하기")
	public void getEvent() throws Exception{
		Event event=this.generateEvent(100);
		mockMvc.perform(get("/api/events/{id}",event.getId()))
			.andDo(print())
			.andExpect(jsonPath("name").exists())
			.andExpect(jsonPath("id").exists())
			.andExpect(jsonPath("_links.self").exists())
			.andExpect(jsonPath("_links.profile").exists())
			.andDo(document("get-an-event",
					HypermediaDocumentation.links(
							HypermediaDocumentation.linkWithRel("self").description("link_toself"),
							HypermediaDocumentation.linkWithRel("profile").description("link_toself")
							)
						));
	}
	
	//@Test
	@TestDescription("없는 이벤트 조회했을때 404 응답")
	public void getEvent404() throws Exception{
		mockMvc.perform(get("/api/events/111111"))
		.andExpect(status().is(404));
	}

	
	//@Test
	@TestDescription("이벤트를 수정하기")
	public void updateEvent1() throws Exception {
		Event event = this.generateEvent(200);
		EventDTO eventDTO = this.modelMapper.map(event, EventDTO.class);
		
		String eventName = "Update Event";
		eventDTO .setName(eventName);
		
		mockMvc.perform(put("/api/events/{id}",event.getId())
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(eventDTO)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("name").value(eventName))
			.andExpect(jsonPath("_links.self").exists())
			.andDo(document("update-event"));
	}
	
	//@Test
	@TestDescription("입력값이 비어있는 경우에 이벤트 수정 실패")
	public void updateEvent400_Empty() throws Exception {
		Event event = this.generateEvent(200);
		EventDTO eventDTO = new EventDTO();

		
		mockMvc.perform(put("/api/events/{id}",event.getId())
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(eventDTO)))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
	
	//@Test
	@TestDescription("입력값이 잘못되어있는 경우에 이벤트 수정 실패")
	public void updateEvent400_Wrong() throws Exception {
		Event event = this.generateEvent(200);
		EventDTO eventDTO = modelMapper.map(event,EventDTO.class);
		eventDTO.setBasePrice(1000);
		eventDTO.setMaxPrice(100);
		
		mockMvc.perform(put("/api/events/{id}",event.getId())
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(eventDTO)))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
	
	//@Test
	@TestDescription("존재하지 않는 이벤트 수정실패")
	public void updateEvent404() throws Exception {
		Event event = this.generateEvent(200);
		EventDTO eventDTO = modelMapper.map(event,EventDTO.class);
		mockMvc.perform(put("/api/events/3123123")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(eventDTO)))
			.andDo(print())
			.andExpect(status().isNotFound());
	}
	
	private Event generateEvent(int i) {
		Event event = Event.builder()
				.name("event "+i)
				.description("testEvent")
				.beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
				.closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
				.beginEventDateTime(LocalDateTime.of(2018,11,25,14,21))
				.endEventDateTime(LocalDateTime.of(2018,11,26,14,21))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남역 D2 스타텁 팩토리")
				.free(false)
				.offline(true)
				.eventStatus(EventStatus.DRAFT)
				.build();
		
		eventRepository.save(event);
		return event;
	}
	
}
