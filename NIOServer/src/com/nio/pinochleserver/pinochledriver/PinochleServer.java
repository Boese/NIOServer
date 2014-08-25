package com.nio.pinochleserver.pinochledriver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import naga.ConnectionAcceptor;
import naga.NIOServerSocket;
import naga.NIOSocket;
import naga.ServerSocketObserver;
import naga.SocketObserver;
import naga.eventmachine.EventMachine;

public class PinochleServer implements ServerSocketObserver{

	private static EventMachine m_eventMachine;
    private static List<PinochleDriver> currentGames;
    
    PinochleServer(EventMachine machine) {
    	m_eventMachine = machine;
    	currentGames = new ArrayList<PinochleDriver>();
    	try {
			currentGames.add(new PinochleDriver());
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
		for (PinochleDriver pinochleDriver : currentGames) {
			if(!pinochleDriver.isFull()) {
				allGamesFull = false;
				pinochleDriver.addSocket(socket);
				break;
			}
		}
		if(allGamesFull) {
			PinochleDriver d = null;
			try {
				d = new PinochleDriver();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			d.addSocket(socket);
			currentGames.add(d);
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

		@Override
		public void connectionBroken(NIOSocket arg0, Exception arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void connectionOpened(NIOSocket arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void packetReceived(NIOSocket arg0, byte[] arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void packetSent(NIOSocket arg0, Object arg1) {
			// TODO Auto-generated method stub
			
		}
	}

}
