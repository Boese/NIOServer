package com.nio.login;

import naga.*;
import naga.eventmachine.DelayedEvent;
import naga.eventmachine.EventMachine;
import naga.packetreader.AsciiLinePacketReader;
import naga.packetwriter.AsciiLinePacketWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginServer implements ServerSocketObserver {
	private final EventMachine m_eventMachine;
    private final AuthenticateServer authenticate;
    
    LoginServer(EventMachine machine)
	{
        m_eventMachine = machine;
        authenticate = new AuthenticateServer(this);
    }

    public void acceptFailed(IOException exception)
    {
        System.out.println("Failed to accept connection: " + exception);
    }

    public void serverSocketDied(Exception exception)
    {
        // If the server socket dies, we could possibly try to open a new socket.
        System.out.println("Server socket died.");
        System.exit(-1);
    }

    public void newConnection(NIOSocket nioSocket)
    {
        System.out.println("New user connected from " + nioSocket.getIp() + ".");
        authenticate.authenticateSocket(nioSocket);
    }
    
	public static void main(String... args)
	{
		int port = 5218;
		try
		{
            EventMachine machine = new EventMachine();
			NIOServerSocket socket = machine.getNIOService().openServerSocket(port);
			socket.listen(new LoginServer(machine));
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
    
    private static class AuthenticateServer implements SocketObserver {
    	private final static long LOGIN_TIMEOUT = 5 * 60 * 100;
        private final static long INACTIVITY_TIMEOUT = 5 * 60 * 100;
        private final LoginServer m_server;
        private List<NIOSocket> authenticatedSockets;
        private Map<NIOSocket,DelayedEvent> delayedEvents;
        
        AuthenticateServer(LoginServer server) {
        	m_server = server;
        	authenticatedSockets = new ArrayList<NIOSocket>();
        	delayedEvents = new HashMap<NIOSocket,DelayedEvent>();
		}
        
        void authenticateSocket(NIOSocket socket) {
        	socket.setPacketReader(new AsciiLinePacketReader());
            socket.setPacketWriter(new AsciiLinePacketWriter());
            socket.listen(this);
        }
        
		@Override
		public void connectionBroken(NIOSocket socket, Exception e) {
			System.out.println("Socket disconnected");
			socket.closeAfterWrite();
			delayedEvents.remove(socket);
		}

		@Override
		public void connectionOpened(NIOSocket socket) {
			DelayedEvent e = m_server.getEventMachine().executeLater( new Runnable()
            {
                public void run()
                {
                	socket.write("Disconnected due to inactivity.".getBytes());
                	socket.closeAfterWrite();
                }
            }, INACTIVITY_TIMEOUT);
            socket.write("Please enter your username:".getBytes());
			delayedEvents.put(socket, e);
		}
		
		private void scheduleInactivityEvent(NIOSocket socket, String message)
        {
            // Cancel the last disconnect event, schedule another.
            if (delayedEvents.get(socket) != null) delayedEvents.get(socket).cancel();
            DelayedEvent e = delayedEvents.get(socket);
            e = m_server.getEventMachine().executeLater(new Runnable()
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
		public void packetReceived(NIOSocket socket, byte[] message) {
			/*
			 * Look up username in MongoDB
			 * if create random number and encrypt
			 * send to user
			 * schedule inactivity event
			 * user decrypts and encrypts with password
			 * sends back encrypted key
			 * encrypt number using users password
			 * if they match authenticate user
			 * if not boot user
			 */
			
			/*
			 * if username does not exist prompt user
			 * to create new account -> hits rest webservice to insert into MongoDB *Not Server!
			 * webservice will handle forgotten passwords, new users, email verification
			 */
			String randomKey = "1233211458";
			String user = "Chris";
			
			String temp = new String(message);
			JSONObject object = null;
			try {
				object = new JSONObject(temp);
			} catch (JSONException e) {
				System.out.println("not JSON");
			}
			if(object != null) {
				String username = object.optString("username");
				String key = object.optString("key");
				
				if(username.equalsIgnoreCase(user)) {
					if(key.equalsIgnoreCase(randomKey)) {
						delayedEvents.get(socket).cancel();
						socket.write("authenticated!".getBytes());
						authenticatedSockets.add(socket);
						socket.closeAfterWrite();
					}
					delayedEvents.get(socket).cancel();
					scheduleInactivityEvent(socket, "Random Key: " + randomKey);
				}
				else
					socket.write("User doesn't exist!".getBytes());
			}
			else
				socket.closeAfterWrite();
		}

		@Override
		public void packetSent(NIOSocket socket, Object message) {
			
		}
    	
    }
}
