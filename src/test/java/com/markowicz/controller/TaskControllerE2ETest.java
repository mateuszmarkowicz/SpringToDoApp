package com.markowicz.controller;

import com.markowicz.model.Task;
import com.markowicz.model.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerE2ETest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    TaskRepository repo;

    @Test
    void httpGet_returnsAllTasks(){
        //given
        int initial = repo.findAll().size();
        repo.save(new Task("foo", LocalDateTime.now()));
        repo.save(new Task("bar", LocalDateTime.now()));
        //when
        Task[] result = restTemplate.getForObject("http://localhost:"+port+"/tasks", Task[].class);
        //then
        assertThat(result).hasSize(initial+2);

    }

    @Test
    void httpGet_createTask(){
        //given
        Task task = new Task("foo", LocalDateTime.now());
        //when
        Task result = restTemplate.postForObject("http://localhost:"+port+"/tasks",task, Task.class);
        //then
        assertThat(result).isInstanceOf(Task.class);
        assertThat(result.getDescription()).isEqualTo("foo");
        assertThat(result.isDone()).isEqualTo(false);
    }
}