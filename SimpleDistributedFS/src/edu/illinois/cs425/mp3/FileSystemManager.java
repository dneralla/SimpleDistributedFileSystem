package edu.illinois.cs425.mp3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.InetAddress;

public class FileSystemManager {

	@SuppressWarnings("resource")
	public static String getChunk(String fileName, int chunkId) {
		FileReader fr = null;
		String out = null;
		try {
			char chunk[] = new char[Process.CHUNK_SIZE];
			fr = new FileReader(new File(InetAddress.getLocalHost().toString().substring(0,6)+"."+fileName + "." + chunkId));
			fr.read(chunk);
			out = new String(chunk);
			fr.close();
			return out;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static void putChunk(String chunk, String fileName, int chunkId) {
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter(new File(InetAddress.getLocalHost().toString().substring(0,6)+"."+fileName + "." + chunkId)));
			writer.write(chunk);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteChunk(String fileName, int chunkId) {
		try{
		   File f = new File(InetAddress.getLocalHost().toString().substring(0,6)+"."+fileName + "." + chunkId);
		   f.delete();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
