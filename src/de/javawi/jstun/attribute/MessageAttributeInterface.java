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
	/* stun1
	public enum MessageAttributeType { MappedAddress, ResponseAddress, ChangeRequest, SourceAddress, ChangedAddress, Username, Password, MessageIntegrity, ErrorCode, UnknownAttribute, ReflectedFrom, Dummy };
	final static int MAPPEDADDRESS = 0x0001;
	final static int RESPONSEADDRESS = 0x0002;
	final static int CHANGEREQUEST = 0x0003;
	final static int SOURCEADDRESS = 0x0004;
	final static int CHANGEDADDRESS = 0x0005;
	final static int REFLECTEDFROM = 0x000b;
	final static int PASSWORD = 0x0007;
	*/

	public enum MessageAttributeType {
		MappedAddress(0x0001), Username(0x0006), MessageIntegrity(0x0008), ErrorCode(0x0009), UnknownAttribute(0x000A), XORMappedAddress(0x0020), Dummy(0x0000);

		private final int e;

		MessageAttributeType(int encoding) {
			e = encoding;
		}

		public int getEncoding() {
			return e;
		}

		/* TODO
		 * final static int REALM = 0x0014;
		 * final static int NONCE = 0x0015;
		 */

		// TODO add comprehension-optional attributes
	};
}
