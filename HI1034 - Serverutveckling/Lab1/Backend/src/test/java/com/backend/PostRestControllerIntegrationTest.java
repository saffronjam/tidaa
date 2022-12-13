package com.backend;

import com.backend.bodies.PostForm;
import com.backend.bodies.UserForm;
import com.backend.controllers.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.backend.bodies.LoginForm;
import com.backend.repos.PostRepository;
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
public class PostRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostController postController;

    @Autowired
    private PostRepository postRepository;

    @Test
    public void contextLoads() {
        assertThat(postController).isNotNull();
    }

    @Test
    public void attemptCreatePostWithInvalidToken() throws Exception {
        var form = new PostForm("content", "invalidToken", null);

        mockMvc.perform(post("/posts")
                    .content(form.toJson())
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
