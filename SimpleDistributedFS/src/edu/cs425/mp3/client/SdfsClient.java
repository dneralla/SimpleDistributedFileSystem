package edu.cs425.mp3.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.illinois.cs425.mp3.messages.DeleteMessage;
import edu.illinois.cs425.mp3.messages.DeleteRelayMessage;
import edu.illinois.cs425.mp3.messages.GenericMessage;
import edu.illinois.cs425.mp3.messages.GetFileMessage;
import edu.illinois.cs425.mp3.messages.PutChunkMessage;
import edu.illinois.cs425.mp3.Process;

public class SdfsClient {

	private InetAddress masterNode;
	public static final int PORT = Process.TCP_SERVER_PORT;
	public static final String RCV_DIR = "";
	

	public SdfsClient() throws UnknownHostException {
		masterNode = InetAddress.getByName("linux5.ews.illinois.edu");
	}

	public static void main(String args[]) throws Exception {

		SdfsClient client = new SdfsClient();
		String inputLine;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("[Please Enter Command]$ ");
		while ((inputLine = in.readLine()) != null) {
			
			if (inputLine.startsWith("put")) {
			     long starttime =System.currentTimeMillis();
				if (inputLine.split(" ").length == 3) {
					String localFileName = inputLine.substring(
							inputLine.indexOf(" ") + 1,
							inputLine.lastIndexOf(" "));
					String sdfsFileName = inputLine.substring(inputLine
							.lastIndexOf(" ") + 1);
					System.out.println("In put");
					
					client.chunkAndSend(localFileName, sdfsFileName);
				} else {
					String arguments[] = inputLine.split(" ");
					String localFileName = arguments[1];
					String sdfsFileName = arguments[2];
					InetAddress bootStrapNode = InetAddress
							.getByName(arguments[3]);
					client.chunkAndSend(localFileName, sdfsFileName,
							bootStrapNode);

				}
				System.out.println("Time taken for put::" +(System.currentTimeMillis()-starttime));

			} else if (inputLine.startsWith("get")) {
				long starttime =System.currentTimeMillis();
				if (inputLine.split(" ").length == 2) {
					
					
					String sdfsFileName = inputLine.substring(inputLine
							.indexOf(" ") + 1);
					client.getFile(sdfsFileName);
				} else {
					String arguments[] = inputLine.split(" ");
					String sdfsFileName = arguments[1];
					InetAddress bootStrapNode = InetAddress
							.getByName(arguments[2]);
					client.getFile(sdfsFileName, bootStrapNode);

				}
				System.out.println("Time taken:: for get" +(System.currentTimeMillis()-starttime));
			} else if (inputLine.startsWith("delete")) {
				long starttime =System.currentTimeMillis();
				if (inputLine.split(" ").length == 2) {
					String sdfsFileName = inputLine.substring(inputLine
							.indexOf(" ") + 1);
					client.deleteFile(sdfsFileName);
				} else {
					String arguments[] = inputLine.split(" ");
					String sdfsFileName = arguments[1];
					InetAddress bootStrapNode = InetAddress
							.getByName(arguments[2]);
					client.deleteFile(sdfsFileName, bootStrapNode);
					
					System.out.println("Time taken::" +(System.currentTimeMillis()-starttime));
					
				} 
			} else {
				System.out.println("Command entered in wrong format.Enter the following format");
				System.out.println("put <localfileName> <sdfsfilename>");
				System.out.println("get <sdfsfileNamw>");
				System.out.println("delete <sdfsfilename>");

			}
			System.out.print("[Please Enter Command]$ ");

		}

	}

	public void chunkAndSend(String localFileName, String sdfsFileName) {
		chunkAndSend(localFileName, sdfsFileName, this.masterNode);
	}

	public void getFile(String sdfsFile) {
		getFile(sdfsFile, this.masterNode);
	}

	public void deleteFile(String sdfsFile) {
		
		deleteFile(sdfsFile, this.masterNode);
	}

	public void chunkAndSend(String localFileName, String sdfsFileName,
			InetAddress bootStrapNode) {

		try {
			System.out.println("InCHUNK AND SEND");
			FileReader fr = new FileReader(new File(localFileName));
			char chunk[] = new char[Process.CHUNK_SIZE];
			
			ObjectOutputStream out = null;
			ObjectInputStream in = null;
			OutputStream os = null;
			Socket requestSocket;
			PutChunkMessage pMsg;
			int chunkId = 0;
			System.out.println("Reading chunk");
		   while (fr.read(chunk) != -1) {
					requestSocket = new Socket(bootStrapNode, SdfsClient.PORT);
                   out = new ObjectOutputStream(requestSocket.getOutputStream());
			    in = new ObjectInputStream(requestSocket.getInputStream());
				out.flush();
				pMsg = new PutChunkMessage(new String(chunk), chunkId,
						sdfsFileName);
				System.out.println("Sending " + chunkId + " chunk");
				chunkId++;
				sendMessage(pMsg, out);
				in.read();
				out.close();
				in.close();
				chunk= new char[Process.CHUNK_SIZE];
				}
				

			//}

		} catch (Exception ex) {

		}

		
	}

	public void getFile(String sdfsFile, InetAddress bootStrapNode) {
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		OutputStream os = null;
		Socket requestSocket;
		String pcMsg;
		PrintWriter p = null;
		File outputFile;
		outputFile = new File(sdfsFile);
		if(outputFile.exists())
			outputFile.delete();
		// delete output file
		try {
			requestSocket = new Socket(bootStrapNode, SdfsClient.PORT);
			out = new ObjectOutputStream(requestSocket.getOutputStream());

			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			GetFileMessage gMsg = new GetFileMessage(sdfsFile);
			sendMessage(gMsg, out);
			int noChunks = (Integer) in.readObject();
			System.out.println("no.of chunks"+noChunks);
			for (int i = 0; i < noChunks; i++) {
				
				pcMsg = (String) in.readObject();
				System.out.println("Size recieved "+ pcMsg.length()+"chunk id"+i);
				outputFile = new File(sdfsFile);
				if (!outputFile.exists()) 
					p = new PrintWriter(new BufferedWriter(new FileWriter(
							sdfsFile, true)));

				p.print(pcMsg); 
                in.read();

			}

			out.close();
			in.close();
			p.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void deleteFile(String sdfsFile, InetAddress bootStrapNode) {
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		Socket requestSocket;

		try {
			requestSocket = new Socket(bootStrapNode, SdfsClient.PORT);
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			DeleteMessage dMsg = new DeleteMessage(sdfsFile);
			sendMessage(dMsg, out);

			out.close();
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void sendMessage(GenericMessage msg, ObjectOutputStream out) {
		try {
			out.writeObject(msg);
			out.flush();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

}