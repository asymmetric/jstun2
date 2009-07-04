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
package it.unibo.cs.voipdroid;

import java.util.Vector;

import org.zoolu.sdp.SessionDescriptor;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.call.Call;
import org.zoolu.sip.call.CallListener;
import org.zoolu.sip.call.SdpTools;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.provider.SipInterface;

import android.util.Log;

/**
 * Implements the CallListener interface to manage sip calls
 * 
 */
public class VOIPDroidListener implements CallListener {

	private final int SENT = 1;
	private final int RECEIVED = 2;

	public void onReceivedMessage(SipInterface sip, Message message) {
		// TODO Auto-generated method stub
		Log.v("ACTIONS", "onReceivedMessage");
	}

	public void onCallAccepted(Call call, String sdp, Message resp) {
		// TODO Auto-generated method stub
		Log.v("ACTIONS", "onCallAccepted");
		VOIPDroid.setCallState("Call accepted");
		// call.ackWithAnswer(sdp);
		// call.accept(sdp);

		// Log.v("ACTIONS",sdp.toString());
		// Log.v("ACTIONS",resp.toString());
		// call.ackWithAnswer(sdp);
		// Message ack = new Message(SipMethods.ACK);
		// ack.addRouteHeader(new RouteHeader(new NameAddress("ekiga.net")));
		// ack.addViaHeader(resp.getViaHeader());
		//		
		// ack.setFromHeader(new FromHeader(resp.getFromHeader()));
		// ack.setCallIdHeader(new CallIdHeader(resp.getCallIdHeader()));
		// ack.setToHeader(resp.getToHeader());
		// ContactHeader ch = new ContactHeader(new
		// NameAddress("sip:bedoonandroid@" +
		// call.getSip_provider().getViaAddress()));
		// ack.addContactHeader(ch, false);
		// ack.addContactHeader(new
		// ContactHeader(resp.getToHeader().getNameAddress()), false);
		// Log.v("MMM",resp.toString());
		// Log.v("ACTIONS",ack.toString());
		// call.getSip_provider().sendMessage(ack);
		// Log.v("ACTIONS","MESSAGE SENT");
		// call.bye();
		// Log.v("ACTIONS","AFTER BYE");

		// DialogInfo d = new DialogInfo();
		// Message ack = MessageFactory.create2xxAckRequest((Dialog)d,
		// resp.getBody());
		//		
		// call.getSip_provider().sendMessage(ack);
		Log.v("ACTIONS", "ACKed");
	}

	public void onCallCanceling(Call call, Message cancel) {
		// TODO Auto-generated method stub
		Log.v("ACTIONS", "onCallCancelling");
	}

	public void onCallClosed(Call call, Message resp) {
		// TODO Auto-generated method stub
		Log.v("ACTIONS", "onCallClosed");
		VOIPDroid.setCallState("Call Closed");
		switch (VOIPDroid.getOnCall()) {
		case SENT: {
			VOIPDroid.setCall(VOIPDroid.call, VOIPDroid.getSp(), VOIPDroid
					.getListener());
			VOIPDroid.setOnCall(0);
		}
		case RECEIVED: {
			VOIPDroid.setCall(VOIPDroid.callListener, VOIPDroid.getSp(),
					VOIPDroid.getListener());
			VOIPDroid.getCallListener().listen();
			VOIPDroid.setOnCall(0);
		}
		default:
			VOIPDroid.setOnCall(0);
		}
	}

	public void onCallClosing(Call call, Message bye) {
		// TODO Auto-generated method stub
		Log.v("ACTIONS", "onCallClosing");
		VOIPDroid.setCallState("Call Closing");
		switch (VOIPDroid.getOnCall()) {
		case SENT: {
			VOIPDroid.setCall(VOIPDroid.call, VOIPDroid.getSp(), VOIPDroid
					.getListener());
			VOIPDroid.setOnCall(0);
		}
		case RECEIVED: {
			VOIPDroid.setCall(VOIPDroid.callListener, VOIPDroid.getSp(),
					VOIPDroid.getListener());
			VOIPDroid.getCallListener().listen();
			VOIPDroid.setOnCall(0);
		}
		default:
			VOIPDroid.setOnCall(0);
		}
	}

	public void onCallConfirmed(Call call, String sdp, Message ack) {
		// TODO Auto-generated method stub
		Log.v("ACTIONS", "onCallConfirmed");
	}

	public void onCallIncoming(Call call, NameAddress callee,
			NameAddress caller, String sdp, Message invite) {
		Log.v("ACTIONS", "onCallIncoming");
		call.ring();
		String local_session;
		Log.v("SDP", sdp);
		Log.v("SDP2", call.getLocalSessionDescriptor());
		if (sdp != null && sdp.length() > 0) {
			SessionDescriptor remote_sdp = new SessionDescriptor(sdp);
			SessionDescriptor local_sdp = new SessionDescriptor(call
					.getLocalSessionDescriptor());
			SessionDescriptor new_sdp = new SessionDescriptor(remote_sdp
					.getOrigin(), remote_sdp.getSessionName(), local_sdp
					.getConnection(), local_sdp.getTime());
			new_sdp.addMediaDescriptors(local_sdp.getMediaDescriptors());
			new_sdp = SdpTools.sdpMediaProduct(new_sdp, remote_sdp
					.getMediaDescriptors());
			new_sdp = SdpTools.sdpAttirbuteSelection(new_sdp, "rtpmap");
			local_session = new_sdp.toString();
		} else {
			local_session = call.getLocalSessionDescriptor();
		}
		// accept immediatly
		 call.accept(local_session);
//		VOIPDroid.incomingCall(caller, local_session);
	}

	public void onCallModifying(Call call, String sdp, Message invite) {
		// TODO Auto-generated method stub
		Log.v("ACTIONS", "onCallModifying");
	}

	public void onCallReInviteAccepted(Call call, String sdp, Message resp) {
		// TODO Auto-generated method stub
		Log.v("ACTIONS", "onCallReInviteAccepted");
	}

	public void onCallReInviteRefused(Call call, String reason, Message resp) {
		// TODO Auto-generated method stub
		Log.v("ACTIONS", "onCallReInviteRefused");
	}

	public void onCallReInviteTimeout(Call call) {
		// TODO Auto-generated method stub
		Log.v("ACTIONS", "onCallReInviteTimeout");
	}

	public void onCallRedirection(Call call, String reason,
			Vector<String> contact_list, Message resp) {
		// TODO Auto-generated method stub
		Log.v("ACTIONS", "onCallRedirection");
	}

	public void onCallRefused(Call call, String reason, Message resp) {
		// TODO Auto-generated method stub
		switch (Integer.valueOf(resp.getStatusLine().getCode())) {
		case 480:
			Log.v("ACTIONS", "CONTACT OFFLINE");
			break;
		case 603:
			Log.v("ACTIONS", "CONTACT DECLINED CALL");
			break;
		}
	}

	public void onCallRinging(Call call, Message resp) {
		// TODO Auto-generated method stub
		Log.v("ACTIONS", "MARUZZELLA, telephone is ringing");
	}

	public void onCallTimeout(Call call) {
		// TODO Auto-generated method stub
		Log.v("ACTIONS", "onCallTimeout");
	}
}