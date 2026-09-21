import java.util.*;

/*
CPU is the shared simulation engine. It holds the four queues that
correspond to the classic process states (new -> ready -> running ->
finished) and exposes the primitive operations (admit, dispatcher, run,
preempt, finish) that every scheduling algorithm is built from.

CPU never decides which process runs next - it has no scheduling policy
of its own. Each Scheduler subclass picks a process from the ready queue
according to its own algorithm; CPU only tracks time and moves processes
between queues to carry out that choice.
*/
public class CPU {
    private List<Process> allProcesses; // used to look up a process by pid after simulation
    private List<Process> newQueue;     // not yet arrived, sorted by ArrTime ascending
    private LinkedList<Process> readyQueue = new LinkedList<>();   // arrived, waiting for CPU
    private LinkedList<Process> runningQueue = new LinkedList<>(); // 0 or 1 process, currently on CPU
    private List<Process> finishedQueue = new ArrayList<>();       // completed processes
    private List<String> log = new ArrayList<>(); // one entry per dispatch: "T{time}: p{pid}"
    private int currentTime = 0;

    public CPU(List<Process> processes) {
        allProcesses = processes;

        newQueue = new ArrayList<>(processes);
        newQueue.sort((a, b) -> {
            if (a.getArrTime() != b.getArrTime()) return a.getArrTime() - b.getArrTime();
            return a.getPid() - b.getPid();
        });

        // reset each process's simulation state before starting
        for (Process p : processes) {
            p.setRemainingTime(p.getSerTime());
            p.setExecutedTime(0);
            p.setQuantum(4);
            p.setQuantumUsed(0);
        }
    }

    /*
    Moves any process whose arrival time has passed from newQueue into
    readyQueue. Called at the start of every scheduling loop iteration,
    by every algorithm.
    */
    public void admit() {
        while (!newQueue.isEmpty() && newQueue.get(0).getArrTime() <= currentTime) {
            readyQueue.addLast(newQueue.remove(0));
        }
    }

    /*
    If the running process's remainingTime has reached 0, remove it from
    the CPU, compute its final turnaround/waiting time, and record it as
    finished.
    */
    public void finish() {
        if (!runningQueue.isEmpty() && runningQueue.getFirst().getRemainingTime() == 0) {
            Process p = runningQueue.removeFirst();
            int turn = currentTime - p.getArrTime();       // Turnaround = Completion - Arrival
            p.setTurnTime(turn);
            p.setWaitTime(turn - p.getSerTime());           // Waiting = Turnaround - Service
            finishedQueue.add(p);
        }
    }

    // true once every process has arrived, run, and finished; the simulation is complete
    public boolean queuesIsEmpty() {
        return newQueue.isEmpty() && readyQueue.isEmpty() && runningQueue.isEmpty();
    }

    public boolean runningQueueIsEmpty() {
        return runningQueue.isEmpty();
    }

    public LinkedList<Process> getReadyQueue() {
        return readyQueue;
    }

    public LinkedList<Process> getRunningQueue() {
        return runningQueue;
    }

    /*
    Moves process p from readyQueue onto the CPU, advancing the clock by
    p's dispatch overhead (getDisp()). The move is logged so the final
    schedule trace can be printed.
    */
    public void dispatcher(Process p) {
        readyQueue.remove(p);
        currentTime += p.getDisp();
        if (p.getExecutedTime() == 0) {
            p.setStartTime(currentTime); // record only the very first time this process runs
        }
        runningQueue.addLast(p);
        log.add("T" + currentTime + ": p" + p.getPid());
    }

    /*
    Advances the simulation by exactly one time unit, giving the CPU to
    whichever process is currently running (if any).
    */
    public void runProcesses() {
        if (!runningQueue.isEmpty()) {
            Process p = runningQueue.getFirst();
            p.setExecutedTime(p.getExecutedTime() + 1);
            p.setRemainingTime(p.getRemainingTime() - 1);
        }
        currentTime += 1;
    }

    /*
    Preemption: takes whatever process is running off the CPU and sends
    it to the back of the ready queue, without marking it finished. Used
    by preemptive algorithms (NRR, PAP) when a process is interrupted.
    */
    public void preempt() {
        if (!runningQueue.isEmpty()) {
            readyQueue.addLast(runningQueue.removeFirst());
        }
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public List<String> getLog() {
        return log;
    }

    // linear search over allProcesses; fine at assignment scale (small process counts)
    private Process findByPid(int pid) {
        for (Process p : allProcesses) {
            if (p.getPid() == pid) return p;
        }
        return null;
    }

    public int waitTime(int pid) {
        return findByPid(pid).getWaitTime();
    }

    public int turTime(int pid) {
        return findByPid(pid).getTurnTime();
    }
}