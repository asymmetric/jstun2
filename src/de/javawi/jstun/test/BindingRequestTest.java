package de.javawi.jstun.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import de.javawi.jstun.attribute.AbstractMappedAddress;
import de.javawi.jstun.attribute.ErrorCode;
import de.javawi.jstun.attribute.Software;
import de.javawi.jstun.attribute.AbstractMessageAttribute.MessageAttributeType;
import de.javawi.jstun.attribute.exception.AttributeReflectionException;
import de.javawi.jstun.attribute.exception.MessageAttributeException;
import de.javawi.jstun.header.MessageHeader;
import de.javawi.jstun.header.MessageHeaderInterface;
import de.javawi.jstun.header.MessageType;
import de.javawi.jstun.header.exception.MessageHeaderParsingException;
import de.javawi.jstun.header.exception.MessageTypeException;
import de.javawi.jstun.util.UtilityException;

public class BindingRequestTest {
	
//	final Logger logger = LoggerFactory.getLogger(BindingRequestTest.class);
	private static final Logger logger = Logger.getLogger("de.javawi.jstun");
	
	InetAddress stunServer;
	InetAddress address;
	int port;
	int timeout;
	DatagramSocket s1;
	private byte[] data;
	
	public BindingRequestTest(InetAddress localAddress, String stunServer, int port) throws UnknownHostException {
		this.address = localAddress;
		this.port = port;
		this.stunServer = InetAddress.getByName(stunServer);
	}
	
	public boolean test() throws UtilityException, MessageHeaderParsingException,
	MessageAttributeException, IOException, MessageTypeException, AttributeReflectionException {
		s1 = new DatagramSocket();
		
		s1.connect(stunServer, port);
		s1.setSoTimeout(500); // TODO configurable
		
		MessageHeader bindReq = new MessageHeader(new MessageType(MessageHeaderInterface.BINDING, MessageHeaderInterface.REQUEST));
		bindReq.initHeader();
		bindReq.addMessageAttribute(new Software());
		data = bindReq.getBytes();
		
		DatagramPacket send = new DatagramPacket(data, data.length);
		s1.send(send);
//		logger.debug("Binding Request sent");
		
		MessageHeader bindResp = new MessageHeader();
		
		while (! (bindResp.equalTransactionID(bindReq)) ) {
			DatagramPacket recv = new DatagramPacket(new byte[200], 200); // TODO size?
			s1.receive(recv);
			
			byte[] recvData = recv.getData();
			bindResp = MessageHeader.parseHeader(recvData);
			bindResp.parseAttributes(recvData);
		}
//		logger.debug("Binding Response received");
		
		ErrorCode errorCode =  (ErrorCode) bindResp.getMessageAttribute(MessageAttributeType.ErrorCode);
		
		if (errorCode != null) {
//			logger.warn("Error! Code: {}, Reason: {}", errorCode.getResponseCode(), errorCode.getReason());
			logger.warning("Error! Code: "+errorCode.getResponseCode()+" Reason: " + errorCode.getReason());
			return false;
		} else {
			AbstractMappedAddress amattr = bindResp.getMappedAddress();
			logger.info("Your public transport address is " + amattr.getAddress().toString()+":"+amattr.getPort());
		}
		
		return true;
	}

}
