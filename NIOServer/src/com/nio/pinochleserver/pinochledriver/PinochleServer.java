package com.nio.pinochleserver.pinochledriver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import com.nio.pinochleserver.enums.GameResponse;
import com.nio.pinochleserver.statemachine.FourHandedPinochle;

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
    
    //**implement Observer for Games (Observables), when they change state to gameOver, remove game
    
    PinochleServer(EventMachine machine) {
    	m_eventMachine = machine;
    	currentGames = new ArrayList<Game>();
    	try {
			currentGames.add(new Game(this));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	@Override
	public void acceptFailed(IOException arg0) {
		System.out.println("failed to accept connection : " + arg0);
	}

	@Override
	public void newConnection(NIOSocket socket) {
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
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	@Override
	public void serverSocketDied(Exception arg0) {
		System.out.println("Server Socket Died : " + arg0);
		System.exit(1);
	}
	
	public void removeGame(Game g) {
		currentGames.remove(g);
	}
	
	public static void main(String[] args) {
		int port = 5218;
		try
		{
            EventMachine machine = new EventMachine();
			NIOServerSocket socket = machine.getNIOService().openServerSocket(port);
			socket.listen(new PinochleServer(machine));
			socket.setConnectionAcceptor(ConnectionAcceptor.ALLOW);
            machine.start();
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
        private final static int INACTIVITY_TIMEOUT = 5 * 60 * 100;
		private List<NIOSocket> sockets;
		private FourHandedPinochle p;
        private DelayedEvent disconnectEvent;
        private PinochleServer server;
        private NIOSocket currentSocket;
        private String socketResponse;
        private boolean gameOver = false;
		
		private Game(PinochleServer server) {
			p = new FourHandedPinochle();
			this.sockets = new ArrayList<NIOSocket>();
			this.server = server;
			currentSocket = null;
			socketResponse = "";
		}
		
		private void broadcastGame(List<String> messages) {
			int i = 0;
			for (NIOSocket nioSocket : sockets) {
				nioSocket.write(messages.get(i).getBytes());
				i++;
			}
		}
		private void broadcast(String messages) {
			for (NIOSocket nioSocket : sockets) {
				nioSocket.write(messages.getBytes());
			}
		}
		
		private boolean Play() {
			//Broadcast until player response is needed
				GameResponse g = p.play(socketResponse);
				
				if(p.getCurrentResponse() == "gameOver") {
					gameOver = true;
					return false;
				}
				
				switch(g) {
				case Broadcast: broadcastGame(p.getBroadcastResponse());
					break;
				case Player:
						currentSocket = p.getPlayer(p.getCurrentTurn()).getSocket();
						//currentSocket.write(p.getCurrentResponse().getBytes());
						scheduleInactivityEvent(currentSocket, p.getCurrentResponse());
					break;
				}
				
				socketResponse = null;
				if(g.equals(GameResponse.Broadcast))
					return true;
				return false;
		}
		
		@Override
		public void connectionBroken(NIOSocket socket, Exception arg1) {
            broadcast("Waiting for players ... (" + sockets.size() + " players)");
            broadcast("GAME IS PAUSED");
            System.out.println("socket disconnected : " + socket);
            if(sockets.contains(socket))
            	sockets.remove(socket);
			try {
				p.removePlayer(socket);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// When player replaces another handle it ***
		@Override
		public void connectionOpened(NIOSocket socket) {
            System.out.println("new socket connected : " + socket);
            
            if(isFull()) {
            	broadcast("GAME IS STARTING!");
                broadcast("Waiting for players ... (" + sockets.size() + " players)");
            	boolean done = false;
            	do {
            		done = Play();
            	}while(done);
            }
            else {
            	broadcast("Waiting for players ... (" + sockets.size() + " players)");
            }
		}
		
		private void scheduleInactivityEvent(NIOSocket socket, String message)
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
            socket.write(message.getBytes());
        }

		@Override
		public void packetReceived(NIOSocket socket, byte[] packet) {
			if(currentSocket == socket) {
				disconnectEvent.cancel();
				socketResponse = new String(packet);
				currentSocket = null;
				boolean done = false;
            	do {
            		done = Play();
            	}while(done);
			}
		}

		@Override
		public void packetSent(NIOSocket socket, Object arg1) {
			// Check if game is Over and reset!
			if(gameOver) {
				server.getEventMachine().asyncExecute(new Runnable() {
					@Override
					public void run() {
						for (NIOSocket s : sockets) {
							s.closeAfterWrite();
							p = new FourHandedPinochle();
						}
						sockets.clear();
					}
				});
			}
		}
		
		public boolean addSocket(NIOSocket socket) {
			 boolean success = true;
			 try {
				sockets.add(socket);
				p.addPlayer(socket);
				socket.setPacketReader(new AsciiLinePacketReader());
	            socket.setPacketWriter(new AsciiLinePacketWriter());
				socket.listen(this);
			} catch (Exception e) {
				success = false;
				e.printStackTrace();
			}
			 return success;
		 }
		 
		 public boolean isFull() {
			 if(sockets.size() == 4)
				 return true;
			 return false;
		 }
	}

}
