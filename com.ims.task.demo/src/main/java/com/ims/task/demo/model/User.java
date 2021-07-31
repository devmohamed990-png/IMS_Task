package com.ims.task.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USERS")
@NamedQuery(name = "User.findAll", query = "SELECT U FROM User U")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "EMAIL", unique = true, nullable = false)
	private String email;

	@Column(name = "ADDRESS")
	private String address;

	@Column(name = "PASSWORD", nullable = false)
	private String password;

	@Column(name = "IS_ADMIN", nullable = false)
	private Boolean isAdmin = false;

	@Column(name = "IS_LOGIN", nullable = false)
	private Boolean isLogin = true;

	public User(String email, String address, String password, Boolean isAdmin, Boolean isLogin) {

		this.email = email;
		this.address = address;
		this.password = password;
		this.isAdmin = isAdmin;
		this.isLogin = isLogin;
	}
}