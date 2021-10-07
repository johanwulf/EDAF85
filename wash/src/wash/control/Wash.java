package wash.control;
import actor.ActorThread;
import wash.io.WashingIO;
import wash.simulation.WashingSimulator;

public class Wash {

    public static void main(String[] args) throws InterruptedException {
        WashingSimulator sim = new WashingSimulator(Settings.SPEEDUP);
        
        WashingIO io = sim.startSimulation();

        TemperatureController temp = new TemperatureController(io);
        WaterController water = new WaterController(io);
        SpinController spin = new SpinController(io);

        temp.start();
        water.start();
        spin.start();

        ActorThread<WashingMessage> actor = null;

        while (true) {
            int n = io.awaitButton();
            System.out.println("user selected program " + n);

            switch (n) {
                case 0:
                    actor.interrupt();
                    break;
                case 1:
                    actor = new WashingProgram1(io, temp, water, spin);
                    actor.start();
                    break;
                case 2:
                    actor = new WashingProgram2(io, temp, water, spin);
                    actor.start();
                    break;
                case 3:
                    actor = new WashingProgram3(io, temp, water, spin);
                    actor.start();
                    break;
            }
        }
    }
};
