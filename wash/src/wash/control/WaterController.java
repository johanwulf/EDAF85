package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class WaterController extends ActorThread<WashingMessage> {

    private WashingIO io;
    private double water;
    private int command;
    private double inputFlow = 0.1;
    private double outputFlow = 0.2;
    private boolean drained;
    private ActorThread<WashingMessage> sender;

    public WaterController(WashingIO io) {
        this.io = io;
        this.water = 10;
    }

    @Override
    public void run() {
        try {
            while (true) {
                WashingMessage m = receiveWithTimeout(20000 / Settings.SPEEDUP);
                
                if (m != null) {
                    command = m.getCommand();
                    sender = m.getSender();

                    switch (command) {
                        case WashingMessage.WATER_IDLE:
                            io.drain(false);
                            io.fill(false);
                            break;
                        case WashingMessage.WATER_FILL:
                            water = m.getValue();
                            io.drain(false);
                            io.fill(true);
                            break;
                        case WashingMessage.WATER_DRAIN:
                            drained = false;
                            water = 0;
                            io.fill(false);
                            io.drain(true);
                            break;
                    }
                }

                if (command == WashingMessage.WATER_FILL) {
                    if (io.getWaterLevel() + inputFlow*2 > water) {
                        io.fill(false);
                        sender.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
                    }
                } else if (command == WashingMessage.WATER_DRAIN) {
                    if (io.getWaterLevel() - outputFlow*2 < water && !drained) {
                        drained = true;
                        sender.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
                    }
                }

            }
        } catch (Exception unexpected) {
            throw new Error(unexpected);
        }
    }
}
