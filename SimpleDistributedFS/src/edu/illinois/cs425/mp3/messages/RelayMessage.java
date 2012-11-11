package edu.illinois.cs425.mp3.messages;

import edu.illinois.cs425.mp3.MemberNode;
import edu.illinois.cs425.mp3.Process;

public abstract class RelayMessage extends Message {

	public RelayMessage(MemberNode sourceNode, MemberNode centralNode,
			MemberNode alteredNode) {
		super(sourceNode, centralNode, alteredNode);
	}

	public abstract Message getNewMulticastMessage(MemberNode sourceNode,
			MemberNode centralNode, MemberNode alteredNode);

	@Override
	public void processMessage(Process process) {

		try {
			MemberNode self = process.getNode();
			if (mergeIntoMemberList(process)) {
				Message message = getNewMulticastMessage(self,
						getCentralNode(), getAlteredNode());
				process.getUdpServer().sendMessage(message,
						message.getSourceNode());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
