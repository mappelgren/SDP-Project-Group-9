package pc.vision;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Observable;
import java.util.Scanner;

/**
 * A class that holds the constants for various values about the pitch, such as
 * thresholding values and dimension variables.
 * 
 * @author Alex Adams (s1046358), Peter Henderson (s1117205)
 */
public class PitchConstants extends Observable {
	/** The number of objects there are thresholds for */
	public static final int NUM_THRESHOLDS = 5;

	/** The threshold index associated with the ball */
	public static final int BALL = 0;
	/** The threshold index associated with the blue robot */
	public static final int BLUE = 1;
	/** The threshold index associated with the yellow robot */
	public static final int YELLOW = 2;
	/** The threshold index associated with the grey circles */
	public static final int GREY = 3;
	/** The threshold index associated with the green plate */
	public static final int GREEN = 4;
	/** Names of threshold objects */
	public static final String[] THRESHOLD_NAMES = { "Ball", "Blue plate",
			"Yellow plate", "Grey dot on the plate", "Green plate" };

	/** The minimum value for the red, green, and blue colour components */
	public static final int RGBMIN = 0;
	/** The maximum value for the red, green, and blue colour components */
	public static final int RGBMAX = 255;
	/** The minimum value for the hue, saturation, and value colour components */
	public static final float HSVMIN = 0.0f;
	/** The maximum value for the hue, saturation, and value colour components */
	public static final float HSVMAX = 1.0f;

	// The pitch number. 0 is the main pitch, 1 is the side pitch
	private int pitchNum;

	// Threshold upper and lower values
	private int[] redLower = new int[NUM_THRESHOLDS];
	private int[] redUpper = new int[NUM_THRESHOLDS];
	private boolean[] redInverted = new boolean[NUM_THRESHOLDS];
	private int[] greenLower = new int[NUM_THRESHOLDS];
	private int[] greenUpper = new int[NUM_THRESHOLDS];
	private boolean[] greenInverted = new boolean[NUM_THRESHOLDS];
	private int[] blueLower = new int[NUM_THRESHOLDS];
	private int[] blueUpper = new int[NUM_THRESHOLDS];
	private boolean[] blueInverted = new boolean[NUM_THRESHOLDS];
	private float[] hueLower = new float[NUM_THRESHOLDS];
	private float[] hueUpper = new float[NUM_THRESHOLDS];
	private boolean[] hueInverted = new boolean[NUM_THRESHOLDS];
	private float[] saturationLower = new float[NUM_THRESHOLDS];
	private float[] saturationUpper = new float[NUM_THRESHOLDS];
	private boolean[] saturationInverted = new boolean[NUM_THRESHOLDS];
	private float[] valueLower = new float[NUM_THRESHOLDS];
	private float[] valueUpper = new float[NUM_THRESHOLDS];
	private boolean[] valueInverted = new boolean[NUM_THRESHOLDS];
	// Debug
	private boolean[] debug = new boolean[NUM_THRESHOLDS];

	// Pitch dimensions
	// When scanning the pitch we look at pixels starting from 0 + topBuffer and
	// 0 + leftBuffer, and then scan to pixels at 480 - bottomBuffer and 640 -
	// rightBuffer.
	private int topBuffer;
	private int bottomBuffer;
	private int leftBuffer;
	private int rightBuffer;

	// Holds the x values of the pitch divisions. Used when detecting the plates
	// on the board.
	private int[] dividers = new int[3];

	public int[] getDividers() {
		return this.dividers;
	}

	public void setDividers(int[] dividers) {
		if (dividers.length != 3) {
			System.err.println("Dividers array not the right size to set!");
		} else {
			if (!Arrays.equals(this.dividers, dividers)) {
				this.dividers = dividers;
				setChanged();
			}
			notifyObservers();
		}
	}

	/**
	 * Default constructor.
	 * 
	 * @param pitchNum
	 *            The pitch that we are on.
	 */
	public PitchConstants(int pitchNum) {
		for (int i = 0; i < NUM_THRESHOLDS; ++i)
			this.debug[i] = false;
		// Just call the setPitchNum method to load in the constants
		setPitchNum(pitchNum);
	}

	/**
	 * Gets the lower threshold value for red for the object specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @return The lower red threshold value
	 */
	public int getRedLower(int i) {
		return this.redLower[i];
	}

	/**
	 * Sets the lower threshold value for red for the object specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @param lower
	 *            The value to set the threshold to
	 */
	public void setRedLower(int i, int lower) {
		if (this.redLower[i] != lower) {
			this.redLower[i] = lower;
			setChanged();
			notifyObservers();
		}
	}

	/**
	 * Gets the upper threshold value for red for the object specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @return The upper red threshold value
	 */
	public int getRedUpper(int i) {
		return this.redUpper[i];
	}

	/**
	 * Sets the upper threshold value for red for the object specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @param upper
	 *            The value to set the threshold to
	 */
	public void setRedUpper(int i, int upper) {
		if (this.redUpper[i] != upper) {
			this.redUpper[i] = upper;
			setChanged();
			notifyObservers();
		}
	}

	/**
	 * Tests whether the thresholds are inverted for red for the object
	 * specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @return true if inverted, false otherwise
	 */
	public boolean isRedInverted(int i) {
		return this.redInverted[i];
	}

	/**
	 * Sets whether the thresholds are inverted for red for the object specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @param inverted
	 *            true if red should be inverted, false otherwise
	 */
	public void setRedInverted(int i, boolean inverted) {
		if (this.redInverted[i] != inverted) {
			this.redInverted[i] = inverted;
			setChanged();
			notifyObservers();
		}
	}

	public void setRed(int i, int lower, int upper, boolean inverted) {
		if (this.redLower[i] != lower || this.redUpper[i] != upper
				|| this.redInverted[i] != inverted) {
			this.redLower[i] = lower;
			this.redUpper[i] = upper;
			this.redInverted[i] = inverted;
			setChanged();
			notifyObservers();
		}
	}

	/**
	 * Gets the lower threshold value for green for the object specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @return The lower green threshold value
	 */
	public int getGreenLower(int i) {
		return this.greenLower[i];
	}

	/**
	 * Sets the lower threshold value for green for the object specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @param lower
	 *            The value to set the threshold to
	 */
	public void setGreenLower(int i, int lower) {
		this.greenLower[i] = lower;
		setChanged();
		notifyObservers();
	}

	/**
	 * Gets the upper threshold value for green for the object specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @return The upper green threshold value
	 */
	public int getGreenUpper(int i) {
		return this.greenUpper[i];
	}

	/**
	 * Sets the upper threshold value for green for the object specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @param upper
	 *            The value to set the threshold to
	 */
	public void setGreenUpper(int i, int upper) {
		this.greenUpper[i] = upper;
		setChanged();
		notifyObservers();
	}

	/**
	 * Tests whether the thresholds are inverted for green for the object
	 * specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @return true if inverted, false otherwise
	 */
	public boolean isGreenInverted(int i) {
		return this.greenInverted[i];
	}

	/**
	 * Sets whether the thresholds are inverted for green for the object
	 * specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @param inverted
	 *            true if green should be inverted, false otherwise
	 */
	public void setGreenInverted(int i, boolean inverted) {
		this.greenInverted[i] = inverted;
		setChanged();
		notifyObservers();
	}

	public void setGreen(int i, int lower, int upper, boolean inverted) {
		if (this.greenLower[i] != lower || this.greenUpper[i] != upper
				|| this.greenInverted[i] != inverted) {
			this.greenLower[i] = lower;
			this.greenUpper[i] = upper;
			this.greenInverted[i] = inverted;
			setChanged();
			notifyObservers();
		}
	}

	/**
	 * Gets the lower threshold value for blue for the object specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @return The lower blue threshold value
	 */
	public int getBlueLower(int i) {
		return this.blueLower[i];
	}

	/**
	 * Sets the lower threshold value for blue for the object specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @param lower
	 *            The value to set the threshold to
	 */
	public void setBlueLower(int i, int lower) {
		this.blueLower[i] = lower;
		setChanged();
		notifyObservers();
	}

	/**
	 * Gets the upper threshold value for blue for the object specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @return The upper blue threshold value
	 */
	public int getBlueUpper(int i) {
		return this.blueUpper[i];
	}

	/**
	 * Sets the upper threshold value for blue for the object specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @param upper
	 *            The value to set the threshold to
	 */
	public void setBlueUpper(int i, int upper) {
		this.blueUpper[i] = upper;
		setChanged();
		notifyObservers();
	}

	/**
	 * Tests whether the thresholds are inverted for blue for the object
	 * specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @return true if inverted, false otherwise
	 */
	public boolean isBlueInverted(int i) {
		return this.blueInverted[i];
	}

	/**
	 * Sets whether the thresholds are inverted for blue for the object
	 * specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @param inverted
	 *            true if blue should be inverted, false otherwise
	 */
	public void setBlueInverted(int i, boolean inverted) {
		this.blueInverted[i] = inverted;
		setChanged();
		notifyObservers();
	}

	public void setBlue(int i, int lower, int upper, boolean inverted) {
		if (this.blueLower[i] != lower || this.blueUpper[i] != upper
				|| this.blueInverted[i] != inverted) {
			this.blueLower[i] = lower;
			this.blueUpper[i] = upper;
			this.blueInverted[i] = inverted;
			setChanged();
			notifyObservers();
		}
	}

	/**
	 * Gets the lower threshold value for hue for the object specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @return The lower hue threshold value
	 */
	public float getHueLower(int i) {
		return this.hueLower[i];
	}

	/**
	 * Sets the lower threshold value for hue for the object specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @param lower
	 *            The value to set the threshold to
	 */
	public void setHueLower(int i, float lower) {
		this.hueLower[i] = lower;
		setChanged();
		notifyObservers();
	}

	/**
	 * Gets the upper threshold value for hue for the object specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @return The upper hue threshold value
	 */
	public float getHueUpper(int i) {
		return this.hueUpper[i];
	}

	/**
	 * Sets the upper threshold value for hue for the object specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @param upper
	 *            The value to set the threshold to
	 */
	public void setHueUpper(int i, float upper) {
		this.hueUpper[i] = upper;
		setChanged();
		notifyObservers();
	}

	/**
	 * Tests whether the thresholds are inverted for hue for the object
	 * specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @return true if inverted, false otherwise
	 */
	public boolean isHueInverted(int i) {
		return this.hueInverted[i];
	}

	/**
	 * Sets whether the thresholds are inverted for hue for the object specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @param inverted
	 *            true if hue should be inverted, false otherwise
	 */
	public void setHueInverted(int i, boolean inverted) {
		this.hueInverted[i] = inverted;
		setChanged();
		notifyObservers();
	}

	public void setHue(int i, float lower, float upper, boolean inverted) {
		if (this.hueLower[i] != lower || this.hueUpper[i] != upper
				|| this.hueInverted[i] != inverted) {
			this.hueLower[i] = lower;
			this.hueUpper[i] = upper;
			this.hueInverted[i] = inverted;
			setChanged();
			notifyObservers();
		}
	}

	/**
	 * Gets the lower threshold value for colour saturation for the object
	 * specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @return The lower colour saturation threshold value
	 */
	public float getSaturationLower(int i) {
		return this.saturationLower[i];
	}

	/**
	 * Sets the lower threshold value for colour saturation for the object
	 * specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @param lower
	 *            The value to set the threshold to
	 */
	public void setSaturationLower(int i, float lower) {
		this.saturationLower[i] = lower;
		setChanged();
		notifyObservers();
	}

	/**
	 * Gets the upper threshold value for colour saturation for the object
	 * specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @return The upper colour saturation threshold value
	 */
	public float getSaturationUpper(int i) {
		return this.saturationUpper[i];
	}

	/**
	 * Sets the upper threshold value for colour saturation for the object
	 * specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @param upper
	 *            The value to set the threshold to
	 */
	public void setSaturationUpper(int i, float upper) {
		this.saturationUpper[i] = upper;
		setChanged();
		notifyObservers();
	}

	/**
	 * Tests whether the thresholds are inverted for saturation for the object
	 * specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @return true if inverted, false otherwise
	 */
	public boolean isSaturationInverted(int i) {
		return this.saturationInverted[i];
	}

	/**
	 * Sets whether the thresholds are inverted for saturation for the object
	 * specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @param inverted
	 *            true if saturation should be inverted, false otherwise
	 */
	public void setSaturationInverted(int i, boolean inverted) {
		this.saturationInverted[i] = inverted;
		setChanged();
		notifyObservers();
	}

	public void setSaturation(int i, float lower, float upper, boolean inverted) {
		if (this.saturationLower[i] != lower
				|| this.saturationUpper[i] != upper
				|| this.saturationInverted[i] != inverted) {
			this.saturationLower[i] = lower;
			this.saturationUpper[i] = upper;
			this.saturationInverted[i] = inverted;
			setChanged();
			notifyObservers();
		}
	}

	/**
	 * Gets the lower threshold value for colour value for the object specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @return The lower colour value threshold value
	 */
	public float getValueLower(int i) {
		return this.valueLower[i];
	}

	/**
	 * Sets the lower threshold value for colour value for the object specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @param lower
	 *            The value to set the threshold to
	 */
	public void setValueLower(int i, float lower) {
		this.valueLower[i] = lower;
		setChanged();
		notifyObservers();
	}

	/**
	 * Gets the upper threshold value for colour value for the object specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @return The upper colour value threshold value
	 */
	public float getValueUpper(int i) {
		return this.valueUpper[i];
	}

	/**
	 * Sets the upper threshold value for colour value for the object specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @param upper
	 *            The value to set the threshold to
	 */
	public void setValueUpper(int i, float upper) {
		this.valueUpper[i] = upper;
		setChanged();
		notifyObservers();
	}

	/**
	 * Tests whether the thresholds are inverted for value for the object
	 * specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @return true if inverted, false otherwise
	 */
	public boolean isValueInverted(int i) {
		return this.valueInverted[i];
	}

	/**
	 * Sets whether the thresholds are inverted for value for the object
	 * specified
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @param inverted
	 *            true if value should be inverted, false otherwise
	 */
	public void setValueInverted(int i, boolean inverted) {
		this.valueInverted[i] = inverted;
		setChanged();
		notifyObservers();
	}

	public void setValue(int i, float lower, float upper, boolean inverted) {
		if (this.valueLower[i] != lower || this.valueUpper[i] != upper
				|| this.valueInverted[i] != inverted) {
			this.valueLower[i] = lower;
			this.valueUpper[i] = upper;
			this.valueInverted[i] = inverted;
			setChanged();
			notifyObservers();
		}
	}

	/**
	 * Gets the width of the pitch
	 * 
	 * @return the width of the pitch in pixels
	 */
	public int getPitchWidth() {
		return (640 - this.rightBuffer - this.leftBuffer);
	}

	/**
	 * Gets the height of the pitch
	 * 
	 * @return the height of the pitch in pixels
	 */
	public int getPitchHeight() {
		return (480 - this.bottomBuffer - this.topBuffer);
	}

	/**
	 * Gets the top buffer for the pitch
	 * 
	 * @return the distance from the top of the pitch to the top of the image
	 *         produced by the video device
	 */
	public int getTopBuffer() {
		return this.topBuffer;
	}

	/**
	 * Sets the top buffer for the pitch
	 * 
	 * @param topBuffer
	 *            The new value for the top buffer
	 */
	public void setTopBuffer(int topBuffer) {
		this.topBuffer = topBuffer;
		setChanged();
		notifyObservers();
	}

	/**
	 * Gets the bottom buffer for the pitch
	 * 
	 * @return the distance from the bottom of the pitch to the bottom of the
	 *         image produced by the video device
	 */
	public int getBottomBuffer() {
		return this.bottomBuffer;
	}

	/**
	 * Sets the bottom buffer for the pitch
	 * 
	 * @param bottomBuffer
	 *            The new value for the bottom buffer
	 */
	public void setBottomBuffer(int bottomBuffer) {
		this.bottomBuffer = bottomBuffer;
		setChanged();
		notifyObservers();
	}

	/**
	 * Gets the left buffer for the pitch
	 * 
	 * @return the distance from the left of the pitch to the left of the image
	 *         produced by the video device
	 */
	public int getLeftBuffer() {
		return this.leftBuffer;
	}

	/**
	 * Sets the left buffer for the pitch
	 * 
	 * @param leftBuffer
	 *            The new value for the left buffer
	 */
	public void setLeftBuffer(int leftBuffer) {
		this.leftBuffer = leftBuffer;
		setChanged();
		notifyObservers();
	}

	/**
	 * Gets the right buffer for the pitch
	 * 
	 * @return the distance from the right of the pitch to the right of the
	 *         image produced by the video device
	 */
	public int getRightBuffer() {
		return this.rightBuffer;
	}

	/**
	 * Sets the right buffer for the pitch
	 * 
	 * @param topBuffer
	 *            The new value for the right buffer
	 */
	public void setRightBuffer(int rightBuffer) {
		this.rightBuffer = rightBuffer;
		setChanged();
		notifyObservers();
	}

	/**
	 * Tests whether debug mode is enabled for the threshold set i refers to
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @return true if debug mode is enabled, false otherwise
	 */
	public boolean debugMode(int i) {
		return this.debug[i];
	}

	/**
	 * Enables or disables debug mode for the threshold set i refers to. This
	 * method permits multiple debug modes to be enabled
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @param debug
	 *            A boolean value to enable debug mode if true, and disable
	 *            otherwise
	 */
	public void setDebugMode(int i, boolean debug) {
		if (this.debug[i] != debug) {
			this.debug[i] = debug;
			setChanged();
			notifyObservers();
		}
	}

	/**
	 * Enables or disables debug mode for the threshold set i refers to. This
	 * method permits multiple debug modes to be enabled only if allowMultiple
	 * is set to true.
	 * 
	 * @param i
	 *            One of: BALL, BLUE, YELLOW, GREY, GREEN - other values will
	 *            cause an ArrayIndexOutOfBoundsException
	 * @param debug
	 *            A boolean value to enable debug mode if true, and disable
	 *            otherwise
	 * @param allowMultiple
	 *            A boolean value specifying whether to allow multiple debug
	 *            modes to be set
	 */
	public void setDebugMode(int i, boolean debug, boolean allowMultiple) {
		if (allowMultiple)
			setDebugMode(i, debug);
		else {
			for (int j = 0; j < 5; ++j)
				setDebugMode(j, (i == j) && debug);
		}
	}

	/**
	 * Gets the current pitch number
	 * 
	 * @return The pitch number
	 */
	public int getPitchNum() {
		return this.pitchNum;
	}

	/**
	 * Sets a new pitch number, loading in constants from the corresponding
	 * file.
	 * 
	 * @param newPitchNum
	 *            The pitch number to use.
	 */
	public void setPitchNum(int newPitchNum) {
		assert (newPitchNum == 0 || newPitchNum == 1) : "Invalid pitch number";
		this.pitchNum = newPitchNum;

		loadConstants(System.getProperty("user.dir") + "/constants/pitch"
				+ this.pitchNum);

		setChanged();
		notifyObservers();
	}

	public void saveConstants(int pitchNumber) {
		saveConstants(String.valueOf(pitchNumber));
	}

	/**
	 * Save the constants to a file.
	 * 
	 * @param fileName
	 *            The file to save the constants to
	 */
	public void saveConstants(String fileName) {
		try {
			// Update the pitch dimensions file
			FileWriter pitchDimFile = new FileWriter(new File("constants/pitch"
					+ this.pitchNum + "Dimensions"));
			pitchDimFile.write(String.valueOf(getTopBuffer()) + "\n");
			pitchDimFile.write(String.valueOf(getBottomBuffer()) + "\n");
			pitchDimFile.write(String.valueOf(getLeftBuffer()) + "\n");
			pitchDimFile.write(String.valueOf(getRightBuffer()) + "\n");
			pitchDimFile.write(String.valueOf(this.dividers[0]) + "\n");
			pitchDimFile.write(String.valueOf(this.dividers[1]) + "\n");
			pitchDimFile.write(String.valueOf(this.dividers[2]) + "\n");
			pitchDimFile.close();

			FileWriter pitchFile = new FileWriter(new File("constants/pitch"
					+ this.pitchNum));

			// Iterate over the ball, blue robot, yellow robot, grey circles,
			// and green plates in the order they're defined above.
			for (int i = 0; i < NUM_THRESHOLDS; ++i) {
				pitchFile.write(String.valueOf(getRedLower(i)) + "\n");
				pitchFile.write(String.valueOf(getRedUpper(i)) + "\n");
				pitchFile.write(String.valueOf(isRedInverted(i)) + "\n");

				pitchFile.write(String.valueOf(getGreenLower(i)) + "\n");
				pitchFile.write(String.valueOf(getGreenUpper(i)) + "\n");
				pitchFile.write(String.valueOf(isGreenInverted(i)) + "\n");

				pitchFile.write(String.valueOf(getBlueLower(i)) + "\n");
				pitchFile.write(String.valueOf(getBlueUpper(i)) + "\n");
				pitchFile.write(String.valueOf(isBlueInverted(i)) + "\n");

				pitchFile.write(String.valueOf(getHueLower(i)) + "\n");
				pitchFile.write(String.valueOf(getHueUpper(i)) + "\n");
				pitchFile.write(String.valueOf(isHueInverted(i)) + "\n");

				pitchFile.write(String.valueOf(getSaturationLower(i)) + "\n");
				pitchFile.write(String.valueOf(getSaturationUpper(i)) + "\n");
				pitchFile.write(String.valueOf(isSaturationInverted(i)) + "\n");

				pitchFile.write(String.valueOf(getValueLower(i)) + "\n");
				pitchFile.write(String.valueOf(getValueUpper(i)) + "\n");
				pitchFile.write(String.valueOf(isValueInverted(i)) + "\n");
			}
			pitchFile.close();

			System.out.println("Wrote successfully!");
		} catch (IOException e) {
			System.err.println("Cannot save constants file " + fileName + ":");
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Load in the constants from a file. Note that this assumes that the
	 * constants file is well formed.
	 * 
	 * @param fileName
	 *            The file to load the constants from.
	 */
	private void loadConstants(String fileName) {
		Scanner scannerDim;

		try {
			scannerDim = new Scanner(new File(fileName + "Dimensions"));
			assert (scannerDim != null);

			// Pitch Dimensions
			this.topBuffer = scannerDim.nextInt();
			this.bottomBuffer = scannerDim.nextInt();
			this.leftBuffer = scannerDim.nextInt();
			this.rightBuffer = scannerDim.nextInt();

			this.dividers[0] = scannerDim.nextInt();
			this.dividers[1] = scannerDim.nextInt();
			this.dividers[2] = scannerDim.nextInt();

			scannerDim.close();
		} catch (Exception e) {
			System.err.println("Cannot load pitch dimensions file " + fileName
					+ "Dimensions:");
			System.err.println(e.getMessage());
			loadDefaultConstants();
			return;
		}

		Scanner scanner;

		try {
			scanner = new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			System.err.println("Cannot load thresholds file " + fileName + ":");
			System.err.println(e.getMessage());
			loadDefaultConstants();
			return;
		}

		assert (scanner != null);

		// Iterate over the ball, blue robot, yellow robot, grey circles, and
		// green plates in the order they're defined above.
		for (int i = 0; i < NUM_THRESHOLDS; ++i) {
			this.redLower[i] = scanner.nextInt();
			this.redUpper[i] = scanner.nextInt();
			this.redInverted[i] = scanner.nextBoolean();
			this.greenLower[i] = scanner.nextInt();
			this.greenUpper[i] = scanner.nextInt();
			this.greenInverted[i] = scanner.nextBoolean();
			this.blueLower[i] = scanner.nextInt();
			this.blueUpper[i] = scanner.nextInt();
			this.blueInverted[i] = scanner.nextBoolean();
			this.hueLower[i] = scanner.nextFloat();
			this.hueUpper[i] = scanner.nextFloat();
			this.hueInverted[i] = scanner.nextBoolean();
			this.saturationLower[i] = scanner.nextFloat();
			this.saturationUpper[i] = scanner.nextFloat();
			this.saturationInverted[i] = scanner.nextBoolean();
			this.valueLower[i] = scanner.nextFloat();
			this.valueUpper[i] = scanner.nextFloat();
			this.valueInverted[i] = scanner.nextBoolean();
		}

		scanner.close();
	}

	/**
	 * Loads default values for the constants, used when loading from a file
	 * fails.
	 */
	private void loadDefaultConstants() {
		// Iterate over the ball, blue robot, yellow robot, grey circles, and
		// green plates in the order they're defined above.
		for (int i = 0; i < NUM_THRESHOLDS; ++i) {
			this.redLower[i] = RGBMIN;
			this.redUpper[i] = RGBMAX;
			this.redInverted[i] = false;
			this.greenLower[i] = RGBMIN;
			this.greenUpper[i] = RGBMAX;
			this.greenInverted[i] = false;
			this.blueLower[i] = RGBMIN;
			this.blueUpper[i] = RGBMAX;
			this.blueInverted[i] = false;
			this.hueLower[i] = HSVMIN;
			this.hueUpper[i] = HSVMAX;
			this.hueInverted[i] = false;
			this.saturationLower[i] = HSVMIN;
			this.saturationUpper[i] = HSVMAX;
			this.saturationInverted[i] = false;
			this.valueLower[i] = HSVMIN;
			this.valueUpper[i] = HSVMAX;
			this.valueInverted[i] = false;
		}

		// Pitch Dimensions
		this.topBuffer = 40;
		this.bottomBuffer = 40;
		this.leftBuffer = 20;
		this.rightBuffer = 20;

		this.dividers[0] = 70;
		this.dividers[1] = 120;
		this.dividers[2] = 170;
	}
}
