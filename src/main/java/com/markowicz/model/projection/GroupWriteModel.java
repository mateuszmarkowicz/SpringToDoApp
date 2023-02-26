package com.markowicz.model.projection;

import com.markowicz.model.Project;
import com.markowicz.model.TaskGroup;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupWriteModel {
    private String description;
    /**
     * Deadline from the latest task in group.
     */
    private Set<GroupTaskWriteModel> tasks;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<GroupTaskWriteModel> getTasks() {
        return tasks;
    }

    public void setTasks(Set<GroupTaskWriteModel> tasks) {
        this.tasks = tasks;
    }

    public TaskGroup toGroup(Project project){
        var result =  new TaskGroup();
        result.setDescription(description);
        result.setTasks(
                tasks.stream()
                .map(source -> source.toTask(result))
                .collect(Collectors.toSet())
        );
        result.setProject(project);
        return result;
    }
}
