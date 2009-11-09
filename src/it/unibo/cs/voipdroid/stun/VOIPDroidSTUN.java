/* 
*  Copyright 2007, 2008, 2009 Luca Bonora, Luca Bedogni, Lorenzo Manacorda
*  
*  This file is part of VOIPDroid.
*
*  VOIPDroid is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*  
*  VOIPDroid is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*  
*  You should have received a copy of the GNU General Public License
*  along with VOIPDroid.  If not, see <http://www.gnu.org/licenses/>.
*/
package it.unibo.cs.voipdroid.stun;

import it.unibo.cs.voipdroid.VOIPDroid;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.util.Log;

//import com.jstun.core.attribute.ChangedAddress;
import de.jstun.core.attribute.ErrorCode;
import de.jstun.core.attribute.MappedAddress;
import de.jstun.core.attribute.MessageAttribute;
import de.jstun.core.attribute.MessageAttributeParsingException;
import de.jstun.core.header.MessageHeader;
import de.jstun.core.header.MessageHeaderException;
import de.jstun.core.header.MessageHeaderParsingException;
import de.jstun.core.header.MessageHeaderInterface.MessageHeaderClass;
import de.jstun.core.header.messagetype.*;
import de.jstun.core.util.UtilityException;

public class VOIPDroidSTUN {
	
	private MessageHeader sendMh;
	private MessageHeader recvMh;
	private byte[] data;
	private DatagramSocket udp_s;
	
	private InetAddress public_address = java.net.InetAddress.getLocalHost(); // TODO rifare meglio?
	private InetAddress local_address = null;
	
	// TODO salvarli nel db
	private String server;
	private final int port = 3478;
	
	// TODO per essere non-blocking, forse dovrebbe estendere Thread (come UdpProvider)
	public VOIPDroidSTUN() throws UtilityException, IOException, 
			MessageHeaderParsingException, MessageAttributeParsingException {
		
		server = VOIPDroid.getStunServer(); // TODO non posso passarglielo come param? decoupling
		udp_s = new DatagramSocket();
		udp_s.connect(InetAddress.getByName(server), port);
		udp_s.setSoTimeout(2000);
		
		Binding bReq = new Binding(MessageHeaderClass.REQUEST);
		sendMh = new MessageHeader(bReq).initializeHeader();
		data = sendMh.getBytes();
		Log.v("STUN", "CREATE OK");
	}
	
	public void sendReq() throws IOException {
		DatagramPacket send = new DatagramPacket(data, data.length, InetAddress.getByName(server), port);
		udp_s.send(send);
		Log.v("STUN", "SEND OK");
	}
	
	// TODO perche' va in timeout?
	public boolean getReply() throws IOException, MessageAttributeParsingException, MessageHeaderException, UtilityException {
		recvMh = new MessageHeader();
		while (!(recvMh.equalTransactionID(sendMh))) {
			DatagramPacket receive = new DatagramPacket(new byte[200], 200);
			udp_s.receive(receive);
			recvMh = MessageHeader.parseHeader(receive.getData());
			recvMh.parseAttributes(receive.getData());
		}
		
		// indicates the source IP address and port the server saw in the Binding Request
		MappedAddress ma = (MappedAddress) recvMh.getMessageAttribute(MessageAttribute.MessageAttributeType.MappedAddress);
//		ChangedAddress ca = (ChangedAddress) recvMh.getMessageAttribute(MessageAttribute.MessageAttributeType.ChangedAddress);
		ErrorCode ec = (ErrorCode) recvMh.getMessageAttribute(MessageAttribute.MessageAttributeType.ErrorCode);
		if (ec != null) {
			Log.w("STUN GETREPLY", "Message header contains errorcode message attribute.");
			throw new MessageHeaderException("Error in header");
		}
		
		// XXX nell'emulatore sara' sempre diverso:
		// http://developer.android.com/guide/developing/tools/emulator.html#networkaddresses
		public_address = ma.getAddress().getInetAddress();
		local_address = udp_s.getLocalAddress();
		if ((ma.getPort() == udp_s.getLocalPort()) && (public_address.equals(local_address))) {
			Log.i("STUN", "NOT NATTED");
			return false;
		} else {
			Log.i("STUN", "NATTED");
			return true;
		}
		
		
	}

	public InetAddress getPublicAddress() {
		return public_address;
	}
	

}
