package com.markowicz.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    List<Task> findAll();
    Page<Task> findAll(Pageable page);
    List<Task> findByDone(boolean done);
    Optional<Task> findById(Integer id);

    List<Task> findAllByGroup_Id(Integer id);

    List<Task> findAllByDeadlineNullOrDeadlineBefore(LocalDateTime today);
    boolean existsById(Integer id);
    boolean existsByDoneIsFalseAndGroup_Id(Integer groupId);
    Task save(Task entity);



}
