package com.ims.task.demo.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ims.task.demo.dao.CategoryDAO;
import com.ims.task.demo.dao.PictureDAO;
import com.ims.task.demo.dao.PictureStatusLookupDAO;
import com.ims.task.demo.dao.UserDAO;
import com.ims.task.demo.dto.ErrorDTO;
import com.ims.task.demo.dto.PictureDTO;
import com.ims.task.demo.dto.SuccessDTO;
import com.ims.task.demo.enums.PictureStatusLookupEnum;
import com.ims.task.demo.model.Picture;
import com.ims.task.demo.model.User;
import com.ims.task.demo.utils.FileUtils;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class PictureServiceImpl implements PictureService {

	@Autowired
	private PictureDAO pictureDAO;

	@Autowired
	private PictureStatusLookupDAO pictureStatusLookupDAO;

	@Autowired
	private CategoryDAO categoryDAO;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private ModelMapper modelMapper;

	private static final int VALID_FILE_SIZE_MB = 2;
	private static final String STORAGE_PATH = "storage/";

	@Override
	public ResponseEntity<?> getAcceptedPictures() {

		try {

			List<Picture> pictures = pictureDAO
					.findByPictureStatusLookupCode(PictureStatusLookupEnum.ACCEPTED.toString());

			pictures.forEach(element -> {
				element.setId(null);
				element.setDescription(null);
				element.setCategory(null);
				element.setWidth(null);
				element.setHeight(null);
			});

			log.info("pictures Size >>>>>>>>>>>>>>> {}", pictures.size());

			Type listType = new TypeToken<List<PictureDTO>>() {
			}.getType();
			List<PictureDTO> pictureDTOs = modelMapper.map(pictures, listType);

			return new ResponseEntity<List<PictureDTO>>(pictureDTOs, HttpStatus.OK);

		} catch (Exception ex) {

			log.error("Exception Message >>>>>>>>>>>>>>> {}", ex.getMessage());

			ex.printStackTrace();

			return new ResponseEntity<ErrorDTO>(new ErrorDTO("something went wrong."),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> uploadFile(String email, String description, String category, MultipartFile attachment) {

		String folderPath = null;

		try {

			User user = userDAO.findByEmail(email);

			if (user == null || !user.getIsLogin())
				return new ResponseEntity<ErrorDTO>(new ErrorDTO("Please, login first and upload file."),
						HttpStatus.UNAUTHORIZED);

			List<String> categorynames = categoryDAO.findAllCategories();

			if (!categorynames.contains((Object) category))
				return new ResponseEntity<ErrorDTO>(new ErrorDTO("Please, enter valid category."),
						HttpStatus.BAD_REQUEST);

			if (!FileUtils.validateFileType(
					FileUtils.getFileContentType(attachment.getOriginalFilename(), attachment.getInputStream())))
				return new ResponseEntity<ErrorDTO>(new ErrorDTO("Please, enter valid file (jpg, png, gif)."),
						HttpStatus.BAD_REQUEST);

			if (!FileUtils.checkFileSize(attachment.getSize(), VALID_FILE_SIZE_MB))
				return new ResponseEntity<ErrorDTO>(
						new ErrorDTO("File size should be less than " + VALID_FILE_SIZE_MB + " mb."),
						HttpStatus.BAD_REQUEST);

			String imageCode = UUID.randomUUID().toString();

			if (pictureDAO.findByPictureCode(imageCode) != null)
				return this.uploadFile(email, description, category, attachment);

			folderPath = STORAGE_PATH + imageCode + "/";
			File file = FileUtils.convertMultiPartToFile(attachment, folderPath);
			BufferedImage bufferedImage = ImageIO.read(file);

			pictureDAO.save(new Picture(attachment.getOriginalFilename(), description, bufferedImage.getHeight(),
					bufferedImage.getWidth(), null, imageCode, categoryDAO.findByName(category), user,
					pictureStatusLookupDAO.findByCode(PictureStatusLookupEnum.UPLADED.toString())));

			return new ResponseEntity<SuccessDTO>(new SuccessDTO("Uploading Completed."), HttpStatus.OK);

		} catch (Exception ex) {

			if (folderPath != null)
				FileUtils.deleteFolder(new File(folderPath));

			log.error("Exception Message >>>>>>>>>>>>>>> {}", ex.getMessage());

			ex.printStackTrace();

			return new ResponseEntity<ErrorDTO>(new ErrorDTO("something went wrong."),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> getUploadedPictures(String email) {

		try {

			User user = userDAO.findByEmail(email);

			if (user != null) {

				if (user.getIsAdmin()) {

					if (user.getIsLogin()) {

						List<Picture> pictures = pictureDAO
								.findByPictureStatusLookupCode(PictureStatusLookupEnum.UPLADED.toString());

						pictures.forEach(element -> {
							element.setDescription(null);
							element.setCategory(null);
							element.setWidth(null);
							element.setHeight(null);
							element.setPictureURL(null);
						});

						Type listType = new TypeToken<List<PictureDTO>>() {
						}.getType();
						List<PictureDTO> pictureDTOs = modelMapper.map(pictures, listType);

						return new ResponseEntity<List<PictureDTO>>(pictureDTOs, HttpStatus.OK);

					} else {

						return new ResponseEntity<ErrorDTO>(new ErrorDTO("Please, login first."),
								HttpStatus.UNAUTHORIZED);
					}

				} else {

					return new ResponseEntity<ErrorDTO>(new ErrorDTO("you aren't an admin."), HttpStatus.UNAUTHORIZED);
				}

			} else {

				return new ResponseEntity<ErrorDTO>(new ErrorDTO("Invalid token."), HttpStatus.UNAUTHORIZED);
			}

		} catch (Exception ex) {

			log.error("Exception Message >>>>>>>>>>>>>>> {}", ex.getMessage());

			ex.printStackTrace();

			return new ResponseEntity<ErrorDTO>(new ErrorDTO("something went wrong."),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> getImageById(String email, long id) {

		try {

			ResponseEntity<?> adminResponse = checkUserAdministration(email);

			if (adminResponse.getStatusCode() == HttpStatus.OK) {

				Optional<Picture> pictureOptional = pictureDAO.findById(id);

				if (!pictureOptional.isEmpty()) {

					Picture picture = pictureOptional.get();

					if (!picture.getPictureStatusLookup().getCode()
							.equals(PictureStatusLookupEnum.REJECTED.toString())) {

						PictureDTO pictureDTO = new PictureDTO(picture.getId(), picture.getName(),
								picture.getPictureURL(), picture.getDescription(), picture.getCategory().getName(),
								picture.getWidth(), picture.getHeight());

						return new ResponseEntity<PictureDTO>(pictureDTO, HttpStatus.OK);

					} else {

						return new ResponseEntity<ErrorDTO>(new ErrorDTO("This image is deleted."),
								HttpStatus.NOT_FOUND);
					}

				} else {

					return new ResponseEntity<ErrorDTO>(new ErrorDTO("Invalid Image Code."), HttpStatus.NOT_FOUND);
				}

			} else {

				return adminResponse;
			}

		} catch (Exception ex) {

			log.error("Exception Message >>>>>>>>>>>>>>> {}", ex.getMessage());

			ex.printStackTrace();

			return new ResponseEntity<ErrorDTO>(new ErrorDTO("something went wrong."),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> acceptOrRejectImage(String email, long id, boolean isAccepted) {

		try {

			ResponseEntity<?> adminResponse = checkUserAdministration(email);

			if (adminResponse.getStatusCode() == HttpStatus.OK) {

				Optional<Picture> pictureOptional = pictureDAO.findById(id);

				if (!pictureOptional.isEmpty()) {

					Picture picture = pictureOptional.get();

					if (!picture.getPictureStatusLookup().getCode()
							.equals(PictureStatusLookupEnum.REJECTED.toString())) {

						boolean deletionStatus = true;

						String folderPath = STORAGE_PATH + picture.getPictureCode() + "/";
						String imagePath = folderPath + picture.getName();

						if (isAccepted) {

							File imageFile = new File(imagePath);

							picture.setPictureURL(imageFile.getAbsolutePath());
							picture.setPictureStatusLookup(
									pictureStatusLookupDAO.findByCode(PictureStatusLookupEnum.ACCEPTED.toString()));
						} else {

							picture.setPictureStatusLookup(
									pictureStatusLookupDAO.findByCode(PictureStatusLookupEnum.REJECTED.toString()));

							deletionStatus = FileUtils.deleteFolder(new File(folderPath));
						}

						pictureDAO.save(picture);

						if (deletionStatus) {
							return new ResponseEntity<SuccessDTO>(new SuccessDTO("Processing Successfully."),
									HttpStatus.OK);
						} else {
							return new ResponseEntity<SuccessDTO>(new SuccessDTO("Can't delete this file."),
									HttpStatus.BAD_REQUEST);
						}

					} else {

						return new ResponseEntity<ErrorDTO>(new ErrorDTO("This image is deleted."),
								HttpStatus.NOT_FOUND);
					}

				} else {

					return new ResponseEntity<ErrorDTO>(new ErrorDTO("Invalid Image Code."), HttpStatus.NOT_FOUND);
				}

			} else {

				return adminResponse;
			}

		} catch (Exception ex) {

			log.error("Exception Message >>>>>>>>>>>>>>> {}", ex.getMessage());

			ex.printStackTrace();

			return new ResponseEntity<ErrorDTO>(new ErrorDTO("something went wrong."),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ResponseEntity<?> checkUserAdministration(String email) {

		User user = userDAO.findByEmail(email);

		if (user != null) {

			if (user.getIsAdmin()) {

				if (user.getIsLogin()) {

					return new ResponseEntity<ErrorDTO>(HttpStatus.OK);

				} else {

					return new ResponseEntity<ErrorDTO>(new ErrorDTO("Please, login first."), HttpStatus.UNAUTHORIZED);
				}

			} else {

				return new ResponseEntity<ErrorDTO>(new ErrorDTO("you aren't an admin."), HttpStatus.UNAUTHORIZED);
			}

		} else {

			return new ResponseEntity<ErrorDTO>(new ErrorDTO("Invalid token."), HttpStatus.UNAUTHORIZED);
		}
	}
}