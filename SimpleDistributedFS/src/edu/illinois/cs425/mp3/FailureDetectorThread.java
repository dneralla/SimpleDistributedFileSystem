package edu.illinois.cs425.mp3;

import java.util.Date;

import edu.illinois.cs425.mp3.messages.LeaveMessage;
import edu.illinois.cs425.mp3.messages.MulticastFailureMessage;

/*
 * Class spawns thread, which will be used for
 * detecting node failures.
 */
public class FailureDetectorThread extends Thread {

	private final Process process;
	private final long detectionTime;
	private boolean pause;
	public static volatile long lastReceivedHeartBeatTime;

	public static void setLastReceivedHeartBeatTime(long lastReceivedTime) {
		if(lastReceivedTime > FailureDetectorThread.lastReceivedHeartBeatTime)
			lastReceivedHeartBeatTime = lastReceivedTime;
	}

	public FailureDetectorThread(Process process, long detectionTime) {
		this.lastReceivedHeartBeatTime = System.currentTimeMillis();
		this.process = process;
		this.detectionTime = detectionTime;
		this.pause = false;
	}

	@Override
	public void run() {
		while (true) {
			if (System.currentTimeMillis() - lastReceivedHeartBeatTime > detectionTime) {
				
				if (pause)
					continue;
				System.out.println("In failuredetector thread"+lastReceivedHeartBeatTime);
				pause = true;
				System.out.println("Failure Detected: "
						+ process.getHeartbeatSendingNode().getHostAddress());
				process.getLogger().info(
						"Failure Detected: "
								+ process.getHeartbeatSendingNode()
										.getHostAddress());
				try {
					new LeaveMessage(process.getNode(), process.getNode(), process.getHeartbeatSendingNode()).
					processNodeDisappearance(process);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				
				if(pause) {
					pause = false;
				}
			}
		}
	}

}
