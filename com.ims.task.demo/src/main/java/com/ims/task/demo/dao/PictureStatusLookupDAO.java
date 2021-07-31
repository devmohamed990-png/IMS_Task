package com.ims.task.demo.dao;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ims.task.demo.model.PictureStatusLookup;

public interface PictureStatusLookupDAO extends JpaRepository<PictureStatusLookup, Long> {

	@Cacheable(value = "PictureStatusLookup", key = "{#root.methodName, #name}")
	public PictureStatusLookup findByCode(String code);
}