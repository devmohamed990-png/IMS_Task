package com.ims.task.demo.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ims.task.demo.model.Picture;

public interface PictureDAO extends JpaRepository<Picture, Long> {

	@Query("SELECT P FROM Picture P JOIN FETCH P.pictureStatusLookup S WHERE S.code = ?1")
	public List<Picture> findByPictureStatusLookupCode(String code);

	@Transactional
	public void deleteByUserEmail(String email);
	
	public Picture findByPictureCode(String pictureCode);	
}