package maze; 

import java.util.Random;


public class BasicRobot implements Robot{
	
	
	boolean robotIsDefault; // a boolean that will allow us to determine if the default configuration (with just forward and left)
					 // sensors is being used
	
	Sensor frontSensor;
	Sensor rearSensor;
	Sensor leftSensor;
	Sensor rightSensor;
	
	boolean sensorOnFront;
	boolean sensorOnRear;
	boolean sensorOnLeft;
	boolean sensorOnRight;
	
	
	boolean robotCollision;
	
	public int batteryLevel = 1000;
	
	private int curXposition;
	private int curYposition;

	
	private int startingX;
	private int startingY;
	
	Maze theMaze;
	
	Random aRandom;
	int numberOfSensors = 0;
	
	
	//the constructor that will allow the inclusion of all desired sensors
	public BasicRobot(boolean includeFront, boolean includeRear, boolean includeLeft, boolean includeRight){
		
		robotIsDefault = false;
		
		if ( includeFront ){
			frontSensor = new Sensor("front");
			sensorOnFront = true;
			numberOfSensors++;
		}
		if ( includeRear ){
			rearSensor = new Sensor("rear");
			sensorOnRear = true;
			numberOfSensors++;
		}
		if ( includeLeft ){
			leftSensor = new Sensor("left");
			sensorOnLeft = true;
			numberOfSensors++;
		}
		if ( includeRight ){
			rightSensor = new Sensor("right");
			sensorOnRight = true;
			numberOfSensors++;
		}
		
		
		//aRandom = new Random();
		//int x = aRandom.nextInt(16);
		
		
		//theMaze = new Maze();
		//theMaze.build(x);//build a complete maze
	}
	
		
	//the constructor for the default configuration: there are only forward and left sensors
	public BasicRobot(){
		
		robotIsDefault = true;
		
		//we will only have sensors on the left and front
		frontSensor = new Sensor("front");
		leftSensor = new Sensor("left");
		
		sensorOnFront = true;
		sensorOnLeft = true;
		
		numberOfSensors += 2;
	}
			
	
	
	
	
	/**
	 * The method by which we provide a maze to the robot
	 * @param theMaze
	 */
	public void provideMaze(Maze theMaze){
		this.theMaze = theMaze;
		
		
	}
	
	
	
	
	
	
	
	/**
	 * Rotates the robot a specified number of degrees	
	 */
	public void rotate(int degree) throws UnsupportedArgumentException {
		
		//our rotate method only needs to do at most a full rotation.  Also
		//we must receive a multiple of 90
		
		if ( (degree % 90 == 0) && (Math.abs(degree) <= 360)  ){
			
			int numRotations = Math.abs(degree) / 90;
			
			int energyRequired = numRotations * 2;
			
			if (hasEnergy(energyRequired)){ //we need to see if we have the required energy for the rotation
			
				int control = numRotations;
				while ( control > 0 ){
					theMaze.rotate((degree / numRotations) / 90 );//will give us either -1 or 1 depending on positive or negative
					batteryLevel -= 2;
					control--;

				}
			}
		
				
			
		}
		else
			throw new UnsupportedArgumentException();
		
	}
	
	
	/**
	 * Moves robot forward or backward a given number of steps. A step matches a single cell.
	 * Since a robot may only have a distance sensor in its front, driving backwards may happen blindly as distance2Obstacle may not provide values for that direction.
	 * If the robot runs out of energy somewhere on its way, it stops, which can be checked by hasStopped() and by checking the battery level. 
	 * @param distance is the number of cells to move according to the robots current direction if forward = true, opposite direction if forward = false
	 * @param forward specifies if the robot should move forward (true) or backward (false)
	 * @throws HitObstacleException if robot hits an obstacle like a wall or border, which also make the robot stop, i.e. hasStopped() = true 
	 */
	public void move(int distance, boolean forward) throws HitObstacleException {
		
		int counter = 0; 
		
		int initialX = getCurrentPosition()[0];
		int initialY = getCurrentPosition()[1];
		
		int energyRequired = distance * 3;
		 
		if (!robotCollision && hasEnergy(energyRequired)){ //we can only move under the condition that there has not been a collision
			if ( forward ){
				while ( counter < distance && hasEnergy(3) ){
					theMaze.walk(1);
					counter++;
					batteryLevel -= 3;
				}
			}
			else{
				while ( counter < distance && hasEnergy(3) ){
					theMaze.walk(-1);
					counter++;
					batteryLevel -= 3;
				}
			}


			int postX = getCurrentPosition()[0];
			int postY = getCurrentPosition()[1];


			if ( (initialX == postX) && (initialY == postY)){
				robotCollision = true;
				throw new HitObstacleException ();
			}
		
		}
	}
	
	
	
	/**
	 * Provides the current position as (x,y) coordinates for the maze cell as an array of length 2 with [x,y].
	 * Note that 0 <= x < width, 0 <= y < height of the maze. 
	 * @return array of length 2, x = array[0], y=array[1]
	 */
	public int[] getCurrentPosition() {
		return theMaze.getCurrentPosition();
	}
	
	
	/**
	 * Tells if current position is at the goal. Used to recognize termination of a search.
	 * Note that goal recognition is limited by the sensing functionality of robot such that isAtGoal returns false
	 * even if it is positioned directly at the exit but has no distance sensor towards the exit direction. 
	 * @return true if robot is at the goal and has a distance sensor in the direction of the goal, false otherwise
	 */
	public boolean isAtGoal() {
		
		try{
			if ( canSeeGoalAhead() && ( distanceToObstacleAhead() == 0 ))
				return true;
		}
		catch(UnsupportedMethodException unavailableSensor){};
		
		
		try{
			if ( canSeeGoalBehind() && ( distanceToObstacleBehind() == 0))
				return true;
		}
		catch( UnsupportedMethodException unavailableSensor ){};
		
		
		
		try{
			if ( canSeeGoalOnLeft() && ( distanceToObstacleOnLeft() == 0))
				return true;
		}
		catch( UnsupportedMethodException unavailableSensor ){};
		
		
		try{
			if ( canSeeGoalOnRight() && ( distanceToObstacleOnRight() == 0))
				return true;
		}
		catch( UnsupportedMethodException unavailableSensor ){};
		
		
	
		return false;
		
		
	}
	
	
	
	/**
	 * Provides the current direction as (dx,dy) values for the robot as an array of length 2 with [dx,dy].
	 * Note that dx,dy are elements of {-1,0,1} and as in bitmasks masks in Cells.java and dirsx,dirsy in MazeBuilder.java.
	 * 
	 * @return array of length 2, dx = array[0], dy=array[1]
	 */	
	public int[] getCurrentDirection() {
		return theMaze.getCurrentDirection(); 
	}
	
	
	
	
	/**
	 * The robot has a given battery level (energy level) that it draws energy from during operations. 
	 * The particular energy consumption is device dependent such that a call for distance2Obstacle may use less energy than a move forward operation.
	 * If battery level <= 0 then robot stops to function and hasStopped() is true.
	 * @return current battery level, level is > 0 if operational. 
	 */
	public float getCurrentBatteryLevel() {
		return batteryLevel;
	}
	
	
	
	/**
	 * Gives the energy consumption for a full 360 degree rotation.
	 * Scaling by other degrees approximates the corresponding consumption. 
	 * @return energy for a full rotation
	 */
	public float getEnergyForFullRotation() {
		
		//each 90 degree rotation has an energy consumption of 2 units and
		//there are four 90 degree rotation in a full rotation
		return 8;
		
		
	}
	
	
	/**
	 * Gives the energy consumption for moving 1 step forward.
	 * For simplicity, we assume that this equals the energy necessary to move 1 step backwards and that scaling by a larger number of moves is 
	 * approximately the corresponding multiple.
	 * @return energy for a single step forward
	 */
	public float getEnergyForStepForward() {
		//each step forward (and backward) consumes 3 units of energy
		return 3;
	}
	
	
	
	/**
	 * Method returns whether the robot has enough energy to 
	 * complete a certain operation. The parameter is the 
	 * energy consumption for the operation
	 * @return
	 */
	public boolean hasEnergy(int x){
		return  (batteryLevel - x) >= 0;
		
	}
	
	
	/**
	 * Tells if the robot has stopped for reasons like lack of energy, hitting an obstacle, etc. We 
	 * will have to allow a pa
	 * @return true if the robot has stopped, false otherwise
	 */
	public boolean hasStopped() {
		return (!( hasEnergy(3) )) || robotCollision; //
		
		
		
	}
	
	
	
	/**
	 * Tells if a sensor can identify the goal in the robot's current forward direction from the current position.
	 * @return true if the goal (here: exit of the maze) is visible in a straight line of sight
	 * @throws UnsupportedMethodException if robot has no sensor in this direction
	 */
	public boolean canSeeGoalAhead() throws UnsupportedMethodException {
		
		/* we know that if we run into a cell that does not have a wall in the particular direction, 
		 * but it does have a border then we have found the exit. We will proceed by finding the distance 
		 * to the next obstacle and determining if it was just a border (meaning there is no wall). In this
		 * case we have found an exit.
		 */
		
		if (sensorOnFront){
			
			
			//int[] distanceList = getCurrentDirection();
			
			int distance = frontSensor.getDistance(getCurrentDirection(), getCurrentPosition(), theMaze);
			
			int i = 0;
			
			int xPosition = getCurrentPosition()[0];
			int yPosition = getCurrentPosition()[1];
			
			int xDirection = getCurrentDirection()[0];
			int yDirection = getCurrentDirection()[1];
			
			
			//it is always the case that either x or y will equal zero and the other number will be 1 or -1
			
			if (xDirection != 0){
				
				switch(xDirection){
				
				case 1: //we are moving right
					while ( i < distance ){ //move in that direction 
						xPosition += 1; 
						i++;
					}
					break;
					
				case -1: //we are moving left
					while ( i < distance ){ //move in that direction 
						xPosition -= 1; 
						i++;
					}
					break;
				}
				
			}
			else{ //we are dealing with a vertical direction (up or down)
				
				switch(yDirection){
				
				case 1: //we are moving down
					while ( i < distance ){ //move in that direction 
						yPosition += 1; 
						i++;
					}
					break;
					
				case -1: //we are moving up
					while ( i < distance ){ //move in that direction 
						yPosition -= 1; 
						i++;
					}
					break;
				}
				
			}
			
			
			
			
			
			
			
			if ( theMaze.mazecells.hasMaskedBitsFalse( xPosition, yPosition, frontSensor.bitMask ) && 
				theMaze.mazecells.hasMaskedBitsTrue(xPosition, yPosition, frontSensor.bitMask << 5) ){  //if a border is up in that direction
						return true;
			}
					
			else
				return false;
			
		}
		
		
		throw new UnsupportedMethodException();
		
		
		
	}
	
	
	
	/**
	 * Methods analogous to canSeeGoalAhead but for a the robot's current backward direction
	 * @return true if the goal (here: exit of the maze) is visible in a straight line of sight
	 * @throws UnsupportedMethodException if robot has no sensor in this direction
	 */
	public boolean canSeeGoalBehind() throws UnsupportedMethodException {
		
		if (sensorOnRear){
			int distance = rearSensor.getDistance(getCurrentDirection(),getCurrentPosition(), theMaze);
			
			int i = 0;
			
			int xPosition = getCurrentPosition()[0];
			int yPosition = getCurrentPosition()[1];
			
			int xDirection = getCurrentDirection()[0];
			int yDirection = getCurrentDirection()[1];
			
			
			//it is always the case that either x or y will equal zero and the other number will be 1 or -1
			
			if (xDirection != 0){
				
				switch(xDirection){
				
				case 1: //we are moving right
					while ( i < distance ){ //move in the opposite direction 
						xPosition -= 1; 
						i++;
					}
					break;
					
				case -1: //we are moving left
					while ( i < distance ){ //move in opposite direction 
						xPosition += 1; 
						i++;
					}
					break;
				}
				
			}
			else{ //we are dealing with a vertical direction (up or down)
				
				switch(yDirection){
				
				case 1: //we are moving down
					while ( i < distance ){ //move in opposite direction 
						yPosition -= 1; 
						i++;
					}
					break;
					
				case -1: //we are moving up
					while ( i < distance ){ //move in opposite direction
						yPosition += 1; 
						i++;
					}
					break;
				}
				
			}
			
			
			if ( theMaze.mazecells.hasMaskedBitsFalse( xPosition, yPosition, rearSensor.bitMask ) && 
				theMaze.mazecells.hasMaskedBitsTrue(xPosition, yPosition, rearSensor.bitMask << 5) ){  //if a border is up in that direction
						return true;
			}
					
			else
				return false;
			
		}
		
		
		throw new UnsupportedMethodException();
		
	}
	
	
	
	/**
	 * Methods analogous to canSeeGoalAhead but for the robot's current left direction (left relative to forward)
	 * @return true if the goal (here: exit of the maze) is visible in a straight line of sight
	 * @throws UnsupportedMethodException if robot has no sensor in this direction
	 */
	public boolean canSeeGoalOnLeft() throws UnsupportedMethodException {
		
		if (sensorOnLeft){
			int distance = leftSensor.getDistance(getCurrentDirection(), getCurrentPosition(), theMaze);
			
			int i = 0;
			
			int xPosition = getCurrentPosition()[0];
			int yPosition = getCurrentPosition()[1];
			
			int xDirection = getCurrentDirection()[0];
			int yDirection = getCurrentDirection()[1];
			
			
			//it is always the case that either x or y will equal zero and the other number will be 1 or -1
			
			if (xDirection != 0){
				
				switch(xDirection){
				
				case 1: //we are moving right
					while ( i < distance ){ //move down 
						yPosition += 1; 
						i++;
					}
					break;
					
				case -1: //we are moving left
					while ( i < distance ){ //move up
						yPosition -= 1; 
						i++;
					}
					break;
				}
				
			}
			else{ //we are dealing with a vertical direction (up or down)
				
				switch(yDirection){
				
				case 1: //we are moving down
					while ( i < distance ){ //move left
						xPosition -= 1; 
						i++;
					}
					break;
					
				case -1: //we are moving up
					while ( i < distance ){ //move right
						xPosition += 1; 
						i++;
					}
					break;
				}
				
			}
			
			
			if ( theMaze.mazecells.hasMaskedBitsFalse( xPosition, yPosition, leftSensor.bitMask ) && 
				theMaze.mazecells.hasMaskedBitsTrue(xPosition, yPosition, leftSensor.bitMask << 5) ){  //if a border is up in that direction
						return true;
			}
					
			else
				return false;
			
		}
		
		
		throw new UnsupportedMethodException();
		
		
	}
	
	
	
	/**
	 * Methods analogous to canSeeGoalAhead but for the robot's current right direction (right relative to forward)
	 * @return true if the goal (here: exit of the maze) is visible in a straight line of sight
	 * @throws UnsupportedMethodException if robot has no sensor in this direction
	 */
	public boolean canSeeGoalOnRight() throws UnsupportedMethodException {
		
		if (sensorOnRight){
			int distance = rightSensor.getDistance(getCurrentDirection(), getCurrentPosition(), theMaze);
			
			int i = 0;
			
			int xPosition = getCurrentPosition()[0];
			int yPosition = getCurrentPosition()[1];
			
			int xDirection = getCurrentDirection()[0];
			int yDirection = getCurrentDirection()[1];
			
			
			//it is always the case that either x or y will equal zero and the other number will be 1 or -1
			
			if (xDirection != 0){
				
				switch(xDirection){
				
				case 1: //we are moving right
					while ( i < distance ){ //move up
						yPosition -= 1; 
						i++;
					}
					break;
					
				case -1: //we are moving left
					while ( i < distance ){ //move down
						yPosition += 1; 
						i++;
					}
					break;
				}
				
			}
			else{ //we are dealing with a vertical direction (up or down)
				
				switch(yDirection){
				
				case 1: //we are moving down
					while ( i < distance ){ //move right
						xPosition += 1; 
						i++;
					}
					break;
					
				case -1: //we are moving up
					while ( i < distance ){ //move left
						xPosition -= 1; 
						i++;
					}
					break;
				}
				
			}
			
			
			if ( theMaze.mazecells.hasMaskedBitsFalse( xPosition, yPosition, rightSensor.bitMask ) && 
				theMaze.mazecells.hasMaskedBitsTrue(xPosition, yPosition, rightSensor.bitMask << 5) ){  //if a border is up in that direction
						return true;
			}
					
			else
				return false;
			
		}
		
		throw new UnsupportedMethodException();
		
	}
	
			
	/**
	 * Return the distance to the next obstacle in the forward direction	
	 */
	public int distanceToObstacleAhead() throws UnsupportedMethodException{
		if ( sensorOnFront && hasEnergy(1) )
			return frontSensor.getDistance(getCurrentDirection(), getCurrentPosition(), theMaze); 
		
		throw new UnsupportedMethodException();
	}
	
	/**
	 * Return the distance to the next obstacle on the left
	 */
	public int distanceToObstacleOnLeft() throws UnsupportedMethodException {
		
		if ( sensorOnLeft && hasEnergy(1))
			return leftSensor.getDistance(getCurrentDirection(), getCurrentPosition(), theMaze); 
		
		throw new UnsupportedMethodException();
	}
	
	/**
	 * Return the distance to the next obstacle on the right
	 */
	public int distanceToObstacleOnRight() throws UnsupportedMethodException {
		
		if ( sensorOnRight && hasEnergy(1))			
			return rightSensor.getDistance(getCurrentDirection(), getCurrentPosition(), theMaze);
		
		throw new UnsupportedMethodException();
			
	}
	
	
	/**
	 * Methods analogous to distanceToObstacleAhead but for a the robot's current backward direction
	 * @return number of steps towards obstacle if obstacle is visible in a straight line of sight, Integer.MAX_VALUE otherwise
	 * @throws UnsupportedArgumentException if not supported by robot
	 */
	public int distanceToObstacleBehind() throws UnsupportedMethodException {
		
		if ( sensorOnRear && hasEnergy(1) )			
			return rearSensor.getDistance(getCurrentDirection(), getCurrentPosition(), theMaze);
		
		throw new UnsupportedMethodException();
		
	}
	
	/**
	 * A simple method for returning the number of sensors
	 * @return
	 */
	public int getSensors(){
		return numberOfSensors;
	}
	
	
	
	
	
}//end class









