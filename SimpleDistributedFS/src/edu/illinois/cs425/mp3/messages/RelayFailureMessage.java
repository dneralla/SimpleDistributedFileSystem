package edu.illinois.cs425.mp3.messages;

import edu.illinois.cs425.mp3.MemberNode;

public class RelayFailureMessage extends RelayMessage {
	@Override
	public Message getNewMulticastMessage(MemberNode sourceNode,
			MemberNode centralNode, MemberNode alteredNode) {
		return new MulticastFailureMessage(sourceNode, centralNode, alteredNode);
	}

	public RelayFailureMessage(MemberNode sourceNode, MemberNode centralNode,
			MemberNode alteredNode) {
		super(sourceNode, centralNode, alteredNode);
	}

}
