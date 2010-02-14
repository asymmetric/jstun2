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

import java.net.InetAddress;
import java.net.UnknownHostException;

public interface Address {

	/**
	 * Enum representing the IP family
	 * 
	 */
	public static enum Family {
		IPv4(0x01), IPv6(0x02);

		int encoding;

		Family(int e) {
			encoding = e;
		}

		public int getEncoding() {
			return encoding;
		}
	}

	// convenience constant definitions
	final static int IPv4 = 0x01;
	final static int IPv6 = 0x02;

	public byte[] getBytes() throws UtilityException;

	public InetAddress getInetAddress() throws UtilityException, UnknownHostException;

	public int hashCode();

}
