package edu.illinois.cs425.mp3.messages;

import java.net.InetAddress;

import edu.illinois.cs425.mp3.FileIdentifier;
import edu.illinois.cs425.mp3.FileSystemManager;
import edu.illinois.cs425.mp3.Process;

public class ChunkMessage extends RequestMessage {
	String chunk;
	int chunkId;
	String fileName;
	InetAddress source;

	public ChunkMessage(String chunk, int chunkId, String fileName, InetAddress source) {
		this.chunk = chunk;
		this.chunkId = chunkId;
		this.fileName = fileName;
		this.source = source;
	}

	@Override
	public void processMessage(Process process) throws Exception {
		process.getLogger().info("Saving the received chunk");
		FileSystemManager.putChunk(chunk, fileName, chunkId);
		FileIdentifier fid = new FileIdentifier(chunkId, fileName, process.getNode().getHostAddress());
		process.getFileIndexer().merge(fid);
		outputStream.writeObject("Done");
	}

	public String getChunk() {
		return chunk;
	}

	public String getFileName() {
		return fileName;
	}
}
