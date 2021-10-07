package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class TemperatureController extends ActorThread<WashingMessage> {

    private WashingIO io;
    private double upperbound, lowerbound, temperature;
    private double uppermargin = 0.678;
    private double lowermargin = 0.0952;
    private int command;
    private int count = 0;
    private boolean tempreached = false;
    private ActorThread<WashingMessage> sender;


    public TemperatureController(WashingIO io) {
        this.io = io;
    }

    @Override
    public void run() {
        try {
            while (true) {
                WashingMessage m = receiveWithTimeout(10000 / Settings.SPEEDUP);

                if (m != null) {
                    command = m.getCommand();
                    sender = m.getSender();
                    switch (command) {
                        case WashingMessage.TEMP_IDLE:
                            io.heat(false);
                            m.getSender().send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
                            break;
                        case WashingMessage.TEMP_SET:
                            count = 0;
                            temperature = io.getTemperature();
                            upperbound = m.getValue();
                            lowerbound = m.getValue()-2;
                            tempreached = false;
                            break;
                    }
                }

                if (command == WashingMessage.TEMP_SET) {
                    if (io.getTemperature() < lowerbound + lowermargin) {
                        io.heat(true);

                        if (tempreached) {
                            tempreached = false;
                        }
                    } else if (io.getTemperature() > upperbound - uppermargin) {
                        io.heat(false);

                        if (!tempreached) {
                            tempreached = true;
                        }
                    }
                }

                if (tempreached && count == 0) {
                    count++;
                    sender.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
                }  
            }
        } catch (Exception unexpected) {

        }
    }
}
