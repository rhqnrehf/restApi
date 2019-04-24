package io.onshipping.demo.common;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.validation.Errors;

import io.onshipping.demo.index.IndexController;


public class ErrorsResource extends Resource<Errors>{

	public ErrorsResource(Errors content,Link...links) {
		super(content,links);
		add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(IndexController.class).index()).withRel("index"));

	}

	
}
