package edu.illinois.cs425.mp3;

/**
 * Class for holding all the properties of server
 * i.e hostname, hostport & timestamp
 */
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

public class MemberNode implements java.io.Serializable {

	private InetAddress hostAddress;
	private Date timeStamp;

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public MemberNode(InetAddress hostAddress) {
		this.hostAddress = hostAddress;
		this.timeStamp = new Date();
	}

	public InetAddress getHostAddress() {
		return hostAddress;
	}

	public void setHostAddress(InetAddress hostAddress) {
		this.hostAddress = hostAddress;
	}

	public MemberNode(String hostName) throws UnknownHostException {
		this(InetAddress.getByName(hostName));
	}

	public boolean compareTo(MemberNode node) {
		if (node == null)
			return false;
		if (this.getHostAddress().equals(node.getHostAddress()))
			return true;
		return false;
	}

	@Override
	public boolean equals(Object node) {
		if (node == null || !(node instanceof MemberNode))
			return false;
		if (hostAddress.equals(((MemberNode) node).getHostAddress()))
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = 0;
		hashCode = 31 * hashCode
				+ (hostAddress == null ? 0 : hostAddress.hashCode());
		return hashCode;
	}

	public String getDescription() {
		return hostAddress.toString() + ":" + Process.UDP_SERVER_PORT;
	}

	// server which joined older dominates
	public boolean isGreater(MemberNode node) {
		return this.timeStamp.before(node.getTimeStamp());
	}
}
