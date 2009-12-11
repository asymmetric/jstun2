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

import java.util.logging.Logger;

import de.javawi.jstun.attribute.exception.MessageAttributeParsingException;
import de.javawi.jstun.util.Address;
import de.javawi.jstun.util.Address.family;

public class MappedAddress extends MappedXORMapped {

	private static Logger logger = Logger.getLogger("de.javawi.stun.attribute.MappedAddress");
	public MappedAddress(int family) {
		super(MessageAttribute.MessageAttributeType.MappedAddress, family);
	}

	/**
	 * Create a new MessageAttribute
	 * 
	 * @param data
	 * @param f
	 *            Address family, specified as an {@link Address.family}
	 * @return
	 * @throws MessageAttributeParsingException
	 */
	public static AbstractMessageAttribute parse(byte[] data, family f)
			throws MessageAttributeParsingException {
		MappedAddress ma = new MappedAddress(family);
		MappedXORMapped.parse(ma, data); // TODO: ??
		logger.finer("Message Attribute: Mapped Address parsed: " + ma.toString() + ".");
		return ma;
	}
}
