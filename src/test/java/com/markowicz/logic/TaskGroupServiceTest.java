package com.markowicz.logic;

import com.markowicz.model.Task;
import com.markowicz.model.TaskGroup;
import com.markowicz.model.TaskGroupRepository;
import com.markowicz.model.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaskGroupServiceTest {

    @Test
    @DisplayName("should return IllegalStateException when at least one undone task exists")
    void toggleGroup_undoneTaskExists_throwsIllegalStateException() {
        //given
        TaskRepository mockTaskRepository = taskRepositoryReturning(true);
        //system under test
        var toTest = new TaskGroupService(null, mockTaskRepository);
        //when
        var exception = catchThrowable(()-> toTest.toggleGroup(1));
        //then
        assertThat(exception)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("has undone tasks");

    }

    @Test
    @DisplayName("should return IllegalArgumentException when all task are done and no task group for given id")
    void toggleGroup_noUndoneTaskExists_And_taskGroupNotFound_throwsIllegalStateException() {
        //given
        TaskRepository mockTaskRepository = taskRepositoryReturning(false);
        //and
        var mockTaskGroupRepository = mock(TaskGroupRepository.class);
        when(mockTaskGroupRepository.findById(anyInt())).thenReturn(Optional.empty());
        //system under test
        var toTest = new TaskGroupService(mockTaskGroupRepository, mockTaskRepository);
        //when
        var exception = catchThrowable(()-> toTest.toggleGroup(1));
        //then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");

    }
    @Test
    @DisplayName("should toggle task group status(done/undone)")
    void toggleGroup_noUndoneTaskExists_And_taskGroupExist_toggleTaskStatus() {
        //given
        TaskRepository mockTaskRepository = taskRepositoryReturning(false);
        //and
        TaskGroup taskGroup = new TaskGroup();
        var beforeToggle=taskGroup.isDone();
        taskGroup.setDescription("foo");
        taskGroup.setDone(false);
        var mockTaskGroupRepository = mock(TaskGroupRepository.class);
        when(mockTaskGroupRepository.findById(anyInt())).thenReturn(Optional.of(taskGroup));

        //system under test
        var toTest = new TaskGroupService(mockTaskGroupRepository, mockTaskRepository);
        //when
        toTest.toggleGroup(1);
        //then
        assertThat(taskGroup.isDone()).isEqualTo(!beforeToggle);

    }

    //moje
//    @Test
//    @DisplayName("should toggle task group status(done/undone)")
//    void toggleGroup_noUndoneTaskExists_And_taskGroupExist_toggleTaskStatus() {
//        //given
//        TaskRepository mockTaskRepository = taskRepositoryReturning(false);
//        //and
//        TaskGroup taskGroup = new TaskGroup();
//        taskGroup.setDescription("foo");
//        taskGroup.setDone(false);
//        var mockTaskGroupRepository = mock(TaskGroupRepository.class);
//        when(mockTaskGroupRepository.findById(anyInt())).thenReturn(Optional.of(taskGroup));
//        when(mockTaskGroupRepository.save(taskGroup)).thenReturn(taskGroup);
//
//        //system under test
//        var toTest = new TaskGroupService(mockTaskGroupRepository, mockTaskRepository);
//        //when
//        toTest.toggleGroup(1);
//        //then
//        assertThat(taskGroup.isDone()).isEqualTo(true);
//
//    }

    private static TaskRepository taskRepositoryReturning(boolean value) {
        var mockTaskRepository = mock(TaskRepository.class);
        when(mockTaskRepository.existsByDoneIsFalseAndGroup_Id(anyInt())).thenReturn(value);
        return mockTaskRepository;
    }
}