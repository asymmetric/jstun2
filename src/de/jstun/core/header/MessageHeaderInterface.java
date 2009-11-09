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

package de.jstun.core.header;

public interface MessageHeaderInterface {
	
	public enum MessageHeaderClass { 
		// classes (to be OR'ed with methods)
		REQUEST (0x00), INDICATION (0x10), SUCCESSRESPONSE (0x100), ERRORRESPONSE (0x110);
		
		private final int e;
		
		// Constructor
		MessageHeaderClass(int encoding) { e = encoding; }
		
		public int getEncoding() { return e; }
	}
	
	final static int HEADERSIZE = 20; // size in bytes

	final static int MAGICCOOKIE = 0x2112A442; // TODO network order?
	final static int MAGICCOOKIESIZE = 4;
	final static int TRANSACTIONIDSIZE = 12; // in bytes
	
	final static int MESSAGETYPESHIFT = 14; // 14 bits of shift for a 32 bits int

	// TODO remove all below?
	// methods
	final static int BINDING = 0x01;

	// pre-defined class-method associations
	final static int BINDINGREQUEST = 0x0001;
	final static int BINDINGRESPONSE = 0x0101;
	final static int BINDINGFAILURERESPONSE = 0x0111;
}