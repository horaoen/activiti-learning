package com.horaoen.activitilearning;

import com.horaoen.activitilearning.util.ProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class ExpressionTest {
    @Test
    public void simpleLeave02ValueExpressionTest() {
        final String deployName = "simple-leave with expression";
        final String path = "flow/simple-leave02.bpmn20.xml";
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        ProcessUtil.deployByResourcePath(defaultProcessEngine, path, deployName);

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
    
    @Test
    public void simpleLeave03MethodExpressionTest() {
        final String path = "flow/simple-leave03.bpmn20.xml";
        final String deployName = "simple leave with method expression";
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        ProcessUtil.deployByResourcePath(defaultProcessEngine, path, deployName);

        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("simple-leave03");

        TaskService taskService = defaultProcessEngine.getTaskService();
        // 人事审批
        Task task = taskService.createTaskQuery()
                .taskAssignee(new com.horaoen.activitilearning.service.TaskService().getAssignee()).singleResult();
        
        assertNotNull(task);
        assertEquals(processInstance.getId(), task.getProcessInstanceId());
        
        // complete
        taskService.complete(task.getId());
        
        // 经理审批
        task = taskService.createTaskQuery()
                .taskAssignee(new com.horaoen.activitilearning.service.TaskService().getAssignee()).singleResult();
        assertNotNull(task);
        assertEquals(processInstance.getId(), task.getProcessInstanceId());

        // complete
        taskService.complete(task.getId());
    }
}
