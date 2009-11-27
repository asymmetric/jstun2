package de.javawi.jstun.header.messagetype;

import de.javawi.jstun.header.MessageHeaderInterface;
import de.javawi.jstun.header.MessageHeaderInterface.MessageHeaderClass;

public abstract class AbstractMessageType {
	
	// TODO find a design pattern for this situation

	protected int encoding;

	public AbstractMessageType(MessageHeaderInterface.MessageHeaderClass klass,
			int methodEncoding) {
		encoding = klass.getEncoding() | methodEncoding;
	}

	public int getEncoding() {
		return encoding;
	}

	/*
	 * Gets the encoding, leaving the first two bits as 0b00
	 */
	public int getShiftedEncoding() {
		return encoding << MessageHeaderInterface.MESSAGETYPESHIFT;
	}

	public static MessageHeaderClass getClass(int encoding) {
		// REQUEST (0x00), INDICATION (0x10), SUCCESSRESPONSE (0x100),
		// ERRORRESPONSE (0x110);

	}
	
	public static int getMessageType
}
