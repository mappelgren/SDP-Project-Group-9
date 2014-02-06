package pc.vision.recognisers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import pc.vision.PitchConstants;
import pc.vision.PixelInfo;
import pc.vision.Position;
import pc.vision.VideoStream;
import pc.vision.Vision;
import pc.vision.interfaces.ObjectRecogniser;
import pc.world.MovingObject;
import pc.world.WorldState;

public class BallRecogniser implements ObjectRecogniser {
	private Vision vision;
	private WorldState worldState;
	private PitchConstants pitchConstants;

	public BallRecogniser(Vision vision, WorldState worldState,
			PitchConstants pitchConstants) {
		this.vision = vision;
		this.worldState = worldState;
		this.pitchConstants = pitchConstants;
	}

	@Override
	public void processFrame(PixelInfo[][] pixels, BufferedImage frame, Graphics2D debugGraphics,
			BufferedImage debugOverlay) {
		ArrayList<Position> ballPoints = new ArrayList<Position>();
		int top = this.pitchConstants.getTopBuffer();
		int left = this.pitchConstants.getLeftBuffer();
		int right = VideoStream.FRAME_WIDTH - this.pitchConstants.getRightBuffer();
		int bottom = VideoStream.FRAME_HEIGHT - this.pitchConstants.getBottomBuffer();
		
		for (int row = top; row < bottom; row++){
			for (int column = left; column < right; column++){				
				PixelInfo p = pixels[column][row];
				if (p != null){				
					if (vision.isColour(pixels[column][row], PitchConstants.BALL)){
						ballPoints.add(new Position(column,row));
						if (this.pitchConstants.debugMode(PitchConstants.BALL)) {
							debugOverlay.setRGB(column, row, 0xFF000000);
						}
					}
				}
			}
		}
		
		Position ball = vision.calculatePosition(ballPoints);

		debugGraphics.setColor(Color.red);
		debugGraphics.drawLine(0, ball.getY(), 640, ball.getY());
		debugGraphics.drawLine(ball.getX(), 0, ball.getX(), 480);
		
		worldState.setBallX(ball.getX());
		worldState.setBallY(ball.getY());

		MovingObject ball_m =  new MovingObject(ball.getX(), ball.getY());
		worldState.SetBall(ball_m);
	}

}
