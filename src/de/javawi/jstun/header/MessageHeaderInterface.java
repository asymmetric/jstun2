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
		REQUEST(0x00), INDICATION(0x10), SUCCESSRESPONSE(0x100), ERRORRESPONSE(0x110);

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

	static final int FIRSTWORDMASK = 0x3FFFFFFF; // to be AND'ed with 14<<(class OR method)
	static final int HEADERSIZE = 20; // size in bytes

	/**
	 * The Magic Cookie as per <a href="http://tools.ietf.org/html/rfc5389">RFC5389</a>
	 */
	static final int MAGICCOOKIE = 0x2112A442;
	static final int MAGICCOOKIESIZE = 4;
	static final int TRANSACTIONIDSIZE = 12; // in bytes
	
	// static definitions of STUN2 classes
	final static int REQUEST = 0x00;
	final static int INDICATION = 0x10;
	final static int SUCCESS_RESPONSE = 0x100;
	final static int ERROR_RESPONSE = 0x110;
	
	/**
	 * Convenience array containing the STUN2 classes, <br> i.e. 
	 * REQUEST, INDICATION, SUCCESS_RESPONSE, ERROR_RESPONSE. <br><br>
	 * <b>NOTE</b>: <u>Keep it sorted</u>! There are {@link java.util.Arrays#binarySearch(int[], int) Arrays.binarySearch()} calls that require it!
	 */
	final static int[] CLASS_ARRAY = { REQUEST, INDICATION, SUCCESS_RESPONSE, ERROR_RESPONSE };

	// pre-defined class-method associations
	static final int BINDINGREQUEST = 0x0001;
	static final int BINDINGINDICATION = 0x0011; // STUN2 only
	static final int BINDINGRESPONSE = 0x0101;
	static final int BINDINGERRORRESPONSE = 0x0111;

	// STUN1 only
	static final int SHAREDSECRETREQUEST = 0x0002;
	static final int SHAREDSECRETRESPONSE = 0x0102;
	static final int SHAREDSECRETERRORRESPONSE = 0x0112;
}
