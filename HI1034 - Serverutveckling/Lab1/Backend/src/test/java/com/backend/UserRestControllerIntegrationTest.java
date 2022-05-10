package com.backend;

import com.backend.bodies.FriendForm;
import com.backend.bodies.UserForm;
import com.backend.controllers.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.backend.bodies.LoginForm;
import com.backend.repos.UserRepository;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
class UserRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        assertThat(userController).isNotNull();
    }

    @Test
    public void attemptBadLogin() throws Exception {
        this.mockMvc.perform(post("/login")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
        var form = new LoginForm("badUserName", "badPassword");

        this.mockMvc.perform(post("/login")
                        .content(form.toJson())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void attemptSuccessfulLogin() throws Exception {
        var form = new LoginForm("Emil", "Emil");

        this.mockMvc.perform(post("/login")
                    .content(form.toJson())
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void attemptCreateUser() throws Exception {
        var form = new UserForm("newUser", "newUserName","newUserPassword");
        this.mockMvc.perform(post("/register")
                    .content(form.toJson())
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        var user = userRepository.findByUsernameAndPassword(form.username(), form.password());
        userRepository.delete(user.get());
    }

    @Test
    public void attemptAddFriend() throws Exception {
        var token = "e8afc8d8f3e146e496fccae31c53fa4f";
        var form = new FriendForm(token, Integer.toUnsignedLong(1));

        this.mockMvc.perform(post("/users/friends")
                .content(form.toJson()).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        var user = userRepository.findByToken(token);
        user.get().getFriends().clear();
        userRepository.save(user.get());
    }
}
