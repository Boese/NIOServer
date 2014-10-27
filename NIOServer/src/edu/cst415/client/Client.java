package edu.cst415.client;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import naga.NIOService;
import naga.NIOSocket;
import naga.SocketObserver;
import naga.packetreader.RegularPacketReader;

public class Client {
	
	static String foreignHostIp = "192.168.101.210";
	static int foreignHostPort = 2605;
	
	static String logFile1 = "Lab2.Scenario1.BoeseC.txt";
	static String logFile2 = "Lab2.Scenario2.BoeseC.txt";
	static String logFile3 = "Lab2.Scenario3.BoeseC.txt";
	static String logFileTest = "test.txt";
	
	static int RequestType = 2;
	static int ResponseDelay = 0;
	static int MessageCount = 0;
	static long initialTime = 0;
	
	static List<String> log;
	static List<String> sentMessages;
	static BufferedWriter writer;
	
	static NIOService service;
	static NIOSocket socket;
	
	static class EventLoop extends TimerTask {
		@Override
		public void run() {
			socket.write(getMessage(MessageCount));
			MessageCount++;
		}
	}
	
	static class WaitEvent extends TimerTask {
		@Override
		public void run() {
			socket.closeAfterWrite();
		}
	}
	
	static class ParseMessage {
		public Long getMessageVal(String message, int messageNum) {
			String[] messages = SplitUsingTokenizer(message, "|");
			return Long.parseLong(messages[messageNum]);
		}
		
		private String[] SplitUsingTokenizer(String subject, String delimiters) {
			   StringTokenizer strTkn = new StringTokenizer(subject, delimiters);
			   ArrayList<String> arrLis = new ArrayList<String>(subject.length());

			   while(strTkn.hasMoreTokens())
			      arrLis.add(strTkn.nextToken());

			   return arrLis.toArray(new String[0]);
			}
	}
	
	public static void main(String[] args) throws IOException {
		service = new NIOService();
		socket = service.openSocket(foreignHostIp,foreignHostPort);
		
		socket.setPacketReader(new RegularPacketReader(2, true));
		ParseMessage parseMessage = new ParseMessage();
		
		RandomAccessFile file = new RandomAccessFile(logFileTest, "rw");
		file.setLength(0);
		file.close();
		
		log = new ArrayList<String>();
		writer = new BufferedWriter(new FileWriter(logFileTest, true));
		
		sentMessages = new ArrayList<String>();
		
		socket.listen(new SocketObserver() {

			@Override
			public void connectionBroken(NIOSocket nioSocket,
					Exception exception) {
					try {
						for (String string : log) {
			        		try {
			        			writer.write("<CR>");
								writer.write(string);
								writer.write("<LF>");
								writer.newLine();
							} catch (IOException e) {}
						}
						writer.write(getTrailer());
						writer.close();
						System.out.println("Done");
						System.exit(0);
					} catch (IOException e) {
						System.exit(1);
					}
			}

			@Override
			public void connectionOpened(NIOSocket socket) {
				System.out.println("connected");
				initialTime = System.currentTimeMillis();
				new Timer().schedule(new WaitEvent(), (20 * 10000));
				
				for(int i = 0; i < 100; i++)
					new Timer().schedule(new EventLoop(), 50*(i+1));
			}
			
			@Override
			public void packetReceived(NIOSocket socket, byte[] packet) {
				System.out.println("Message Received : " + new String(packet));
				
				long messageReceivedNumber = parseMessage.getMessageVal(new String(packet),2);
				long sentTime = parseMessage.getMessageVal(sentMessages.get((int)messageReceivedNumber), 1);
				if((messageReceivedNumber % 10) == 0) {
					int delay = ((Long)System.currentTimeMillis()).intValue() - (int)sentTime;
					if(delay <= 3000)
		        		log.add(new String(packet).trim());
					else if(delay > 3000 && delay <= 20000) {
						log.add(sentMessages.get((int)messageReceivedNumber) + " - Stand In Response");
		        		log.add(new String(packet).trim() + " - Lat Response");
					}
		        	else {
		        		log.add(sentMessages.get((int)messageReceivedNumber) + " - Stand In Response");
		        		log.add(new String(packet).trim() + " - Spur Response");
		        	}
		        }
				else
					log.add(new String(packet).trim());
			}

			@Override
			public void packetSent(NIOSocket arg0, Object arg1) {
				System.out.println(sentMessages.get(MessageCount-1));
			}
			
		});
		
		while (true)
            service.selectNonBlocking();
	}
	
	public static byte[] getMessage(int num)
	{
		//if((num % 10) == 0)
		//	ResponseDelay = 4000;
		//else
			ResponseDelay += 10;
		String data = "REQ|" + ((Long)System.currentTimeMillis()).intValue() 
				+ "|" + Integer.toString(MessageCount) + "|BoeseC|21-0068|" + ResponseDelay + "|10.1.20.13|"
				+ socket.socket().getLocalPort() + "|577|" + foreignHostIp + "|2605|F|" + RequestType + "|";
		
		byte[] message = ByteBuffer.allocate(data.length() + 2)
				.putShort((short)data.length())
				.put(data.getBytes()).array();
		
		sentMessages.add(new String(message));
		log.add(new String(message));
		return message;
	}
	
	public static String getTrailer() {
		DateFormat day = new SimpleDateFormat("ddMMyyyy");
		DateFormat hour = new SimpleDateFormat("hhmmss");
		Date dateobj = new Date();
		
		return "<CR>" + day.format(dateobj) + "|" + hour.format(dateobj) + "|0|0|0<LF>";
	}
}
