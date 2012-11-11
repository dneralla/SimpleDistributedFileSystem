package edu.illinois.cs425.mp3;

import java.net.InetAddress;

public class FileIdentifier implements java.io.Serializable {

	private int chunkId;
	private String sdfsFileName;
	private InetAddress chunkAddress;

	public FileIdentifier(int chunkId, String sdfsFileName, InetAddress chunkAddress) {
		this.chunkId = chunkId;
		this.sdfsFileName = sdfsFileName;
		this.chunkAddress = chunkAddress;
	}

	public int getChunkId() {
		return chunkId;
	}

	public void setChunkId(int chunkId) {
		this.chunkId = chunkId;
	}

	public String getSdfsFileName() {
		return sdfsFileName;
	}

	public void setSdfsFileName(String sdfsFileName) {
		this.sdfsFileName = sdfsFileName;
	}

	public InetAddress getChunkAddress() {
		return chunkAddress;
	}

	public void setChunkAddress(InetAddress chunkAddress) {
		this.chunkAddress = chunkAddress;
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof FileIdentifier))
			return false;
		else
			return chunkId==((FileIdentifier)o).getChunkId() && sdfsFileName.equals(((FileIdentifier)o).sdfsFileName);

	}

}
