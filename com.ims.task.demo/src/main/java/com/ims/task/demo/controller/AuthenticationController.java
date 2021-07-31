package com.ims.task.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ims.task.demo.dto.AuthenticationRequestDTO;
import com.ims.task.demo.dto.AuthenticationResponseDTO;
import com.ims.task.demo.dto.ErrorDTO;
import com.ims.task.demo.service.JwtService;
import com.ims.task.demo.service.MyUserDetailsService;
import com.ims.task.demo.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private MyUserDetailsService userDetailsService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody AuthenticationRequestDTO authenticationRequestDTO) throws Exception {

		try {

			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authenticationRequestDTO.getEmail(), authenticationRequestDTO.getPassword()));

		} catch (BadCredentialsException e) {

			return new ResponseEntity<ErrorDTO>(new ErrorDTO("Email or Password incorrect"), HttpStatus.UNAUTHORIZED);
		}

		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequestDTO.getEmail());

		final String jwt = jwtService.generateToken(userDetails);

		userService.login(authenticationRequestDTO.getEmail());

		return ResponseEntity.ok(new AuthenticationResponseDTO(jwt));
	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {

		return userService.logout(jwtService.extractEmail(token.substring(7, token.length())));
	}
}