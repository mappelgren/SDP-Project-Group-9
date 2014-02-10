package pc.strategy;

import java.io.IOException;

import pc.comms.BrickCommServer;
import pc.vision.interfaces.WorldStateReceiver;
import pc.world.WorldState;

public class AttackerStrategy implements WorldStateReceiver {

	private BrickCommServer brick;
	private ControlThread controlThread;
	private boolean ballCaught;
	private boolean insideArea;

	public AttackerStrategy(BrickCommServer brick) {
		this.brick = brick;
		controlThread = new ControlThread();
	}

	public void startControlThread() {
		controlThread.start();
	}

	@Override
	public void sendWorldState(WorldState worldState) {
		System.out.println("worked");
		int robotX = worldState.getYellowX()
, 			robotY = worldState.getYellowY();
		double robotO = worldState.getYellowOrientation();
		int targetX = worldState.getBallX(), targetY = worldState
				.getBallY();
System.out.println(robotO);
		if (targetX == 0 || targetY == 0 || robotX == 0 || robotY == 0
				|| robotO == 0
				|| Math.hypot(robotX - targetX, robotY - targetY) < 10) {
			worldState.setMoveR(0);
			synchronized (controlThread) {
				controlThread.rotateBy = 0;
				controlThread.travelDist = 0;
			}
			return;
		}

		double robotRad = Math.toRadians(robotO);
		double targetRad = Math.atan2(targetY - robotY, targetX - robotX);

		if (robotRad > Math.PI)
			robotRad -= 2 * Math.PI;

		double ang1 = targetRad - robotRad;
		while (ang1 > Math.PI)
			ang1 -= 2 * Math.PI;
		while (ang1 < -Math.PI)
			ang1 += 2 * Math.PI;
		ballCaught = false;
		double dist = Math.hypot(robotX - targetX, robotY - targetY);
		System.out.println(dist * 3 + "ang:  " + ang1);
		synchronized (controlThread) {
			controlThread.rotateBy = 0;
			controlThread.travelDist = 0;

			if (Math.abs(ang1) > Math.PI / 16) {
				controlThread.rotateBy =  (int) Math.toDegrees(-ang1 * 0.8);
			}
			else {
				controlThread.travelDist = (int) (dist * 3);
			}
		}
	}

	private class ControlThread extends Thread {
		public int rotateBy = 0;
		public int travelDist = 0;

		public ControlThread() {
			super("Robot control thread");
			setDaemon(true);
		}

		
		@Override
		public void run() {
			try {
				while (true) {
					int travelDist, rotateBy;
					synchronized (this) {
					rotateBy = this.rotateBy;
					travelDist = this.travelDist;
					}
					if (rotateBy != 0) {
//						brick.robotKick(700);
//						brick.robotPrepCatch();
						brick.robotRotateBy(rotateBy);
						
					}
					else if (travelDist > 3) {
						brick.robotTravel(travelDist);
					}
					else if (travelDist < 3) {
						brick.robotCatch();
						ballCaught = true;
					}
					Thread.sleep(400);
					}
//				
//				while (ballCaught)
//				{
//					brick.robotRotateBy(60);
//					brick.robotKick(700);
//					ballCaught = false;
//				}
			}
			catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

	}
	}
}
