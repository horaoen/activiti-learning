package com.horaoen.activitilearning;

import com.horaoen.activitilearning.util.ProcessUtil;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ListenerTest {
    private static ProcessEngine processEngine;

    @BeforeAll
    public static void deploy() {
        final String path = "flow/simple-leave04.bpmn20.xml";
        final String deployName = "simple leave with listener";
        processEngine = ProcessEngines.getDefaultProcessEngine();
        ProcessUtil.deployByResourcePath(processEngine, path, deployName);
    }
    @Test
    public void createEventListenerTest() {
        ProcessInstance processInstance = processEngine.getRuntimeService()
                .startProcessInstanceByKey("simple-leave04");

        Task mazi = processEngine.getTaskService().createTaskQuery()
                .taskAssignee("mazi")
                .processInstanceId(processInstance.getId()).singleResult();
        
        assertNotNull(mazi);
        //删除流程
        processEngine.getRuntimeService().deleteProcessInstance(processInstance.getId(), "clean data");
    }
}
