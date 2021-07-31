package com.ims.task.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PICTURE")
@NamedQuery(name = "Picture.findAll", query = "SELECT P FROM Picture P")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Picture {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "NAME", columnDefinition = "VARCHAR(500)")
	private String name;

	@Column(name = "DESCRIPTION", columnDefinition = "VARCHAR(500)")
	private String description;

	@Column(name = "HEIGHT", nullable = false)
	private Integer height;

	@Column(name = "WIDTH", nullable = false)
	private Integer width;

	@Column(name = "PIC_URL")
	private String pictureURL;

	@Column(name = "PIC_CODE", nullable = false, unique = true, columnDefinition = "VARCHAR(100)")
	private String pictureCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CATEGORY_ID")
	private Category category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STATUS_CODE", referencedColumnName = "CODE")
	private PictureStatusLookup pictureStatusLookup;

	public Picture(String name, String description, Integer height, Integer width, String pictureURL,
			String pictureCode, Category category, User user, PictureStatusLookup pictureStatusLookup) {

		this.name = name;
		this.description = description;
		this.height = height;
		this.width = width;
		this.pictureURL = pictureURL;
		this.pictureCode = pictureCode;
		this.category = category;
		this.user = user;
		this.pictureStatusLookup = pictureStatusLookup;
	}
}