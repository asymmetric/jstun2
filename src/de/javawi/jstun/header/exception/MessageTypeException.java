package de.javawi.jstun.header.exception;

public class MessageTypeException extends MessageHeaderException {
	private static final long serialVersionUID = 5930836029136925736L;

	public MessageTypeException(int n) {
		super("The value " + n + " does not correspond to any class");
	}
}
