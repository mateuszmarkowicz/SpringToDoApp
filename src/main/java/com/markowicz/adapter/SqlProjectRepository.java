package com.markowicz.adapter;

import com.markowicz.model.Project;
import com.markowicz.model.ProjectRepository;
import com.markowicz.model.TaskGroup;
import com.markowicz.model.TaskGroupRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SqlProjectRepository extends ProjectRepository, JpaRepository<Project, Integer> {
    @Override
    @Query("select distinct p from Project p join fetch p.steps")
    List<Project> findAll();
}
