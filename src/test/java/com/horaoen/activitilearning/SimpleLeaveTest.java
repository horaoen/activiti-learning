package com.horaoen.activitilearning;

import com.horaoen.activitilearning.util.ProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class SimpleLeaveTest {
    private static ProcessEngine processEngine;
    
    @BeforeAll
    public static void deploy() {
        final String deployName = "simple-leave";
        final String path = "flow/simple-leave01.bpmn20.xml";
        processEngine = ProcessEngines.getDefaultProcessEngine();
        ProcessUtil.deployByResourcePath(processEngine, path, deployName);
    }

    @Test
    public void repoQueryTest() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        List<Deployment> deployments = repositoryService.createDeploymentQuery().list();
        deployments.forEach(d -> log.info("deployId: {}, deployName: {}", d.getId(), d.getName()));
        assertNotEquals(0, deployments.size());

        List<ProcessDefinition> definitions = repositoryService.createProcessDefinitionQuery().list();
        definitions.forEach(definition -> log.info("definitionId: {}, definitionName: {}", definition.getId(), definition.getName()));
        assertNotEquals(0, definitions.size());
    }
    
    @Test
    public void startProcessTest() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("simple-leave01");
        assertNotNull(processInstance.getId());
        
        // delete 
        runtimeService.deleteProcessInstance(processInstance.getId(), "clean data");
    }

    @Test
    public void taskQueryTest() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("simple-leave01");
        
        TaskService taskService = processEngine.getTaskService();

        List<Task> tasks = taskService.createTaskQuery().taskAssignee("zhangsan").list();
        log.info("task size: {}", tasks.size());
        assertNotEquals(0, tasks.size());

        // delete 
        runtimeService.deleteProcessInstance(processInstance.getId(), "clean data");
    }

    @Test
    public void completeTaskTest() {
        TaskService taskService = processEngine.getTaskService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.startProcessInstanceByKey("simple-leave01");
        
        List<Task> zhangsan = taskService.createTaskQuery().taskAssignee("zhangsan").list();
        int tasksSize = zhangsan.size();
        assertNotEquals(0, tasksSize);
        // complete all zhangsan's task
        zhangsan.forEach(task -> taskService.complete(task.getId()));

        zhangsan = taskService.createTaskQuery().taskAssignee("zhangsan").list();
        assertEquals(0, zhangsan.size());

        // complete all lisi's task
        List<Task> lisi = taskService.createTaskQuery().taskAssignee("lisi").list();
        assertEquals(tasksSize, lisi.size());

        lisi.forEach(task -> taskService.complete(task.getId()));
        lisi = taskService.createTaskQuery().taskAssignee("lisi").list();
        assertEquals(0, lisi.size());
    }
}
