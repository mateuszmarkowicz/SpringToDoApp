package com.markowicz.controller;

import com.markowicz.model.Task;
import com.markowicz.model.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureMockMvc
class TasmControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository repo;
    @Test
    void httpGet_returnGivenTask() throws Exception {
        //given
        int id = repo.save(new Task("foo", LocalDateTime.now())).getId();
        //when + then
        mockMvc.perform(get("/tasks/"+id))
                .andExpect(status().is2xxSuccessful());
    }
        @Test
        void httpGet_returnAllTasks() throws Exception {
            //given
            repo.save(new Task("foo", LocalDateTime.now()));
            repo.save(new Task("foo2", LocalDateTime.now()));
            //when + then
            mockMvc.perform(get("/tasks"))
                    .andDo(print())
                    .andExpect(content().string(containsString("\"description\":\"foo\"")))
                    .andExpect(content().string(containsString("\"description\":\"foo2\"")))
                    .andExpect(status().is2xxSuccessful());
        }

}
