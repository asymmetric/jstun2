package de.javawi.jstun.header;

import java.util.Arrays;

import de.javawi.jstun.header.exception.MessageTypeException;

public class MessageType {
	
	// TODO find a design pattern for this situation

	protected int encoding;

	public MessageType(int methodEncoding, MessageHeaderInterface.MessageHeaderClass klass) {
		encoding = klass.getEncoding() | methodEncoding;
	}
	
	/**
	 * Constructor for the MessageType type, with int constants instead of Enums.
	 * It should be faster
	 * 
	 * @param klass	The STUN2 class encoding
	 * @param methodEncoding The STUN2 method encoding 
	 * @throws MessageTypeException If the specified class isn't one of those specified in {@link de.javawi.jstun.header.MessageHeaderInterface#CLASS_ARRAY CLASS_ARRAY}
	 */
	public MessageType(int methodEncoding, int klass) throws MessageTypeException {
		if (Arrays.binarySearch(MessageHeaderInterface.CLASS_ARRAY, klass) < 0)
			throw new MessageTypeException(klass);
		encoding = klass | methodEncoding;
	}

	public final int getEncoding() {
		// to make sure the first 2 bits are 0
		return encoding & 0x3FF; // TODO check here!
	}

	// TODO implement
//	public static MessageHeaderClass getClass(int encoding) {
		// REQUEST (0x00), INDICATION (0x10), SUCCESSRESPONSE (0x100),
		// ERRORRESPONSE (0x110);


//	}
}
