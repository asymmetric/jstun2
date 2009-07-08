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

package de.jstun.core.attribute;

public interface MessageAttributeInterface {
	public enum MessageAttributeType { MappedAddress, ResponseAddress, ChangeRequest, SourceAddress, ChangedAddress, Username, Password, MessageIntegrity, ErrorCode, UnknownAttribute, ReflectedFrom, Dummy };
	final static int MAPPEDADDRESS = 0x0001; // TODO revise all
	final static int USERNAME = 0x0006;
	final static int PASSWORD = 0x0007;
	final static int MESSAGEINTEGRITY = 0x0008;
	final static int ERRORCODE = 0x0009;
	final static int UNKNOWNATTRIBUTE = 0x000a;
	final static int DUMMY = 0x0000;
}