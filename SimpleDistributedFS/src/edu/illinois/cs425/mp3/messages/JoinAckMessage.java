package edu.illinois.cs425.mp3.messages;

import java.util.List;

import edu.illinois.cs425.mp3.MemberNode;
import edu.illinois.cs425.mp3.Process;

public class JoinAckMessage extends Message {

	private MemberNode neighbourNode;
	private List<MemberNode> globalList;

	public List<MemberNode> getGlobalList() {
		return globalList;
	}

	public void setGlobalList(List<MemberNode> globalList) {
		this.globalList = globalList;
	}

	public MemberNode getNeighbourNode() {
		return neighbourNode;
	}

	public void setNeighbourNode(MemberNode neighbourNode) {
		this.neighbourNode = neighbourNode;
	}

	public JoinAckMessage(MemberNode sourceNode, MemberNode centralNode,
			MemberNode alteredNode) {
		super(sourceNode, centralNode, alteredNode);
	}

	@Override
	public void processMessage(Process process) {
		try {
			process.getLogger().info(
					"Join Acknowledging and updating neighbor as "
							+ getNeighbourNode().getHostAddress());
			process.setNeighborNode(getNeighbourNode());
			process.setGlobalList(getGlobalList());
			MemberNode self = process.getNode();
			MulticastJoinMessage message = new MulticastJoinMessage(self, self,
					self);
			process.getMulticastServer().multicastUpdate(message);
			process.setMaster(getSourceNode());
		} catch (Exception e) {
			process.getLogger().info(
					"Updating neighbor or multicasting update failed");
			System.out.println("updating neighbor failed");
		}
	}

}
