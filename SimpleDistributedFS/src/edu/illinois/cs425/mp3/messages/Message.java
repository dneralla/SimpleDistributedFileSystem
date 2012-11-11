package edu.illinois.cs425.mp3.messages;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.List;

import edu.illinois.cs425.mp3.MemberNode;
import edu.illinois.cs425.mp3.Process;

/*
 * Generic class for handling all UDP messages.
 * Depending on the type(class) of message received,
 * the corresponding processMesmethod is invoked.
 */
public abstract class Message extends GenericMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int MAX_MESSAGE_LENGTH = 1024;

	private InetAddress host;
	private int port;
	private int multicastPort;
	private InetAddress multicastGroup;
	private InetAddress originalHost;
	private int originalPort;

	// sourceNode always contains the details of message sender
    // centralNode always contains the details of node who multicasts the update
	// alteredNode always contains the details of joined/failed/left node
	private MemberNode sourceNode;

	private MemberNode centralNode;

	private MemberNode alteredNode;

	public Message(MemberNode sourceNode, MemberNode centralNode,
			MemberNode alteredNode) {
		this.sourceNode = sourceNode;
		this.centralNode = centralNode;
		this.alteredNode = alteredNode;
	}

	public MemberNode getCentralNode() {
		return centralNode;
	}

	public void setCentralNode(MemberNode centralNode) {
		this.centralNode = centralNode;
	}

	public MemberNode getAlteredNode() {
		return alteredNode;
	}

	public void setAlteredNode(MemberNode alteredNode) {
		this.alteredNode = alteredNode;
	}

	public MemberNode getSourceNode() {
		return sourceNode;
	}

	public void setSourceNode(MemberNode sourceNode) {
		this.sourceNode = sourceNode;
	}

	public InetAddress getOriginalHost() {
		return originalHost;
	}

	public void setOriginalHost(InetAddress originalHost) {
		this.originalHost = originalHost;
	}

	public int getOriginalPort() {
		return originalPort;
	}

	public void setOriginalPort(int originalPort) {
		this.originalPort = originalPort;
	}

	public int getMulticastPort() {
		return multicastPort;
	}

	public void setMulticastPort(int multicastPort) {
		this.multicastPort = multicastPort;
	}

	public InetAddress getMulticastGroup() {
		return multicastGroup;
	}

	public void setMulticastGroup(InetAddress multicastGroup) {
		this.multicastGroup = multicastGroup;
	}

	public byte[] toBytes() throws Exception {
		byte[] yourBytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(this);
			yourBytes = bos.toByteArray();
		} finally {
			out.close();
			bos.close();
		}

		return yourBytes;
	}

	public InetAddress getHost() {
		return host;
	}

	public void setHost(InetAddress host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDescription() {
		return "Source Node : " +  getSourceNode().getDescription()
				+ "Central Node: " + getCentralNode().getDescription()
				+ "Altered Node: " + getAlteredNode().getDescription();
	}

	public boolean checkIsIntructionJoinVariant() {
		return this instanceof JoinMessage
				|| this instanceof MulticastJoinMessage
				|| this instanceof RelayJoinMessage;
	}

	public synchronized boolean mergeIntoMemberList(Process process) {
		
		int index = process.getGlobalList().indexOf(getAlteredNode());
		MemberNode matchingNode = index == -1 ? null : process.getGlobalList().get(index);
		if (checkIsIntructionJoinVariant()) {
			if (matchingNode == null
					&&
					/* && checkHasJoinArrivedLate() */
					(process.getRecentLeftNode() == null
							|| !process.getRecentLeftNode()
									.equals(getAlteredNode()) || getAlteredNode()
							.getTimeStamp()
							.after(process
									.getRecentLeftNode().getTimeStamp()))) {
				process.getGlobalList().add(getAlteredNode());
				return true;
			} else if (matchingNode.getTimeStamp().before(
					getAlteredNode().getTimeStamp())) {
				matchingNode.setTimeStamp(getAlteredNode().getTimeStamp());
				return true;
			}
		} else {

			if (matchingNode != null
					&& matchingNode.getTimeStamp().before(
							getAlteredNode().getTimeStamp())) {
				process.getGlobalList().remove(getAlteredNode());
				process.setRecentLeftNode(getAlteredNode());
				return true;
			} else {
				process.setRecentLeftNode(getAlteredNode());
			}

		}
		return false;
	}
}
