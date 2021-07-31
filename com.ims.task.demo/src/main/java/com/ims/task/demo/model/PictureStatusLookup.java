package com.ims.task.demo.model;

import java.io.Serializable;

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
@Table(name = "PICTURE_STATUS_LOOKUP")
@NamedQuery(name = "PictureStatusLookup.findAll", query = "SELECT P FROM PictureStatusLookup P")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PictureStatusLookup implements Serializable {

	/**
	 * 
	 * 
	 * 
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "CODE")
	private String code;

	@Column(name = "NAME")
	private String name;
}