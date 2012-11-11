package edu.illinois.cs425.mp3;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import edu.illinois.cs425.mp3.messages.GenericMessage;
import edu.illinois.cs425.mp3.messages.RequestMessage;

public class TCPServerThread extends Thread {
	private Process process = null;
	private Socket socket = null;

	public TCPServerThread(Socket socket, Process process) {
		super("TcpServerThread");
		this.socket = socket;
		this.process = process;
	}

	@Override
	public void run() {
		try {

			// 1. get Input and Output streams
			ObjectOutputStream out = new ObjectOutputStream(socket
					.getOutputStream());
			out.flush();

			ObjectInputStream in = new ObjectInputStream(socket
					.getInputStream());

			GenericMessage message = (GenericMessage) in.readObject();

			if(message instanceof RequestMessage) {
				((RequestMessage) message).setOutputStream(out);
			}

			message.processMessage(process);
			out.flush();
			out.close();
			in.close();
			socket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
