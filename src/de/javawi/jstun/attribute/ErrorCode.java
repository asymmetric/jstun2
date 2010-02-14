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

import de.javawi.jstun.attribute.exception.MessageAttributeException;
import de.javawi.jstun.attribute.exception.MessageAttributeParsingException;
import de.javawi.jstun.util.Utility;
import de.javawi.jstun.util.UtilityException;

public class ErrorCode extends AbstractMessageAttribute {

	/*
    *	0                   1                   2                   3
    *	0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
   	*	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   	*	|           Reserved, should be 0         |Class|     Number    |
   	*	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   	*	|      Reason Phrase (variable)                                ..
   	*	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   	*/


	int responseCode;
	String reason;
	private int classHeader;
	private int number;

	public ErrorCode() {
		super(MessageAttributeType.ErrorCode);
	}
	
	// TODO review
	public ErrorCode(byte[] data) throws UtilityException, MessageAttributeParsingException {
		this();
		if (data.length < 4) { // TODO unmagic all
			throw new MessageAttributeParsingException("Data array too short");
		}

		byte classHeaderByte = data[3];
		int classHeader = Utility.oneByteToInteger(classHeaderByte);
		if ((classHeader < 3) || (classHeader > 6))
			throw new MessageAttributeParsingException("Class parsing error");

		byte numberByte = data[4];
		int number = Utility.oneByteToInteger(numberByte);
		if ((number < 0) || (number > 99))
			throw new MessageAttributeParsingException("Number parsing error");

		setResponseCode(classHeader, number);
	}

	public void setResponseCode(int classHeader, int number) throws MessageAttributeParsingException {
		int responseCode = (classHeader * 100) + number;
		switch (responseCode) {
			// TODO cfr rfc for additional requirements
			case TRY_ALTERNATE: reason = "Try Alternate"; break;
			case BAD_REQUEST: reason = "Bad Request"; break;
			case UNAUTHORIZED: reason = "Unauthorized"; break;
			case UNKOWN_ATTRIBUTE: reason = "Unkown Attribute"; break;
			case STALE_NONCE: reason = "Stale Nonce"; break;
			case SERVER_ERROR: reason = "Server Error"; break;
		default: throw new MessageAttributeParsingException("Response Code unknown");
		}
		this.responseCode = responseCode;
		this.classHeader = classHeader;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public String getReason() {
		return reason;
	}

	public byte[] getBytes() throws UtilityException {
		int length = reason.length();
		// length adjustment
		if ((length % 4) != 0) {
			length += 4 - (length % 4);
		}
		// message attribute header
		length += 4;
		byte[] result = new byte[length];
		// message attribute header
		// type
		System.arraycopy(Utility.integerToTwoBytes(typeToInteger(type)), 0, result, 0, 2);
		// length
		System.arraycopy(Utility.integerToTwoBytes(length-4), 0, result, 2, 2);

		// error code header
		result[6] = Utility.integerToOneByte(classHeader);
		result[7] = Utility.integerToOneByte(responseCode%100);
		byte[] reasonArray = reason.getBytes();
		System.arraycopy(reasonArray, 0, result, 8, reasonArray.length);
		return result;
	}
	
	
	/**
	 * @deprecated Use the constructor instead
	 * @param data
	 * @return
	 * @throws MessageAttributeParsingException
	 */
	public static ErrorCode parse(byte[] data) throws MessageAttributeParsingException {
		try {
			if (data.length < 4) {
				throw new MessageAttributeParsingException("Data array too short");
			}
			byte classHeaderByte = data[3];
			int classHeader = Utility.oneByteToInteger(classHeaderByte);
			if ((classHeader < 1) || (classHeader > 6)) throw new MessageAttributeParsingException("Class parsing error");
			byte numberByte = data[4];
			int number = Utility.oneByteToInteger(numberByte);
			if ((number < 0) || (number > 99)) throw new MessageAttributeParsingException("Number parsing error");
			ErrorCode result = new ErrorCode();
			result.setResponseCode(classHeader, number);
			return result;
		} catch (UtilityException ue) {
			throw new MessageAttributeParsingException("Parsing error");
		} catch (MessageAttributeException mae) {
			throw new MessageAttributeParsingException("Parsing error");
		}
	}
}
