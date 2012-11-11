package edu.illinois.cs425.mp3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.illinois.cs425.mp3.messages.GenericMessage;

public class TCPServer implements Server {
	Process process;
	ServerSocket serverSocket;
	ObjectOutputStream out;

	boolean keepListening;
	private static final long timeOut = 100000;

	public TCPServer(Process process) {
		this.process = process;
	}

	@Override
	public void start(int serverPort) {
		try {
			keepListening = true;
			serverSocket = new ServerSocket(serverPort);
			while (keepListening) {
				new TCPServerThread(serverSocket.accept(), process).start();
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + serverPort);
			System.exit(1);
		}
	}

	@Override
	public void stop() throws IOException {
		serverSocket.close();
	}

	@Override
	public void sendMessage(GenericMessage message, InetAddress host, int port) {
		ObjectOutputStream out = null;
		ObjectInputStream in = null;

		try {
			// 1. creating a socket to connect to the server
			Socket requestSocket = new Socket(host, port);

			// 2. get Input and Output streams
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();

			in = new ObjectInputStream(requestSocket.getInputStream());

			// 3: Communicating with the server
			out.writeObject(message);
		} catch (UnknownHostException unknownHost) {
			System.err.println("Host name unkown!");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public Object sendRequestMessage(GenericMessage message, InetAddress host, int port) throws ClassNotFoundException {
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
        Object response = null;
		try {
			// 1. creating a socket to connect to the server
			Socket requestSocket = new Socket(host, port);

			// 2. get Input and Output streams
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();

			in = new ObjectInputStream(requestSocket.getInputStream());

			// 3: Communicating with the server

			out.writeObject(message);

			out.flush();

//            (new Thread() {
//            	@Override
//				public void run() {
//                long time = System.currentTimeMillis();
//            	while(System.currentTimeMillis() - time > TCPServer.timeOut) {
//            		return;
//            	 }
//            	}
//            }).start();
            response = in.readObject();
            
		} catch (UnknownHostException unknownHost) {
			System.err.println("Host name unkown!");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
		return response;
	}
}
