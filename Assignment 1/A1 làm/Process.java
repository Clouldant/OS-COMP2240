// Data class representing a single process, plus the mutable state needed
// to simulate it running through a scheduling algorithm.
public class Process {
    //static data, read from the input file
    private int pid;
    private int disp;           
    private int agingInterval;  
    private int arrTime;        
    private int serTime;        
    private int priority;       

    //final results, filled in once the process finishes simulating
    private int waitTime;
    private int turnTime;
    private int startTime;      // time of its FIRST dispatch (not every dispatch)

    //simulation-only state, reset at the start of every algorithm run
    private int remainingTime;  // service time still left to run
    private int executedTime;   // total CPU time received so far (used by PAP's aging formula)
    private int quantum;        // current quantum size (used by NRR, starts at 4, shrinks over time)
    private int quantumUsed;    // how much of the current quantum has been used (used by NRR)

    /*Constructors */
    public Process(int pid, int arrTime, int serTime, int priority) {
        this.pid = pid;
        this.arrTime = arrTime;
        this.serTime = serTime;
        this.priority = priority;
    }
    public Process(int pid, int waitTime, int turnTime){
        this.pid = pid;
        this.waitTime = waitTime;
        this.turnTime = turnTime;
    }
    public Process(int pid){
        this.pid=pid;
    }
    /*Getters and Setters */
    public int getStartTime() {
        return startTime;
    }
    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }
    public int getRemainingTime() {
        return remainingTime;
    }
    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }
    public int getQuantum() {
        return quantum;
    }
    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }
    public int getExecutedTime() {
        return executedTime;
    }
    public void setExecutedTime(int executedTime) {
        this.executedTime = executedTime;
    }
    public int getQuantumUsed() {
        return quantumUsed;
    }
    public void setQuantumUsed(int quantumUsed) {
        this.quantumUsed = quantumUsed;
    }
    public int getWaitTime() {
        return waitTime;
    }
    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }
    public int getTurnTime() {
        return turnTime;
    }
    public void setTurnTime(int turnTime) {
        this.turnTime = turnTime;
    }
    public int getPid() {
        return pid;
    }
    public void setPid(int pid) {
        this.pid = pid;
    }
    public int getDisp() {
        return disp;
    }
    public void setDisp(int disp) {
        this.disp = disp;
    }
    public int getAgingInterval() {
        return agingInterval;
    }
    public void setAgingInterval(int agingInterval) {
        this.agingInterval = agingInterval;
    }
    public int getArrTime() {
        return arrTime;
    }
    public void setArrTime(int arrTime) {
        this.arrTime = arrTime;
    }
    public int getSerTime() {
        return serTime;
    }
    public void setSerTime(int serTime) {
        this.serTime = serTime;
    }
    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }

}
