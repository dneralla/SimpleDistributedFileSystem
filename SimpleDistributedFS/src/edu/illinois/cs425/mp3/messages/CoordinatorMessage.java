package edu.illinois.cs425.mp3.messages;

import java.util.ArrayList;
import java.util.List;

import edu.illinois.cs425.mp3.FileIdentifier;
import edu.illinois.cs425.mp3.FileIndexer;
import edu.illinois.cs425.mp3.MemberNode;
import edu.illinois.cs425.mp3.Process;

public class CoordinatorMessage extends GenericMessage {
	public MemberNode masterNode;
	public CoordinatorMessage(MemberNode masterNode) {
		this.masterNode = masterNode;
	}
	@Override
	public void processMessage(Process process) throws Exception {
		process.setMaster(masterNode);
		System.out.println("New master is:" + masterNode.getHostAddress());

		if(process.getNode().equals(masterNode)) {
			
			for(MemberNode node: process.getGlobalList()) {
				List<FileIdentifier> fileIdentifiers = (List<FileIdentifier>) process.getTcpServer().sendRequestMessage(new FileIndexerRequestMessage(null), node.getHostAddress(), process.TCP_SERVER_PORT);
				for(FileIdentifier fid: fileIdentifiers)
					process.getFileIndexer().merge(fid);
				System.out.println("File indexer received from: " + node.getHostAddress() + fileIdentifiers.size());
				process.getFileIndexer().print();	
			}
			process.ensureReplicaCount();
		}
		
	}
}
