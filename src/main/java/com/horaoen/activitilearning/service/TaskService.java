package com.horaoen.activitilearning.service;


import java.io.Serializable;

public class TaskService implements Serializable {
    private final static String USER_CODE = "wangwu";
    public String getAssignee() {
        return USER_CODE;
    }
}
