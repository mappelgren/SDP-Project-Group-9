package pc.strategy;

import java.util.ArrayList;

import lejos.pc.comm.NXTCommException;
import pc.comms.BrickCommServer;
import pc.comms.BtInfo;
import pc.strategy.interfaces.Strategy;
import pc.vision.Vision;

public class StrategyController {
	
	public enum StrategyType{
		PASSING, ATTACKING, DEFENDING
	}
	
	public BrickCommServer bcsAttacker, bcsMeow;
	
	private Vision vision;
	private ArrayList<Strategy> currentStrategies = new ArrayList<Strategy>();
	
	public StrategyController(Vision vision){
		this.vision = vision;
		
		bcsAttacker = null;
		bcsMeow = null;
		try {
			bcsAttacker = new BrickCommServer();
			bcsAttacker.guiConnect(BtInfo.group10);
			bcsMeow = new BrickCommServer();
			bcsMeow.guiConnect(BtInfo.MEOW);
		} catch (NXTCommException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Change to a particular strategy, removing and stopping the previously running strategy(s).
	 * 
	 * @param type - The strategy type to run
	 */
	public void changeToStrategy(StrategyType type){
		// Stop old threads
		for (Strategy s : this.currentStrategies){
			s.stopControlThread();
			vision.removeWorldStateReciver(s);
		}
		
		//TODO: Make sure passing strategies don't need pitch constants. Use only world state.
		switch(type){
		case PASSING:
			Strategy ps = new PassingStrategy(bcsAttacker,bcsMeow,null);
			currentStrategies.add(ps);
			vision.addWorldStateReceiver(ps);
			ps.startControlThread();
			break;
		case ATTACKING:
			Strategy as = new AttackerStrategy(bcsAttacker,null);
			currentStrategies.add(as);
			vision.addWorldStateReceiver(as);
			as.startControlThread();
			break;
		case DEFENDING:
			Strategy ds = new InterceptorStrategy(bcsMeow);
			currentStrategies.add(ds);
			vision.addWorldStateReceiver(ds);
			ds.startControlThread();
			break;
		}
		
	}
	
}
