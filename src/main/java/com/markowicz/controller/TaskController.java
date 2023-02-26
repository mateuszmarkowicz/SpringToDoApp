package com.markowicz.controller;

import com.markowicz.logic.TaskService;
import com.markowicz.model.Task;
import com.markowicz.model.TaskRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/tasks")
class TaskController {
    private final TaskRepository repository;
    private final TaskService service;
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    TaskController(TaskRepository repository, TaskService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping(params={"!sort", "!page", "!size"})
    CompletableFuture<ResponseEntity<List<Task>>> readAllTasks(){
        logger.warn("Exposing all the tasks!");
        return service.findAllAsync().thenApply(ResponseEntity::ok);
    }

    @GetMapping
    ResponseEntity<List<Task>> readAllTasks(Pageable page){
        logger.info("Custom pageable");
        return ResponseEntity.ok(repository.findAll(page).getContent());
    }

    @GetMapping("/{id}")
    ResponseEntity<Task> readTask(@PathVariable int id){
        logger.info("Task readed");
        return ResponseEntity.of(repository.findById(id));
    }

    @GetMapping("/search/done")
    ResponseEntity<List<Task>> readDoneTasks(@RequestParam(defaultValue = "true") boolean state){
        return ResponseEntity.ok(
                repository.findByDone(state)
        );
    }

    @GetMapping("/search/today")
    ResponseEntity<List<Task>> readTaskToDoToday(){
        return ResponseEntity.ok(
                repository.findAllByDeadlineNullOrDeadlineBefore(
                        LocalDateTime.now().withHour(23).withMinute(59).withSecond(59))
        );
    }


    @PutMapping("/{id}")
    ResponseEntity<?> updateTask(@PathVariable int id, @RequestBody @Valid Task toUpdate){
        if(!repository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        repository.findById(id)
                .ifPresent(task-> {
                    task.updateFrom(toUpdate);
                    repository.save(task);
                });
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PatchMapping("{id}")
    public ResponseEntity<?> toggleTask(@PathVariable int id){
        if(!repository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        repository.findById(id)
                        .ifPresent(task-> task.setDone(!task.isDone()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    ResponseEntity<?> createTask(@RequestBody @Valid Task toAdd){
        Task addedTask = repository.save(toAdd);
        return ResponseEntity.created(URI.create("/"+addedTask.getId())).body(addedTask);
    }




//    List<Task> readAllTasks() {
//        logger.warn("Exposing all the tasks!");
//        return repository.findAll();
//    }
}

