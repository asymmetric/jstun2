package de.javawi.jstun.header.messagetype.method;

import de.javawi.jstun.header.MessageHeaderInterface;
import de.javawi.jstun.header.messagetype.AbstractMessageType;

public class Binding extends AbstractMessageType {

	private final static int methodEncoding = 0x01;

	public Binding(MessageHeaderInterface.MessageHeaderClass c) {
		super(c, methodEncoding);
	}

}
