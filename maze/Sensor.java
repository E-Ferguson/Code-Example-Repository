package maze;


import java.util.Arrays;



/**
 * A basic class that will represent a sensor. For ease of use the constructor accepts a 
 * literal string that represents the direction. Each Sensor instance only needs one 
 * method: a getDistance() method that will return the distance between the robot and the next
 * object in the specified direction. It is quite important to note that the direction in which the 
 * sensor performs its operations is relative to the orientation of the robot.  For instance, if the
 * robot is facing right and we have a left sensor, then we will be checking the top walls for each cell
 * above the current cell.  Thus, we will need a private orientSensor() method that, based on the 
 * current direction of the robot, tells us what walls our sensor should check.  
 * @author Eddie Ferguson
 *
 */
public class Sensor{
	
	//the two directions in which we will travel--these will most definitely change as the sensor is used
	int xSensingDirection;
	int ySensingDirection;
	
	int bitMask; //a bitmask that will correlate to the representation for walls and bounds in cells.java
	
	//int curXposition;
	//int curYposition;
	
	//a compass-like representation of the sensors (down, right, up, left): up and down have
	//to be reversed because of the layout of the maze
	final int[] directionList = {0, 2, 1, 3}; 
	final int offset;
	
	final int[] xDirections = {0, -1, 0, 1};
	final int[] yDirections = {-1, 0, 1, 0};
	
	public Sensor(String direction){
		
		String[] directionList = {"front", "rear", "left", "right"};
		
		//assert that the passed string is in our direction list
		assert Arrays.asList(directionList).contains(direction);
		
		
		/* 
		 * Our first task is to provide some sort of way to make the sensor
		 * translatable to the current direction in which the robot is 
		 * facing.  In other words our sensor needs to adapt: a left sensor
		 * does not always travel "west" nor does a forward sensor always
		 * travel "north".  Rather, the sensor needs to travel relative to
		 * the direction that the robot is facing, so that a forward sensor
		 * can really travel in all four directions. In order to do this, 
		 * we will conceptualize the sensor types as we would a compass, with 
		 * the forward sensor in the north position, the right sensor in the east
		 * position, and so on. We will consider each direction as the offset from 
		 * the direction we are facing. The forward direction has an offset of 0 
		 * since we are always facing that direction. The right has an offset 
		 * of 1, and so on.  For our purposes, the offsets will not change, 
		 * though the directions in which the robot faces definitely will.
		 */
		if (direction == "front"){//2 -> prev 0
			
			offset = 0;
			/*this.xDirection  = xDirections[0];
			this.yDirection = yDirections[0]; */
			//this.bitMask = 1;
		}
		else if(direction == "right"){//3 -> prev 1
			offset = 1;
			/*this.xDirection  = xDirections[1];
			this.yDirection = yDirections[1];*/
			//this.bitMask = 2;
		}
		else if(direction == "rear"){//0 -> prev 2
			offset = 2;
			/*this.xDirection  = xDirections[2];
			this.yDirection = yDirections[2];*/
			//this.bitMask = 4;
		}
		else if(direction == "left"){//1 -> prev 3
			offset = 3;
			/*this.xDirection  = xDirections[3];
			this.yDirection = yDirections[3];*/
			//this.bitMask = 8; 
		}
		//else should never be entered by our assertion. Simply included so that Java 
		//won't recognize the potential for the variable to have not been initialized
		else
			offset = -1;
		
	}
	
	/**
	 * gets a distance to an obstacle given the kind of sensor that we have
	 * 
	 */
	
	public int getDistance(int[] theDirection, int[] thePosition, Maze theMaze) {
		
		//int[] theDirection = getCurrentDirection(); //from the basicRobot class
		
		int curXposition = thePosition[0];
		int curYposition = thePosition[1];
		
		System.out.println("The Positions: x = " + curXposition + " y = " + curYposition);
		
		orientSensor(theDirection);
		
		int distance = 0;
		
		while ( true ){
			//System.out.println("in sensor.java");
			if ( theMaze.mazecells.hasMaskedBitsFalse(curXposition, curYposition, bitMask) && 
				theMaze.mazecells.hasMaskedBitsFalse(curXposition, curYposition, bitMask << 5 ) ){
				
				curXposition += xSensingDirection;
				curYposition += ySensingDirection;
				distance++;
			}
			else
				break;
		}
		
		//System.out.println("The distance: " + distance);
		//r.batteryLevel -= 1; //sensing in one direction entails an energy cost of 1
		
		return distance;
		
	}
	
	
	/**
	 * We need two pieces of information in order to orient the sensor correctly and determine
	 * the direction in which it needs to be sent: the current direction in which the robot is facing
	 * and the offset for the current Sensor object.  Implicit in the  (i.e., a front or rear sensor).
	 */
		
	private void orientSensor(int[] theDirection){
		
		//int[] theDirection = getCurrentDirection(); //from the basicRobot class
		
		int curx = theDirection[0];
		int cury = theDirection[1];
		
		//we will first find the current direction and represent it as a single digit
		int currentDirectionIndex = 0;
		switch (curx + cury * 2) {
		case 1:  currentDirectionIndex = 3 ; break;  // dx=1,  dy=0  theMaze.mazecells.CW_RIGHT
		case -1: currentDirectionIndex = 1 ; break;  // dx=-1, dy=0  theMaze.mazecells.CW_LEFT
		case 2:  currentDirectionIndex = 2 ; break; // dx=0,  dy=1  theMaze.mazecells.CW_BOT
		case -2: currentDirectionIndex = 0 ; break; // dx=0,  dy=-1 theMaze.mazecells.CW_TOP
		}
		
		//direction returns to us the exponent representation for walls as they are modeled in Cells.java
		//In other words, for a top wall it will return 0, a bottom wall 1, etc.  We then have to use
		//exponentiation to get the actual value.
		int adjustedIndex = (currentDirectionIndex + offset) % 4;
		int direction = directionList[adjustedIndex];
	
		bitMask = (int) Math.pow(2, direction );
		
		xSensingDirection = xDirections[adjustedIndex];
		ySensingDirection = yDirections[adjustedIndex];
		
	}
		
		
		
	
	
	
	
	
		
	
	
}//end class








