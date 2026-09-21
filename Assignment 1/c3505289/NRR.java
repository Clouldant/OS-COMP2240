/*
NRR (Narrow Round Robin): preemptive, time-sliced scheduling where each
process keeps its own shrinking quantum, instead of one shared quantum
for everyone.

Rules: every process starts with quantum q = 4. Whenever a process uses
its full quantum without finishing, q is reduced by 1 for its next turn
(never below 2). If another process is ready when the quantum runs out,
the interrupted process goes to the back of the ready queue (a real
context switch, which costs dispatcher time next time it runs). If
nobody else is ready, the process just keeps running with its (now
smaller) quantum renewed immediately, without running the dispatcher
again (no extra `disp` cost, no re-queueing).
*/
public class NRR implements Scheduler {
    public void execute(CPU cpu) {
        do {
            cpu.admit();
            cpu.finish();

            if (cpu.queuesIsEmpty()) {
                break;
            }

            if (cpu.runningQueueIsEmpty() && !cpu.getReadyQueue().isEmpty()) {
                Process p = cpu.getReadyQueue().getFirst(); // FIFO: still round-robin order
                cpu.dispatcher(p);
                p.setQuantumUsed(0); // starting a fresh quantum for this turn
                continue;
            }

            cpu.runProcesses(); // run the current process for exactly 1 time unit

            if (!cpu.runningQueueIsEmpty()) {
                Process running = cpu.getRunningQueue().getFirst();
                running.setQuantumUsed(running.getQuantumUsed() + 1);

                boolean usedFullQuantum = running.getQuantumUsed() == running.getQuantum();
                boolean notFinished = running.getRemainingTime() > 0;

                if (usedFullQuantum && notFinished) {
                    running.setQuantum(Math.max(2, running.getQuantum() - 1)); // shrink its own quantum, floor of 2

                    if (!cpu.getReadyQueue().isEmpty()) {
                        cpu.preempt(); // someone else is waiting, so preempt to the back of the queue
                    } else {
                        running.setQuantumUsed(0); // nobody else ready, keep running with the renewed quantum
                    }
                }
            }
        } while (!cpu.queuesIsEmpty());
    }
}