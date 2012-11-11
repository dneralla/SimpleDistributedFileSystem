package edu.illinois.cs425.mp3.messages;

import edu.illinois.cs425.mp3.MemberNode;

public abstract class MulticastMessage extends Message {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public MulticastMessage(MemberNode sourceNode, MemberNode centralNode,
			MemberNode alteredNode) {
		super(sourceNode, centralNode, alteredNode);
	}

	public abstract RelayMessage getNewRelayMessage(MemberNode sourceNode,
			MemberNode centralNode, MemberNode alteredNode);

}
