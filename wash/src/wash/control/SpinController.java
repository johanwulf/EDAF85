package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class SpinController extends ActorThread<WashingMessage> {

    private WashingIO io;
    private int spin, command;

    public SpinController(WashingIO io) {
        this.io = io;
        this.spin = WashingIO.SPIN_LEFT;
        this.command = WashingIO.SPIN_IDLE;
    }

    @Override
    public void run() {
        try {
            while (true) {
                WashingMessage m = receiveWithTimeout(60000 / Settings.SPEEDUP);

                if (m != null) {
                    command = m.getCommand();
                    int mode = (command == WashingMessage.SPIN_FAST) ? WashingIO.SPIN_FAST : command;
                    io.setSpinMode(mode);
                    m.getSender().send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
                } 

                if (command == WashingMessage.SPIN_SLOW) {
                    spin = (spin == WashingIO.SPIN_LEFT) ? WashingIO.SPIN_RIGHT : WashingIO.SPIN_LEFT;
                    io.setSpinMode(spin);
                }
            }
        } catch (InterruptedException unexpected) {
            throw new Error(unexpected);
        }
    }
}
