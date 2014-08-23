package com.nio.validationserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Scanner;

import naga.NIOService;
import naga.NIOSocket;
import naga.SocketObserver;
import naga.packetreader.RegularPacketReader;
import naga.packetwriter.RegularPacketWriter;

public class ValidationClient
{
        /**
         * Make a login request to the server.
         *
         * @param args assumed to be 4 strings representing host, port, account and password.
         */
        public static void main(String... args)
        {
                try
                {
                        //Localhost on port 5218
                        String host = "localhost";
                        int port = 5218;
                        
                        //read username & password
                        Scanner scanner = new Scanner(System.in);
                        System.out.println("username: ");
                        String account = scanner.nextLine();
                        System.out.println("password: ");
                        String password = scanner.nextLine();
                        scanner.close();
                        
                        // Prepare the login packet, packing two UTF strings together
                        // using a data output stream.
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        DataOutputStream dataStream = new DataOutputStream(stream);
                        dataStream.writeUTF(account);
                        dataStream.writeUTF(password);
                        dataStream.flush();
                        final byte[] content = stream.toByteArray();
                        dataStream.close();

                        // Start up the service.
                        NIOService service = new NIOService();

                        // Open our socket.
                        NIOSocket socket = service.openSocket(host, port);

                        // Use regular 1 byte header reader/writer
                        socket.setPacketReader(new RegularPacketReader(1, true));
                        socket.setPacketWriter(new RegularPacketWriter(1, true));

                        // Start listening to the socket.
                        socket.listen(new SocketObserver()
                        {
                                public void connectionOpened(NIOSocket nioSocket)
                                {
                                        System.out.println("Sending login...");
                                        nioSocket.write(content);
                                }

                                public void packetReceived(NIOSocket socket, byte[] packet)
                                {
                                        try
                                        {
                                                // Read the UTF-reply and print it.
                                                String reply = new DataInputStream(new ByteArrayInputStream(packet)).readUTF();
                                                System.out.println("Reply was: " + reply);
                                                // Exit the program.
                                                System.exit(0);
                                        }
                                        catch (Exception e)
                                        {
                                                e.printStackTrace();
                                        }
                                }

                                public void connectionBroken(NIOSocket nioSocket, Exception exception)
                                {
                                        System.out.println("Connection failed.");
                                        // Exit the program.
                                        System.exit(-1);
                                }

								@Override
								public void packetSent(NIOSocket arg0,
										Object arg1) {
									// TODO Auto-generated method stub
									
								}
                        });
                        // Read IO until process exits.
                        while (true)
                        {
                                service.selectBlocking();
                        }
                }
                catch (Exception e)
                {
                        e.printStackTrace();
                }
        }

}
