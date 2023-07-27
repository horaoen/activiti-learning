package com.horaoen.activitilearning.util;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;

public class ProcessUtil {
    public static Deployment deployByResourcePath(ProcessEngine engine, String path, String deployName) {
        RepositoryService repositoryService = engine.getRepositoryService();
        return repositoryService.createDeployment()
                .addClasspathResource(path)
                .enableDuplicateFiltering()
                .name(deployName)
                .deploy();
    }
}
