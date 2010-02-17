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
import de.javawi.jstun.header.MessageHeaderInterface;
import de.javawi.jstun.util.Address;
import de.javawi.jstun.util.IPv4Address;
import de.javawi.jstun.util.Utility;
import de.javawi.jstun.util.UtilityException;
import de.javawi.jstun.util.Address.Family;

public class XORMappedAddress extends AbstractMappedAddress {

	/*	 0                   1                   2                   3
		 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
		+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
		|0 0 0 0 0 0 0 0|    Family     |           Port                |
		+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
		|                                                               |
		|                 Address (32 bits or 128 bits)                 |
		|                                                               |
		+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 */


	/**
	 * Default constructor.<br>
	 * Implicit parameters: <br> {@link MessageAttributeType} = {@link XORMappedAddress}<br>
	 */
	public XORMappedAddress() {
		super(MessageAttributeType.XORMappedAddress);
	}

//	// TODO we should check it's an appropriate type
//	public XORMappedAddress(MessageAttributeType type) {
////		if (type != MessageAttributeType.MappedAddress && type != MessageAttributeType.XORMappedAddress)
////			throw new MessageAttributeException("Wrong MessageAttributeType");
//		super(type);
//		this.type = type;
//	}

	public XORMappedAddress(byte[] data) throws MessageAttributeParsingException {
		this();
		parseData(data);
	}

//	public XORMappedAddress(MessageAttributeType type, byte[] data) throws MessageAttributeParsingException {
//		this(type);
//		parseData(data);
//	}



	// TODO we should do XOR-decoding
	@Override
	protected void parseData(byte[] data) throws MessageAttributeParsingException {
		try {
			if (data.length < 8) { // TODO why 8?
				throw new MessageAttributeParsingException("Data array too short");
			}

			// Get the IP family directly from the packet
			int familyInt = Utility.oneByteToInteger(data[1]);

			if (familyInt == Address.IPv4) {

				byte[] addressArray = new byte[4];
				System.arraycopy(data, 4, addressArray, 0, 4);

				// store it in the local vars
				this.address = new IPv4Address(addressArray);
				this.family = Family.IPv4;

			} else if (familyInt == Address.IPv6) {
				// TODO implement
				throw new MessageAttributeParsingException("IPv6 is currently unsupported");
			} else
				throw new MessageAttributeParsingException("Family " + familyInt
						+ " is not supported");

			// Geth the port from the packet
			byte[] portArray = new byte[2];
			System.arraycopy(data, 2, portArray, 0, 2);

			// store it
			this.port = Utility.twoBytesToInteger(portArray);

		} catch (UtilityException ue) {
			throw new MessageAttributeParsingException("Parsing error");
		} catch (MessageAttributeException mae) {
			throw new MessageAttributeParsingException("Port parsing error");
		}
	}

	// TODO it should differ, based on the IP protocol family
	/* Used to get the attribute as a byte[], in order to send it on the network */
	@Override
	public byte[] getBytes() throws UtilityException {
		// 4 bytes common header + 4B own header + 4B address
		final int IPv4LENGTH = 4;
		byte[] result = new byte[COMMONHEADERSIZE + HEADER_LENGTH + IPv4LENGTH]; // TODO this should be variable
		// message attribute header
		// type
		System.arraycopy(Utility.integerToTwoBytes(typeToInteger(type)), 0, result, 0, 2);
		// length
		// TODO should be variable
		System.arraycopy(Utility.integerToTwoBytes(HEADER_LENGTH + IPv4LENGTH), 0, result, 2, 2);

		// mappedaddress header
		// padding
		result[4] = 0x0;
		// family
		result[5] = Utility.integerToOneByte(family.getEncoding());

		if (type == MessageAttributeType.MappedAddress) {
			// port
			System.arraycopy(Utility.integerToTwoBytes(port), 0, result, 6, 2);
			// address
			System.arraycopy(address.getBytes(), 0, result, 8, IPv4LENGTH);
		}
		else {
			// calculate X-Port
			// TODO should it be >>> or >> ?
			int shiftedMC = MessageHeaderInterface.MAGICCOOKIE >> 16;
			int xPort = port ^ shiftedMC;

			byte[] xPortByte = Utility.integerToTwoBytes(xPort);
			System.arraycopy(xPortByte, 0, result, 6, 2);

			// calculate X-Address
			long addressInt = Utility.fourBytesToLong(address.getBytes());
			long xAddress = addressInt ^ MessageHeaderInterface.MAGICCOOKIE;
			int xaddint = (int) xAddress;

			byte[] xAddressByte = Utility.integerToFourBytes((int) xAddress);
			System.arraycopy(xAddressByte, 0, result, 8, 4);
		}
		return result;
	}

	/**
	 * @deprecated Use the <b>MappedXORMapped(type, data, address, port)</b> constructor
	 *             instead
	 * @param ma
	 *            The {@link XORMappedAddress} object to put the parsed data in.
	 * @param data
	 *            The byte[] containing the data to parse.
	 * @return A {@link XORMappedAddress} object containing the mapped data.
	 * @throws MessageAttributeParsingException
	 */
	protected static XORMappedAddress parse(XORMappedAddress ma, byte[] data)
	throws MessageAttributeParsingException {
		try {
			if (data.length < 8) { // TODO why 8?
				throw new MessageAttributeParsingException("Data array too short");
			}
			int family = Utility.oneByteToInteger(data[1]);

			if (family != 0x01)
				throw new MessageAttributeParsingException("Family " + family
						+ " is not supported");
			byte[] portArray = new byte[2];
			System.arraycopy(data, 2, portArray, 0, 2);
			ma.setPort(Utility.twoBytesToInteger(portArray));
			int firstOctet = Utility.oneByteToInteger(data[4]);
			int secondOctet = Utility.oneByteToInteger(data[5]);
			int thirdOctet = Utility.oneByteToInteger(data[6]);
			int fourthOctet = Utility.oneByteToInteger(data[7]);
			ma.setAddress(new IPv4Address(firstOctet, secondOctet, thirdOctet, fourthOctet));
			return ma;
		} catch (UtilityException ue) {
			throw new MessageAttributeParsingException("Parsing error");
		} catch (MessageAttributeException mae) {
			throw new MessageAttributeParsingException("Port parsing error");
		}
	}
}
