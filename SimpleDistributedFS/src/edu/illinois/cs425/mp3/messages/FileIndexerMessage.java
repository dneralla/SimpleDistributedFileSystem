package edu.illinois.cs425.mp3.messages;

import edu.illinois.cs425.mp3.FileIndexer;
import edu.illinois.cs425.mp3.Process;

public class FileIndexerMessage extends GenericMessage {
	private final FileIndexer fileIndexer;

	public FileIndexerMessage(FileIndexer fileIndexer) {
		this.fileIndexer = fileIndexer;
	}

	@Override
	public void processMessage(Process process) throws Exception {
		  process.getFileIndexer().merge(fileIndexer);
	}
}
