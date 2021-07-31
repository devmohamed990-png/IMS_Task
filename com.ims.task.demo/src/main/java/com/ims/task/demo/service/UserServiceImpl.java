package com.ims.task.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ims.task.demo.dao.UserDAO;
import com.ims.task.demo.dto.ErrorDTO;
import com.ims.task.demo.dto.RegisterDTO;
import com.ims.task.demo.dto.SuccessDTO;
import com.ims.task.demo.model.User;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public ResponseEntity<?> register(RegisterDTO registerDTO) {

		try {

			User user = userDAO.findByEmail(registerDTO.getEmail());

			if (user == null) {

				userDAO.save(new User(registerDTO.getEmail(), registerDTO.getAddress(),
						bCryptPasswordEncoder.encode(registerDTO.getPassword()), false, false));

				return new ResponseEntity<SuccessDTO>(new SuccessDTO("Registration Successful."), HttpStatus.OK);

			} else {

				return new ResponseEntity<ErrorDTO>(new ErrorDTO("This email is already used."),
						HttpStatus.BAD_REQUEST);
			}

		} catch (Exception ex) {

			log.error("Exception Message >>>>>>>>>>>>>>>> {}", ex.getMessage());

			return new ResponseEntity<ErrorDTO>(new ErrorDTO("something went wrong."),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public void login(String email) {

		try {

			User user = userDAO.findByEmail(email);
			user.setIsLogin(true);

			userDAO.save(user);

		} catch (Exception ex) {

			log.error("Exception Message >>>>>>>>>>>>>>>> {}", ex.getMessage());

			ex.printStackTrace();
		}
	}

	@Override
	public ResponseEntity<?> logout(String email) {

		try {

			User user = userDAO.findByEmail(email);
			user.setIsLogin(false);

			userDAO.save(user);

			return new ResponseEntity<SuccessDTO>(new SuccessDTO("Logout successfully."), HttpStatus.OK);

		} catch (Exception ex) {

			log.error("Exception Message >>>>>>>>>>>>>>>> {}", ex.getMessage());

			ex.printStackTrace();

			return new ResponseEntity<ErrorDTO>(new ErrorDTO("Can't logout."), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}