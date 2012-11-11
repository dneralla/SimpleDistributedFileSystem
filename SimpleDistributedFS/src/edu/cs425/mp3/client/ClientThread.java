package edu.cs425.mp3.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import edu.illinois.cs425.mp3.messages.GenericMessage;
import edu.illinois.cs425.mp3.messages.PutChunkMessage;

public class ClientThread extends Thread {
	char chunk[];
	String sdfsFileName;
	int chunkID;
	InetAddress bootStrapNode;
	public ClientThread(char[] chunk, int chunkID, InetAddress bootStrapNode, String sdfsFileName) {
		this.chunkID = chunkID;
		this.bootStrapNode = bootStrapNode;
		this.sdfsFileName = sdfsFileName;
		this.chunk = chunk;
	}
	
	@Override
	public void run() {
		try {
			Socket requestSocket = new Socket(bootStrapNode, SdfsClient.PORT);
			ObjectOutputStream out = new ObjectOutputStream(
					requestSocket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(
					requestSocket.getInputStream());
			out.flush();
			PutChunkMessage pMsg = new PutChunkMessage(new String(chunk),
					chunkID, sdfsFileName);
			System.out.println("Sending " + chunkID + " chunk");
			sendMessage(pMsg, out);
			in.read();
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendMessage(GenericMessage msg, ObjectOutputStream out) {
		try {
			out.writeObject(msg);
			out.flush();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
}
