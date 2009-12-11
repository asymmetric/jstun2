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

package de.javawi.jstun.attribute.legacy;

import java.util.logging.Logger;

import de.javawi.jstun.attribute.AbstractMessageAttribute;
import de.javawi.jstun.attribute.MessageAttributeInterface;
import de.javawi.jstun.attribute.MessageAttributeInterface.MessageAttributeType;
import de.javawi.jstun.attribute.exception.MessageAttributeParsingException;


public class ResponseAddress extends MappedResponseChangedSourceAddressReflectedFrom {
	private static Logger logger = Logger.getLogger("de.javawi.stun.attribute.ResponseAddress");
	public ResponseAddress() {
		super(MessageAttribute.MessageAttributeType.ResponseAddress);
	}
	
	public static AbstractMessageAttribute parse(byte[] data) throws MessageAttributeParsingException {
		ResponseAddress ra = new ResponseAddress();
		MappedResponseChangedSourceAddressReflectedFrom.parse(ra, data);
		logger.finer("Message Attribute: Response Address parsed: " + ra.toString() + ".");
		return ra;
	}
}
