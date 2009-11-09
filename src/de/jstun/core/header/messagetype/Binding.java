package de.jstun.core.header.messagetype;

import de.jstun.core.header.MessageHeaderInterface;

public class Binding extends MessageType {
	
	private final static int methodEncoding = 0x01;
	
	public Binding(MessageHeaderInterface.MessageHeaderClass c) {
		
		super(c, methodEncoding);
	}

}
