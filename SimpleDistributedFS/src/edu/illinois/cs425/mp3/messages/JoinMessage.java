package edu.illinois.cs425.mp3.messages;

import edu.illinois.cs425.mp3.MemberNode;
import edu.illinois.cs425.mp3.Process;

public class JoinMessage extends Message {
	private static final long serialVersionUID = 1L;

	public JoinMessage(MemberNode sourceNode, MemberNode centralNode,
			MemberNode alteredNode) {
		super(sourceNode, centralNode, alteredNode);
	}

	@Override
	public void processMessage(Process process) {
		try {
			process.getLogger().info(
					"Servicing Join request of node"
							+ getSourceNode().getHostAddress());

			mergeIntoMemberList(process);

			MemberNode oldNeighbourNode = process.getNeighborNode();
			process.setNeighborNode(getSourceNode());

			Message ackMessage = new JoinAckMessage(process.getNode(), null,
					null);

			((JoinAckMessage) ackMessage).setNeighbourNode(oldNeighbourNode);
			((JoinAckMessage) ackMessage)
					.setGlobalList(process.getGlobalList());

			process.getUdpServer().sendMessage(ackMessage, getSourceNode());

		} catch (Exception e) {

			process.getLogger().info(
					"Processing join failed of node"
							+ getSourceNode().getHostAddress());
			System.out.println("Processing join failed");
			e.printStackTrace();
		}

	}
}
