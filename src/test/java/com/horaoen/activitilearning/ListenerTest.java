package com.horaoen.activitilearning;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ListenerTest extends AbstractTest{

    @Test
    @Deployment(resources = "processes/simple-leave04.bpmn20.xml")
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
