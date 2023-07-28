package com.horaoen.activitilearning;

import org.activiti.engine.*;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Before;
import org.junit.Rule;

public class AbstractTest {
    @Rule
    public final ActivitiRule activitiRule = new ActivitiRule();

    protected ProcessEngine processEngine;
    protected RepositoryService repositoryService;
    protected RuntimeService runtimeService;
    protected TaskService taskService;
    protected HistoryService historyService;
    protected ManagementService managementService;

    @Before
    public void init() {
        processEngine = activitiRule.getProcessEngine();
        repositoryService = activitiRule.getRepositoryService();
        runtimeService = activitiRule.getRuntimeService();
        taskService = activitiRule.getTaskService();
        historyService = activitiRule.getHistoryService();
        managementService = activitiRule.getManagementService();
    }
}
