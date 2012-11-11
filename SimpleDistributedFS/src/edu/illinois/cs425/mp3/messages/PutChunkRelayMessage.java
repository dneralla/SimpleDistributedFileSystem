package edu.illinois.cs425.mp3.messages;

import edu.illinois.cs425.mp3.FileIdentifier;
import edu.illinois.cs425.mp3.Process;

public class PutChunkRelayMessage extends RequestMessage {
	FileIdentifier fid;
	public PutChunkRelayMessage(FileIdentifier fid) {
		this.fid = fid;
	}
	@Override
	public void processMessage(Process process) throws Exception {
		process.getFileIndexer().merge(fid);
		for(int i = 1; i< Process.REPLICA_COUNT; i++) {
			process.createReplica(fid);
         }
		outputStream.writeObject("Done");
	}
}
