package edu.illinois.cs425.mp3.messages;

import edu.illinois.cs425.mp3.FileIdentifier;
import edu.illinois.cs425.mp3.Process;

public class FileIndexerRequestMessage extends RequestMessage {
	String fileName = null;
	public FileIndexerRequestMessage(String fileName) {
		this.fileName = fileName;
	}
	@Override
	public void processMessage(Process process) throws Exception {
		process.getLogger().info("Received the request for file indexer");
		if(fileName == null) {
			outputStream.writeObject(process.getFileIndexer().getFileList());
		}
		else
			outputStream.writeObject(process.getFileIndexer().groupBy(fileName));
	}
}
