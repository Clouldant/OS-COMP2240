/*
FCFS (First Come First Served): non-preemptive, no priority involved.
The process that arrives first (the front of the ready queue) always
runs next, and once dispatched it runs to completion with no interruption.
*/
public class FCFS implements Scheduler {
    public void execute(CPU cpu) {
        do {
            cpu.admit();  // move any process whose ArrTime has passed into the ready queue
            cpu.finish(); // if the running process just completed, record it and free the CPU

            if (cpu.queuesIsEmpty()) {
                break; // every process has arrived, run, and finished, so the simulation is done
            }

            /*
            CPU is idle and someone is waiting, so dispatch the one that
            has been waiting longest, meaning the front of the FIFO
            ready queue.
            */
            if (cpu.runningQueueIsEmpty() && !cpu.getReadyQueue().isEmpty()) {
                Process p = cpu.getReadyQueue().getFirst();
                cpu.dispatcher(p); // this costs `disp` time units and logs "T{time}: p{pid}"
                continue; // re-check admit/finish before spending a time unit
            }

            cpu.runProcesses(); // CPU is busy, so just advance time by 1 unit
        } while (!cpu.queuesIsEmpty());
    }
}