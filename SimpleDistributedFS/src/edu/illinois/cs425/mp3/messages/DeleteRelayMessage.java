package edu.illinois.cs425.mp3.messages;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import edu.illinois.cs425.mp3.FileIdentifier;
import edu.illinois.cs425.mp3.FileIndexer;
import edu.illinois.cs425.mp3.Process;

public class DeleteRelayMessage extends RequestMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4428580877131437358L;
	String sdfsFileName;

	public DeleteRelayMessage(String sdfsFile) {
		this.sdfsFileName = sdfsFile;
	}

	@Override
	public void processMessage(Process process) throws IOException {
		InetAddress self = process.getNode().getHostAddress();
		System.out.println("in Delete Message");
		process.getFileIndexer().print();
		System.out.println("no of replicas"+ process.getFileIndexer().getReplicas(
				sdfsFileName).size());
		for (InetAddress host : process.getFileIndexer().getReplicas(
				sdfsFileName)) {
			System.out.println("Relicat present at: " + host);
			try {
				process.getTcpServer().sendRequestMessage(
						new DeleteChunkMessage(sdfsFileName), host,
						process.TCP_SERVER_PORT);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				//System.out.println("Chunk deleted");
		}
			
		List<FileIdentifier> fids = process.getFileIndexer().groupBy(sdfsFileName);
		for(FileIdentifier fid: fids)
			System.out.println(fid.getChunkAddress());
		process.getFileIndexer().getFileList().removeAll(fids);
		outputStream.writeObject("Done");
	}
}