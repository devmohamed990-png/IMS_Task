package com.ims.task.demo.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ims.task.demo.dao.UserDAO;

@Service
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private UserDAO userDAO;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		try {

			com.ims.task.demo.model.User user = userDAO.findByEmail(email);

			return new User(user.getEmail(), user.getPassword(), new ArrayList<>());

		} catch (Exception e) {

			throw new UsernameNotFoundException("User not found");
		}
	}
}