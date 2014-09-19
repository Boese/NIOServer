package com.nio.pinochleserver.pinochleserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import com.nio.pinochleserver.enums.GameResponse;
import com.nio.pinochleserver.pinochlegames.Pinochle;

import naga.ConnectionAcceptor;
import naga.NIOServerSocket;
import naga.NIOSocket;
import naga.ServerSocketObserver;
import naga.SocketObserver;
import naga.eventmachine.DelayedEvent;
import naga.eventmachine.EventMachine;
import naga.packetreader.AsciiLinePacketReader;
import naga.packetwriter.AsciiLinePacketWriter;

public class PinochleServer implements ServerSocketObserver{

	private static EventMachine m_eventMachine;
    private static List<Game> currentGames;
    
    PinochleServer(EventMachine machine) {
    	m_eventMachine = machine;
    	currentGames = new ArrayList<Game>();
    }
    
	@Override
	public void acceptFailed(IOException exception) {
		System.out.println("failed to accept connection : " + exception);
	}

	@Override
	public void newConnection(NIOSocket socket) {
		
		/*
		 * Authenticate Login
		 * Pass Socket to login class
		 * Once authenticated pass encrypted key onto device
		 * Player can then login with key for 24 hours
		 * Login class will will for user to start game
		 * When user starts game it is passed to Game class
		 * 
		 */
		boolean allGamesFull = true;
		for (Game game : currentGames) {
			if(!game.isFull()) {
				allGamesFull = false;
				game.addSocket(socket);
				break;
			}
		}
		if(allGamesFull == true) {
			Game game = null;
			try {
				game = new Game(this);
				game.addSocket(socket);
				currentGames.add(game);
				System.out.println("Created new Game");
				System.out.println("# of currentGames : " + currentGames.size());
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	@Override
	public void serverSocketDied(Exception exception) {
		System.out.println("Server Socket Died : " + exception);
		System.exit(1);
	}
	
	public void removeGame(Game g) {
		currentGames.remove(g);
		System.out.println("Removed Game");
		System.out.println("# of currentGames : " + currentGames.size());
	}
	
	public static void main(String[] args) {
		int port = 5218;
		try
		{
            EventMachine machine = new EventMachine();
			NIOServerSocket socket = machine.getNIOService().openServerSocket(port);
			System.out.println("***Pinochle Server Started!***");
			socket.listen(new PinochleServer(machine));
			socket.setConnectionAcceptor(ConnectionAcceptor.ALLOW);
            machine.start();
            
            // Stop Server when user presses any key
            Scanner s = new Scanner(System.in);
			s.nextLine();
			s.close();
			System.exit(0);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public EventMachine getEventMachine()
    {
        return m_eventMachine;
    }
	
	private static class Game implements SocketObserver {

		//private final static long LOGIN_TIMEOUT = 30 * 1000;
        private final static int INACTIVITY_TIMEOUT = 30 * 1000;
		private List<NIOSocket> sockets;
		private Pinochle pinochleGame;
        private DelayedEvent disconnectEvent;
        private PinochleServer server;
        private NIOSocket currentSocket;
        private JSONObject socketResponse;
        private boolean gameOver = false;
		
		private Game(PinochleServer server) {
			pinochleGame = new Pinochle();
			this.sockets = new ArrayList<NIOSocket>();
			this.server = server;
			currentSocket = null;
			socketResponse = null;
		}
		
		private void broadcastGame() {
			for (NIOSocket socket : sockets) {
					socket.write(pinochleGame.pinochleMessage.getPinochleMessage(socket).toString().getBytes());
			}
		}
		private void broadcast(String message) {
			for (NIOSocket nioSocket : sockets) {
				nioSocket.write(message.getBytes());
			}
		}
		
		public void drivePlay() {
			boolean done = false;
        	do {
        		done = Play();
        	}while(done);
		}
		
		private boolean Play() {
			//Broadcast until player response is needed
			GameResponse g = pinochleGame.Play(socketResponse);
			currentSocket = null; socketResponse = null;
			
			switch(g) {
			case Player:
				currentSocket = pinochleGame.getCurrentSocket();
				scheduleInactivityEvent(currentSocket);
			case Broadcast: 
				broadcastGame();
				break;
			case Gameover: gameOver = true;
				break;
			default:
				break;
			}
			
			if(g.equals(GameResponse.Broadcast))
				return true;
			return false;
		}
		
		@Override
		public void connectionBroken(NIOSocket socket, Exception arg1) {
            if(sockets.contains(socket))
            	sockets.remove(socket);
			try {
				pinochleGame.removePlayer(socket);
			} catch (Exception e) {
			}
			broadcast("Waiting for players ... (" + sockets.size() + " players)");
            System.out.println("socket disconnected on port : " + socket.getPort());
            
			if(sockets.size() == 0)
				server.removeGame(this);
			
			if(disconnectEvent != null) disconnectEvent.cancel();
			currentSocket = null;
			Play();
		}
		
		@Override
		public void connectionOpened(NIOSocket socket) {
            System.out.println("new socket connected on port : " + socket.getPort());
            socket.write(("Player : " + pinochleGame.getPosition(socket).toString()).getBytes());
            
            if(isFull()) {
            	broadcast("Waiting for players ... (" + sockets.size() + " players)");
            	drivePlay();
            }
            else {
            	broadcast("Waiting for players ... (" + sockets.size() + " players)");
            }
		}
		
		private void scheduleInactivityEvent(NIOSocket socket)
        {
            // Cancel the last disconnect event, schedule another.
            if (disconnectEvent != null) disconnectEvent.cancel();
            disconnectEvent = server.getEventMachine().executeLater(new Runnable()
            {
                public void run()
                {
                    socket.write("Disconnected due to inactivity.".getBytes());
                    socket.closeAfterWrite();
                }
            }, INACTIVITY_TIMEOUT);
        }

		@Override
		public void packetReceived(NIOSocket socket, byte[] packet) {
			if(currentSocket == socket) {
				if (disconnectEvent != null) disconnectEvent.cancel(); 
				String temp = new String(packet).trim();
				try {
					socketResponse = new JSONObject(temp);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				currentSocket = null;
				drivePlay();
			}
		}

		@Override
		public void packetSent(NIOSocket socket, Object arg1) {
			// Check if game is over and close socket
			if(gameOver) 
				socket.closeAfterWrite();
		}
		
		public void addSocket(NIOSocket socket) {
			 try {
				sockets.add(socket);
				pinochleGame.addPlayer(socket);
				socket.setPacketReader(new AsciiLinePacketReader());
	            socket.setPacketWriter(new AsciiLinePacketWriter());
				socket.listen(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		 }
		 
		 public boolean isFull() {
			 if(sockets.size() == 4)
				 return true;
			 return false;
		 }
	}

}
