package com.horaoen.activitilearning;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * summary: local的变量global能查到，global的变量local查不到
 */
public class TaskServiceVariableTest extends AbstractTest {
    @Test
    @Deployment(resources = "processes/simple-leave05.bpmn20.xml")
    public void globalVariableTest() {
        // start a process
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("simple-leave05");

        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        assertEquals(2, tasks.size());

        Task task1, task2, task3;
        if (tasks.get(0).getName().equals("部门领导")) {
            task1 = tasks.get(0);
            task2 = tasks.get(1);
        } else {
            task1 = tasks.get(1);
            task2 = tasks.get(0);
        }

        // set variable
        taskService.setVariable(task2.getId(), "variable1", "a");
        assertEquals(taskService.getVariable(task1.getId(), "variable1"),
                taskService.getVariable(task2.getId(), "variable1"));
        // complete
        taskService.complete(task2.getId());
        assertEquals(taskService.getVariable(task1.getId(), "variable1"), "a");

        // task2完成，task1依然能查到
        assertNotNull(taskService.getVariable(task1.getId(), "variable1"));
        tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        task3 = tasks.stream().filter(task -> !task.getId().equals(task1.getId())).findAny().orElse(null);
        assertNotNull(task3);

        assertEquals("a", taskService.getVariable(task3.getId(), "variable1"));
        taskService.complete(task3.getId());
    }

    @Test
    @Deployment(resources = "processes/simple-leave05.bpmn20.xml")
    public void localVariableTest() {
        // start a process
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("simple-leave05");

        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        assertEquals(2, tasks.size());

        Task task1, task2, task3;
        if (tasks.get(0).getName().equals("部门领导")) {
            task1 = tasks.get(0);
            task2 = tasks.get(1);
        } else {
            task1 = tasks.get(1);
            task2 = tasks.get(0);
        }

        // set variable
        taskService.setVariableLocal(task2.getId(), "variable1", "a");

        assertNull(taskService.getVariable(task1.getId(), "variable1"));
        assertNull(taskService.getVariableLocal(task1.getId(), "variable1"));

        assertEquals(taskService.getVariable(task2.getId(), "variable1"), "a");
        assertEquals(taskService.getVariableLocal(task2.getId(), "variable1"), "a");

        taskService.complete(task2.getId());
        tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        task3 = tasks.stream().filter(task -> !task.getId().equals(task1.getId())).findAny().orElse(null);

        assertNotNull(task3);
        assertNull(taskService.getVariable(task3.getId(), "variable1"));
        assertNull(taskService.getVariableLocal(task3.getId(), "variable1"));
    }

    /**
     * globalSet local没法获取
     * globalSet传的taskId主要是为了获取executionId，taskId没没存储
     * localSet get
     */
    @Test
    @Deployment(resources = "processes/simple-leave05.bpmn20.xml")
    public void globalSetLocalGetTest() {
        // start a process
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("simple-leave05");

        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        assertEquals(2, tasks.size());

        Task task1, task2, task3;
        if (tasks.get(0).getName().equals("部门领导")) {
            task1 = tasks.get(0);
            task2 = tasks.get(1);
        } else {
            task1 = tasks.get(1);
            task2 = tasks.get(0);
        }

        // set variable
        taskService.setVariable(task1.getId(), "variable1", "a");
        assertNull(taskService.getVariableLocal(task1.getId(), "variable1"));
        assertEquals("a", taskService.getVariable(task1.getId(), "variable1"));
        taskService.complete(task2.getId());

        tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        task3 = tasks.stream().filter(task -> !task.getId().equals(task1.getId())).findAny().orElse(null);

        assertNotNull(task3);

        assertEquals("a", taskService.getVariable(task3.getId(), "variable1"));
        assertNull(taskService.getVariableLocal(task3.getId(), "variable1"));
    }

    /**
     * localSet global能get
     * 因为localSet 既存了execution又存了taskId
     * 跨task无法get
     */
    @Test
    @Deployment(resources = "processes/simple-leave05.bpmn20.xml")
    public void localSetGlobalGetTest() {
        // start a process
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("simple-leave05");

        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        assertEquals(2, tasks.size());

        Task task1, task2, task3;
        if (tasks.get(0).getName().equals("部门领导")) {
            task1 = tasks.get(0);
            task2 = tasks.get(1);
        } else {
            task1 = tasks.get(1);
            task2 = tasks.get(0);
        }

        // set variable
        taskService.setVariableLocal(task1.getId(), "variable1", "a");
        assertEquals("a", taskService.getVariable(task1.getId(), "variable1"));
        taskService.complete(task2.getId());

        tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        task3 = tasks.stream().filter(task -> !task.getId().equals(task1.getId())).findAny().orElse(null);

        assertNotNull(task3);

        assertNull(taskService.getVariableLocal(task3.getId(), "variable1"));
        assertNull(taskService.getVariable(task3.getId(), "variable1"));
    }
}
