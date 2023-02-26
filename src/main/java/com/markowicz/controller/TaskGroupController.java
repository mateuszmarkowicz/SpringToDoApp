package com.markowicz.controller;

import com.markowicz.logic.TaskGroupService;
import com.markowicz.model.Task;
import com.markowicz.model.TaskRepository;
import com.markowicz.model.projection.GroupReadModel;
import com.markowicz.model.projection.GroupWriteModel;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/groups")
class TaskGroupController {
    private final TaskGroupService taskGroupService;
    private final TaskRepository taskRepository;
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    TaskGroupController(final TaskGroupService taskGroupService, final TaskRepository taskRepository) {
        this.taskGroupService = taskGroupService;
        this.taskRepository = taskRepository;
    }

    @GetMapping
    ResponseEntity<List<GroupReadModel>> readAllGroups(){
        return ResponseEntity.ok(taskGroupService.readAll());
    }

    @GetMapping("/{id}")
    ResponseEntity<List<Task>> readAllTaskInGroup(@PathVariable int id){
        return ResponseEntity.ok(taskRepository.findAllByGroup_Id(id));
    }

    @PostMapping
    ResponseEntity<GroupReadModel> createGroup(@RequestBody @Valid GroupWriteModel toAdd){
        GroupReadModel group = taskGroupService.createGroup(toAdd);
        return ResponseEntity.created(URI.create("/"+group.getId())).body(group);
    }

    @PatchMapping("/{id}")
    ResponseEntity<?> toggleGroup(@PathVariable int id){
            taskGroupService.toggleGroup(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<?> handlerIllegalArgument(IllegalArgumentException e){
        return ResponseEntity.notFound().build();
    }
    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<String> handlerIllegalState(IllegalStateException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
