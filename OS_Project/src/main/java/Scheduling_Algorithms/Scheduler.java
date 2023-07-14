package Scheduling_Algorithms;

import java.util.*;
import java.time.LocalDate;

enum SchedulerType {FCFS, SJFP, SJFNP, PRP, PRNP, RR}

public class Scheduler {
    String schedulerType;
    Queue<Process> readyQueue;
    LocalDate runTime;
    int quantum;
    ArrayList<String> Output = new ArrayList<>();
    private float avgWaitingTime;
    private float avgTurnaroundTime;

    //constructor with quantum time for round robin
    public Scheduler(String schedulerType, int quantum, Queue<Process> readyQueue) {
        this.schedulerType = schedulerType;
        this.readyQueue = readyQueue;
        this.runTime = LocalDate.now();
        this.quantum = quantum;
    }

    public Scheduler(String schedulerType, Queue<Process> readyQueue) {
        this.schedulerType = schedulerType;
        this.readyQueue = readyQueue;
        this.runTime = LocalDate.now();
    }

    //thread 1
    public void addProcess(Process process) {
        readyQueue.add(process);
    }

    public ArrayList<String> getOutput() {
        return Output;
    }

    public void setOutput(ArrayList<String> output) {
        Output = output;
    }

    public float getAvgWaitingTime() {
        return avgWaitingTime;
    }

    public void setAvgWaitingTime(float avgWaitingTime) {
        this.avgWaitingTime = avgWaitingTime;
    }

    public float getAvgTurnaroundTime() {
        return avgTurnaroundTime;
    }

    public void setAvgTurnaroundTime(float avgTurnaroundTime) {
        this.avgTurnaroundTime = avgTurnaroundTime;
    }

    public void schedule() {
        switch (schedulerType) {
            case "FCFS":
                FCFS();
                break;
            case "SJF_P":
                SJFP();
                break;
            case "SJF_NP":
                SJFNP();
                break;
            case "Priority_P":
                PRP();
                break;
            case "Priority_NP":
                PRNP();
                break;
            case "RR":
                RR();
                break;
            default:
                break;
        }
    }

    void FCFS() {
        int currentTime = 0; // initialize the current time to 0

        // sort the readyQueue by arrival time
        ArrayList<Process> processes = new ArrayList<>(readyQueue);
        Collections.sort(processes, Comparator.comparingInt(Process::getArrivalTime));
        Queue<Process> sortedQueue = new LinkedList<>(processes);
        // process each process in the readyQueue
        for (Process process : processes) {
            // set the waiting time and turnaround time for the current process
            if (process.arrivalTime > currentTime) {
                currentTime = process.arrivalTime;
            }
            process.setTurnaroundTime(currentTime + process.getBurstTime() - process.getArrivalTime());
            process.setWaitingTime(process.getTurnaroundTime() - process.getBurstTime());
            // update the current time
            currentTime += process.getBurstTime();
        }
        float averageWaitingTime = 0;
        float averageTurnaroundTime = 0;
        float size = 0;
        int secondCurrentTime = 0;
        int flag = 0;
        int nullTime = 0;
        System.out.println("*************FCFS*************");
        //System.out.println("name\tturnarround\t\twaiting\t");
        while (!sortedQueue.isEmpty()) {
            Process currentProcess = sortedQueue.poll();
            size++;
            //System.out.println(currentProcess.getName()+"\t\t\t"+currentProcess.getTurnaroundTime()+"\t\t\t\t"+currentProcess.getWaitingTime());
            if (secondCurrentTime < currentProcess.getArrivalTime()) {
                nullTime = currentProcess.getArrivalTime() - secondCurrentTime;
                secondCurrentTime = currentProcess.getArrivalTime();
                flag = 1;
            }
            int temp = currentProcess.burstTime;
            while (temp != 0) {
                temp--;
                if (flag == 1) {
                    while (nullTime != 0) {
                        nullTime--;
                        System.out.print("N");
                        Output.add("N");
                    }
                    flag = 0;
                }
                System.out.print(currentProcess.getName());
                Output.add(currentProcess.getName());

            }
            secondCurrentTime += currentProcess.getBurstTime();
            averageWaitingTime += currentProcess.getWaitingTime();
            averageTurnaroundTime += currentProcess.getTurnaroundTime();
        }
        System.out.println();

        avgWaitingTime = averageWaitingTime / size;
        avgTurnaroundTime = averageTurnaroundTime / size;
    }

    void SJFP() {

        int time = 0;
        int completed = 0;
        Process runningProcess = null;
        Queue<Process> ArrivedProcess = new LinkedList<>();
        Queue<Process> finishedQueue = new LinkedList<>();

        int queue_size = readyQueue.size();

        ArrayList<Process> ready_Queue = new ArrayList<>(readyQueue);
        ready_Queue.sort(Comparator.comparingInt(Process::getRemainingTime));
        readyQueue = new LinkedList<>(ready_Queue);

        // Loop until all processes are completed
        while (completed < queue_size) {
            Queue<Process> Temp = new LinkedList<>();
            // Check if any processes have arrived and add them to the ready queue
            while (!readyQueue.isEmpty()) {
                Process p = readyQueue.poll();
                if (p.arrivalTime <= time) {
                    ArrivedProcess.add(p);
                } else {
                    Temp.add(p);
                }
            }
            readyQueue = Temp;

            // If there is a running process, check if it has completed or if there is a shorter process in the ready queue
            if (runningProcess != null) {
                // arrange
                ArrayList<Process> temp = new ArrayList<>(ArrivedProcess);
                temp.sort(Comparator.comparingInt(Process::getRemainingTime));
                ArrivedProcess = new LinkedList<>(temp);
                if (runningProcess.remainingTime == 0) {
                    //System.out.println("Process " + runningProcess.name + " completed at time " + time);
                    runningProcess.turnaroundTime = time - runningProcess.arrivalTime;
                    runningProcess.waitingTime = runningProcess.turnaroundTime - runningProcess.burstTime;
                    finishedQueue.add(runningProcess);
                    completed++;
                    runningProcess = null;
                } else if (!ArrivedProcess.isEmpty() && ArrivedProcess.peek().remainingTime < runningProcess.remainingTime) {
                    ArrivedProcess.add(runningProcess);
                    ArrayList<Process> temp2 = new ArrayList<>(ArrivedProcess);
                    temp2.sort(Comparator.comparingInt(Process::getRemainingTime));
                    ArrivedProcess = new LinkedList<>(temp2);
                    runningProcess = ArrivedProcess.poll();
                }
            }

            // If there is no running process, get the shortest process from the ready queue and start it
            if (runningProcess == null && !ArrivedProcess.isEmpty()) {
                ArrayList<Process> temp = new ArrayList<>(ArrivedProcess);
                temp.sort(Comparator.comparingInt(Process::getRemainingTime));
                ArrivedProcess = new LinkedList<>(temp);
                runningProcess = ArrivedProcess.poll();
            }

            // Increment time and decrement remaining time for running process
            if (runningProcess != null) {
                runningProcess.remainingTime--;
                System.out.println("Process " + runningProcess.name + " for 1 sec ");
                Output.add(runningProcess.getName());
            }

            if (runningProcess == null && ArrivedProcess.isEmpty() && completed < queue_size) {
                System.out.println("Null for 1 sec");
                Output.add("N");
            }
            time++;
        }
        float averageTurnaroundTime = 0;
        float averageWaitingTime = 0;
        while (!finishedQueue.isEmpty()) {
            Process p = finishedQueue.poll();
            averageTurnaroundTime += p.turnaroundTime;
            averageWaitingTime += p.waitingTime;
        }
        avgWaitingTime = averageWaitingTime / queue_size;
        avgTurnaroundTime = averageTurnaroundTime / queue_size;
    }

    void SJFNP() {
        int time = 0;
        int completed = 0;
        Process runningProcess = null;
        Queue<Process> ArrivedProcess = new LinkedList<>();
        Queue<Process> finishedQueue = new LinkedList<>();

        int queue_size = readyQueue.size();

        ArrayList<Process> ready_Queue = new ArrayList<>(readyQueue);
        ready_Queue.sort(Comparator.comparingInt(Process::getRemainingTime));
        readyQueue = new LinkedList<>(ready_Queue);

        // Loop until all processes are completed
        while (completed < queue_size) {
            Queue<Process> Temp = new LinkedList<>();
            // Check if any processes have arrived and add them to the ready queue
            while (!readyQueue.isEmpty()) {
                Process p = readyQueue.poll();
                if (p.arrivalTime <= time) {
                    ArrivedProcess.add(p);
                } else {
                    Temp.add(p);
                }
            }
            readyQueue = Temp;

            // If there is a running process, check if it has completed or if there is a shorter process in the ready queue
            if (runningProcess != null) {
                // arrange
                ArrayList<Process> temp = new ArrayList<>(ArrivedProcess);
                temp.sort(Comparator.comparingInt(Process::getRemainingTime));
                ArrivedProcess = new LinkedList<>(temp);
                if (runningProcess.remainingTime == 0) {
                    //System.out.println("Process " + runningProcess.name + " completed at time " + time);
                    runningProcess.turnaroundTime = time - runningProcess.arrivalTime;
                    runningProcess.waitingTime = runningProcess.turnaroundTime - runningProcess.burstTime;
                    finishedQueue.add(runningProcess);
                    completed++;
                    runningProcess = null;
                }
            }

            // If there is no running process, get the shortest process from the ready queue and start it
            if (runningProcess == null && !ArrivedProcess.isEmpty()) {
                ArrayList<Process> temp = new ArrayList<>(ArrivedProcess);
                temp.sort(Comparator.comparingInt(Process::getRemainingTime));
                ArrivedProcess = new LinkedList<>(temp);
                runningProcess = ArrivedProcess.poll();
            }

            // Increment time and decrement remaining time for running process
            if (runningProcess != null) {
                runningProcess.remainingTime--;
                System.out.println("Process " + runningProcess.name + " for 1 sec ");
                Output.add(runningProcess.getName());
            }
            if (runningProcess == null && ArrivedProcess.isEmpty() && completed < queue_size) {
                System.out.println("Null for 1 sec");
                Output.add("N");
            }
            time++;
        }
        float averageTurnaroundTime = 0;
        float averageWaitingTime = 0;
        while (!finishedQueue.isEmpty()) {
            Process p = finishedQueue.poll();
            averageTurnaroundTime += p.turnaroundTime;
            averageWaitingTime += p.waitingTime;
        }

        avgWaitingTime = averageWaitingTime / queue_size;
        avgTurnaroundTime = averageTurnaroundTime / queue_size;
    }

    void PRP() {
        int time = 0;
        int completed = 0;
        Process runningProcess = null;
        Queue<Process> ArrivedProcess = new LinkedList<>();
        Queue<Process> finishedQueue = new LinkedList<>();

        int queue_size = readyQueue.size();

        ArrayList<Process> ready_Queue = new ArrayList<>(readyQueue);
        ready_Queue.sort(Comparator.comparingInt(Process::getPriority));
        readyQueue = new LinkedList<>(ready_Queue);

        // Loop until all processes are completed
        while (completed < queue_size) {
            Queue<Process> Temp = new LinkedList<>();
            // Check if any processes have arrived and add them to the ready queue
            while (!readyQueue.isEmpty()) {
                Process p = readyQueue.poll();
                if (p.arrivalTime <= time) {
                    ArrivedProcess.add(p);
                } else {
                    Temp.add(p);
                }
            }
            readyQueue = Temp;

            // If there is a running process, check if it has completed or if there is a shorter process in the ready queue
            if (runningProcess != null) {
                // arrange
                ArrayList<Process> temp = new ArrayList<>(ArrivedProcess);
                temp.sort(Comparator.comparingInt(Process::getPriority));
                ArrivedProcess = new LinkedList<>(temp);
                if (runningProcess.remainingTime == 0) {
                    //System.out.println("Process " + runningProcess.name + " completed at time " + time);
                    runningProcess.turnaroundTime = time - runningProcess.arrivalTime;
                    runningProcess.waitingTime = runningProcess.turnaroundTime - runningProcess.burstTime;
                    finishedQueue.add(runningProcess);
                    completed++;
                    runningProcess = null;
                } else if (!ArrivedProcess.isEmpty() && ArrivedProcess.peek().priority < runningProcess.priority) {
                    ArrivedProcess.add(runningProcess);
                    ArrayList<Process> temp2 = new ArrayList<>(ArrivedProcess);
                    temp2.sort(Comparator.comparingInt(Process::getPriority));
                    ArrivedProcess = new LinkedList<>(temp2);
                    runningProcess = ArrivedProcess.poll();
                }
            }

            // If there is no running process, get the shortest process from the ready queue and start it
            if (runningProcess == null && !ArrivedProcess.isEmpty()) {
                ArrayList<Process> temp = new ArrayList<>(ArrivedProcess);
                temp.sort(Comparator.comparingInt(Process::getPriority));
                ArrivedProcess = new LinkedList<>(temp);
                runningProcess = ArrivedProcess.poll();
            }

            // Increment time and decrement remaining time for running process
            if (runningProcess != null) {
                runningProcess.remainingTime--;
                System.out.println("Process " + runningProcess.name + " for 1 sec ");
                Output.add(runningProcess.getName());
            }
            if (runningProcess == null && ArrivedProcess.isEmpty() && completed < queue_size) {
                System.out.println("Null for 1 sec");
                Output.add("N");
            }
            time++;
        }
        float averageTurnaroundTime = 0;
        float averageWaitingTime = 0;
        while (!finishedQueue.isEmpty()) {
            Process p = finishedQueue.poll();
            averageTurnaroundTime += p.turnaroundTime;
            averageWaitingTime += p.waitingTime;
        }
        /*
        averageTurnaroundTime = averageTurnaroundTime / queue_size;
        averageWaitingTime = averageWaitingTime / queue_size;
        System.out.println("Average Turnaround Time = " + averageTurnaroundTime);
        System.out.println("Average Waiting Time = " + averageWaitingTime);
         */
        avgWaitingTime = averageWaitingTime / queue_size;
        avgTurnaroundTime = averageTurnaroundTime / queue_size;
    }

    void PRNP() {
        int time = 0;
        int completed = 0;
        Process runningProcess = null;
        Queue<Process> ArrivedProcess = new LinkedList<>();
        Queue<Process> finishedQueue = new LinkedList<>();
        int queue_size = readyQueue.size();
        ArrayList<Process> ready_Queue = new ArrayList<>(readyQueue);
        ready_Queue.sort(Comparator.comparingInt(Process::getPriority));
        readyQueue = new LinkedList<>(ready_Queue);
// Loop until all processes are completed
        while (completed < queue_size) {
            Queue<Process> Temp = new LinkedList<>();
// Check if any processes have arrived and add them to the ready queue
            while (!readyQueue.isEmpty()) {
                Process p = readyQueue.poll();
                if (p.arrivalTime <= time) {
                    ArrivedProcess.add(p);
                } else {
                    Temp.add(p);
                }
            }
            readyQueue = Temp;
// If there is a running process, check if it has completed or if there is a shorter process in the ready queue
            if (runningProcess != null) {
// arrange
                ArrayList<Process> temp = new ArrayList<>(ArrivedProcess);
                temp.sort(Comparator.comparingInt(Process::getPriority));
                ArrivedProcess = new LinkedList<>(temp);
                if (runningProcess.remainingTime == 0) {
//System.out.println("Process " + runningProcess.name + " completed at time " + time);
                    runningProcess.turnaroundTime = time - runningProcess.arrivalTime;
                    runningProcess.waitingTime = runningProcess.turnaroundTime - runningProcess.burstTime;
                    finishedQueue.add(runningProcess);
                    completed++;
                    runningProcess = null;
                }
            }
            // If there is no running process, get the shortest process from the ready queue and start it
            if (runningProcess == null && !ArrivedProcess.isEmpty()) {
                ArrayList<Process> temp = new ArrayList<>(ArrivedProcess);
                temp.sort(Comparator.comparingInt(Process::getPriority));
                ArrivedProcess = new LinkedList<>(temp);
                runningProcess = ArrivedProcess.poll();
            }
            // Increment time and decrement remaining time for running process
            if (runningProcess != null) {
                runningProcess.remainingTime--;
                System.out.println("Process " + runningProcess.name + " for 1 sec ");
                Output.add(runningProcess.getName());
            }
            if (runningProcess == null && ArrivedProcess.isEmpty() && completed < queue_size) {
                System.out.println("Null for 1 sec");
                Output.add("N");
            }
            time++;
        }
        float averageTurnaroundTime = 0;
        float averageWaitingTime = 0;
        while (!finishedQueue.isEmpty()) {
            Process p = finishedQueue.poll();
            averageTurnaroundTime += p.turnaroundTime;
            averageWaitingTime += p.waitingTime;
        }

        avgWaitingTime = averageWaitingTime / queue_size;
        avgTurnaroundTime = averageTurnaroundTime / queue_size;
    }

    void RR() {
        int completed = 0;
        Queue<Process> ArrivedProcess = new LinkedList<>();
        int queue_size = readyQueue.size();
        Queue<Process> finishedQueue = new LinkedList<>();
        int currentTime = 0;

        ArrayList<Process> ready_Queue = new ArrayList<>(readyQueue);
        ready_Queue.sort(Comparator.comparingInt(Process::getArrivalTime));
        readyQueue = new LinkedList<>(ready_Queue);

        while (completed < queue_size) {
            Queue<Process> Temp = new LinkedList<>();
            // Check if any processes have arrived and add them to the ready queue
            while (!readyQueue.isEmpty()) {
                Process p = readyQueue.poll();
                if (p.arrivalTime <= currentTime) {
                    ArrivedProcess.add(p);
                } else {
                    Temp.add(p);
                }
            }
            readyQueue = Temp;

            if (!ArrivedProcess.isEmpty()) {
                Process currentProcess = ArrivedProcess.poll();

                int remainingTime = currentProcess.getRemainingTime();
                int executedTime = Math.min(quantum, remainingTime);
                currentProcess.setRemainingTime(remainingTime - executedTime);
                for (int i = 0; i < executedTime; i++) {
                    System.out.println("process " + currentProcess.name + " for 1 sec");
                    Output.add(currentProcess.getName());
                }
                currentTime += executedTime;

                if (currentProcess.getRemainingTime() > 0) {
                    Temp = new LinkedList<>();
                    // Check if any processes have arrived and add them to the ready queue
                    while (!readyQueue.isEmpty()) {
                        Process p = readyQueue.poll();
                        if (p.arrivalTime <= currentTime) {
                            ArrivedProcess.add(p);
                        } else {
                            Temp.add(p);
                        }
                    }
                    readyQueue = Temp;
                    ArrivedProcess.add(currentProcess);
                } else {
                    currentProcess.turnaroundTime = currentTime - currentProcess.arrivalTime;
                    currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
                    finishedQueue.add(currentProcess);
                    completed++;
                }
            } else {
                System.out.println("NULL for 1 sec");
                Output.add("N");
                currentTime++;
            }
        }
        float averageTurnaroundTime = 0;
        float averageWaitingTime = 0;
        int size = finishedQueue.size();
        while (!finishedQueue.isEmpty()) {
            Process p = finishedQueue.poll();
            averageTurnaroundTime += p.turnaroundTime;
            averageWaitingTime += p.waitingTime;
        }
        /*
        averageTurnaroundTime = averageTurnaroundTime / size;
        averageWaitingTime = averageWaitingTime / size;
        System.out.println("Average Turnaround Time = " + averageTurnaroundTime);
        System.out.println("Average Waiting Time = " + averageWaitingTime);
         */
        avgWaitingTime = averageWaitingTime / size;
        avgTurnaroundTime = averageTurnaroundTime / size;

        for (String output : Output) {
            System.out.print(output);
        }
    }
}




