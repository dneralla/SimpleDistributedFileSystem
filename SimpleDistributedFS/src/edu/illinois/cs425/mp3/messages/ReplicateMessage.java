package edu.illinois.cs425.mp3.messages;

import edu.illinois.cs425.mp3.MemberNode;
import edu.illinois.cs425.mp3.Process;

public class ReplicateMessage extends GenericMessage {
	MemberNode node;
	public ReplicateMessage(MemberNode node) {
		this.node = node;
	}
	@Override
	public void processMessage(Process process) throws Exception {
		process.replicateNode(node.getHostAddress());
	}

}
