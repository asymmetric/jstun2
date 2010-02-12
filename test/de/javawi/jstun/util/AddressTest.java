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

package de.javawi.jstun.util;

import junit.framework.TestCase;

import org.junit.Test;

public class AddressTest extends TestCase {
	IPv4Address ipv4;
	IPv6Address ipv6;
	Address addr;
	
	public AddressTest(String mesg) {
		super(mesg);
	}

	protected void setUp() throws Exception {
		ipv4 = new IPv4Address("192.168.100.1");
		ipv6 = new IPv6Address("de:ad:be:af");
		addr = new IPv4Address("192.168.1.1");
	}

	/*
	 * Test method for 'de.javawi.jstun.util.IPv4Address.IPv4Address(int, int, int, int)'
	 */
	@Test
	public void testAddressIntIntIntInt() {
		try {
			Address comp = new IPv4Address(192,168,100,1);
			assertTrue(ipv4.equals(comp));
		} catch (UtilityException ue) {
			ue.printStackTrace();
		}
	}

	/*
	 * Test method for 'de.javawi.jstun.util.Address.Address(String)'
	 */
	@Test
	public void testAddressString() {
		try {
			Address comp = new IPv4Address("192.168.100.1");
			assertTrue(ipv4.equals(comp));

			comp = new IPv6Address("de:ad:be:af");
			assertTrue(ipv6.equals(comp));
		} catch (UtilityException ue) {
			ue.printStackTrace();
		}
	}

	/*
	 * Test method for 'de.javawi.jstun.util.Address.Address(byte[])'
	 */
	@Test
	public void testAddressByteArray() {
		try {
			byte[] data = {(byte)192, (byte)168, (byte)100, (byte)1};
			Address comp = new IPv4Address(data);
			assertTrue(ipv4.equals(comp));
		} catch (UtilityException ue) {
			ue.printStackTrace();
		}
	}

	/*
	 * Test method for 'de.javawi.jstun.util.Address.toString()'
	 */
	public void testToString() {
		try {
			Address comp = new IPv4Address("192.168.100.1");
			assertTrue(ipv4.equals(comp));
		} catch (UtilityException ue) {
			ue.printStackTrace();
		}
	}

	/*
	 * Test method for 'de.javawi.jstun.util.Address.getBytes()'
	 */
	public void testGetBytes() {
		try {
			byte[] data = ipv4.getBytes();
			assertTrue(data[0] == (byte)192);
			assertTrue(data[1] == (byte)168);
			assertTrue(data[2] == (byte)100);
			assertTrue(data[3] == (byte)1);
		} catch (UtilityException ue) {
			ue.printStackTrace();
		}
		
	}

	/*
	 * Test method for 'de.javawi.jstun.util.Address.getInetAddress()'
	 */
	public void testGetInetAddress() {
		try {
			Address comp = new IPv4Address("192.168.100.1");
			assertTrue(ipv4.getInetAddress().equals(comp.getInetAddress()));
			comp = new IPv4Address("192.168.100.2");
			assertFalse(ipv4.getInetAddress().equals(comp.getInetAddress()));
		} catch (UtilityException ue) {
			ue.printStackTrace();
		} catch (java.net.UnknownHostException uhe) {
			uhe.printStackTrace();
		}
	}

	/*
	 * Test method for 'de.javawi.jstun.util.Address.equals(Object)'
	 */
	public void testEqualsObject() {
		try {
			Address comp = new IPv4Address("192.168.100.1");
			assertTrue(ipv4.equals(comp));
			comp = new IPv4Address("192.168.100.2");
			assertFalse(ipv4.equals(comp));
		} catch (UtilityException ue) {
			ue.printStackTrace();
		}
	}

}
