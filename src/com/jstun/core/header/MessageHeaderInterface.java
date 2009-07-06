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

package com.jstun.core.header;

public interface MessageHeaderInterface {
	public enum MessageHeaderClass { Request, SuccessResponse, FailureResponse, Indication }
	public enum MessageHeaderMethod { Binding } // TODO are there more?
	public enum MessageHeaderType { BindingRequest, BindingResponse, BindingFailureResponse }

	final static int MAGICCOOKIE = 0x2112A442;

	// classes
	final static int REQUEST = 0x00;
	final static int INDICATION = 0x01;
	final static int SUCCESSRESPONSE = 0x02;
	final static int FAILURERESPONSE = 0x03;

	// methods
	final static int BINDING = 0x00;

	// pre-defined class-method associations
	final static int BINDINGREQUEST = 0x0001; // TODO are there more? indication?
	final static int BINDINGRESPONSE = 0x0101;
	final static int BINDINGFAILURERESPONSE = 0x0111;
}