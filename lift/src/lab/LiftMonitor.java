package lab;

import lift.LiftView;
import java.util.*;

import javax.sql.rowset.spi.SyncResolver;

public class LiftMonitor {
    private int currentFloor, direction, load;
    private boolean moving;
    private boolean exiting;
    private int[] waitEntry, waitExit;
    private LiftView view;

    public LiftMonitor(LiftView view) {
        this.view = view;
        this.waitEntry = new int[7];
        this.waitExit = new int[7];
        this.currentFloor = 0;
        this.direction = 1;
        this.moving = true;
        this.exiting = false;
        this.load = 0;
    }

    synchronized public void waitingEntry(int currFloor) throws InterruptedException {
        waitEntry[currFloor]++;
        while(currentFloor != currFloor || moving == true) wait();
        if (load < 4) {
            load++;
            notifyAll();
        }
    }

    synchronized public void waitingExit(int destFloor) throws InterruptedException {
        waitExit[destFloor]++;
        waitEntry[currentFloor]--;
        notifyAll();
        while(currentFloor != destFloor || moving == true) wait();
        exiting = true;
    }

    synchronized public void exitedLift(int floor) throws InterruptedException {
        while (moving || currentFloor != floor) wait();
        waitExit[currentFloor]--;
        load--;
        exiting = false;
        notifyAll();
    }

    synchronized public int moveLift() throws InterruptedException {
        while (waitingPassenger() || (exiting)) wait();
        moving = true;
        currentFloor += direction;

        if (currentFloor == 0 || currentFloor == 6) {
            direction *= -1;
        }

        notifyAll();

        return currentFloor;
    }

    synchronized public boolean openDoors() throws InterruptedException {
        if (waitingPassenger() && load < 4) {
            moving = false;
            view.openDoors(currentFloor);
            notifyAll();
            return true;
        }
        return false;
    }

    synchronized public void closeDoors() throws InterruptedException {
        while (waitingPassenger()) wait();
        view.closeDoors();
        moving = true;
        notifyAll();
    }

    synchronized public boolean waitingPassenger() {
        if (waitEntry[currentFloor] > 0 || waitExit[currentFloor] > 0) return true;
        return false;
    }
}
