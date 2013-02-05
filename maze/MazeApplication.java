/**
 * 
 */
package maze;

import java.awt.*; 
import java.awt.event.*;

import java.awt.Button;
import java.awt.event.KeyListener;


import javax.swing.JFrame;
import javax.swing.JPanel;

import javax.swing.JButton;

/**
 * This class is a wrapper class to startup the Maze as a Java application
 * 
 *
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 */
public class MazeApplication extends JFrame {

	Maze maze ;
	KeyListener kl ;
	
	

	/**
	 * Constructor
	 */
	@SuppressWarnings("unused")
	public MazeApplication() {//throws InterruptedException {
		// System.out.println("MazeApplication:init started, object is displayable? " + this.isDisplayable());

		maze = new Maze() ;
		add(maze) ;
		
		kl = new SimpleKeyListener(this, maze) ;
		addKeyListener(kl) ;
		
		setVisible(true) ;
		maze.init();
		System.out.println("Maze displayable?" + maze.isDisplayable());
		// pack() ;
		this.setSize(500, 500) ;
		
		//repaint() ;
		
		
		//JButton(hello);
		//Button bStore = new Button("store");
		//bStore.addActionListener(this);
		//add(bStore);
		//Gambler gambler = new Gambler(maze);
		
		//Thread.sleep(5000);
		
		JPanel buttonHolder = new JPanel();
		
		
		
		JButton Gambler = new JButton("Gambler");
		buttonHolder.add(Gambler);
		Gambler.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Gambler gambler = new Gambler(maze);
				
				try{
                                    gambler.drive2Exit();
				}
				catch(Exception E){}
				
			}
		});
		
		
		JButton CuriousGambler = new JButton("CuriousGambler");
		buttonHolder.add(CuriousGambler);
		
		CuriousGambler.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				CuriousGambler curiousgambler = new CuriousGambler(maze);
				try{
                                    curiousgambler.drive2Exit();
				}
				catch(Exception E){}
			}
		});
		
		
		JButton WallFollower = new JButton("WallFollower");
		buttonHolder.add(WallFollower);
		
		WallFollower.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				WallFollower wallfollower = new WallFollower(maze);
				try{
                                    wallfollower.drive2Exit();
				}
				catch(Exception E){}
			}
		});
		
		
		
		JButton Wizard = new JButton("Wizard");
		buttonHolder.add(Wizard);
		
		Wizard.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
				Wizard wizard = new Wizard(maze);
				
				try{
                                    wizard.drive2Exit();
				}
				catch(Exception E){}
				
				
			}
		});
		
	
		this.add( buttonHolder, BorderLayout.SOUTH );
                //System.exit(1);
                this.setVisible(true);
		maze.redraw();
		
		
	}
	
	


	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		// quick test
		int[][] twoDimArray = { {1,2,3}, {4,5,6}, {7,8,9} };
		for (int i = 0 ; i < 3 ; i++)
		{
			for (int j = 0 ; j < 3 ; j++)
				System.out.println("(i,j,value):" + i + " " + j + " " + twoDimArray[i][j]);
		}
		*/
		// TODO Auto-generated method stub
		
		//try{
		MazeApplication a = new MazeApplication() ;
		a.repaint() ;
		//}
		//catch(InterruptedException j){}
	}

}
