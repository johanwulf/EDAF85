package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

/**
 * Program 3 for washing machine. This also serves as an example of how washing
 * programs can be structured.
 * 
 * This short program stops all regulation of temperature and water levels,
 * stops the barrel from spinning, and drains the machine of water.
 * 
 * It can be used after an emergency stop (program 0) or a power failure.
 */
public class WashingProgram1 extends ActorThread<WashingMessage> {

    private WashingIO io;
    private ActorThread<WashingMessage> temp;
    private ActorThread<WashingMessage> water;
    private ActorThread<WashingMessage> spin;
    
    public WashingProgram1(WashingIO io,
                           ActorThread<WashingMessage> temp,
                           ActorThread<WashingMessage> water,
                           ActorThread<WashingMessage> spin) 
    {
        this.io = io;
        this.temp = temp;
        this.water = water;
        this.spin = spin;
    }
    
    @Override
    public void run() {
        try {
            io.lock(true);
            System.out.println("washing program 1 started");

            water.send(new WashingMessage(this, WashingMessage.WATER_FILL, 10));
            receive();
            water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));

            temp.send(new WashingMessage(this, WashingMessage.TEMP_SET, 40));
            receive();

            spin.send(new WashingMessage(this, WashingMessage.SPIN_SLOW));
            receive();
            
            // Spin for five simulated minutes
            Thread.sleep(30*60000 / Settings.SPEEDUP);
            System.out.println("washing done");

            temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
            receive();

            water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
            receive();

            for (int i = 0; i < 5; i++) {
                water.send(new WashingMessage(this, WashingMessage.WATER_FILL, 10));
                receive();
                water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
                Thread.sleep(120*1000/Settings.SPEEDUP);
                water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
                receive();
            }
            
            spin.send(new WashingMessage(this, WashingMessage.SPIN_FAST));
            receive();
            System.out.println("spinning");
            Thread.sleep(300*1000 / Settings.SPEEDUP);
            water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
            
            System.out.println("setting SPIN_OFF");
            spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
            receive();
            
            io.lock(false);
            
            System.out.println("washing program 3 finished");
        } catch (InterruptedException e) {
            temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
            water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
            spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
            System.out.println("washing program terminated");
        }
    }
}
