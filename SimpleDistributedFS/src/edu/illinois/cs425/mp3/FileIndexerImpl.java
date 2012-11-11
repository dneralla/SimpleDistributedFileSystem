package edu.illinois.cs425.mp3;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileIndexerImpl implements FileIndexer, java.io.Serializable {

	public static final int MAX = 1000;
	List<FileIdentifier> fileList;
	private final Process process;

	/*
	 * Constructor.
	 */
	public FileIndexerImpl(Process process) {
		this.process = process;
		this.fileList = new ArrayList<FileIdentifier>();
	}

	@Override
	public List<FileIdentifier> getFileList() {
		return fileList;
	}

	public void setFileList(List<FileIdentifier> fileList) {
		this.fileList = fileList;
	}

	@Override
	public synchronized void merge(FileIndexer fileIndexer) {
		Iterator<FileIdentifier> it = ((FileIndexerImpl) fileIndexer)
				.getFileList().iterator();
		while (it.hasNext())
			merge(it.next());
	}

	@Override
	public synchronized void merge(FileIdentifier fileIdentifier) {
		Iterator<FileIdentifier> it = fileList.iterator();
		FileIdentifier temp;
		while (it.hasNext()) {
			temp = it.next();
			if (temp.equals(fileIdentifier) && temp.getChunkAddress().equals(fileIdentifier.getChunkAddress()))
				return;
		}
		fileList.add(fileIdentifier);
	}

	@Override  
	public List<FileIdentifier> groupBy(String fileName, int chunkId) {
		Iterator<FileIdentifier> it = fileList.iterator();
		List<FileIdentifier> returnList = new ArrayList<FileIdentifier>();
		FileIdentifier temp;
		while (it.hasNext()) {
			temp = it.next();
			if (temp.getSdfsFileName().equals(fileName) && temp.getChunkId() == chunkId)
				returnList.add(temp);

		}

		return returnList;
	}

	@Override
	public List<InetAddress> getReplicas(String fileName) {
		Iterator<FileIdentifier> it = fileList.iterator();
		List<InetAddress> returnList = new ArrayList<InetAddress>();
		FileIdentifier temp;
		while (it.hasNext()) {
			temp = it.next();
			if (temp.getSdfsFileName().equals(fileName) && !returnList.contains(temp.getChunkAddress()))
				returnList.add(temp.getChunkAddress());
		}

		return returnList;
	}


	@Override
	public List<FileIdentifier> groupBy(InetAddress node) {
		Iterator<FileIdentifier> it = fileList.iterator();
		List<FileIdentifier> returnList = new ArrayList<FileIdentifier>();
		FileIdentifier temp;
		while (it.hasNext()) {
			temp = it.next();
			if (temp.getChunkAddress().equals(node))
				returnList.add(temp);

		}

		return returnList;
	}

	@Override
	public synchronized List<InetAddress> getSourceAndDestination(FileIdentifier id) {

		Iterator<MemberNode> it = this.process.getGlobalList().iterator();
		List<InetAddress> returnList = new ArrayList<InetAddress>();
		InetAddress temp;
		InetAddress bestSource = null;
		InetAddress bestDestination = null;
		int minSourceCost = MAX;
		int minDestCost = MAX;
		
			while (it.hasNext()) {
				temp = it.next().getHostAddress();
				if (this.isPresentAtNode(id,temp)) {
					int tempSrcCost = groupBy(temp).size();
					if (tempSrcCost <= minSourceCost) {
						minSourceCost = tempSrcCost;
						bestSource = temp;
					}
				} else {
					int tempDestCost = groupBy(temp).size();
					if (tempDestCost <= minDestCost) {
						minDestCost = tempDestCost;
						bestDestination = temp;
					}

				}
			}

		

		returnList.add(bestSource);
		returnList.add(bestDestination);
		return returnList;
	}

	@Override
	public boolean isPresent(FileIdentifier fileIdentifier) {
		Iterator<FileIdentifier> it = fileList.iterator();
		FileIdentifier temp;
		while (it.hasNext()) {
			temp = it.next();
			if (temp.equals(fileIdentifier))
				return true;
		}
		return false;
	}

	public boolean isPresentAtNode(FileIdentifier fid, InetAddress node) {
		Iterator<FileIdentifier> it = fileList.iterator();
		FileIdentifier temp;
		while (it.hasNext()) {
			temp = it.next();
			if (temp.getChunkAddress().equals(node) && temp.equals(fid))
				return true;
		}

		return false;
	}

	@Override
	public void delete(FileIdentifier fid) {
		int temp = -1;
		FileIdentifier id;
		for(int i = 0; i < getFileList().size(); i++) {
			id = getFileList().get(i);
			if(fid.equals(id) && id.getChunkAddress().equals(fid.getChunkAddress())) {
				temp = i;
			}
		}
		if(temp != -1)
			getFileList().remove(temp);
	}

	@Override
	public List<FileIdentifier> groupBy(String fileName) {
		Iterator<FileIdentifier> it = fileList.iterator();
		List<FileIdentifier> returnList = new ArrayList<FileIdentifier>();
		FileIdentifier temp;
		while (it.hasNext()) {
			temp = it.next();
			if (temp.getSdfsFileName().equals(fileName))
				returnList.add(temp);

		}
		return returnList;
	}

	public void print() {
		for(FileIdentifier fid: getFileList()) {
			System.out.println(fid.getSdfsFileName()+ " " + fid.getChunkId() + " " + fid.getChunkAddress());
		}
		
	}
}
