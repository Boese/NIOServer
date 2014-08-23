package com.nio.echoserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class testEchoServerLoad {
	public static void main(String...args) throws IOException {
		List<NIOEchoClient> l = new ArrayList<NIOEchoClient>();
		for(int i = 0; i < 1000; i++) {
			l.add(new NIOEchoClient());
			System.out.println(i);
		}
		System.in.read();
	}
}
