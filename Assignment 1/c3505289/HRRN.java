/*
HRRN (Highest Response Ratio Next): non-preemptive, but the next process
to run is chosen dynamically every time the CPU goes idle, instead of
always picking whoever arrived first. This balances short jobs against
long waiting jobs.

Response Ratio = (Waiting Time + Service Time) / Service Time
A higher ratio means either the process has waited a long time, or it is
short (so running it quickly is efficient). Whichever ready process has
the highest ratio runs next. Ties are broken by picking the smaller pid.
*/
public class HRRN implements Scheduler {
    public void execute(CPU cpu) {
        do {
            cpu.admit();
            cpu.finish();

            if (cpu.queuesIsEmpty()) {
                break;
            }

            if (cpu.runningQueueIsEmpty() && !cpu.getReadyQueue().isEmpty()) {
                Process p = selectHighestRatio(cpu); // pick by ratio, not simply FIFO order
                cpu.dispatcher(p);
                continue;
            }

            /*
            Still non-preemptive: once running, a process is never
            interrupted, so while someone is running we simply tick
            time forward.
            */
            cpu.runProcesses();
        } while (!cpu.queuesIsEmpty());
    }

    /*
    Scans every process currently in the ready queue and returns the one
    with the highest response ratio at the current time. Ties go to the
    smaller pid, an arbitrary but consistent tie break rule.
    */
    private Process selectHighestRatio(CPU cpu) {
        Process best = null;
        double bestRatio = -1;
        int time = cpu.getCurrentTime();

        for (Process p : cpu.getReadyQueue()) {
            int wait = time - p.getArrTime();
            double ratio = (double) (wait + p.getSerTime()) / p.getSerTime();

            if (best == null || ratio > bestRatio || (ratio == bestRatio && p.getPid() < best.getPid())) {
                bestRatio = ratio;
                best = p;
            }
        }
        return best;
    }
}