package edu.illinois.cs425.mp3.messages;

import edu.illinois.cs425.mp3.FileSystemManager;
import edu.illinois.cs425.mp3.Process;

public class GetChunkMessage extends RequestMessage {

	String fileName;
	int chunkId;
	public GetChunkMessage(int chunkId, String fileName) {
		this.fileName = fileName;
		this.chunkId = chunkId;
	}
	@Override
	public void processMessage(Process process) throws Exception {
		process.getLogger().info("Sending the "+chunkId+" chunk of:"+this.fileName);
		outputStream.writeObject(FileSystemManager.getChunk(fileName, chunkId));
	}

}
