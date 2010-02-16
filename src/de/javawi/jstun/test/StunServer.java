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

package de.javawi.jstun.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import de.javawi.jstun.attribute.UnknownAttribute;
import de.javawi.jstun.attribute.XORMappedAddress;
import de.javawi.jstun.attribute.AbstractMessageAttribute.MessageAttributeType;
import de.javawi.jstun.attribute.exception.MessageAttributeException;
import de.javawi.jstun.attribute.exception.MessageAttributeParsingException;
import de.javawi.jstun.attribute.exception.UnknownMessageAttributeException;
import de.javawi.jstun.header.MessageHeader;
import de.javawi.jstun.header.MessageHeaderInterface;
import de.javawi.jstun.header.MessageHeaderInterface.MessageHeaderClass;
import de.javawi.jstun.header.exception.MessageHeaderParsingException;
import de.javawi.jstun.header.messagetype.method.Binding;
import de.javawi.jstun.util.IPv4Address;
import de.javawi.jstun.util.UtilityException;

/*
 * This class implements a STUN server as described in RFC 3489.
 * The server requires a machine that is dual-homed to be functional.
 */
public class StunServer {
	private static Logger logger = Logger.getLogger("de.javawi.stun.test.StunServer");
	Vector<DatagramSocket> sockets;

	public StunServer(int primaryPort, InetAddress primary, int secondaryPort,
			InetAddress secondary) throws SocketException {
		sockets = new Vector<DatagramSocket>();
		sockets.add(new DatagramSocket(primaryPort, primary));
		sockets.add(new DatagramSocket(secondaryPort, primary));
		sockets.add(new DatagramSocket(primaryPort, secondary));
		sockets.add(new DatagramSocket(secondaryPort, secondary));
	}

	public void start() throws SocketException {
		for (DatagramSocket socket : sockets) {
			socket.setReceiveBufferSize(2000);
			StunServerReceiverThread ssrt = new StunServerReceiverThread(socket);
			ssrt.start();
		}
	}

	/*
	 * Inner class to handle incoming packets and react accordingly.
	 * I decided not to start a thread for every received Binding Request, because the time
	 * required to receive a Binding Request, parse it, generate a Binding Response and send
	 * it varies only between 2 and 4 milliseconds. This amount of time is small enough so
	 * that no extra thread is needed for incoming Binding Request.
	 */
	class StunServerReceiverThread extends Thread {
		private final DatagramSocket receiverSocket;
//		private DatagramSocket changedPort;
//		private DatagramSocket changedIP;
		private DatagramSocket changedPortIP;

		StunServerReceiverThread(DatagramSocket datagramSocket) {
			receiverSocket = datagramSocket;
//			for (DatagramSocket socket : sockets) {
//				if ((socket.getLocalPort() != receiverSocket.getLocalPort())
//						&& (socket.getLocalAddress().equals(receiverSocket.getLocalAddress())))
//					changedPort = socket;
//				if ((socket.getLocalPort() == receiverSocket.getLocalPort())
//						&& (!socket.getLocalAddress().equals(receiverSocket.getLocalAddress())))
//					changedIP = socket;
//				if ((socket.getLocalPort() != receiverSocket.getLocalPort())
//						&& (!socket.getLocalAddress().equals(receiverSocket.getLocalAddress())))
//					changedPortIP = socket;
//			}
		}

		public void run() {
			while (true) {
				try {
					DatagramPacket receive = new DatagramPacket(new byte[200], 200);
					receiverSocket.receive(receive);
					logger.finest(receiverSocket.getLocalAddress().getHostAddress() + ":"
							+ receiverSocket.getLocalPort() + " datagram received from "
							+ receive.getAddress().getHostAddress() + ":" + receive.getPort());
					
					MessageHeader receiveMH = new MessageHeader(receive.getData());

					try {
						boolean stun2;

						/* TODO backwards compatibility checks
						 * check for unknown attributes
						 */
						stun2 = receiveMH.equalMagicCookie();
							/* TODO if it's stun1 :
							 * SHOULD not use multiplexing
							 */


						// TODO check for unknown attributes
						receiveMH.parseAttributes(receive.getData()); // TODO doesn't work

						if (receiveMH.getType().getEncoding() == MessageHeaderInterface.BINDINGREQUEST) {
							logger.config(receiverSocket.getLocalAddress().getHostAddress()
									+ ":" + receiverSocket.getLocalPort()
									+ " Binding Request received from "
									+ receive.getAddress().getHostAddress() + ":"
									+ receive.getPort());

							MessageHeader sendMH = new MessageHeader(new Binding(MessageHeaderClass.SUCCESSRESPONSE));
							sendMH.setTransactionID(receiveMH.getTransactionID());

							XORMappedAddress ma;
							// (XOR)Mapped address attribute
							if (stun2)
								ma = new XORMappedAddress();
							else
								ma = new XORMappedAddress(MessageAttributeType.MappedAddress);

							// TODO make it work independently of the IP version
							ma.setAddress(new IPv4Address((Inet4Address) receive.getAddress()));
							ma.setPort(receive.getPort());
							sendMH.addMessageAttribute(ma);
						}
						else {
							/* TODO there are no other cases for now,
							 * as there is one single method defined in the RFC.
							 * what should we do here?
							 */
						}
					} catch (UnknownMessageAttributeException umae) {
						umae.printStackTrace();
						// Generate Binding error response
						MessageHeader sendMH = new MessageHeader(new Binding(MessageHeaderClass.ERRORRESPONSE));
						sendMH.setTransactionID(receiveMH.getTransactionID());

						// Unknown attributes
						UnknownAttribute ua = new UnknownAttribute();
						// TODO rework here, non e' normale che al primo unknown spedisce il pacchetto
						ua.addAttribute(umae.getType());
						sendMH.addMessageAttribute(ua);

						byte[] data = sendMH.getBytes();
						DatagramPacket send = new DatagramPacket(data, data.length);
						send.setPort(receive.getPort());
						send.setAddress(receive.getAddress());
						receiverSocket.send(send);
						logger.config(changedPortIP.getLocalAddress().getHostAddress() + ":"
								+ changedPortIP.getLocalPort()
								+ " send Binding Error Response to "
								+ send.getAddress().getHostAddress() + ":" + send.getPort());
					}
				} catch (IOException ioe) {
					ioe.printStackTrace();
				} catch (MessageAttributeParsingException mape) {
					mape.printStackTrace();
				} catch (MessageAttributeException mae) {
					mae.printStackTrace();
				} catch (MessageHeaderParsingException mhpe) {
					mhpe.printStackTrace();
				} catch (UtilityException ue) {
					ue.printStackTrace();
				} catch (ArrayIndexOutOfBoundsException aioobe) {
					aioobe.printStackTrace();
				}
			}
		}
	}

	/*
	 * To invoke the STUN server two IP addresses and two ports are required.
	 */
	public static void main(String args[]) {
		try {
			if (args.length != 4) {
				System.out.println("usage: java de.javawi.jstun.test.demo.StunServer PORT1 IP1 PORT2 IP2");
				System.out.println();
				System.out.println(" PORT1 - the first port that should be used by the server");
				System.out.println("   IP1 - the first ip address that should be used by the server");
				System.out.println(" PORT2 - the second port that should be used by the server");
				System.out.println("   IP2 - the second ip address that should be used by the server");
				System.exit(0);
			}
			Handler fh = new FileHandler("logging_server.txt");
			fh.setFormatter(new SimpleFormatter());
			Logger.getLogger("de.javawi.stun").addHandler(fh);
			Logger.getLogger("de.javawi.stun").setLevel(Level.ALL);
			StunServer ss = new StunServer(Integer.parseInt(args[0]), InetAddress
					.getByName(args[1]), Integer.parseInt(args[2]), InetAddress
					.getByName(args[3]));
			ss.start();
		} catch (SocketException se) {
			se.printStackTrace();
		} catch (UnknownHostException uhe) {
			uhe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}