package edu.illinois.cs425.mp3;

import java.io.IOException;
import java.net.InetAddress;

import edu.illinois.cs425.mp3.messages.GenericMessage;

public interface Server {
	public void start(int port) throws IOException;
	public void stop() throws Exception;
	public void sendMessage(GenericMessage message, InetAddress hostAddress, int hostPort) throws Exception;
}
