package com.nio.pinochleserver.pinochleserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nio.pinochleserver.enums.Card;
import com.nio.pinochleserver.enums.Request;
import com.nio.pinochleserver.enums.Suit;
import com.nio.pinochleserver.player.PlayerResponse;

import naga.NIOService;
import naga.NIOSocket;
import naga.SocketObserver;
import naga.packetreader.AsciiLinePacketReader;
import naga.packetwriter.AsciiLinePacketWriter;

public class PinochleClient {
	PinochleClient(){}
	
	private static Request request = Request.Null;
	private static List<Card> cards = new ArrayList<Card>();
	private static NIOSocket socket = null;
	private static ObjectMapper mapper = new ObjectMapper();
	private static PlayerResponse response;
	
	private static void requestNeeded() {
		//Start new thread to capture input. Write to NIOSocket
		BufferedReader s = new BufferedReader(new InputStreamReader(System.in));
        Thread t = new Thread() {

			@Override
			public void run() {
		                try{
		                	response = new PlayerResponse();
		                	int x;
		                		switch(request) {
		                		case Card:
		                			x = Integer.parseInt(s.readLine());
		                			response.setCard(cards.get(x));
		                			break;
								case Bid:
									x = Integer.parseInt(s.readLine());
									response.setBid(x);
									break;
								case Cards: 
									List<Card> tempCards = new ArrayList<Card>();
									int a = Integer.parseInt(s.readLine());
									int b = Integer.parseInt(s.readLine());
									int c = Integer.parseInt(s.readLine());
									int d = Integer.parseInt(s.readLine());
									a++;b++;c++;d++;
									tempCards.add(cards.get(a));
									tempCards.add(cards.get(b));
									tempCards.add(cards.get(c));
									tempCards.add(cards.get(d));
									response.setCards(tempCards);
									break;
								case Null:
									break;
								case Trump: 
									x = Integer.parseInt(s.readLine());
									x--;
									Suit trump = Suit.Hearts;
									response.setTrump(trump.getNext(x));
									break;
								default:
									break;
		                	}
		                		socket.write(mapper.writeValueAsBytes(response));
		                }
		                catch(Exception e) {
		                	e.printStackTrace();
		                }
			}
        	
        };
        t.start();
	}
	
	public static void main(String[] args) {
		try
        {
                // Start up the service.
                NIOService service = new NIOService();

                // Open our socket.
                socket = service.openSocket("localhost", 5217);

                // Use regular 1 byte header reader/writer
                socket.setPacketReader(new AsciiLinePacketReader());
                socket.setPacketWriter(new AsciiLinePacketWriter());
                
                // Start listening to the socket.
                socket.listen(new SocketObserver()
                {
                	@Override
                    public void connectionOpened(NIOSocket nioSocket)
                    {
                            System.out.println("connection opened");
                    }
                	@Override
		            public void packetSent(NIOSocket socket, Object tag)
		            {
		                System.out.println("Packet sent");
		            }
                	@Override
	            	public void packetReceived(NIOSocket socket, byte[] packet)
                    {
                            try
                            {
                                String message = new String(packet).trim();
	                                try {
	                                	JSONObject j = new JSONObject(message);
	                                	request = Request.valueOf(j.optString("currentRequest"));
	                                	if(!cards.containsAll(mapper.readValue(j.optString("cards"), new TypeReference<List<Card>>() { }))) {
	                                		cards = mapper.readValue(j.optString("cards"), new TypeReference<List<Card>>() { });
	                                		System.out.println("Cards : " + cards);
	                                	}
	                                	switch(request) {
				                		case Card: System.out.println("Enter Card (1-4):");
				                		requestNeeded();
				                			break;
										case Bid: System.out.println("Enter Bid :");
										requestNeeded();
											break;
										case Cards: System.out.println("Enter Cards (1-12),(1-12),(1-12),(1-12):");
										requestNeeded();
											break;
										case Null: System.out.println(j.optString("currentMessage"));
											break;
										case Trump: System.out.println("Enter Trump (1-4):");
										requestNeeded();
											break;
										default:
											break;
	                                	}
	                                }
	                                catch(Exception e) {
	                                	System.out.println(message);
	                                }
                            }
                            catch (Exception e)
                            {
                                    e.printStackTrace();
                            }
                    }
                	@Override
                    public void connectionBroken(NIOSocket nioSocket, Exception exception)
                    {
                            System.out.println("Connection failed.");
                            // Exit the program.
                            System.exit(-1);
                    }
            });
                // Read IO until process exits.
                while (true)
                {
                        service.selectNonBlocking();
                }
        }
        catch (Exception e)
        {
                e.printStackTrace();
        }
	}

}
