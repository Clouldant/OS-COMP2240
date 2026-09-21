import java.io.FileNotFoundException;
import java.util.*;

/*
Entry point: runs the same input through all four scheduling algorithms,
then prints each one's dispatch log, per-process Turnaround/Waiting
table, and a final Summary table of averages, all to the console,
matching the exact output format required by the assignment spec.
*/
public class A1 {
    public static void main(String[] args) throws FileNotFoundException {
        String name = args[0];

        // reference list, only used to get the set of pids in sorted order
        List<Process> ref = IO.readInput(name);
        List<Integer> pids = new ArrayList<>();
        for (Process p : ref) pids.add(p.getPid());
        Collections.sort(pids);

        String[] labels = {"FCFS", "NRR", "HRRN", "PAP"};
        Scheduler[] schedulers = { new FCFS(), new NRR(), new HRRN(), new PAP() };

        // running totals for the final Summary table, one slot per algorithm
        double[] avgTurns = new double[labels.length];
        double[] avgWaits = new double[labels.length];

        for (int i = 0; i < labels.length; i++) {
            /*
            Each algorithm needs its own fresh list of Process objects,
            because simulation state (remainingTime, quantum, and so on)
            is stored directly on the Process instances and would
            otherwise leak between runs.
            */
            CPU cpu = new CPU(IO.readInput(name));
            schedulers[i].execute(cpu);

            // print the dispatch log: one "T{time}: p{pid}" line per dispatch
            System.out.println(labels[i] + ":");
            for (String line : cpu.getLog()) {
                System.out.println(line);
            }
            System.out.println();

            // print the per-process Turnaround/Waiting table
            System.out.printf("%-9s%-17s%-13s%n", "Process", "Turnaround Time", "Waiting Time");

            int totalTurn = 0, totalWait = 0;
            for (int pid : pids) {
                int turn = cpu.turTime(pid);
                int wait = cpu.waitTime(pid);
                totalTurn += turn;
                totalWait += wait;
                System.out.printf("%-9s%-17d%-13d%n", "p" + pid, turn, wait);
            }
            System.out.println();

            avgTurns[i] = (double) totalTurn / pids.size();
            avgWaits[i] = (double) totalWait / pids.size();
        }

        // print the final Summary table comparing all four algorithms
        System.out.println("Summary");
        System.out.printf("%-11s%-25s%-13s%n", "Algorithm", "Average Turnaround Time", "Waiting Time");
        for (int i = 0; i < labels.length; i++) {
            System.out.printf("%-11s%-25.2f%-13.2f%n", labels[i], avgTurns[i], avgWaits[i]);
        }
    }
}