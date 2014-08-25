package com.nio.pinochleserver.pinochledriver;

import java.util.Scanner;

import naga.NIOService;
import naga.NIOSocket;

import com.nio.pinochleserver.enums.GameResponse;
import com.nio.pinochleserver.statemachine.FourHandedPinochle;

public class PinochleDriver {
	
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
		String move = "";
			do {
					GameResponse g = p.play(move);
					switch(g) {
					case Broadcast: for (String response : p.getBroadcastResponse()) {
									System.out.println(response);
					}
						break;
					case Player:
									System.out.println(p.getCurrentResponse());
						break;
					default:
						break;
					
					}
					move = s.nextLine();
					
			}while(p.getCurrentResponse() != "gameOver");	// redeal if everyone passed
			s.close();
			
		}
}
