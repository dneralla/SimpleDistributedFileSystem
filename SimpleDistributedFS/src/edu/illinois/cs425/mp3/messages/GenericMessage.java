package edu.illinois.cs425.mp3.messages;
import java.io.Serializable;

import edu.illinois.cs425.mp3.Process;

public abstract class GenericMessage implements Serializable{
	/**
	 * Default serial uid.
	 */
	private static final long serialVersionUID = 1L;
	public abstract void processMessage(Process process) throws Exception;
}
