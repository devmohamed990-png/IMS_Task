package com.ims.task.demo.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PictureDTO implements Serializable {

	/**
	 *
	 * 
	 * 
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("code")
	private Long id;
	
	@JsonProperty("name")
	private String name;

	@JsonProperty("url")
	private String pictureURL;

	@JsonProperty("description")
	private String description;

	@JsonProperty("category")
	private String category;

	@JsonProperty("width")
	private Integer width;

	@JsonProperty("height")
	private Integer height;
}