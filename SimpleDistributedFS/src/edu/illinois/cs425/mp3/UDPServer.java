package edu.illinois.cs425.mp3;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import edu.illinois.cs425.mp3.messages.GenericMessage;
import edu.illinois.cs425.mp3.messages.Message;

public class UDPServer implements Server {
	private DatagramSocket socket;
	private Process process = null;
	public UDPServer(Process process) {
		this.process = process;
	}

	@Override
	public void stop() {
		socket.close();
	}

	public DatagramSocket getSocket() {
		return socket;
	}

	@Override
	public void start(int port) throws IOException {
		socket = new DatagramSocket(port);

		DatagramPacket packet;
		Message message;

		while (true) {
			byte receiveMessage[] = new byte[Message.MAX_MESSAGE_LENGTH];

			try {
				packet = new DatagramPacket(receiveMessage,
						receiveMessage.length);
				socket.receive(packet);
				port = packet.getPort();
				ByteArrayInputStream bis = new ByteArrayInputStream(
						packet.getData());
				ObjectInputStream in = null;
				in = new ObjectInputStream(bis);
				message = (Message) in.readObject();
				new MessageHandler(message, process).start();
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void sendMessage(GenericMessage message, InetAddress hostAddress,
			int hostPort) throws Exception {
		DatagramPacket packet = new DatagramPacket(
				((Message) message).toBytes(),
				((Message) message).toBytes().length, hostAddress, hostPort);
		socket.send(packet);
	}

	public void sendMessage(GenericMessage message, MemberNode node)
			throws Exception {
		sendMessage(message, node.getHostAddress(), Process.UDP_SERVER_PORT);
	}
}
