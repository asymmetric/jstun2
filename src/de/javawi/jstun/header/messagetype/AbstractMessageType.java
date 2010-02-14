package de.javawi.jstun.header.messagetype;

import de.javawi.jstun.header.MessageHeaderInterface;

public abstract class AbstractMessageType {
	
	// TODO find a design pattern for this situation

	protected int encoding;

	public AbstractMessageType(MessageHeaderInterface.MessageHeaderClass klass,
			int methodEncoding) {
		encoding = klass.getEncoding() | methodEncoding;
	}

	public final int getEncoding() {
		// to make sure the first 2 bits are 0
		return encoding & 0x3FF;
	}

	// TODO implement
//	public static MessageHeaderClass getClass(int encoding) {
		// REQUEST (0x00), INDICATION (0x10), SUCCESSRESPONSE (0x100),
		// ERRORRESPONSE (0x110);


//	}
}
