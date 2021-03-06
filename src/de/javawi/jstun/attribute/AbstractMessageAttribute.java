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

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import de.javawi.jstun.attribute.exception.AttributeReflectionException;
import de.javawi.jstun.attribute.exception.MessageAttributeParsingException;
import de.javawi.jstun.attribute.exception.UnknownMessageAttributeException;
import de.javawi.jstun.util.Utility;
import de.javawi.jstun.util.UtilityException;

public abstract class AbstractMessageAttribute {
	private static Logger logger = Logger.getLogger("de.javawi.stun.util.MessageAttribute");

	/*
	    0                   1                   2                   3
	    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	   |         Type                  |            Length             |
	   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	   |                         Value (variable)                ....
	   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	*/

	public enum MessageAttributeType {
		MappedAddress(0x0001), Username(0x0006), ErrorCode(0x0009),
		UnknownAttribute(0x000A), XORMappedAddress(0x0020), Dummy(0x0000), Software(0x8022);

		private final int e;

		MessageAttributeType(int encoding) {
			e = encoding;
		}

		public int getEncoding() {
			return e;
		}
	}

	// TODO useless?
	final static int DUMMY = 0x0;
	final static int MAPPED_ADDRESS = 0x0001;
	final static int USERNAME = 0x0006; // TODO do we support this?
	final static int ERROR_CODE = 0x0009;
	final static int UNKNOWN_ATTRIBUTE = 0x000A;
	final static int XOR_MAPPED_ADDRESS = 0x0020;
	final static int SOFTWARE = 0x8022;

	final static int[] ATTRIBUTES = { DUMMY, MAPPED_ADDRESS, USERNAME, ERROR_CODE,
		UNKNOWN_ATTRIBUTE, XOR_MAPPED_ADDRESS, SOFTWARE };

	final static int TRY_ALTERNATE = 300;
	final static int BAD_REQUEST = 400;
	final static int UNAUTHORIZED = 401;
	final static int UNKOWN_ATTRIBUTE = 420;
	final static int STALE_NONCE = 438;
	final static int SERVER_ERROR = 500;

	final static int COMMONHEADERSIZE = 4;
	final static int ALIGNMENT = 4;

	final static int TYPE_SIZE = 2;
	final static int LENGTH_SIZE = 2;

	protected MessageAttributeType type;

	/**
	 * Sets the <b>type</b> instance field with the specified {@link MessageAttributeType}
	 * @param type
	 */
	public AbstractMessageAttribute(MessageAttributeType type) {
		setType(type);
	}

	/**
	 * Sets the {@link MessageAttributeType}
	 *
	 * @param type
	 */
	private void setType(MessageAttributeType type) {
		this.type = type;
	}

	public MessageAttributeType getType() {
		return type;
	}

	public static int typeToInteger(MessageAttributeType type) {
		return type.getEncoding();
	}

	@Deprecated
	// TODO why long??
	public final static MessageAttributeType longToType(long type) {
		MessageAttributeType[] values = MessageAttributeType.values();

		for (MessageAttributeType ma : values) {
			if (type == ma.getEncoding())
				return ma;
		}
		return null; // TODO should throw exception??
	}

	public final static MessageAttributeType intToType(int type) {
		MessageAttributeType[] values = MessageAttributeType.values();

		for (MessageAttributeType ma : values) {
			if (type == ma.getEncoding())
				return ma;
		}
		return null; // TODO should throw exception??
	}

	// The Attribute header is 4 bytes long
	abstract public byte[] getBytes() throws UtilityException;

	public int getLength() throws UtilityException {
		int length = getBytes().length;
		return length;
	}

	public final static AbstractMessageAttribute parseCommonHeader(byte[] data)
			throws MessageAttributeParsingException, UnknownMessageAttributeException, AttributeReflectionException {
		try {

			byte[] typeArray = new byte[2];
			System.arraycopy(data, 0, typeArray, 0, 2);
			int type = Utility.twoBytesToInteger(typeArray);

			byte[] lengthArray = new byte[2];
			System.arraycopy(data, 2, lengthArray, 0, 2);
			int lengthValue = Utility.twoBytesToInteger(lengthArray);

			byte[] valueArray = new byte[lengthValue];
			System.arraycopy(data, 4, valueArray, 0, lengthValue);

			AbstractMessageAttribute ma = null;

			for (MessageAttributeType mat : MessageAttributeType.values()) {
				if (type == mat.getEncoding()) {
					String fullName = "de.javawi.jstun.attribute." + mat.toString();
					Class<?> cl = Class.forName(fullName);

					ma = (AbstractMessageAttribute) cl.getConstructor(byte[].class).newInstance(valueArray);
					break;
				}
			}
			if (ma == null) {
				if (type <= 0x7fff) {
					throw new UnknownMessageAttributeException("Mandatory attribute "+type+" unknown", type);
				} else if ( (type > 0x8000 && type < 0xFFFF) || type == 0x8000 || type == 0xFFFF ){ // TODO unmagic
					logger.info("Unknown optional message attribute " + type);
					ma = Dummy.parse(valueArray);
				} else {
					logger.info("MessageAttribute with type " + type + " unkown.");
					ma = Dummy.parse(valueArray);
				}
			}

			return ma;

		} catch (UtilityException ue) {
			throw new MessageAttributeParsingException("Parsing error");
		} catch (ClassNotFoundException e) {
			// TODO it should be another exception!
			throw new AttributeReflectionException("Class not found");
		} catch (InstantiationException e) {
			throw new AttributeReflectionException("Class not instantiable");
		} catch (IllegalAccessException e) {
			throw new AttributeReflectionException("Class not accessible");
		} catch (IllegalArgumentException e) {
			throw new AttributeReflectionException("Wrong constructor invocation");
		} catch (SecurityException e) {
			throw new AttributeReflectionException("Security Manager denied access!");
		} catch (InvocationTargetException e) {
			throw new AttributeReflectionException("The underlying constructor threw an exception");
		} catch (NoSuchMethodException e) {
			throw new AttributeReflectionException("Constructor not found");
		}
	}
}
