package lab;

import java.util.Random;

import lift.LiftView;
import lift.Passenger;

public class PassengerThread extends Thread {
    private LiftView view;
    private LiftMonitor monitor;

    public PassengerThread(LiftView view, LiftMonitor monitor) {
        this.view = view;
        this.monitor = monitor;
    }

    public void run() {
        Random rand = new Random();
        int time = rand.nextInt(46);
        try {
            Thread.sleep(time*1000);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        Passenger pass = view.createPassenger();
        pass.begin();

        int startFloor = pass.getStartFloor();
        int destFloor = pass.getDestinationFloor();

        try {
            monitor.waitingEntry(startFloor);
            pass.enterLift();
            monitor.waitingExit(destFloor);
            pass.exitLift();
            monitor.exitedLift(destFloor);
            pass.end();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
