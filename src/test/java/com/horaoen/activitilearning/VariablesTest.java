package com.horaoen.activitilearning;

import com.horaoen.activitilearning.util.ProcessUtil;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VariablesTest {
    private static ProcessEngine processEngine;
    @BeforeAll
    public static void deploy() {
        final String path = "flow/simple-leave05.bpmn20.xml";
        final String deployName = "simple leave with no variables";
        processEngine = ProcessEngines.getDefaultProcessEngine();
        ProcessUtil.deployByResourcePath(processEngine, path, deployName);
    }

    /**
     * Note that variables can be set local for a given execution 
     * (remember a process instance consists of a tree of executions). 
     * The variable will only be visible on that execution, and not 
     * higher in the tree of executions. This can be useful if data 
     * shouldn’t be propagated to the process instance level, or the 
     * variable has a new value for a certain path in the process instance 
     * (for example when using parallel paths).
     */
    @Test
    public void variableScopeTest() {
        // start
        ProcessInstance processInstance = processEngine.getRuntimeService()
                .startProcessInstanceByKey("simple-leave05");

        TaskService taskService = processEngine.getTaskService();
        
        Task task1 = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();
        
        // variable会流转到子流程, localVariable不会
        taskService.setVariable(task1.getId(), "task1", "first task");
        taskService.setVariableLocal(task1.getId(), "task1Local", "first local task variable");
        
        taskService.complete(task1.getId());

        Task task2 = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();
        
        // task1在task2的执行树上
        assertNotNull(taskService.getVariable(task2.getId(), "task1"));
        assertNull(taskService.getVariable(task2.getId(), "task1Local"));
       
        taskService.complete(task2.getId());
        System.out.println("aa");
        
    }
}
