package com.horaoen.activitilearning;

import com.horaoen.activitilearning.util.ProcessUtil;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProcessUtilTest {
    @Test
    public void deployByResourcePathTest() {
        // deploy
        final String deployName = "simple-leave with expression";
        final String resourcePath = "flow/simple-leave02.bpmn20.xml";
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        Deployment deployment = ProcessUtil.deployByResourcePath(defaultProcessEngine, resourcePath, deployName);
        
        assertNotNull(deployment.getId());
        assertEquals(deployName, deployment.getName());

        RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();
        List<Deployment> deployments = repositoryService.createDeploymentQuery().deploymentName(deployName).list();
        assertEquals(1, deployments.size());
    }
}
