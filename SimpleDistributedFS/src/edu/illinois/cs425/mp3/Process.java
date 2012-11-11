package edu.illinois.cs425.mp3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import edu.illinois.cs425.mp3.messages.PutChunkMessage;
import edu.illinois.cs425.mp3.messages.ChunkTransferMessage;
import edu.illinois.cs425.mp3.messages.CoordinatorMessage;
import edu.illinois.cs425.mp3.messages.GenericMessage;
import edu.illinois.cs425.mp3.messages.HeartBeatMessage;
import edu.illinois.cs425.mp3.messages.JoinMessage;
import edu.illinois.cs425.mp3.messages.LeaveMessage;
import edu.illinois.cs425.mp3.messages.Message;

/*
 * Class for starting server. For each new request, a separate thread is spawned
 * for processing it. For sending heartbeats periodically, a separate thread is
 * created and it sends messages if flag sendHeartBeat is set. All variables which
 * are accessed by multiple threads are declared as volatile.
 */
public class Process {
	public static final int MULTICAST_LISTENER_PORT = 4446;
	public static final String MULTICAST_GROUP = "230.0.0.1";

	public static final int UDP_SERVER_PORT = 4447;
	public static final int TCP_SERVER_PORT = 4448;
	public static final int REPLICA_COUNT = 2;
	public static final int CHUNK_SIZE = 1024 * 1024;

	private int chunkSize = 12;

	private MemberNode self;
	private MemberNode master;
	private volatile MemberNode neighborNode;
	private volatile long lastReceivedHeartBeatTime;
	private volatile List<MemberNode> globalList;
	private volatile Logger logger;
	private volatile MemberNode recentLeftNode;
	private MemberNode heartbeatSendingNode;
	private boolean isInRing = false;

	private volatile FileIndexer fileIndexer;
	private final FileSystemManager fsManager;

	private MulticastServer multicastServer;
	private TCPServer tcpServer;
	private final UDPServer udpServer;
	Thread multiCastServerThread;

	public TCPServer getTcpServer() {
		return tcpServer;
	}

	public void setTcpServer(TCPServer tcpServer) {
		this.tcpServer = tcpServer;
	}

	public UDPServer getUdpServer() {
		return udpServer;
	}

	public MemberNode getMaster() {
		return master;
	}

	public void setMaster(MemberNode master) {
		this.master = master;
	}

	public MulticastServer getMulticastServer() {
		return multicastServer;
	}

	public void setMulticastServer(MulticastServer multicastServer) {
		this.multicastServer = multicastServer;
	}

	public TCPServer getTCPServer() {
		return tcpServer;
	}

	public void setTCPServer(TCPServer tcpServer) {
		this.tcpServer = tcpServer;
	}

	public boolean isInRing() {
		return isInRing;
	}

	public void setInRing(boolean isInRing) {
		this.isInRing = isInRing;
	}

	public MemberNode getRecentLeftNode() {
		return recentLeftNode;
	}

	public void setRecentLeftNode(MemberNode recentLeftNode) {
		this.recentLeftNode = recentLeftNode;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public List<MemberNode> getGlobalList() {
		return globalList;
	}

	public void setGlobalList(List<MemberNode> globalList) {
		this.globalList = globalList;
	}

	public MemberNode getNeighborNode() {
		return neighborNode;
	}

	public void setNeighborNode(MemberNode neighborNode) {
		this.neighborNode = neighborNode;
	}

	private Process() throws IOException {
		this.self = new MemberNode(InetAddress.getLocalHost().getHostName());
		this.neighborNode = self;
		this.recentLeftNode = null;
		this.heartbeatSendingNode = self;
		this.master = null;
		this.globalList = new ArrayList<MemberNode>();
		this.globalList.add(self);
		this.udpServer = new UDPServer(this);
		this.multicastServer = new MulticastServer(this);
		this.tcpServer = new TCPServer(this);
		this.fsManager = new FileSystemManager();
		this.fileIndexer = new FileIndexerImpl(this);
	}

	public long getLastReceivedHeartBeatTime() {
		return lastReceivedHeartBeatTime;
	}

	public void setLastReceivedHeartBeatTime(long lastReceivedHeartBeat) {
		this.lastReceivedHeartBeatTime = lastReceivedHeartBeat;
	}

	public MemberNode getNode() {
		return self;
	}

	public synchronized void logNetworkData(Message m) throws Exception {
		File file = new File("Server_"
				+ InetAddress.getLocalHost().getHostName() + "_"
				+ String.valueOf(getNode()) + ".network");
		FileWriter fw = new FileWriter(file.getName(), true);
		if (!(m instanceof HeartBeatMessage))
			fw.write(m.toBytes().toString());
		fw.close();

	}

	public synchronized void printNodes() {
		System.out.print("[");
		for (MemberNode node : getGlobalList())
			if (node != null)
				System.out.print(node.getHostAddress() + " " + UDP_SERVER_PORT
						+ ", ");
		System.out.println("]");
	}

	public MemberNode getHeartbeatSendingNode() {
		return heartbeatSendingNode;
	}

	public void setHeartbeatSendingNode(MemberNode heartbeatSendingNode) {
		this.heartbeatSendingNode = heartbeatSendingNode;
	}

	@SuppressWarnings("null")
	public static void main(String[] args) throws Exception {

		Process process = new Process();

		FileHandler fileTxt = new FileHandler("Server_"
				+ InetAddress.getLocalHost().getHostName() + "_"
				+ Process.UDP_SERVER_PORT + ".log");
		SimpleFormatter formatterTxt = new SimpleFormatter();

		// Assumption: Master server starts on linux5 machine and at 5095 port
		process.setMaster(new MemberNode("linux5.ews.illinois.edu"));

		// Create Logger
		LogManager lm = LogManager.getLogManager();
		lm.reset();
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		logger.setLevel(Level.INFO);

		fileTxt.setFormatter(formatterTxt);
		logger.addHandler(fileTxt);

		lm.addLogger(logger);

		File file = new File("Server_"
				+ InetAddress.getLocalHost().getHostName() + "_"
				+ Process.UDP_SERVER_PORT + ".network");
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();

		try {
			if (process.getMaster().getHostAddress()
					.equals(InetAddress.getLocalHost())) {
				System.out.println("I'm tracker node");
			}
			process.startServers();
			process.setLogger(logger);

		} catch (SocketException e) {
			System.out.println("Error: Unable to open socket");
			System.exit(-1);
		} catch (IOException e) {
			System.out.println("Could not listen on port.");
			System.exit(-1);
		} catch (Exception e) {
			System.out.println("Byte Construction failed");
			System.exit(-1);

		}
		logger.info("Staring logging");
		// starting heartbeat thread
		System.out.println("InMain");
		int T = 100;
		new HeartBeatServiceThread(process, T).start();
		new FailureDetectorThread(process, 5 * T).start();
		process.new UserCommandExecutor().start();

	}

	private void startServers() throws IOException {
		new Thread() {
			public void run() {
				try {
					udpServer.start(UDP_SERVER_PORT);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		multiCastServerThread = new Thread() {
			public void run() {
				try {
					multicastServer.start(MULTICAST_LISTENER_PORT);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		multiCastServerThread.start();
		new Thread() {
			public void run() {
				tcpServer.start(TCP_SERVER_PORT);
			}
		}.start();
	}

	public void sendMessage(GenericMessage message, MemberNode node)
			throws Exception {
		tcpServer.sendMessage(message, node.getHostAddress(), TCP_SERVER_PORT);
	}

	public class UserCommandExecutor extends Thread {
		@Override
		public void run() {
			try {
				String inputLine;
				BufferedReader in = new BufferedReader(new InputStreamReader(
						System.in));
				System.out.print("[Please Enter Command]$ ");
				while ((inputLine = in.readLine()) != null) {
					if (inputLine.startsWith("join")) {
						getNode().setTimeStamp(new Date());
						Message message = new JoinMessage(getNode(), null,
								getNode());
						getLogger().info(
								"Join message sending to"
										+ master.getHostAddress());

						boolean isMasterUp = (inputLine.indexOf(" ") == -1) ? true
								: false;
						if (isMasterUp) {
							sendMessage(message, master);
						} else {
							String name = inputLine.substring(inputLine
									.indexOf(" ") + 1);
							int port = Process.TCP_SERVER_PORT;
							MemberNode temporaryMaster = new MemberNode(name);
							sendMessage(message, temporaryMaster);
						}
						getLogger().info("Join message Sent");

					} else if (inputLine.equals("leave")) {
						getNode().setTimeStamp(new Date());
						LeaveMessage leaveMessage = new LeaveMessage(getNode(),
								null, getNode());
						sendMessage(leaveMessage, getNeighborNode());
						multiCastServerThread.stop();
						setNeighborNode(getNode());

						setInRing(false);
						getLogger().info("Leave Message sent");
					} else if (inputLine.startsWith("print")) {
						printNodes();
					} else if (inputLine.equals("next")) {
						System.out.println("Neighbour Port: "
								+ getNeighborNode().getDescription());
					} else if (inputLine.equals("fileIndexer")) {
						getFileIndexer().print();
					} else if (inputLine.equals("master")) {
						System.out.println(master.getHostAddress());
					} else if (inputLine.equals("help")) {
						System.out
								.println("Usage: [join|leave] <hostname:hostport>");
					} else if (inputLine.equals("exit")) {
						System.exit(0);
					}

					System.out.print("[Please Enter Command]$ ");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public MemberNode electMaster() {
		MemberNode master = getNode();
		for (MemberNode node : getGlobalList()) {
			if (node.isGreater(master)) {
				master = node;
			}
		}
		getLogger().info("New master is elected: " + master.getHostAddress());
		return master;
	}

	public void startMasterElection() {
		CoordinatorMessage message = new CoordinatorMessage(electMaster());
		getLogger().info("Sending co-ordinator messages");
		for (MemberNode node : getGlobalList()) {
			tcpServer.sendMessage(message, node.getHostAddress(),
					TCP_SERVER_PORT);
		}

	}

	public int getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	public void createReplica(FileIdentifier fid) throws ClassNotFoundException {
		ChunkTransferMessage message;
		List<InetAddress> nodes;
		nodes = fileIndexer.getSourceAndDestination(fid);
		if (nodes.get(0) == null || nodes.get(1) == null)
			return;
		getLogger().info(
				"Creating the replica for " + fid.getChunkId() + " chunk of "
						+ fid.getSdfsFileName() + " at "
						+ nodes.get(1).getHostAddress());

		message = new ChunkTransferMessage(fid.getSdfsFileName(),
				fid.getChunkId(), nodes.get(1));

		tcpServer.sendRequestMessage(message, nodes.get(0), TCP_SERVER_PORT);

		fileIndexer.merge(new FileIdentifier(fid.getChunkId(), fid
				.getSdfsFileName(), nodes.get(1)));
	}

	public void ensureReplicaCount() throws ClassNotFoundException {

		getLogger().info("Started Creating the replica of failed master node");
		Map<FileIdentifier, Integer> m = new HashMap<FileIdentifier, Integer>();
		List<FileIdentifier> uniqueChunks = new ArrayList<FileIdentifier>();

		for (FileIdentifier fid : fileIndexer.getFileList()) {
			Integer value = m.get(fid);
			if (value != null)
				m.put(fid, value + 1);
			else {
				m.put(fid, 1);
				uniqueChunks.add(fid);
			}
		}
		for (FileIdentifier fid : uniqueChunks)
			for (int j = 0; j < REPLICA_COUNT - m.get(fid); j++) {
				createReplica(fid);
			}
		getLogger().info("Created the replica of failed master node");
	}

	public void replicateNode(InetAddress node) throws ClassNotFoundException {
		getLogger().info("Started creating the replica of failed node: " + node.getHostAddress());
		List<FileIdentifier> fids = fileIndexer.groupBy(node);
		fileIndexer.print();
		for (FileIdentifier fid : fids) {
			fileIndexer.delete(fid);
		}
		fileIndexer.print();
		for (FileIdentifier fid : fids) {
			List<InetAddress> nodes = fileIndexer.getSourceAndDestination(fid);
			createReplica(fid);
		}
		getLogger().info("Created the replica of failed node: " + node.getHostAddress());
	}

	public FileIndexer getFileIndexer() {
		return fileIndexer;
	}
}
