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
		if(fileName == null) {
			for(FileIdentifier fid: process.getFileIndexer().getFileList()) {
				System.out.println("File name:" + fid.getSdfsFileName());
			}
			outputStream.writeObject(process.getFileIndexer().getFileList());
			System.out.println("Got request for file indexer");
		}
		else
			outputStream.writeObject(process.getFileIndexer().groupBy(fileName));
	}
}
