package Scheduling_Algorithms;

public class Process {
    String name;
    int burstTime;
    int remainingTime;
    int arrivalTime;
    int turnaroundTime;
    int waitingTime;
    int priority;

    // constructor for priority process
    public Process(String name, int burstTime, int arrivalTime, int priority) {
        super();
        this.name = name;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        if (arrivalTime < 0)
            this.arrivalTime = 0;
        else
            this.arrivalTime = arrivalTime;
        this.priority = priority;
    }

    //constructor for no priority process
    public Process(String name, int burstTime, int arrivalTime) {
        super();
        this.name = name;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        if (arrivalTime < 0)
            this.arrivalTime = 0;
        else
            this.arrivalTime = arrivalTime;
        this.priority = -1;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public void setTurnaroundTime(int turnaroundTime) {
        this.turnaroundTime = turnaroundTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public String getName() {
        return name;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getPriority() {
        return priority;
    }

    public int getBurstTime() {
        return burstTime;
    }

}

