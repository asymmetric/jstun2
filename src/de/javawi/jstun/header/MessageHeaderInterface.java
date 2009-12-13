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

/*
 * stun
 * public interface MessageHeaderInterface {
 * public enum MessageHeaderType { BindingRequest, BindingResponse,
 * BindingErrorResponse, SharedSecretRequest, SharedSecretResponse,
 * SharedSecretErrorResponse };
 * final static int BINDINGREQUEST = 0x0001;
 * final static int BINDINGRESPONSE = 0x0101;
 * final static int BINDINGERRORRESPONSE = 0x0111;
 * final static int SHAREDSECRETREQUEST = 0x0002;
 * final static int SHAREDSECRETRESPONSE = 0x0102;
 * final static int SHAREDSECRETERRORRESPONSE = 0x0112;
 */
package de.javawi.jstun.header;

public interface MessageHeaderInterface {

	public enum MessageHeaderClass {
		// classes (to be OR'ed with methods)
		REQUEST(0x00), INDICATION(0x10), SUCCESSRESPONSE(0x100), ERRORRESPONSE(
				0x110);

		private final int e;

		// Constructor
		MessageHeaderClass(int encoding) {
			e = encoding;
		}

		public int getEncoding() {
			return e;
		}
	}

	public enum MessageHeaderVersion {
		STUN1, STUN2;
	}

	int FIRSTWORDMASK = 0x3FFFFFFF; // to be AND'ed with 14<<(class OR method)
	int HEADERSIZE = 20; // size in bytes

	/**
	 * The Magic Cookie as per <a href="http://tools.ietf.org/html/rfc5389">RFC5389</a>
	 */
	int MAGICCOOKIE = 0x2112A442; // TODO network order?
	int MAGICCOOKIESIZE = 4;
	int TRANSACTIONIDSIZE = 12; // in bytes

	int MESSAGETYPESHIFT = 14; // 14 bits of shift for a 32 bits
	// int

	// pre-defined class-method associations
	int BINDINGREQUEST = 0x0001;
	int BINDINGINDICATION = 0x0011; // STUN2 only
	int BINDINGRESPONSE = 0x0101;
	int BINDINGERRORRESPONSE = 0x0111;

	// STUN1 only
	int SHAREDSECRETREQUEST = 0x0002;
	int SHAREDSECRETRESPONSE = 0x0102;
	int SHAREDSECRETERRORRESPONSE = 0x0112;
}
