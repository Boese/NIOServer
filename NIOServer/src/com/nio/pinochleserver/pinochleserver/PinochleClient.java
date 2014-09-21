package com.nio.pinochleserver.pinochleserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.nio.pinochleserver.enums.Card;
import com.nio.pinochleserver.enums.JSONConvert;
import com.nio.pinochleserver.enums.Request;
import com.nio.pinochleserver.enums.Suit;

import naga.NIOService;
import naga.NIOSocket;
import naga.SocketObserver;
import naga.packetreader.AsciiLinePacketReader;
import naga.packetwriter.AsciiLinePacketWriter;

public class PinochleClient {
	PinochleClient(){}
	
	private static Request request = Request.Null;
	private static List<Card> cards = new ArrayList<Card>();
	private static JSONConvert jConvert = new JSONConvert();
	private static NIOSocket socket = null;
	
	private static void requestNeeded() {
		//Start new thread to capture input. Write to NIOSocket
		BufferedReader s = new BufferedReader(new InputStreamReader(System.in));
        Thread t = new Thread() {

			@Override
			public void run() {
		                try{
		                	JSONObject object = new JSONObject();
		                		switch(request) {
		                		case Card:
		                			int x = Integer.parseInt(s.readLine());
		                			x++;
		                			Card card = cards.get(x);
		                			object = jConvert.convertCardToJSON(card);
		                			socket.write(object.toString().getBytes());
		                			break;
								case Bid:
									int bid = Integer.parseInt(s.readLine());
									object = jConvert.convertBidToJSON(bid);
									socket.write(object.toString().getBytes());
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
									object = jConvert.convertCardsToJSON(tempCards);
									socket.write(object.toString().getBytes());
									break;
								case Null:
									break;
								case Trump: 
									Suit trump = Suit.Clubs.getNext(Integer.parseInt(s.readLine()));
									object = jConvert.convertTrumpToJSON(trump);
									socket.write(object.toString().getBytes());
									break;
								default:
									break;
		                	}
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
                socket = service.openSocket("76.14.226.220", 5217);

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
	                                	//System.out.println(j.toString());
	                                	request = Request.valueOf(j.optString("currentRequest"));
	                                	if(!cards.containsAll(jConvert.getCardsFromJSON(j.optJSONObject("Cards")))) {
	                                		cards = jConvert.getCardsFromJSON(j.optJSONObject("Cards"));
	                                		System.out.println(cards);
	                                	}
	                                	if(j.getBoolean("MyTurn")) {
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
	                                	else
	                                		System.out.println(j.get("currentMessage"));
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
