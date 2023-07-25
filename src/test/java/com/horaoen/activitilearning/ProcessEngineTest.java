package com.horaoen.activitilearning;


import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class ProcessEngineTest {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/activiti7?createDatabaseIfNotExist=true";
    private static final String JDBC_USERNAME = "root";
    private static final String JDBC_PASSWORD = "123456";
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    @Test
    public void getDefaultProcessEngineByXmlTest() {
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        assertNotNull(defaultProcessEngine);
    }

    @Test
    public void createStandaloneProcessByMethodTest() {
        ProcessEngine processEngine = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration()
                .setJdbcUrl(JDBC_URL)
                .setJdbcUsername(JDBC_USERNAME)
                .setJdbcPassword(JDBC_PASSWORD)
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
                .setJdbcDriver(JDBC_DRIVER)
                .buildProcessEngine();
        assertNotNull(processEngine);
    }
}
