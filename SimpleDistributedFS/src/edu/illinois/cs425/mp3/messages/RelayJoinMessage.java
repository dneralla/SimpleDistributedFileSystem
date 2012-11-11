package edu.illinois.cs425.mp3.messages;

import edu.illinois.cs425.mp3.MemberNode;

public class RelayJoinMessage extends RelayMessage {
	public RelayJoinMessage(MemberNode sourceNode, MemberNode centralNode,
			MemberNode alteredNode) {
		super(sourceNode, centralNode, alteredNode);
	}

	@Override
	public Message getNewMulticastMessage(MemberNode sourceNode,
			MemberNode centralNode, MemberNode alteredNode) {
		return new MulticastJoinMessage(sourceNode, centralNode, alteredNode);
	}

}
