package com.ims.task.demo;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ims.task.demo.controller.AuthenticationController;
import com.ims.task.demo.dao.PictureDAO;
import com.ims.task.demo.dao.UserDAO;
import com.ims.task.demo.dto.AuthenticationRequestDTO;
import com.ims.task.demo.dto.PictureDTO;
import com.ims.task.demo.model.User;
import com.ims.task.demo.utils.FileUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@org.springframework.core.annotation.Order(2)
public class PictureControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AuthenticationController authenticationController;

	@Autowired
	private PictureDAO pictureDAO;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private ObjectMapper objectMapper;

	private String TOKEN = null;
	private String ADMIN_TOKEN = null;
	private final List<String> categories = Arrays.asList("living things", "machine", "nature");
	private final String IMAGE_PATH = "src/main/resources/static/1.png";
	private final String EMAIL = "devmohamed990@gmail.com";
	private final String ADDRESS = "cairo-egypt";
	private final String PASSWORD = "ASD@123";
	private final String ADMIN_EMAIL = "admin";
	private final String ADMIN_PASSWORD = "admin123";
	private final String IMAGE_PATH_AFTER_MOVING = "storage/attachment";
	private PictureDTO pictureDTO;

	@BeforeAll
	public void createUserAndGetToken() {

		userDAO.save(new User(EMAIL, ADDRESS, bCryptPasswordEncoder.encode(PASSWORD), false, false));

		try {

			TOKEN = new JSONObject(objectMapper.writeValueAsString(
					authenticationController.login(new AuthenticationRequestDTO(EMAIL, PASSWORD)).getBody()))
							.getString("Token");

			ADMIN_TOKEN = new JSONObject(objectMapper.writeValueAsString(authenticationController
					.login(new AuthenticationRequestDTO(ADMIN_EMAIL, ADMIN_PASSWORD)).getBody())).getString("Token");

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Test
	@Order(0)
	@DisplayName("Upload Image Success")
	public void uploadImageWithSuccessUnitTest() {

		try {

			if (TOKEN != null || !TOKEN.equals("")) {

				MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

				queryParams.add("description", "Testing");
				queryParams.add("category", categories.get(0));

				mockMvc.perform(MockMvcRequestBuilders.multipart("/pic/upload")
						.file("attachment", Files.readAllBytes(Path.of(IMAGE_PATH))).queryParams(queryParams)
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.header("Authorization", "Bearer " + TOKEN.trim())).andExpect(status().isOk())
						.andExpect(jsonPath("$.message", is("Uploading Completed.")));
			}

		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	@Test
	@Order(1)
	@DisplayName("Get Uploaded Images Success")
	public void getUploadedImagesWithSuccessUnitTest() {

		try {

			if (ADMIN_TOKEN != null || !ADMIN_TOKEN.equals("")) {

				ResultActions resultActions = mockMvc
						.perform(get("/pic/uploaded-pic").contentType(MediaType.APPLICATION_JSON_VALUE)
								.header("Authorization", "Bearer " + ADMIN_TOKEN.trim()))
						.andExpect(jsonPath("$", hasSize(1)))
						.andExpect(jsonPath("$[0].code", notNullValue()))
						.andExpect(jsonPath("$[0].name", notNullValue()));
				
				pictureDTO = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(),
						new TypeReference<List<PictureDTO>>() {
						}).get(0);
				
				
			}

		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	@Test
	@Order(2)
	@DisplayName("Get One Uploaded Image Success")
	public void getOneUploadedImageWithSuccessUnitTest() {

		try {

			if (ADMIN_TOKEN != null || !ADMIN_TOKEN.equals("")) {

				MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

				params.add("code", String.valueOf(pictureDTO.getId()));

				mockMvc.perform(get("/pic/image").queryParams(params).contentType(MediaType.APPLICATION_JSON_VALUE)
						.header("Authorization", "Bearer " + ADMIN_TOKEN.trim())).andExpect(status().isOk())
						.andExpect(jsonPath("$.code", is(Integer.parseInt(pictureDTO.getId().toString()))))
						.andExpect(jsonPath("$.name", notNullValue())).andExpect(jsonPath("$.url", notNullValue()))
						.andExpect(jsonPath("$.description", notNullValue()))
						.andExpect(jsonPath("$.category", is(categories.get(0))))
						.andExpect(jsonPath("$.width", notNullValue())).andExpect(jsonPath("$.height", notNullValue()));
			}

		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	@Test
	@Order(3)
	@DisplayName("Accept Uploaded Image Success")
	public void acceptUploadedImageWithSuccessUnitTest() {

		try {

			if (ADMIN_TOKEN != null || !ADMIN_TOKEN.equals("")) {

				MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

				params.add("code", String.valueOf(pictureDTO.getId()));
				params.add("is_accepted", String.valueOf(Boolean.TRUE));

				mockMvc.perform(
						put("/pic/upadte-image").queryParams(params).contentType(MediaType.APPLICATION_JSON_VALUE)
								.header("Authorization", "Bearer " + ADMIN_TOKEN.trim()))
						.andExpect(status().isOk()).andExpect(jsonPath("$.message", is("Processing Successfully.")));
			}

		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	@Test
	@Order(4)
	@DisplayName("Get Accepted Images Success")
	public void acceptAcceptedImagesWithSuccessUnitTest() {

		try {
			
			mockMvc.perform(get("/pic/accepted-pic").contentType(MediaType.APPLICATION_JSON_VALUE))
					.andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].name", notNullValue()))
					.andExpect(jsonPath("$[0].url", notNullValue()));

		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	@AfterAll
	public void deleteUserFromDatabase() {

		FileUtils.deleteFile(IMAGE_PATH_AFTER_MOVING);
		pictureDAO.deleteByUserEmail(EMAIL);
		userDAO.delete(userDAO.findByEmail(EMAIL));
	}
}