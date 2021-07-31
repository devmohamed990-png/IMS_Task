package com.ims.task.demo.dao;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ims.task.demo.model.Category;

public interface CategoryDAO extends JpaRepository<Category, Long> {

	@Query("SELECT C.name FROM Category C")
	@Cacheable(value = "category", key = "#root.methodName")
	public List<String> findAllCategories();

	@Cacheable(value = "category", key = "{#root.methodName, #name}")
	public Category findByName(String name);
}