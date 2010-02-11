package de.javawi.jstun.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import de.javawi.jstun.header.MessageHeader;
import de.javawi.jstun.header.MessageHeaderInterface.MessageHeaderClass;
import de.javawi.jstun.header.exception.MessageHeaderParsingException;
import de.javawi.jstun.header.messagetype.method.Binding;
import de.javawi.jstun.util.Address;
import de.javawi.jstun.util.UtilityException;


public class BindingRequest {
	
	String stunServer;
	Address address;
	int port;
	int timeout;
	DatagramSocket s1;
	private byte[] data;
	
	public BindingRequest(Address localAddress, String stunServer, int port) {
		this.address = localAddress;
		this.port = port;
		this.stunServer = stunServer;
	}
	
	public void test() throws UtilityException, IOException, MessageHeaderParsingException {
		s1 = new DatagramSocket();
		
		s1.connect(InetAddress.getByName(stunServer), port);
		s1.setSoTimeout(500); // TODO configurable
		
		MessageHeader bindReq = new MessageHeader(new Binding(MessageHeaderClass.REQUEST));
		bindReq.initHeader();
		data = bindReq.getBytes();
		
		DatagramPacket send = new DatagramPacket(data, data.length);
		s1.send(send);
		
		DatagramPacket recv = new DatagramPacket(new byte[200], 200); // TODO size?
		s1.receive(recv);
		
		MessageHeader bindResp = new MessageHeader(recv.getData());
		
	}

}
