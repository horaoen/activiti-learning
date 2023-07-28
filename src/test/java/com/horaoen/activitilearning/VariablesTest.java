package com.horaoen.activitilearning;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VariablesTest extends AbstractTest {
    private final static String TASK_1_RUNTIME_VARIABLE_NAME = "task1:runtimeVariable";
    private final static String TASK_1_RUNTIME_VARIABLE_VALUE = "task1:runtimeVariable:value";
    private final static String TASK_2_RUNTIME_VARIABLE_NAME = "task2:runtimeVariable";
    private final static String TASK_2_RUNTIME_VARIABLE_VALUE = "task2:runtimeVariable:value";
    
    /**
     * Note that variables can be set local for a given execution
     * (remember a process instance consists of a tree of executions).
     * The variable will only be visible on that execution, and not
     * higher in the tree of executions. This can be useful if data
     * shouldn’t be propagated to the process instance level, or the
     * variable has a new value for a certain path in the process instance
     * (for example when using parallel paths).
     */
    private ProcessInstance getProcessInstance() {
        return this.processEngine.getRuntimeService()
                .startProcessInstanceByKey("simple-leave05");
    }

    @Test
    @Deployment(resources = "processes/simple-leave05.bpmn20.xml")
    public void variableScopeTest() {
        ProcessInstance processInstance = this.getProcessInstance();
        
        TaskService taskService = processEngine.getTaskService();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        Task task1 = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();

        // variable会流转到子流程, localVariable不会
        taskService.setVariable(task1.getId(), "task1", "first task");
        runtimeService.setVariable(task1.getExecutionId(), "execution1", "execution1");

        taskService.setVariableLocal(task1.getId(), "task1Local", "first local task variable");
        runtimeService.setVariable(task1.getExecutionId(), "execution1Local", "localExecution");

        taskService.complete(task1.getId());

        Task task2 = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();


        // task1在task2的执行树上
        // task variable test
        assertNotNull(taskService.getVariable(task2.getId(), "task1"));
        assertNull(taskService.getVariable(task2.getId(), "task1Local"));

        // runtime variable test
        assertEquals(runtimeService.getVariable(task1.getExecutionId(), "execution1"), "execution1");

        taskService.complete(task2.getId());
        System.out.println("aa");

    }

    /**
     * summary: runtime variable 整个工作流程中各个节点都可访问
     * runtimeService设置的变量在task complete后不会销毁
     */
    @Test
    @Deployment(resources = "processes/simple-leave05.bpmn20.xml")
    public void runtimeServiceVariableTest() {
        ProcessInstance processInstance = this.getProcessInstance();
        Task task1 = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

        runtimeService.setVariable(task1.getExecutionId(), TASK_1_RUNTIME_VARIABLE_NAME, TASK_1_RUNTIME_VARIABLE_VALUE);
        taskService.complete(task1.getId());

        Task task2 = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

        // task2 通过 runtimeService 访问 task1 设置的variable
        assertEquals(TASK_1_RUNTIME_VARIABLE_VALUE, runtimeService.getVariable(task2.getExecutionId(), TASK_1_RUNTIME_VARIABLE_NAME));

        runtimeService.setVariable(task2.getExecutionId(), TASK_2_RUNTIME_VARIABLE_NAME, TASK_2_RUNTIME_VARIABLE_VALUE);

        // task1 通过 runtimeService 访问 task2 设置的variable
        assertEquals(TASK_2_RUNTIME_VARIABLE_VALUE, runtimeService.getVariable(task1.getExecutionId(), TASK_2_RUNTIME_VARIABLE_NAME));
    }

    /**
     * summary: local variable也在整个流程实例中都可访问, 但local的变量优先级更高
     */
    @Test
    @Deployment(resources = "processes/simple-leave05.bpmn20.xml")
    public void runtimeServiceLocalVariableTest() {
        ProcessInstance processInstance = this.getProcessInstance();
        Task task1 = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

        runtimeService.setVariableLocal(task1.getExecutionId(), TASK_1_RUNTIME_VARIABLE_NAME, TASK_1_RUNTIME_VARIABLE_VALUE);
        taskService.complete(task1.getId());

        Task task2 = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

        // task2 通过 runtimeService 访问 task1 设置的variable
        assertEquals(TASK_1_RUNTIME_VARIABLE_VALUE, runtimeService.getVariableLocal(task2.getExecutionId(), TASK_1_RUNTIME_VARIABLE_NAME));

        runtimeService.setVariableLocal(task2.getExecutionId(), TASK_2_RUNTIME_VARIABLE_NAME, TASK_2_RUNTIME_VARIABLE_VALUE);
        runtimeService.setVariableLocal(task1.getExecutionId(), TASK_2_RUNTIME_VARIABLE_NAME, TASK_1_RUNTIME_VARIABLE_VALUE);

        // 查看优先级
//        assertEquals(TASK_2_RUNTIME_VARIABLE_VALUE, runtimeService.getVariableLocal(task2.getExecutionId(), TASK_2_RUNTIME_VARIABLE_NAME));
//        assertEquals(TASK_1_RUNTIME_VARIABLE_VALUE, runtimeService.getVariableLocal(task1.getExecutionId(), TASK_2_RUNTIME_VARIABLE_NAME));
    }

    @Test
    public void taskServiceVariableTest() {

    }

    @Test
    public void taskServiceLocalVariableTest() {

    }
}
