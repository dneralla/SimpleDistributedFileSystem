package edu.illinois.cs425.mp3.messages;

import edu.illinois.cs425.mp3.FileIdentifier;
import edu.illinois.cs425.mp3.FileSystemManager;
import edu.illinois.cs425.mp3.Process;

public class PutChunkMessage extends RequestMessage {
	String chunk;
	int chunkId;
	String fileName;

	public PutChunkMessage(String chunk, int chunkId, String fileName) {
		this.chunk = chunk;
		this.chunkId = chunkId;
		this.fileName = fileName;
	}

	@Override
	public void processMessage(Process process) throws Exception {
		
		FileSystemManager.putChunk(chunk, fileName, chunkId);
		FileIdentifier fid = new FileIdentifier(chunkId, fileName, process.getNode().getHostAddress());
		process.getFileIndexer().merge(fid); 
		outputStream.writeObject(process.getTcpServer().sendRequestMessage(new PutChunkRelayMessage(fid), process.getMaster().getHostAddress(), Process.TCP_SERVER_PORT));
	}

	public String getChunk() {
		return chunk;
	}

	public String getFileName() {
		return fileName;
	}
}
