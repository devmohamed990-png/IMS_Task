package com.ims.task.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ims.task.demo.dto.ErrorDTO;
import com.ims.task.demo.dto.RegisterDTO;
import com.ims.task.demo.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> register(@RequestBody RegisterDTO registerDTO) {

		if (registerDTO.getEmail() == null || registerDTO.getEmail().equals("")
				|| !registerDTO.getEmail().contains("@"))
			return new ResponseEntity<ErrorDTO>(new ErrorDTO("Please, enter valid email like (xxx@domain.com)"),
					HttpStatus.BAD_REQUEST);

		if (registerDTO.getAddress() == null || registerDTO.getAddress().equals(""))
			return new ResponseEntity<ErrorDTO>(new ErrorDTO("Please, enter valid address"), HttpStatus.BAD_REQUEST);

		if (registerDTO.getPassword() == null || registerDTO.getPassword().equals(""))
			return new ResponseEntity<ErrorDTO>(new ErrorDTO("Please, enter valid password"), HttpStatus.BAD_REQUEST);

		return userService.register(registerDTO);
	}
}