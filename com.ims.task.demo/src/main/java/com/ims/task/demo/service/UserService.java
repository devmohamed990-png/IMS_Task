package com.ims.task.demo.service;

import org.springframework.http.ResponseEntity;

import com.ims.task.demo.dto.RegisterDTO;

public interface UserService {

	public ResponseEntity<?> register(RegisterDTO registerDTO);

	public void login(String email);
	
	public ResponseEntity<?> logout(String email);
}