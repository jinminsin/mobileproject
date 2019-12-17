package com.example.healthyapp;

public class Setting {
    private boolean stepNotice;
    private boolean distanceNotice;
    private int stepGoal;
    private int distanceGoal;
    private int goalTime;
    //목표 설정 세팅

    private int appUpdateTime;
    private int sleepTime;
    private int wakeTime;
    private boolean setWidget;
    //앱 구동 설정

    public static int getHours(int time) { return time / 60; }
    public static int getMinutes(int time) { return time % 60; }

    public Setting(boolean stepNotice,boolean distanceNotice,int stepGoal,int distanceGoal,int goalTime, int appUpdateTime,int sleepTime,int wakeTime,boolean setWidget)
    {
        this.stepNotice = stepNotice;
        this.distanceNotice = distanceNotice;
        this.stepGoal = stepGoal;
        this.distanceGoal = distanceGoal;
        this.goalTime = goalTime;
        this.appUpdateTime = appUpdateTime;
        this.sleepTime = sleepTime;
        this.wakeTime = wakeTime;
        this.setWidget = setWidget;
    }

    public int getAppUpdateTime() {
        return appUpdateTime;
    }

    public void setAppUpdateTime(int appUpdateTime) {
        this.appUpdateTime = appUpdateTime;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public int getWakeTime() {
        return wakeTime;
    }

    public void setWakeTime(int wakeTime) {
        this.wakeTime = wakeTime;
    }

    public boolean isSetWidget() {
        return setWidget;
    }

    public void setSetWidget(boolean setWidget) {
        this.setWidget = setWidget;
    }

    public boolean isStepNotice() {
        return stepNotice;
    }

    public void setStepNotice(boolean stepNotice) {
        this.stepNotice = stepNotice;
    }

    public boolean isDistanceNotice() {
        return distanceNotice;
    }

    public void setDistanceNotice(boolean distanceNotice) {
        this.distanceNotice = distanceNotice;
    }

    public int getStepGoal() {
        return stepGoal;
    }

    public void setStepGoal(int stepGoal) {
        this.stepGoal = stepGoal;
    }

    public int getDistanceGoal() {
        return distanceGoal;
    }

    public void setDistanceGoal(int distanceGoal) {
        this.distanceGoal = distanceGoal;
    }

    public int getGoalTime() {
        return goalTime;
    }

    public void setGoalTime(int goalTime) {
        this.goalTime = goalTime;
    }
}
