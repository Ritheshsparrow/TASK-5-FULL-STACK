package com.iktpreobuka.jobster.controllers;

import java.security.Principal;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Iterables;
import com.iktpreobuka.jobster.controllers.util.RESTError;
import com.iktpreobuka.jobster.entities.UserEntity;
import com.iktpreobuka.jobster.repositories.UserRepository;
import com.iktpreobuka.jobster.security.Views;

@Controller
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(value= "/jobster/users")
public class UserController {

	@Autowired
	private UserRepository userRepository;
		
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	

	@Secured("ROLE_ADMIN")
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.GET, path="/users")
	public ResponseEntity<?> getAll(Principal principal) {
		logger.info("################ /jobster/users/getAll started.");
		logger.info("Logged username: " + principal.getName());
		try {
			Iterable<UserEntity> users = userRepository.findAll();
			logger.info("All users (active, deleted, archived).");
			if (Iterables.isEmpty(users)) {
				logger.info("---------------- Users not found.");
		        return new ResponseEntity<>("Users not found.", HttpStatus.NOT_FOUND);
		      }
			logger.info("---------------- Finished OK.");
			return new ResponseEntity<Iterable<UserEntity>>(users, HttpStatus.OK);
		} catch(Exception e) {
			logger.error("---------------- This is an exception message:" + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(1, "Exception occurred: "+ e.getLocalizedMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}	
	
	@CrossOrigin
	//@Secured("ROLE_ADMIN")
	//@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.GET, path="/usersPaginated")
	public ResponseEntity<?> getAllPaginated(
			@RequestParam Optional<Integer> page,
			@RequestParam Optional<Integer> pageSize,
			@RequestParam Optional<Sort.Direction> direction, 
			@RequestParam Optional<String> sortBy,
			Principal principal) {
		logger.info("################ /jobster/users/getAllPaginated started.");
		logger.info("Logged username: " + principal.getName());
		try {
			Pageable pageable = PageRequest.of(page.orElse(0), pageSize.orElse(5), direction.orElse(Sort.Direction.ASC), sortBy.orElse("countryName"));
			Page<UserEntity> usersPage= userRepository.findAll(pageable);
			Iterable<UserEntity> users = usersPage.getContent();
			
			logger.info("All users (active, deleted, archived).");
			if (users == null) {
				logger.info("---------------- Users not found.");
		        return new ResponseEntity<>("Users not found.", HttpStatus.NOT_FOUND);
		      }
			logger.info("---------------- Finished OK.");
			return new ResponseEntity<Iterable<UserEntity>>(users, HttpStatus.OK);
		} catch(Exception e) {
			logger.error("---------------- This is an exception message:" + e.getMessage());
			return new ResponseEntity<RESTError>(new RESTError(1, "Exception occurred: "+ e.getLocalizedMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}	


}
