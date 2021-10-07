package lab;
import lift.LiftView;

public class LiftThread extends Thread {
    private LiftView view;
    private LiftMonitor monitor;
    private int current;
    private int next;

    public LiftThread(LiftView view, LiftMonitor monitor) {
        this.view = view;
        this.monitor = monitor;
    }

    public void run() {
        try {
        while (true) {
            if (monitor.openDoors()) {
                monitor.closeDoors();
            }

            next = monitor.moveLift();
            view.moveLift(current, next);
            current = next;
        }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
