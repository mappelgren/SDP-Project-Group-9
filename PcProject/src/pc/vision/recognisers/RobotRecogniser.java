package pc.vision.recognisers;

import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import pc.vision.PitchConstants;
import pc.vision.PixelInfo;
import pc.vision.Position;
import pc.vision.VideoStream;
import pc.vision.Vision;
import pc.vision.gui.VisionGUI;
import pc.vision.interfaces.ObjectRecogniser;
import pc.world.MovingObject;
import pc.world.WorldState;

public class RobotRecogniser implements ObjectRecogniser {
	private Vision vision;
	private WorldState worldState;
	private PitchConstants pitchConstants;

	public RobotRecogniser(Vision vision, WorldState worldState,
			PitchConstants pitchConstants) {
		this.vision = vision;
		this.worldState = worldState;
		this.pitchConstants = pitchConstants;
	}

	@Override
	public void processFrame(PixelInfo[][] pixels, BufferedImage frame,
			Graphics2D debugGraphics, BufferedImage debugOverlay) {
		Position blueDef, blueAtk, yellowDef, yellowAtk;
		int leftBuffer = this.pitchConstants.getPitchLeft();
		int rightBuffer = frame.getWidth() - leftBuffer
				- this.pitchConstants.getPitchWidth();
		int[] dividers = this.pitchConstants.getDividers();

		// Detect the X,Y coords and rotations of the plates in each section,
		// also look for the points of the ball while looping over the frame to
		// save time
		float blueDefAngle = 0;
		float blueAtkAngle = 0;
		float yellowDefAngle = 0;
		float yellowAtkAngle = 0;;
		boolean leftBlueFirst = false; // TODO: calculate this from the
										// appropriate location
		if (leftBlueFirst) {
			// In order, ltr: Blue Defender, Yellow Attacker, Blue Attacker,
			// Yellow Defender
			blueDef = searchColumn(blueDefAngle, pixels, debugOverlay, leftBuffer,
					dividers[0], true);
			yellowAtk = searchColumn(yellowAtkAngle, pixels, debugOverlay, dividers[0],
					dividers[1], false);
			blueAtk = searchColumn(blueAtkAngle, pixels, debugOverlay, dividers[1],
					dividers[2], true);
			yellowDef = searchColumn(yellowDefAngle, pixels, debugOverlay, dividers[2],
					frame.getWidth() - rightBuffer, false);
			yellowDefAngle = returnAngle;
		} else {
			// In order, ltr: Yellow Defender, Blue Attacker, Yellow Attacker,
			// Blue Defender
			yellowDef = searchColumn(yellowDefAngle, pixels, debugOverlay, leftBuffer,
					dividers[0], false);
			blueAtk = searchColumn(blueAtkAngle, pixels, debugOverlay, dividers[0],
					dividers[1], true);
			yellowAtk = searchColumn(yellowAtkAngle, pixels, debugOverlay, dividers[1],
					dividers[2], false);
			blueDef = searchColumn(blueDefAngle, pixels, debugOverlay, dividers[2],
					frame.getWidth() - rightBuffer, true);
		}

		// Debugging Graphics
		debugGraphics.setColor(Color.CYAN);
		debugGraphics.drawRect(blueDef.getX() - 2, blueDef.getY() - 2, 4, 4);
		debugGraphics.drawRect(blueAtk.getX() - 2, blueAtk.getY() - 2, 4, 4);
		debugGraphics.drawRect(yellowDef.getX() - 2, yellowDef.getY(), 4, 4);
		debugGraphics.drawRect(yellowAtk.getX() - 2, yellowAtk.getY(), 4, 4);

		// TODO: Using the previous position values and the time between frames,
		// calculate the velocities of the robots and the ball.

		// TODO: Update the world state with the new values for position,
		// velocity and rotation.
		// **RELATIVE TO THE ORIGIN FOR POSITION**
		worldState.setBlueX(blueAtk.getX());
		worldState.setBlueY(blueAtk.getY());
		worldState.setBlueOrientation(blueAtkAngle);
		// worldState.setBlueXVelocity(blueAtkVel.getX());
		// worldState.setBlueYVelocity(blueAtkVel.getY());
		worldState.setYellowX(yellowAtk.getX());
		worldState.setYellowY(yellowAtk.getY());
		worldState.setYellowOrientation(yellowAtkAngle);

		// #Dimitar TODO: further code changes needed! the robots need to be
		// correctly
		// identified based on the sections of the field they are in.
		// right now I assume that the yellow is our team and the
		// blue is the enemy team
		MovingObject attackerRobot = new MovingObject(yellowAtk.getX(),
				yellowAtk.getY(), yellowAtkAngle);
		MovingObject defenderRobot = new MovingObject(yellowDef.getX(),
				yellowDef.getY(), yellowDefAngle);

		MovingObject enemyAttackerRobot = new MovingObject(blueAtk.getX(),
				blueAtk.getY(), blueAtkAngle);
		MovingObject enemyDefenderRobot = new MovingObject(blueDef.getX(),
				blueDef.getY(), blueDefAngle);

		worldState.SetAttackerRobot(attackerRobot);
		worldState.SetDefenderRobot(defenderRobot);
		worldState.SetEnemyAttackerRobot(enemyAttackerRobot);
		worldState.SetEnemyDefenderRobot(enemyDefenderRobot);
	}

	/**
	 * Searches a particular column for a plate and updates its points IN PLACE.
	 * Also searches for the pixels that make up the ball, the grey circles on
	 * the plate, and deals with setting the debugOverlay pixels as required.
	 * 
	 * @param colourPoints
	 *            - the ArrayList of points for the coloured section of the
	 *            plate
	 * @param ballPoints
	 *            - the ArrayList of points for the ball
	 * @param frame
	 *            - the current frame of video
	 * @param debugOverlay
	 *            - the image that will be overlayed for debugging
	 * @param leftEdge
	 *            - The x value of the left edge of the section
	 * @param rightEdge
	 *            - The x value of the right edge of the section
	 * @param isBlue
	 *            - True iff we are searching for a blue plate
	 * 
	 * @author Peter Henderson (s1117205)
	 */
	private float returnAngle;
	private Position searchColumn(float returnAngleOld, PixelInfo[][] pixels,
			BufferedImage debugOverlay, int leftEdge, int rightEdge,
			boolean isBlue) {
		ArrayList<Position> points = new ArrayList<Position>(); 
		ArrayList<Position> greenPoints = new ArrayList<Position>();
		int topBuffer = this.pitchConstants.getPitchTop();
		int bottomBuffer = VideoStream.FRAME_HEIGHT - topBuffer
				- this.pitchConstants.getPitchHeight();
		int obj = isBlue ? PitchConstants.OBJECT_BLUE
				: PitchConstants.OBJECT_YELLOW;

		// Find the green plate pixels
		for (int row = topBuffer; row < VideoStream.FRAME_HEIGHT - bottomBuffer; row++) {
			for (int column = leftEdge; column < rightEdge; column++) {
				if (pixels[column][row] != null) {
					if (vision.isColour(pixels[column][row],
							PitchConstants.OBJECT_GREEN)) {
						greenPoints.add(new Position(column, row));
						if (this.pitchConstants
								.debugMode(PitchConstants.OBJECT_GREEN)) {
							debugOverlay.setRGB(column, row, 0xFFFF0099);
						}
					}
				}
			}
		}

		// Green plate bounds.
		Position zeroPoint = new Position(0, 0);
		Position maxX = new Position(0, 0);
		Position maxY = new Position(0, 0);
		Position minX = new Position(VideoStream.FRAME_WIDTH + 1,
				VideoStream.FRAME_HEIGHT + 1);
		Position minY = new Position(VideoStream.FRAME_WIDTH + 1,
				VideoStream.FRAME_HEIGHT + 1);
		Position topLeftCorner = new Position(0, 0);
		Position topRightCorner = new Position(0, 0);
		Position bottomLeftCorner = new Position(0, 0);
		Position bottomRightCorner = new Position(0, 0);
		

		for (Position p : greenPoints) {
			int y = p.getY();
			int x = p.getX();
			if (y <= minY.getY())
				minY = p;
			if (y > maxY.getY())
				maxY = p;
			if (x<= minX.getX())
				minX = p;
			if (x > maxX.getX())
				maxX = p;
		}
		topLeftCorner = new Position(minX.getX() , minY.getY());
		topRightCorner = new Position(maxX.getX(), minY.getY());
		bottomLeftCorner = new Position(minX.getX(), maxY.getY());
		bottomRightCorner = new Position(maxX.getX(), maxY.getY());
		
		minY = topLeftCorner;
		maxY = bottomRightCorner;
		minX = bottomLeftCorner;
		maxX = topRightCorner;
 
		
//		int midX = (maxY.getX() - minY.getX())/2;
//		int midY = (maxY.getY() - minY.getY())/2;
//		Position midPoint = new Position(minY.getX()+midX, minY.getY()+midY);
//		
//		maxX = new Position(midPoint.getX()+midY, midPoint.getY()-midX);
//		minX = new Position(midPoint.getX()-midY, midPoint.getY()+midX);
		

		// Green Plate centroid
		Position greenPlate = vision.calculatePosition(greenPoints);
		int searchRadius = 15; // TODO: Determine if this is the best value.

		// For Debugging
		debugOverlay.getGraphics().drawOval(greenPlate.getX()-searchRadius, greenPlate.getY()-searchRadius, searchRadius*2, searchRadius*2);

		// Find the yellow/blue coloured pixels within the plate bounds.
		int cumulativeGreyX = 0;
		int cumulativeGreyY = 0;
		int numGreyPoints = 0;
		int gx = greenPlate.getX();
		int gy = greenPlate.getY();
		int r2 = searchRadius*searchRadius;
		for (int row = topBuffer; row < VideoStream.FRAME_HEIGHT - bottomBuffer; row++) {
			for (int column = leftEdge; column < rightEdge; column++) {
				int squareDist = ((gx-column)*(gx-column)) + ((gy-row)*(gy-row));
				if (squareDist < r2){
					if (pixels[column][row] != null) {
						if (vision.isColour(pixels[column][row], obj)) {
							points.add(new Position(column, row));
							if (this.pitchConstants.debugMode(obj)) {
								debugOverlay.setRGB(column, row, 0xFFFF0099);
							}
						} else if (vision.isColour(pixels[column][row], PitchConstants.OBJECT_GREY)){
							cumulativeGreyX += column;
							cumulativeGreyY += row;
							numGreyPoints++;
							if (this.pitchConstants.debugMode(PitchConstants.OBJECT_GREY)) {
								debugOverlay.setRGB(column, row, 0xFFFF0099);
							}
						}
					}
				}
			}
		}
		
		Position pos = vision.calculatePosition(points);
		
		if (numGreyPoints > 0) {
			double greyXMean = 1.0 * cumulativeGreyX / numGreyPoints;
			double greyYMean = 1.0 * cumulativeGreyY / numGreyPoints;

			// Debugging Code
			debugOverlay.getGraphics().drawRect((int) greyXMean - 2,
					(int) greyYMean - 2, 4, 4);

			float angle = (float) Math.toDegrees(Math.atan2(pos.getY()
					- greyYMean, pos.getX() - greyXMean));

			returnAngle =  (angle < 0) ? (angle + 360) : angle;
		} else {
			returnAngle = 0;
		}

		// Calculate angles from those.

		return pos;
	}

}
