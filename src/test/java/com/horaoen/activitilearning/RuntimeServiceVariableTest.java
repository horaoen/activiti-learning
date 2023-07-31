package com.horaoen.activitilearning;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RuntimeServiceVariableTest extends AbstractTest {
    @Test
    @Deployment(resources = "processes/simple-leave06.bpmn20.xml")
    public void localSetTest() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("simple-leave06");

        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        Task task1, task2, task3;
        if (tasks.get(0).getName().equals("人事审批")) {
            task2 = tasks.get(0);
            task1 = tasks.get(1);
        } else {
            task2 = tasks.get(1);
            task1 = tasks.get(0);
        }

        // local set
        runtimeService.setVariableLocal(task2.getExecutionId(), "variable1", "a");
        taskService.complete(task2.getId());
        task3 = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .list()
                .stream()
                .filter(task -> !task.getId().equals(task1.getId()))
                .findAny().orElseThrow();

        assertNull(runtimeService.getVariable(task1.getExecutionId(), "variable1"));
        assertNull(runtimeService.getVariableLocal(task1.getExecutionId(), "variable1"));

        assertEquals("a", runtimeService.getVariableLocal(task3.getExecutionId(), "variable1"));
        assertEquals("a", runtimeService.getVariable(task3.getExecutionId(), "variable1"));
        taskService.complete(task3.getId());

        assertNull(runtimeService.getVariable(task1.getExecutionId(), "variable1"));

    }
}
