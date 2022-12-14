package com.example.fyplatest1;

import java.io.Serializable;

public class Challenge implements Serializable {
    static int RUNNING_STEP_CHALLENGE = 1, RUNNING_DISTANCE_CHALLENGE = 2;


    public int challengeType;
    public String challengeName;
    public double completed;
    public int total;
    public MyDate startDate, endDate;

    Challenge(String challengeName, int challengeType, int total, MyDate startDate, MyDate endDate) {
        this(challengeName, challengeType, total, 0, startDate, endDate);
    }

    public Challenge(String challengeName, int challengeType, int total, double completed, MyDate startDate, MyDate endDate) {
        this.challengeName = challengeName;
        this.challengeType = challengeType;
        this.completed = completed;
        this.total = total;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
