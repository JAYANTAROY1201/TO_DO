/*package com.todo.user_service.testcontroller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.todo.FunDooApplication;

*//**
 *<p> purpose: This class is designed to test login class in controller</p><br>
 * @author JAYANTA ROY
 * @version 1.0
 * @since 24/07/18
 *//*
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = FunDooApplication.class)
public class UserControllerTest {

	private MockMvc mockmvc;
	@Autowired
	WebApplicationContext webAppContext;

	@Before
	public void setup() {
		this.mockmvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
	}

	@Test
	public void successfulLoginTest() throws Exception {
		mockmvc.perform(MockMvcRequestBuilders.post("/fundoo/user/login").param("email", "mail2j.roy@gmail.com")
				.param("password", "12345")).andExpect(status().isOk());

	}

	@Test
	public void blankEmailLogin() throws Exception {
		mockmvc.perform(MockMvcRequestBuilders.post("/fundoo/user/login").param("email", "").param("password", "12345"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void blankPasswordLogin() throws Exception {
		mockmvc.perform(
				MockMvcRequestBuilders.post("/fundoo/user/login").param("email", "xxxxxxxxxxx").param("password", ""))
				.andExpect(status().isBadRequest());

	}
}
*/