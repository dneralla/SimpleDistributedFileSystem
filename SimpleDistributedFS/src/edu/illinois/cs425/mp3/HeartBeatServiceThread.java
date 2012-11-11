package edu.illinois.cs425.mp3;

import edu.illinois.cs425.mp3.messages.HeartBeatMessage;
import edu.illinois.cs425.mp3.messages.Message;

public class HeartBeatServiceThread extends Thread {
	Process process;
	Long timeGap;

	public HeartBeatServiceThread(Process process, long timeGap) {
		this.process = process;
		this.timeGap = timeGap;
	}

	@Override
	public void run() {
		try {
			Message m = new HeartBeatMessage(process.getNode(), null, null);
			while (true) {
				process.getLogger().info(
						"HeartBeat Sending to"
								+ process.getNeighborNode().getHostAddress()
										.toString());
				process.getUdpServer().sendMessage(m, process.getNeighborNode());
				Thread.sleep(timeGap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error in sending hearbeat message" + process.getNeighborNode().getHostAddress());
			process.getLogger().info(
					"Error in sending heart beat message to"
							+ process.getNeighborNode().getHostAddress());
		}
	}
}
