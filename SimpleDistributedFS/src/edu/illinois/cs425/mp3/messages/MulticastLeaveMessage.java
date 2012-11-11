package edu.illinois.cs425.mp3.messages;

import edu.illinois.cs425.mp3.MemberNode;
import edu.illinois.cs425.mp3.Process;

public class MulticastLeaveMessage extends MulticastMessage {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void processMessage(Process process) {

		try {

			process.getLogger().info(
					"Multicast leave message Processing  of node"
							+ getAlteredNode().getHostAddress());

			if (mergeIntoMemberList(process)) {
				Message message = getNewRelayMessage(process.getNode(),
						getSourceNode(), getAlteredNode());
				process.getUdpServer().sendMessage(message,
						process.getNeighborNode());
				if (getAlteredNode().compareTo(process.getNeighborNode())) {
					process.setNeighborNode(getSourceNode());
				}

			}

		} catch (Exception e) {
			process.getLogger().info(
					"Multicast leave message  Processing failed of node"
							+ getAlteredNode().getHostAddress());
			e.printStackTrace();
		}
	}

	@Override
	public RelayLeaveMessage getNewRelayMessage(MemberNode sourceNode,
			MemberNode centralNode, MemberNode alteredNode) {
		// TODO Auto-generated method stub
		return new RelayLeaveMessage(sourceNode, centralNode, alteredNode);
	}

	public MulticastLeaveMessage(MemberNode sourceNode, MemberNode centralNode,
			MemberNode alteredNode) {
		super(sourceNode, centralNode, alteredNode);
	}

}
