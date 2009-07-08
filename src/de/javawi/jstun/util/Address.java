package de.javawi.jstun.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public interface Address {

	public byte[] getBytes() throws UtilityException;

	public InetAddress getInetAddress() throws UtilityException,
			UnknownHostException;

	public int hashCode();

}