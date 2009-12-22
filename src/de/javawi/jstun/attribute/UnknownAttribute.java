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


import java.util.Iterator;
import java.util.Vector;

import de.javawi.jstun.attribute.exception.MessageAttributeParsingException;
import de.javawi.jstun.util.Utility;
import de.javawi.jstun.util.UtilityException;

public class UnknownAttribute extends AbstractMessageAttribute {
	/*
	 *  0                   1                   2                   3
	 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |      Attribute 1 Type           |     Attribute 2 Type        |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |      Attribute 3 Type           |     Attribute 4 Type    ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 */

	final static int ATTRIBUTE_SIZE = 2;

	Vector<MessageAttributeType> unknown = new Vector<MessageAttributeType>();

	public UnknownAttribute() {
		super(MessageAttributeType.UnknownAttribute);
	}

	public UnknownAttribute(byte[] data) throws MessageAttributeParsingException, UtilityException {
		int padding, length;

		// TODO useless?
		padding = data.length % 4;
		length = data.length;

		length += padding;

		for (int i = 0; i < length; i += 4) {
			byte[] temp = new byte[4];
			System.arraycopy(data, i, temp, 0, 4);
			long attri = Utility.fourBytesToLong(temp);
			this.addAttribute(AbstractMessageAttribute.longToType(attri));
		}
	}

	public void addAttribute(MessageAttributeType attribute) {
		unknown.add(attribute);
	}

	public byte[] getBytes() throws UtilityException {
		// 4 bytes common header + valueLength bytes values

		int unknownSize = unknown.size();
		int valueLength = ATTRIBUTE_SIZE * unknownSize;

		int totalLength = MessageAttributeInterface.COMMONHEADERSIZE + valueLength;

		byte[] result = new byte[totalLength];

		// message attribute header
		// type
		System.arraycopy(Utility.integerToTwoBytes(typeToInteger(type)), 0, result, 0, 2);
		// length
		System.arraycopy(Utility.integerToTwoBytes(valueLength), 0, result, 2, 2);

		// unkown attribute header
		Iterator<MessageAttributeType> it = unknown.iterator();
		int position = 4;
		while(it.hasNext()) {
			MessageAttributeType attri = it.next();
			System.arraycopy(Utility.integerToTwoBytes(typeToInteger(attri)), 0, result, position, ATTRIBUTE_SIZE);
			position += ATTRIBUTE_SIZE;
		}
		// padding
		if (unknownSize % 2 == 1) {
			byte[] padding = new byte[ATTRIBUTE_SIZE];
			System.arraycopy(padding, 0, result, position, ATTRIBUTE_SIZE);
		}
		return result;
	}

	/**
	 * @deprecated Use the constructor instead
	 * @param data
	 * @return
	 * @throws MessageAttributeParsingException
	 */
	public static UnknownAttribute parse(byte[] data) throws MessageAttributeParsingException {
		try {
			UnknownAttribute result = new UnknownAttribute();
			if (data.length % 4 != 0) throw new MessageAttributeParsingException("Data array too short");
			for (int i = 0; i < data.length; i += 4) {
				byte[] temp = new byte[4];
				System.arraycopy(data, i, temp, 0, 4);
				long attri = Utility.fourBytesToLong(temp);
				result.addAttribute(AbstractMessageAttribute.longToType(attri));
			}
			return result;
		} catch (UtilityException ue) {
			throw new MessageAttributeParsingException("Parsing error");
		}
	}
}
