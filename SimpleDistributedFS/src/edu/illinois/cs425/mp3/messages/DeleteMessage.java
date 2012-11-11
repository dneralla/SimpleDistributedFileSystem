package edu.illinois.cs425.mp3.messages;

import edu.illinois.cs425.mp3.Process;

public class DeleteMessage extends RequestMessage {

	String fileName;
	public DeleteMessage(String fileName) {
		this.fileName = fileName;
	}
	@Override
	public void processMessage(Process process) throws Exception {
		process.getTcpServer().sendMessage(new DeleteRelayMessage(fileName), process.getMaster().getHostAddress(), process.TCP_SERVER_PORT);
		outputStream.writeObject("Done");
	}

}
