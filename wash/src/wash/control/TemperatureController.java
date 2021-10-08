package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class TemperatureController extends ActorThread<WashingMessage> {

    private WashingIO io;
    private double temp;
    private double uppermargin = 0.678;
    private double lowermargin = 0.0952;
    private int command, count;
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
                            temp = m.getValue();
                            tempreached = false;
                            count = 0;
                            break;
                    }
                }

                if (command == WashingMessage.TEMP_SET) {
                    double upperbound = temp - uppermargin;
                    double lowerbound = temp - 2 + lowermargin;
                    if (io.getTemperature() < lowerbound) {
                        io.heat(true);
                    } else if (io.getTemperature() > upperbound) {
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
