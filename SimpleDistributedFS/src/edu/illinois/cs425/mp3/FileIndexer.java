package edu.illinois.cs425.mp3;

import java.net.InetAddress;
import java.util.List;

public interface FileIndexer {
	public List<FileIdentifier> getFileList();

	public void merge(FileIndexer fileIndexer);

	public void merge(FileIdentifier fileIdentifier);

	public List<FileIdentifier> groupBy(String fileName);

	public List<FileIdentifier> groupBy(String fileName, int chunkId);

	public List<FileIdentifier> groupBy(InetAddress node);

	public List<InetAddress> getSourceAndDestination(FileIdentifier id);

	public boolean isPresent(FileIdentifier fileIdentifier);

	public void delete(FileIdentifier fid);
	
	public void print();

	List<InetAddress> getReplicas(String fileName);

}
