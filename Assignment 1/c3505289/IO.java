import java.io.FileNotFoundException;
import java.io.File;
import java.util.*;

/*
Utility class: responsible for reading the input file and building Process
objects. The file format uses BEGIN/END blocks: one BEGIN...END block for
shared config (DISP, AGING_INTERVAL), then one PID...END block per process.
*/
public class IO {

    public static List<Process> readInput(String fileName) throws FileNotFoundException {
        // console scans the file token by token (Scanner splits on whitespace or newlines)
        Scanner console = new Scanner(new File(fileName));
        List<Process> processes = new ArrayList<>();

        int disp = 0;
        int agingInterval = 0;

        while (console.hasNext()) {
            String pointer = console.next();

            if (pointer.equals("EOF")) {
                break; // custom end-of-file marker, stop parsing here

            } else if (pointer.equals("BEGIN")) {
                // config block: read key/value pairs until its own END
                while (console.hasNext()) {
                    String tag = console.next();
                    if (tag.equals("END")) break;

                    if (tag.equals("DISP:") && console.hasNextInt()) {
                        disp = console.nextInt();
                    } else if (tag.equals("AGING_INTERVAL:") && console.hasNextInt()) {
                        agingInterval = console.nextInt();
                    }
                }

            } else if (pointer.equals("PID:")) {
                // process block: read ArrTime, SrvTime, Priority until this process's END
                String pidInput = console.next(); // "p1", "p2", and so on
                int pid = 0;
                if (pidInput.equals("p1")) {
                    pid = 1;
                } else if (pidInput.equals("p2")) {
                    pid = 2;
                } else if (pidInput.equals("p3")) {
                    pid = 3;
                } else if (pidInput.equals("p4")) {
                    pid = 4;
                } else if (pidInput.equals("p5")) {
                    pid = 5;
                }
                int arrTime = 0, srvTime = 0, priority = 0;

                while (console.hasNext()) {
                    String tag = console.next();
                    if (tag.equals("END")) break;

                    if (console.hasNextInt()) {
                        int value = console.nextInt();
                        if (tag.equals("ArrTime:")) arrTime = value;
                        else if (tag.equals("SrvTime:")) srvTime = value;
                        else if (tag.equals("Priority:")) priority = value;
                    }
                }

                /*
                Build the Process once all its fields are read, then attach
                the shared config values (disp, agingInterval) that apply
                to every process.
                */
                Process p = new Process(pid, arrTime, srvTime, priority);
                p.setDisp(disp);
                p.setAgingInterval(agingInterval);
                processes.add(p);
            }
        }

        console.close();
        return processes;
    }
}