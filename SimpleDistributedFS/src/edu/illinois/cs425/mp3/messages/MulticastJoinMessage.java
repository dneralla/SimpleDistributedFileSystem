package edu.illinois.cs425.mp3.messages;

import edu.illinois.cs425.mp3.MemberNode;
import edu.illinois.cs425.mp3.Process;

public class MulticastJoinMessage extends MulticastMessage {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public MulticastJoinMessage(MemberNode sourceNode, MemberNode centralNode,
			MemberNode alteredNode) {
		super(sourceNode, centralNode, alteredNode);
	}

	@Override
	public void processMessage(Process process) {
		try {
			process.getLogger().info(
					"Multicast join message Processing of node"
							+ getAlteredNode().getHostAddress());
			if (mergeIntoMemberList(process)) {
				Message message = getNewRelayMessage(process.getNode(),
						getSourceNode(), getAlteredNode());
				process.getUdpServer().sendMessage(message,
						process.getNeighborNode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			process.getLogger().info(
					"Multicast Join Message processing failed  of node"
							+ getAlteredNode().getHostAddress());
		}
	}

	@Override
	public RelayMessage getNewRelayMessage(MemberNode sourceNode,
			MemberNode centralNode, MemberNode alteredNode) {
		RelayJoinMessage message = new RelayJoinMessage(sourceNode,
				centralNode, alteredNode);
		return message;
	}

}
