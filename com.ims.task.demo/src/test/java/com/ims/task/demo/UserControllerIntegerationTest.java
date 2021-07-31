package com.ims.task.demo;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ims.task.demo.dao.UserDAO;
import com.ims.task.demo.dto.RegisterDTO;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@org.springframework.core.annotation.Order(0)
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerIntegerationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private ObjectMapper objectMapper;

	private final String EMAIL = "devmohamed990@gmail.com";
	private final String ADDRESS = "cairo-egypt";
	private final String PASSWORD = "ASD@123";

	@Test
	@Order(0)
	@DisplayName("Register First Time Success")
	public void registerFirstTimeSuccessUnitTest() {

		try {

			String body = convertObjectToJson(new RegisterDTO(EMAIL, ADDRESS, PASSWORD));

			if (body != null) {

				mockMvc.perform(post("/user/register").contentType(MediaType.APPLICATION_JSON_VALUE).content(body))
						.andExpect(status().isOk()).andExpect(jsonPath("$.message", is("Registration Successful.")));

			}

		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	@Test
	@Order(1)
	@DisplayName("Register With Null Email")
	public void registerWithInvalidEmailUnitTest() {

		try {

			String body = convertObjectToJson(new RegisterDTO(null, ADDRESS, PASSWORD));

			if (body != null) {

				mockMvc.perform(post("/user/register").contentType(MediaType.APPLICATION_JSON_VALUE).content(body))
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.error", is("Please, enter valid email like (xxx@domain.com)")));

			}

		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	@Test
	@Order(2)
	@DisplayName("Register With Null Address")
	public void registerWithInvalidAddressUnitTest() {

		try {

			String body = convertObjectToJson(new RegisterDTO(EMAIL, null, PASSWORD));

			if (body != null) {

				mockMvc.perform(post("/user/register").contentType(MediaType.APPLICATION_JSON_VALUE).content(body))
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.error", is("Please, enter valid address")));

			}

		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	@Test
	@Order(3)
	@DisplayName("Register With Null Password")
	public void registerWithInvalidPasswordUnitTest() {

		try {

			String body = convertObjectToJson(new RegisterDTO(EMAIL, ADDRESS, null));

			if (body != null) {

				mockMvc.perform(post("/user/register").contentType(MediaType.APPLICATION_JSON_VALUE).content(body))
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.error", is("Please, enter valid password")));

			}

		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	@AfterAll
	public void deleteUser() {

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