package com.ims.task.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ims.task.demo.dto.ErrorDTO;
import com.ims.task.demo.service.JwtService;
import com.ims.task.demo.service.PictureService;

@RestController
@RequestMapping("/pic")
public class PictureController {

	@Autowired
	private PictureService pictureService;

	@Autowired
	private JwtService jwtService;

	@GetMapping(value = "/accepted-pic", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAcceptedPictures() {

		return pictureService.getAcceptedPictures();
	}

	@PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> uploadFile(@RequestHeader("Authorization") String token,
			@RequestParam("description") String description, @RequestParam("category") String category,
			@RequestParam("attachment") MultipartFile attachment) {

		if (description == null || description.equals(""))
			return new ResponseEntity<ErrorDTO>(new ErrorDTO("Please, enter valid description."),
					HttpStatus.BAD_REQUEST);

		if (category == null || category.equals(""))
			return new ResponseEntity<ErrorDTO>(new ErrorDTO("Please, enter valid category."), HttpStatus.BAD_REQUEST);

		if (attachment == null)
			return new ResponseEntity<ErrorDTO>(new ErrorDTO("Please, enter valid attachment."),
					HttpStatus.BAD_REQUEST);

		return pictureService.uploadFile(jwtService.extractEmail(token.substring(7, token.length())), description,
				category, attachment);
	}

	@GetMapping(value = "/uploaded-pic", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getUploadedPictures(@RequestHeader("Authorization") String token) {

		return pictureService.getUploadedPictures(jwtService.extractEmail(token.substring(7, token.length())));
	}

	@GetMapping(value = "/image", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getImageById(@RequestHeader("Authorization") String token, @RequestParam("code") long id) {

		if (id <= 0)
			return new ResponseEntity<ErrorDTO>(new ErrorDTO("Invalid Image Code."), HttpStatus.BAD_REQUEST);

		return pictureService.getImageById(jwtService.extractEmail(token.substring(7, token.length())), id);
	}

	@PutMapping(value = "/upadte-image", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> acceptOrRejectImage(@RequestHeader("Authorization") String token,
			@RequestParam("code") long id, @RequestParam("is_accepted") boolean isAccepted) {

		if (id <= 0)
			return new ResponseEntity<ErrorDTO>(new ErrorDTO("Invalid Image Code."), HttpStatus.BAD_REQUEST);

		return pictureService.acceptOrRejectImage(jwtService.extractEmail(token.substring(7, token.length())), id,
				isAccepted);
	}
}