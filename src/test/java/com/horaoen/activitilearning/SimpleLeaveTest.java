package com.horaoen.activitilearning;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SimpleLeaveTest {
    private static final String PROCESS_NAME = "simple-leave";
    @Test
    @Order(1)
    public void simpleLeaveDeployTest() {
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("flow/simple-leave01.bpmn20.xml")
                .name(PROCESS_NAME)
                .enableDuplicateFiltering()
                .deploy();
        assertNotNull(deployment.getId());
        assertEquals(PROCESS_NAME, deployment.getName());
    }

    @Test
    @Order(2)
    public void repoQueryTest() {
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();
        List<Deployment> deployments = repositoryService.createDeploymentQuery().list();
        deployments.forEach(d -> log.info("deployId: {}, deployName: {}", d.getId(), d.getName()));
        assertNotEquals(0, deployments.size());

        List<ProcessDefinition> definitions = repositoryService.createProcessDefinitionQuery().list();
        definitions.forEach(definition -> log.info("definitionId: {}, definitionName: {}", definition.getId(), definition.getName()));
        assertNotEquals(0, definitions.size());
    }
    
    @Test
    @Order(3) 
    public void startProcessTest() {
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("simple-leave01");
        assertNotNull(processInstance.getId());
    }
    
    @Test
    @Order(4)
    public void taskQueryTest() {
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = defaultProcessEngine.getTaskService();

        List<Task> tasks = taskService.createTaskQuery().taskAssignee("zhangsan").list();
        log.info("task size: {}", tasks.size());
        assertNotEquals(0, tasks.size());
    }
    
    @Test
    @Order(5)
    public void completeTaskTest() {
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = defaultProcessEngine.getTaskService();

        List<Task> zhangsan = taskService.createTaskQuery().taskAssignee("zhangsan").list();
        int tasksSize = zhangsan.size();
        assertNotEquals(0, tasksSize);
        // complete all zhangsan's task
        zhangsan.forEach(task -> {
            taskService.complete(task.getId());
        });
        
        zhangsan = taskService.createTaskQuery().taskAssignee("zhangsan").list();
        assertEquals(0, zhangsan.size());
        
        // complete all lisi's task
        List<Task> lisi = taskService.createTaskQuery().taskAssignee("lisi").list();
        assertEquals(tasksSize, lisi.size());
        
        lisi.forEach(task -> {
            taskService.complete(task.getId());
        });
        lisi = taskService.createTaskQuery().taskAssignee("lisi").list();
        assertEquals(0, lisi.size());
    }
}
