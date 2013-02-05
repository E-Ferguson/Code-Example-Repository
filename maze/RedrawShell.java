package maze;

import java.awt.*;

/**
 * Class handles the user interaction for the maze. 
 * It implements a state-dependent behavior that controls the display and reacts to key board input from a user. 
 * After refactoring the original code from an applet into a panel, it is wrapped by a MazeApplication to be a java application 
 * and a MazeApp to be an applet for a web browser. At this point user keyboard input is first dealt with a key listener
 * and then handed over to a Maze object by way of the keyDown method.
 *
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 */


public class redrawShell extends Maze{
	
	
	public void redraw(){
		
		
	}
	
}