package de.javawi.jstun.header.messagetype;

import java.util.Arrays;

import de.javawi.jstun.header.MessageHeaderInterface;
import de.javawi.jstun.header.exception.MessageTypeException;

public abstract class AbstractMessageType {
	
	// TODO find a design pattern for this situation

	protected int encoding;

	public AbstractMessageType(MessageHeaderInterface.MessageHeaderClass klass,
			int methodEncoding) {
		encoding = klass.getEncoding() | methodEncoding;
	}
	
	/**
	 * Constructor for the AbstractMessageType type.
	 * Called by its subclasses
	 * 
	 * @param klass	The STUN2 class encoding
	 * @param methodEncoding The STUN2 method encoding 
	 * @throws MessageTypeException If the specified class isn't one of those specified in {@link de.javawi.jstun.header.MessageHeaderInterface#CLASS_ARRAY CLASS_ARRAY}
	 */
	public AbstractMessageType(int klass, int methodEncoding) throws MessageTypeException {
		if (Arrays.binarySearch(MessageHeaderInterface.CLASS_ARRAY, klass) < 0)
			throw new MessageTypeException(klass);
		encoding = klass | methodEncoding;
	}

	public int getEncoding() {
		// to make sure the first 2 bits are 0
		return encoding & 0x3FF;
	}

	// TODO implement
//	public static MessageHeaderClass getClass(int encoding) {
		// REQUEST (0x00), INDICATION (0x10), SUCCESSRESPONSE (0x100),
		// ERRORRESPONSE (0x110);


//	}
}
