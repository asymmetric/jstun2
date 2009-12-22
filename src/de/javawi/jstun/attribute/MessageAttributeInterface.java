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

public interface MessageAttributeInterface {

	/* TODO since this class defines no method sigs,
	 * i guess it could as well be removed, and the enum be placed
	 * in the implementing abstract class
	 */

	// TODO add comprehension-optional attributes
	enum MessageAttributeType {
		MappedAddress(0x0001), Username(0x0006), MessageIntegrity(0x0008), ErrorCode(0x0009), 
		UnknownAttribute(0x000A), XORMappedAddress(0x0020), Dummy(0x0000), Realm(0x0014), Nonce(0x0015);

		private final int e;

		MessageAttributeType(int encoding) {
			e = encoding;
		}

		public int getEncoding() {
			return e;
		}
	};

	int COMMONHEADERSIZE = 4;
}