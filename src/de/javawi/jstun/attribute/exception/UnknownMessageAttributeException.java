package de.javawi.jstun.attribute.exception;

import de.javawi.jstun.attribute.AbstractMessageAttribute;
import de.javawi.jstun.attribute.Dummy;

public class UnknownMessageAttributeException extends MessageAttributeException {
	private static final long serialVersionUID = 5375193544145543299L;

	Dummy dummy;
	int type;

	// TODO ugly
	public UnknownMessageAttributeException(String mesg, AbstractMessageAttribute d) {
		super(mesg);
		dummy = (Dummy) d;
	}

	public UnknownMessageAttributeException(String mesg, int type) {
		super(mesg);
		this.type = type;
	}

	public int getType() {
//		return dummy.getUnknownType();
		return type;
	}
}
