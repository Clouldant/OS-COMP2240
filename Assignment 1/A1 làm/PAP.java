/*
PAP (Preemptive Aging Priority): preemptive, priority-based scheduling
where a lower numeric priority value means higher actual priority. To
prevent starvation, a process's effective priority automatically improves
the longer it has been waiting, which is called aging.

waitingTime = currentTime - arrivalTime - executedTime
agingAmount = waitingTime / AGING_INTERVAL (integer division)
effectivePriority = basePriority - agingAmount

Effective priority is recalculated every time unit. A ready process
preempts the one currently running only if its effective priority is
strictly lower, meaning strictly better. If two processes are tied on
effective priority, no preemption happens; the currently running process
is left alone, and pid is used only to break ties when picking among
several ready, not yet running, candidates.
*/
public class PAP implements Scheduler {
    public void execute(CPU cpu) {
        do {
            cpu.admit();
            cpu.finish();

            if (cpu.queuesIsEmpty()) {
                break;
            }

            Process best = selectBestPriority(cpu); // best candidate sitting in the ready queue

            if (cpu.runningQueueIsEmpty()) {
                if (best != null) {
                    cpu.dispatcher(best); // CPU is free, so just dispatch whoever is best
                }
                continue;
            }

            Process running = cpu.getRunningQueue().getFirst();
            if (best != null) {
                int runningEp = effectivePriority(running, cpu.getCurrentTime());
                int bestEp = effectivePriority(best, cpu.getCurrentTime());

                /*
                Preempt only when strictly better (bestEp < runningEp).
                Equal effective priorities must not cause a preemption.
                */
                if (bestEp < runningEp) {
                    cpu.preempt();        // running process goes back to the ready queue
                    cpu.dispatcher(best); // new process takes over the CPU
                    continue;
                }
            }

            cpu.runProcesses(); // no one beats the currently running process, so just tick time forward
        } while (!cpu.queuesIsEmpty());
    }

    /*
    Finds the ready queue process with the lowest effective priority
    value, remembering that a lower number means higher priority. Ties
    are broken by the smaller pid, which matters only when choosing
    among waiting candidates, not against the process that is already
    running (that comparison happens separately above).
    */
    private Process selectBestPriority(CPU cpu) {
        Process best = null;
        int bestEp = Integer.MAX_VALUE;

        for (Process p : cpu.getReadyQueue()) {
            int ep = effectivePriority(p, cpu.getCurrentTime());
            if (best == null || ep < bestEp || (ep == bestEp && p.getPid() < best.getPid())) {
                bestEp = ep;
                best = p;
            }
        }
        return best;
    }

    /*
    Computes a process's current effective priority given how long it
    has genuinely been waiting, excluding time it already spent running.
    */
    private int effectivePriority(Process p, int currentTime) {
        int waitingTime = currentTime - p.getArrTime() - p.getExecutedTime();
        int interval = p.getAgingInterval();
        int agingAmount = (interval > 0) ? waitingTime / interval : 0; // integer division
        return p.getPriority() - agingAmount; // subtracting improves (lowers) the value
    }
}