package com.nio.pinochleserver.pinochleserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import com.nio.pinochleserver.pinochlegames.Pinochle;
import com.nio.pinochleserver.pinochlegames.GameStateObserver;

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
				currentGames.add(game);
				System.out.println("Created new Game");
				System.out.println("# of currentGames : " + currentGames.size());
				game.addSocket(socket);
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
		int port = 5217;
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
	
	private static class Game implements SocketObserver,GameStateObserver {

		//private final static long LOGIN_TIMEOUT = 30 * 1000;
        private final static int INACTIVITY_TIMEOUT = 30 * 1000;
		private List<NIOSocket> sockets;
		private Pinochle pinochleGame;
        private DelayedEvent disconnectEvent;
        private PinochleServer server;
        private JSONObject socketResponse;
        private NIOSocket currentSocket;
		
		private Game(PinochleServer server) {
			pinochleGame = new Pinochle();
			pinochleGame.registerObserver(this);
			this.sockets = new ArrayList<NIOSocket>();
			this.server = server;
			socketResponse = null;
		}
		
		@Override
		public void connectionBroken(NIOSocket socket, Exception arg1) {
            removeSocket(socket);
            pinochleGame.Play(null);
		}
		
		@Override
		public void connectionOpened(NIOSocket socket) {
			pinochleGame.Play(null);
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
				currentSocket = null;
				if (disconnectEvent != null) disconnectEvent.cancel(); 
				String temp = new String(packet).trim();
				try {
					socketResponse = new JSONObject(temp);
					pinochleGame.Play(socketResponse);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}
		}

		@Override
		public void packetSent(NIOSocket socket, Object arg1) {}
		
		public void addSocket(NIOSocket socket) {
			 try {
				sockets.add(socket);
				socket.setPacketReader(new AsciiLinePacketReader());
	            socket.setPacketWriter(new AsciiLinePacketWriter());
				socket.listen(this);
				pinochleGame.addPlayer(socket);
				System.out.println("new socket connected on port : " + socket.getPort());
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error adding player to game");
			}
		 }
		
		public void removeSocket(NIOSocket socket) {
			if(sockets.contains(socket))
            {
				try {
					socket.closeAfterWrite();
					sockets.remove(socket);
					pinochleGame.removePlayer(socket);
					System.out.println("socket disconnected on port : " + socket.getPort());
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
            
			if(sockets.size() == 0) {
				server.removeGame(this);
			}
			
			if(disconnectEvent != null) disconnectEvent.cancel();
		}
		 
		 public boolean isFull() {
			 return (pinochleGame.gameFull()) ? true : false;
		 }

		@Override
		public void update(NIOSocket socket, String msg) {
			socket.write(msg.getBytes());
		}

		@Override
		public void request(NIOSocket socket, String msg) {
			currentSocket = socket;
			socket.write(msg.getBytes());
			scheduleInactivityEvent(socket);
		}
		
		@Override
		public void close() {
			for (NIOSocket socket : sockets) {
				socket.closeAfterWrite();
			}
		}

	}

}
