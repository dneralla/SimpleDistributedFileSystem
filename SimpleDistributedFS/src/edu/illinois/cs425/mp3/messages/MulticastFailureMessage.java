package edu.illinois.cs425.mp3.messages;

import edu.illinois.cs425.mp3.MemberNode;
import edu.illinois.cs425.mp3.Process;

public class MulticastFailureMessage extends MulticastMessage {
	private static final long serialVersionUID = 1L;

	@Override
	public void processMessage(Process process) {
		try {
			if (mergeIntoMemberList(process)) {
				Message message = getNewRelayMessage(process.getNode(),
						getSourceNode(), getAlteredNode());
				process.getUdpServer().sendMessage(message, process.getNeighborNode());
				if (getAlteredNode().compareTo(process.getNeighborNode())) {
					process.setNeighborNode(getSourceNode());
				}
			}

		} catch (Exception e) {
			process.getLogger().info(
					"Multicast failure receieve  Processing failed of node"
							+ getAlteredNode().getHostAddress());
		}
	}

	public MulticastFailureMessage(MemberNode sourceNode,
			MemberNode centralNode, MemberNode alteredNode) {
		super(sourceNode, centralNode, alteredNode);
	}

	@Override
	public RelayFailureMessage getNewRelayMessage(MemberNode sourceNode,
			MemberNode centralNode, MemberNode alteredNode) {

		return new RelayFailureMessage(sourceNode, centralNode, alteredNode);
	}

}
