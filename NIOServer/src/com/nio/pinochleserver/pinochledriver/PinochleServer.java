package com.nio.pinochleserver.pinochledriver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

		private final static long LOGIN_TIMEOUT = 30 * 1000;
        //private final static long INACTIVITY_TIMEOUT = 5 * 60 * 100;
		private List<NIOSocket> sockets;
		private FourHandedPinochle p;
        private DelayedEvent disconnectEvent;
        private PinochleServer server;
		
		private Game(PinochleServer server) {
			p = new FourHandedPinochle();
			this.sockets = new ArrayList<NIOSocket>();
			this.server = server;
		}
		
		private void broadcast(String message) {
			for (NIOSocket nioSocket : sockets) {
				nioSocket.write(message.getBytes());
			}
		}
		@Override
		public void connectionBroken(NIOSocket socket, Exception arg1) {
			removeSocket(socket);
			String message = "Waiting for players ... (" + sockets.size() + " players)";
            broadcast(message);
            broadcast("GAME IS PAUSED");
		}

		@Override
		public void connectionOpened(NIOSocket socket) {
			// We start by scheduling a disconnect event for the login.
            disconnectEvent = server.getEventMachine().executeLater(new Runnable()
            {
                public void run()
                {
                	System.out.println("socket disconnected : " + socket);
                    socket.write("Disconnecting due to inactivity".getBytes());
                    socket.closeAfterWrite();
                    removeSocket(socket);
                }
            }, LOGIN_TIMEOUT);

            System.out.println("new socket connected : " + socket);
            // Send the request to log in.
            if(isFull())
            	broadcast("GAME IS STARTING!");
            else {
            	String message = "Waiting for players ... (" + sockets.size() + " players)";
                broadcast(message);
            }
            socket.write("Please enter your name:".getBytes());
		}

		@Override
		public void packetReceived(NIOSocket socket, byte[] arg1) {
			disconnectEvent.cancel();
		}

		@Override
		public void packetSent(NIOSocket socket, Object arg1) {
			// TODO Auto-generated method stub
			
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
		 
		 public boolean removeSocket(NIOSocket socket){
			 boolean success = true;
			 try {
				sockets.remove(socket);
				p.removePlayer(socket);
				socket.close();
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
