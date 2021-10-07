package lab;

import lift.LiftView;
import lift.Passenger;

public class Main {
    public static void main(String[] args) {
        LiftView view = new LiftView();
        LiftMonitor monitor = new LiftMonitor(view);
        for (int i = 0; i < 20; i++) {
            PassengerThread pass = new PassengerThread(view, monitor);
            pass.start();
        }
        
        LiftThread lift = new LiftThread(view, monitor);
        lift.start();
    }
}
