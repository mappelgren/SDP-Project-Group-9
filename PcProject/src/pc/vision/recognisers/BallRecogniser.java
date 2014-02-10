package pc.vision.recognisers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import pc.vision.PitchConstants;
import pc.vision.PixelInfo;
import pc.vision.Position;
import pc.vision.Vision;
import pc.vision.interfaces.ObjectRecogniser;
import pc.world.MovingObject;
import pc.world.WorldState;

public class BallRecogniser implements ObjectRecogniser {
	private Vision vision;
	private WorldState worldState;
	private PitchConstants pitchConstants;
	private Position previousBallPosition = new Position(0,0);

	public BallRecogniser(Vision vision, WorldState worldState,
			PitchConstants pitchConstants) {
		this.vision = vision;
		this.worldState = worldState;
		this.pitchConstants = pitchConstants;
	}

	@Override
	public void processFrame(PixelInfo[][] pixels, BufferedImage frame,
			Graphics2D debugGraphics, BufferedImage debugOverlay) {
		ArrayList<Position> ballPoints = new ArrayList<Position>();
		int top = this.pitchConstants.getPitchTop();
		int left = this.pitchConstants.getPitchLeft();
		int right = left + this.pitchConstants.getPitchWidth();
		int bottom = top + this.pitchConstants.getPitchHeight();

		for (int row = top; row < bottom; row++) {
			for (int column = left; column < right; column++) {
				PixelInfo p = pixels[column][row];
				if (p != null) {
					if (vision.isColour(pixels[column][row],
							PitchConstants.OBJECT_BALL)) {
						ballPoints.add(new Position(column, row));
						if (this.pitchConstants
								.debugMode(PitchConstants.OBJECT_BALL)) {
							debugOverlay.setRGB(column, row, 0xFF000000);
						}
					}
				}
			}
		}

		Position ballPosition = vision.calculatePosition(ballPoints);

		debugGraphics.setColor(Color.red);
		debugGraphics.drawLine(0, ballPosition.getY(), 640, ballPosition.getY());
		debugGraphics.drawLine(ballPosition.getX(), 0, ballPosition.getX(), 480);

		worldState.setBallX(ballPosition.getX());
		worldState.setBallY(ballPosition.getY());

		previousBallPosition = ballPosition;
		MovingObject ball_m = new MovingObject(ballPosition.getX(), ballPosition.getY());
		worldState.SetBall(ball_m);
	}

}
