package com.horaoen.activitilearning;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class ExpressionTest extends AbstractTest {
    @Test
    @Deployment(resources = "processes/simple-leave02.bpmn20.xml")
    public void simpleLeave02ValueExpressionTest() {
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("usercode1", "zhangsan");
        ProcessInstance instance = runtimeService
                .startProcessInstanceByKey("simple-leave02", variables);

        assertNotNull(instance.getId());

        TaskService taskService = processEngine.getTaskService();
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
    @Deployment(resources = "processes/simple-leave03.bpmn20.xml")
    public void simpleLeave03MethodExpressionTest() {
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("taskService", new com.horaoen.activitilearning.service.TaskService());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("simple-leave03", variables);

        TaskService taskService = processEngine.getTaskService();
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
