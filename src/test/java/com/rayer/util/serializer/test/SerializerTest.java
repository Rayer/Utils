package com.rayer.util.serializer.test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SerializerTest {
	
	class FeedServerThread extends Thread {
		boolean isRunning = true;
		int port;
		ServerSocket serverSocket;
		
		FeedServerThread(int port) throws IOException {
			this.port = port;
			serverSocket = new ServerSocket(port);
		}
		
		@Override
		public void run() {
			
			while(isRunning) {
				
			}			
		}

	}; //Work as server

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		//fail("Not yet implemented");
	}

}
