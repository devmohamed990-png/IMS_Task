package com.ims.task.demo.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface PictureService {

	public ResponseEntity<?> getAcceptedPictures();

	public ResponseEntity<?> uploadFile(String email, String description, String category, MultipartFile attachment);

	public ResponseEntity<?> getUploadedPictures(String email);

	public ResponseEntity<?> getImageById(String email, long id);

	public ResponseEntity<?> acceptOrRejectImage(String email, long id, boolean isAccepted);
}