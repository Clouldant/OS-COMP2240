/*
Common contract for every scheduling algorithm. Each algorithm gets its
own class implementing this interface, so A1 can run any of them
polymorphically without knowing which specific algorithm it is calling.
 */
public interface Scheduler {
    void execute(CPU cpu);
}