package com.horaoen.activitilearning;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExpressionTest {
    @Test
    public void simpleLeave02ValueExpression() {
        final String deployName = "simple-leave with expression";
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("flow/simple-leave02.bpmn20.xml")
                .enableDuplicateFiltering()
                .name(deployName)
                .deploy();
        
        assertNotNull(deploy.getId());
        assertEquals(deployName, deploy.getName());

        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();

        HashMap<String, Object> variables = new HashMap<>();
        variables.put("usercode1", "zhangsan");
        ProcessInstance instance = runtimeService
                .startProcessInstanceByKey("simple-leave02", variables);
        
        assertNotNull(instance.getId());

        TaskService taskService = defaultProcessEngine.getTaskService();
        List<Task> zhangsanTasks = taskService.createTaskQuery()
                .taskAssignee("zhangsan")
                .list().stream()
                .filter(task -> instance.getId().equals(task.getProcessInstanceId()))
                .toList();
        assertEquals(1, zhangsanTasks.size());
        
        variables.put("usercode2", "lisi");
        runtimeService.setVariables(zhangsanTasks.get(0).getExecutionId(), variables);
        taskService.complete(zhangsanTasks.get(0).getId());
        
        runtimeService.setVariables(zhangsanTasks.get(0).getExecutionId(), variables);
        List<Task> lisiTasks = taskService.createTaskQuery()
                .taskAssignee("lisi")
                .list().stream()
                .filter(task -> instance.getId().equals(task.getProcessInstanceId()))
                .toList();
        
        assertEquals(1, lisiTasks.size());
        taskService.complete(lisiTasks.get(0).getId());
    }
}
