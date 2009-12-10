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

import de.javawi.jstun.attribute.MessageAttributeInterface;
import de.javawi.jstun.attribute.MessageAttributeInterface.MessageAttributeType;
import de.javawi.jstun.attribute.exception.MessageAttributeParsingException;

public class ReflectedFrom extends MappedResponseChangedSourceAddressReflectedFrom {
	private static Logger logger = Logger.getLogger("de.javawi.stun.attribute.ReflectedFrom");
	
	public ReflectedFrom() {
		super(MessageAttribute.MessageAttributeType.ReflectedFrom);
	}
	
	public static ReflectedFrom parse(byte[] data) throws MessageAttributeParsingException {
		ReflectedFrom result = new ReflectedFrom();
		MappedResponseChangedSourceAddressReflectedFrom.parse(result, data);
		logger.finer("Message Attribute: ReflectedFrom parsed: " + result.toString() + ".");
		return result;
	}

	
}
