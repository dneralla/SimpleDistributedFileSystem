package edu.illinois.cs425.mp3;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import edu.illinois.cs425.mp3.messages.GenericMessage;
import edu.illinois.cs425.mp3.messages.Message;

/*
 * Class for managing multicast server
 * and sending multicast messages.
 */
public class MulticastServer implements Server {


	private MulticastSocket multicastListenerSocket;
	private MulticastSocket multicastServerSocket;
	private InetAddress multicastGroup;
	private Process process;

	public int getMulticastServerPort() {
		return multicastServerSocket.getPort();
	}

	public MulticastSocket getMulticastListenerSocket() {
		return multicastListenerSocket;
	}

	public void setMulticastListenerSocket(
			MulticastSocket multicastListenerSocket) {
		this.multicastListenerSocket = multicastListenerSocket;
	}

	public MulticastSocket getMulticastServerSocket() {
		return multicastServerSocket;
	}

	public void setMulticastServerSocket(MulticastSocket multicastServerSocket) {
		this.multicastServerSocket = multicastServerSocket;
	}

	public Process getServer() {
		return process;
	}

	public void setServer(Process process) {
		this.process = process;
	}

	public void multicastUpdate(Message multicastMessage) throws Exception {
		sendMessage(multicastMessage, getMulticastGroup(), Process.MULTICAST_LISTENER_PORT);
	}

	public MulticastServer(Process process) throws IOException {
		this.process = process;
		try {
			this.multicastGroup = InetAddress.getByName(Process.MULTICAST_GROUP);
			this.multicastServerSocket = new MulticastSocket();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public InetAddress getMulticastGroup() {
		return multicastGroup;
	}

	@Override
	public synchronized void stop() throws IOException {
		multicastListenerSocket.leaveGroup(multicastGroup);
		multicastListenerSocket.close();
		multicastServerSocket.close();
	}

	@Override
	public void start(int port) throws IOException {
		// TODO Auto-generated method stub
		multicastServerSocket = new MulticastSocket();
		multicastListenerSocket = new MulticastSocket(port);
		multicastListenerSocket.joinGroup(multicastGroup);
		DatagramPacket packet;
		Message message;

		while (true) {
			byte receiveMessage[] = new byte[Message.MAX_MESSAGE_LENGTH];
			try {
				packet = new DatagramPacket(receiveMessage,
						receiveMessage.length);
				multicastListenerSocket.receive(packet);
				ByteArrayInputStream bis = new ByteArrayInputStream(
						packet.getData());
				ObjectInputStream in = null;
				in = new ObjectInputStream(bis);
				message = (Message) in.readObject();
				process.getLogger()
						.info(message.getDescription());
				new MessageHandler(message, process).start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
	}

//	@Override
//	public void sendMessage(GenericMessage multicastMessage, InetAddress multicastGroup, int multicastListenerPort) throws Exception {
//		process.getLogger()
//				.info("Sending multicast update of Node"
//						+ ((Message)multicastMessage).getAlteredNode().getHostAddress());
//
//		byte[] bytes = ((Message)multicastMessage).toBytes();
//		DatagramPacket packet = new DatagramPacket(bytes, bytes.length,
//				multicastGroup, multicastListenerPort);
//		getMulticastServerSocket().send(packet);
//	}
	
	@Override
	public void sendMessage(GenericMessage multicastMessage, InetAddress multicastGroup, int multicastListenerPort) throws Exception {
		process.getLogger()
				.info("Sending multicast update of Node"
						+ ((Message)multicastMessage).getAlteredNode().getHostAddress());

//		byte[] bytes = ((Message)multicastMessage).toBytes();
//		DatagramPacket packet = new DatagramPacket(bytes, bytes.length,
//				multicastGroup, multicastListenerPort);
//		getMulticastServerSocket().send(packet);
		for(MemberNode n: process.getGlobalList()) {
			process.getTcpServer().sendMessage(multicastMessage,n.getHostAddress() , Process.TCP_SERVER_PORT);
		}
	}
}
