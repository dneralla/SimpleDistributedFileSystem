package edu.illinois.cs425.mp3.messages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.illinois.cs425.mp3.FileIdentifier;
import edu.illinois.cs425.mp3.Process;

public class GetFileMessage extends RequestMessage {

	String sdfsFileName;

	public GetFileMessage(String sdfsFileName) {
		this.sdfsFileName = sdfsFileName;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void processMessage(Process process) throws ClassNotFoundException,
			IOException {
		process.getLogger().info("Received the get request for "+sdfsFileName);
		ArrayList<FileIdentifier> fileChunkReplicas = null;
		String chunk;
		do {
		} while ((fileChunkReplicas = (ArrayList<FileIdentifier>) process
				.getTCPServer().sendRequestMessage(
						new FileIndexerRequestMessage(sdfsFileName),
						process.getMaster().getHostAddress(),
						process.TCP_SERVER_PORT)) == null);
		Collections.sort(fileChunkReplicas, new Comparator<FileIdentifier>() {
			@Override
			public int compare(FileIdentifier f1, FileIdentifier f2) {
				return f1.getChunkId() - f2.getChunkId();
			}
		});
		int chunkId = 0;
		
		outputStream.writeObject(((Integer)(fileChunkReplicas.get(fileChunkReplicas.size()-1).getChunkId() + 1)));
		for (FileIdentifier fid : fileChunkReplicas) {
			if (fid.getChunkId() == chunkId - 1)
				continue;
			chunk = (String) process.getTcpServer().sendRequestMessage(
					new GetChunkMessage(chunkId, sdfsFileName),
					fid.getChunkAddress(), Process.TCP_SERVER_PORT);
			
			outputStream.writeObject(chunk);
			if (chunk != null)
				chunkId++;
		}
	}
}