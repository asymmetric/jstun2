package de.javawi.jstun.attribute.exception;

import de.javawi.jstun.attribute.AbstractMessageAttribute.MessageAttributeType;

public class UnknownMessageAttributeException extends MessageAttributeParsingException {
	private static final long serialVersionUID = 5375193544145543299L;

	private MessageAttributeType type;

	public UnknownMessageAttributeException(String mesg, MessageAttributeType type) {
		super(mesg);
		this.type = type;
	}

	public MessageAttributeType getType() {
		return type;
	}
}
