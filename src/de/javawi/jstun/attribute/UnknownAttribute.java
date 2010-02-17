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

	// TODO why vector?
	// the Vector containing the unknown attribute types as Integers
	Vector<Integer> unknown = new Vector<Integer>();

	public UnknownAttribute() {
		super(MessageAttributeType.UnknownAttribute);
	}

	public UnknownAttribute(byte[] data) throws MessageAttributeParsingException, UtilityException {
		this();

		int padding, length;

		// TODO useless?
		padding = data.length % 4;
		length = data.length + padding;

		// TODO there shouldn't be a for
		for (int i = 0; i < length; i += TYPE_SIZE) {
			byte[] temp = new byte[TYPE_SIZE];
			System.arraycopy(data, i, temp, 0, TYPE_SIZE);
			int attri = Utility.twoBytesToInteger(temp);
			this.addAttribute(new Integer(attri));
		}
	}

	public void addAttribute(int attribute) {
		unknown.add(new Integer(attribute));
	}

	public byte[] getBytes() throws UtilityException {
		// 4 bytes common header + valueLength bytes values

		int unknownSize = unknown.size();
		int valueLength = TYPE_SIZE * unknownSize;

		int totalLength = COMMONHEADERSIZE + valueLength;

		byte[] result = new byte[totalLength];

		// message attribute header
		// type
		System.arraycopy(Utility.integerToTwoBytes(typeToInteger(type)), 0, result, 0, 2);
		// length
		System.arraycopy(Utility.integerToTwoBytes(valueLength), 0, result, 2, 2);

		// unkown attribute header
		Iterator<Integer> it = unknown.iterator();
		int position = 4;
		while(it.hasNext()) {
			Integer attri = it.next();
			System.arraycopy(attri.intValue(), 0, result, position, TYPE_SIZE);
			position += TYPE_SIZE;
		}
		// padding
		if (unknownSize % 2 == 1) {
			byte[] padding = new byte[TYPE_SIZE];
			System.arraycopy(padding, 0, result, position, TYPE_SIZE);
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
			int l = data.length;
			if (l % 4 != 0) throw new MessageAttributeParsingException("Data array too short");
			for (int i = 0; i < l; i += 2) {
				byte[] temp = new byte[2];
				System.arraycopy(data, i, temp, 0, 4);
				int attri = Utility.twoBytesToInteger(temp);
				result.addAttribute(new Integer(attri));
			}
			return result;
		} catch (UtilityException ue) {
			throw new MessageAttributeParsingException("Parsing error");
		}
	}
}
