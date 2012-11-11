package edu.illinois.cs425.mp3.messages;

import java.util.ArrayList;
import java.util.List;

import edu.illinois.cs425.mp3.FileIdentifier;
import edu.illinois.cs425.mp3.FileSystemManager;
import edu.illinois.cs425.mp3.Process;

public class DeleteChunkMessage extends RequestMessage {
	/**
	 * 
	 */
	String fileName;
	public DeleteChunkMessage(String fileName) {
		this.fileName = fileName;
	}
	@Override
	public void processMessage(Process process) throws Exception {
		List<FileIdentifier> toBeDeleted = new ArrayList<FileIdentifier>();
		for(FileIdentifier fid: process.getFileIndexer().getFileList()) {
			if(fid.getSdfsFileName().equals(fileName)) {
				FileSystemManager.deleteChunk(fileName, fid.getChunkId());
			    toBeDeleted.add(fid);
			}
		}
		process.getFileIndexer().getFileList().removeAll(toBeDeleted);
		outputStream.writeObject("Done");
	}
}