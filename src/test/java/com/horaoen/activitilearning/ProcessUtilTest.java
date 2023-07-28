package com.horaoen.activitilearning;

import com.horaoen.activitilearning.util.ProcessUtil;
import org.activiti.engine.repository.Deployment;
import org.junit.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProcessUtilTest extends AbstractTest {
    @Test
    public void deployByResourcePathTest() {
        // deploy
        final String deployName = "simple-leave with expression";
        final String resourcePath = "processes/simple-leave02.bpmn20.xml";
        Deployment deployment = ProcessUtil.deployByResourcePath(processEngine, resourcePath, deployName);

        assertNotNull(deployment.getId());
        assertEquals(deployName, deployment.getName());

        List<Deployment> deployments = repositoryService.createDeploymentQuery().deploymentName(deployName).list();
        assertEquals(1, deployments.size());
    }
}
