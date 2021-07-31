package com.ims.task.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ims.task.demo.model.User;

public interface UserDAO extends JpaRepository<User, Long>{

	public User findByEmail(String email);
}