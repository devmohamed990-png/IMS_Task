package com.ims.task.demo;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ims.task.demo.dao.UserDAO;
import com.ims.task.demo.dto.AuthenticationRequestDTO;
import com.ims.task.demo.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	private final String EMAIL = "devmohamed990@gmail.com";
	private final String ADDRESS = "cairo-egypt";
	private final String PASSWORD = "ASD@123";
	private static String TOKEN = null;

	@BeforeAll
	public void createUser() {

		userDAO.save(new User(EMAIL, ADDRESS, bCryptPasswordEncoder.encode(PASSWORD), false, false));
	}

	@Test
	@Order(0)
	@DisplayName("Login With Success")
	public void loginWithSuccessUnitTest() {

		try {

			String body = convertObjectToJson(new AuthenticationRequestDTO(EMAIL, PASSWORD));

			if (body != null) {

				ResultActions resultActions = mockMvc
						.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON_VALUE).content(body))
						.andExpect(status().isOk()).andExpect(jsonPath("$.Token", notNullValue()));

				JSONObject resultJsonObject = new JSONObject(
						resultActions.andReturn().getResponse().getContentAsString());

				TOKEN = resultJsonObject.getString("Token");
			}

		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	@Test
	@Order(1)
	@DisplayName("Login With Invalid Credentials")
	public void loginWithInvalidEmailUnitTest() {

		try {

			String body = convertObjectToJson(new AuthenticationRequestDTO(EMAIL + "s", PASSWORD));

			if (body != null) {

				mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON_VALUE).content(body))
						.andExpect(status().isUnauthorized())
						.andExpect(jsonPath("$.error", is("Email or Password incorrect")));

			}

		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	@Test
	@Order(2)
	@DisplayName("Logout With Success")
	public void logoutWithSuccessUnitTest() {

		try {

			mockMvc.perform(post("/auth/logout").contentType(MediaType.APPLICATION_JSON_VALUE).header("Authorization",
					"Bearer " + TOKEN.trim())).andExpect(status().isOk())
					.andExpect(jsonPath("$.message", is("Logout successfully.")));

		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	@AfterAll
	public void deleteUserFromDatabase() {

		userDAO.delete(userDAO.findByEmail(EMAIL));
	}

	private String convertObjectToJson(Object object) {

		try {

			return objectMapper.writeValueAsString(object);

		} catch (JsonProcessingException e) {

			e.printStackTrace();

			return null;
		}
	}
}