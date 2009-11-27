package de.javawi.jstun.header.messagetype.method;

import de.javawi.jstun.header.MessageHeaderInterface;
import de.javawi.jstun.header.messagetype.AbstractMessageType;

public class SharedSecret extends AbstractMessageType {

	private final static int methodEncoding = 0x02;

	public SharedSecret(MessageHeaderInterface.MessageHeaderClass c) {

		super(c, methodEncoding);

	}

}
