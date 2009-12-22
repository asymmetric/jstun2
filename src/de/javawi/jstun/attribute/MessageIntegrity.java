/*
 * This file is part of JSTUN.
 *
 * Copyright (c) 2005 Thomas King <king@t-king.de> - All rights
 * reserved.
 *
 * This software is licensed under either the GNU Public License (GPL),
 * or the Apache 2.0 license. Copies of both license agreements are
 * included in this distribution.
 */

package de.javawi.jstun.attribute;

public class MessageIntegrity extends AbstractMessageAttribute {
	// incomplete message integrity implementation
	public MessageIntegrity() {
		super(MessageAttributeType.MessageIntegrity);
	}
	
	public MessageIntegrity(byte[] data) {
		this();
	}

	public byte[] getBytes() {
		return new byte[0];
	}

	/**
	 * @deprecated Use the constructor instead
	 * @param data
	 * @return
	 */
	public static MessageIntegrity parse(byte[] data) {
		return new MessageIntegrity();
	}
}
