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
	 * @author Lorenzo Manacorda (asymmetric)
	 * 
	 */
	public static enum family {
		IPv4, IPv6;
	}

	static final int IPv4 = 4;
	static final int IPv6 = 6;

	public byte[] getBytes() throws UtilityException;

	public InetAddress getInetAddress() throws UtilityException, UnknownHostException;

	public int hashCode();

}
