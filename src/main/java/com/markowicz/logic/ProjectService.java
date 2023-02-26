package com.markowicz.logic;

import com.markowicz.TaskConfigurationProperties;
import com.markowicz.model.*;
import com.markowicz.model.projection.GroupReadModel;
import com.markowicz.model.projection.GroupTaskWriteModel;
import com.markowicz.model.projection.GroupWriteModel;
import com.markowicz.model.projection.ProjectWriteModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectService {
    private ProjectRepository repository;
    private TaskGroupRepository taskGroupRepository;
    private TaskGroupService taskGroupService;
    private TaskConfigurationProperties config;

    public ProjectService(final ProjectRepository projectRepository,
                          final TaskGroupRepository taskGroupRepository,
                          TaskGroupService taskGroupService,
                          final TaskConfigurationProperties config) {
        this.repository = projectRepository;
        this.taskGroupRepository = taskGroupRepository;
        this.taskGroupService = taskGroupService;
        this.config = config;
    }

    public List<Project> readAll(){
        return repository.findAll();
    }

    public Project createProject(final ProjectWriteModel source){
        return repository.save(source.toProject());
    }

    public GroupReadModel createGroup(LocalDateTime deadline, int projectId){
        if(!config.getTemplate().isAllowMultipleTasks() && taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId) ){
            throw new IllegalStateException("Only one undone group for project is allowed");
        }
        return repository.findById(projectId)
                .map(project -> {
                    var targetGroup = new GroupWriteModel();
                    targetGroup.setDescription(project.getDescription());
                    targetGroup.setTasks(
                            project.getSteps().stream()
                                    .map(projectStep -> {
                                                var task = new GroupTaskWriteModel();
                                                task.setDescription(projectStep.getDescription());
                                                task.setDeadline(deadline.plusDays(projectStep.getDaysToDeadline()));
                                                return task;
                                            }
                                    ).collect(Collectors.toSet())
                    );
                    return taskGroupService.createGroup(targetGroup, project);
                }).orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));
    }

}
