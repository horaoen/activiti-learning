package com.horaoen.activitilearning;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class SimpleLeaveTest extends AbstractTest {
    @Test
    @org.activiti.engine.test.Deployment(resources = "processes/simple-leave01.bpmn20.xml")
    public void repoQueryTest() {
        List<Deployment> deployments = this.repositoryService.createDeploymentQuery().list();
        deployments.forEach(d -> log.info("deployId: {}, deployName: {}", d.getId(), d.getName()));
        assertNotEquals(0, deployments.size());

        List<ProcessDefinition> definitions = this.repositoryService.createProcessDefinitionQuery().list();
        definitions.forEach(definition -> log.info("definitionId: {}, definitionName: {}", definition.getId(), definition.getName()));
        assertNotEquals(0, definitions.size());
    }

    @Test
    @org.activiti.engine.test.Deployment(resources = "processes/simple-leave01.bpmn20.xml")
    public void startProcessTest() {
        ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey("simple-leave01");
        assertNotNull(processInstance.getId());

        // delete 
        this.runtimeService.deleteProcessInstance(processInstance.getId(), "clean data");
    }

    @Test
    @org.activiti.engine.test.Deployment(resources = "processes/simple-leave01.bpmn20.xml")
    public void taskQueryTest() {
        ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey("simple-leave01");

        List<Task> tasks = this.taskService.createTaskQuery().taskAssignee("zhangsan").list();
        log.info("task size: {}", tasks.size());
        assertNotEquals(0, tasks.size());

        // delete 
        this.runtimeService.deleteProcessInstance(processInstance.getId(), "clean data");
    }

    @Test
    @org.activiti.engine.test.Deployment(resources = "processes/simple-leave01.bpmn20.xml")
    public void completeTaskTest() {
        this.runtimeService.startProcessInstanceByKey("simple-leave01");

        List<Task> zhangsan = this.taskService.createTaskQuery().taskAssignee("zhangsan").list();
        int tasksSize = zhangsan.size();
        assertNotEquals(0, tasksSize);
        // complete all zhangsan's task
        zhangsan.forEach(task -> this.taskService.complete(task.getId()));

        zhangsan = this.taskService.createTaskQuery().taskAssignee("zhangsan").list();
        assertEquals(0, zhangsan.size());

        // complete all lisi's task
        List<Task> lisi = this.taskService.createTaskQuery().taskAssignee("lisi").list();
        assertEquals(tasksSize, lisi.size());

        lisi.forEach(task -> this.taskService.complete(task.getId()));
        lisi = this.taskService.createTaskQuery().taskAssignee("lisi").list();
        assertEquals(0, lisi.size());
    }
}
