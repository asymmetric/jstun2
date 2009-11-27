package de.jstun.core.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public interface Address {

	static final int IPv4 = 4;
	static final int IPv6 = 6;

	public byte[] getBytes() throws UtilityException;

	public InetAddress getInetAddress() throws UtilityException,
			UnknownHostException;

	public int hashCode();

}