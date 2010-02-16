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

import junit.framework.TestCase;

import org.junit.Test;

import de.javawi.jstun.attribute.AbstractMessageAttribute.MessageAttributeType;
import de.javawi.jstun.header.MessageHeaderInterface;
import de.javawi.jstun.util.Utility;

public class MappedAddressTest extends TestCase {
	XORMappedAddress map;
	XORMappedAddress xor;
	
	int port = 63584;
	int address = 1413015884;
	String addressString = "84.56.233.76";
	
	byte[] data;
	public MappedAddressTest(String mesg) {
		super(mesg);
	}
	
	public void setUp() throws Exception {
		byte asd = -8;
		byte omar = 96;
		int asdomar = (asd << 8) | omar;
		data = new byte[8];
		data[0] = 0; // padding
		data[1] = 1; // IPv4
		data[2] = -8; // port
		data[3] = 96;
		data[4] = 84; // address
		data[5] = 56;
		data[6] = -23;
		data[7] = 76;
		map = new XORMappedAddress(MessageAttributeType.MappedAddress, data);
		
		xor = new XORMappedAddress(data);
	}

	/*
	 * Test method for 'de.javawi.jstun.attribute.MappedXORMapped.MappedXORMapped()'
	 */
	@Test
	public void testMappedAddress() {
		new XORMappedAddress();
	}

	/*
	 * Test method for 'de.javawi.jstun.attribute.MappedResponseChangedSourceAddressReflectedFrom.getBytes()'
	 */
	@Test
	public void testGetBytes() {
		try {
			byte[] resultMap = map.getBytes();

			assertTrue(resultMap[0] == 0); // type
			assertTrue(resultMap[1] == 1);
			assertTrue(resultMap[2] == 0); // length
			assertTrue(resultMap[3] == 8);
			assertTrue(resultMap[4] == data[0]); // attribute value
			assertTrue(resultMap[5] == data[1]);
			assertTrue(resultMap[6] == data[2]);
			assertTrue(resultMap[7] == data[3]);
			assertTrue(resultMap[8] == data[4]);
			assertTrue(resultMap[9] == data[5]);
			assertTrue(resultMap[10] == data[6]);
			assertTrue(resultMap[11] == data[7]);
			
			byte[] resultXor = xor.getBytes();
			
			int xPort = port ^ MessageHeaderInterface.MAGICCOOKIE >>> 16;
			int xAddr = address ^ MessageHeaderInterface.MAGICCOOKIE;
			
			byte[] portByte = Utility.integerToTwoBytes(xPort);
			byte[] addressByte = Utility.integerToFourBytes(xAddr);
			
			assertTrue(resultXor[0] == 0); // type
			assertTrue(resultXor[1] == 0x20); // XORMapped type = 0x20
			assertTrue(resultXor[2] == 0); // length
			assertTrue(resultXor[3] == 8);
			assertTrue(resultXor[4] == data[0]);
			assertTrue(resultXor[5] == data[1]);
			assertTrue(resultXor[6] == portByte[0]); // X-Port
			assertTrue(resultXor[7] == portByte[1]);
			assertTrue(resultXor[8] == addressByte[0]);
			assertTrue(resultXor[9] == addressByte[1]);
			assertTrue(resultXor[10] == addressByte[2]);
			assertTrue(resultXor[11] == addressByte[3]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Test method for 'de.javawi.jstun.attribute.MappedResponseChangedSourceAddressReflectedFrom.getPort()'
	 */
	@Test
	public void testGetPort() {
		assertTrue(map.getPort() == port);
		assertTrue(xor.getPort() == port);
	}

	/*
	 * Test method for 'de.javawi.jstun.attribute.MappedResponseChangedSourceAddressReflectedFrom.getAddress()'
	 */
	@Test
	public void testGetAddress() {
		try {
			System.out.println(map.getAddress().toString());
			assertTrue(map.getAddress().equals(new de.javawi.jstun.util.IPv4Address(addressString)));
			assertTrue(xor.getAddress().equals(new de.javawi.jstun.util.IPv4Address(addressString)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Test method for 'de.javawi.jstun.attribute.MappedResponseChangedSourceAddressReflectedFrom.setPort(int)'
	 */
	public void testSetPort() {

	}

	/*
	 * Test method for 'de.javawi.jstun.attribute.MappedResponseChangedSourceAddressReflectedFrom.setAddress(Address)'
	 */
	public void testSetAddress() {

	}

	/*
	 * Test method for 'de.javawi.jstun.attribute.MappedResponseChangedSourceAddressReflectedFrom.toString()'
	 */
	public void testToString() {

	}

}
