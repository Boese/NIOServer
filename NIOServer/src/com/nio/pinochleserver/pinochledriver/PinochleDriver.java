package com.nio.pinochleserver.pinochledriver;

import java.util.List;
import java.util.Scanner;

import naga.NIOService;
import naga.NIOSocket;

import com.nio.pinochleserver.statemachine.FourHandedPinochle;

public class PinochleDriver {
	
	private List<NIOSocket> sockets;
	private FourHandedPinochle p;
	 
	 
	 public PinochleDriver() throws Exception {
		// Start up the service.
		 p = new FourHandedPinochle();
         NIOService service = new NIOService();

         // Open our socket.
         NIOSocket socket = service.openSocket("localhost", 5218);
         p.addPlayer(socket);
         p.addPlayer(socket);
         p.addPlayer(socket);
         p.addPlayer(socket);
	 }
	 
	public void startGame() {
		Scanner s = new Scanner(System.in);
		String result = "Starting Game (Press Enter)";
			do {
					System.out.println(result);
					String move = s.nextLine();
					result = p.play(move);
			}while(result != "gameOver");	// redeal if everyone passed
			
			
		}
}
