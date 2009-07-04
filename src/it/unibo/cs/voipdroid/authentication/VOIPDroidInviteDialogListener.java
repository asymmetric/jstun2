package it.unibo.cs.voipdroid.authentication;

import it.unibo.cs.voipdroid.VOIPDroid;

import org.zoolu.sdp.SessionDescriptor;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.call.SdpTools;
import org.zoolu.sip.dialog.ExtendedInviteDialogListener;
import org.zoolu.sip.dialog.InviteDialog;
import org.zoolu.sip.header.CallIdHeader;
import org.zoolu.sip.header.ContactHeader;
import org.zoolu.sip.header.FromHeader;
import org.zoolu.sip.header.Header;
import org.zoolu.sip.header.MultipleHeader;
import org.zoolu.sip.message.Message;

import android.util.Log;


public class VOIPDroidInviteDialogListener implements ExtendedInviteDialogListener {
	
	private final int SENT = 1;
	private final int RECEIVED= 2;

	public void onDlgAck(InviteDialog dialog, String body, Message msg) {
		// TODO Auto-generated method stub
		Log.v("INVITE","1");
	}

	public void onDlgBye(InviteDialog dialog, Message msg) {
		// TODO Auto-generated method stub
		Log.v("INVITE","2");
	}

	public void onDlgByeFailureResponse(InviteDialog dialog, int code,
			String reason, Message msg) {
		// TODO Auto-generated method stub
		Log.v("INVITE","3");
	}

	public void onDlgByeSuccessResponse(InviteDialog dialog, int code,
			String reason, Message msg) {
		// TODO Auto-generated method stub
		Log.v("INVITE","4");
	}

	public void onDlgCall(InviteDialog dialog) {
		// TODO Auto-generated method stub
		Log.v("INVITE","5");
/*		dialog.getSipProvider();
		
		VOIPDroidListener listener = new VOIPDroidListener();
		Call call = new Call(dialog.getSipProvider(),dialog.getRemoteContact().toString(),
				dialog.getLocalContact().toString(), listener);
		call.call("erbedo@ekiga.net");
		Log.v("INVITE","TRYING CALL");*/

	}

	public void onDlgCancel(InviteDialog dialog, Message msg) {
		// TODO Auto-generated method stub
		Log.v("INVITE","6");
		VOIPDroid.setCallState("onDlgCancel");
	}

	public void onDlgClose(InviteDialog dialog) {
		// TODO Auto-generated method stub
		Log.v("INVITE","7");
		VOIPDroid.setCallState("onDlgClose");
		VOIPDroidInviteDialogListener idl = new VOIPDroidInviteDialogListener();
		switch (VOIPDroid.getOnCall()) {
			case SENT: {
//				VOIPDroid.setInviteDialog(VOIPDroid.getSp(), idl);
				VOIPDroid.setOnCall(0);
			}
			case RECEIVED: {
//				VOIPDroid.setInviteDialogListener(VOIPDroid.getSp(), idl);
//				VOIPDroid.getInviteDialogListener().listen();
				VOIPDroid.setOnCall(0);
			}
			default : VOIPDroid.setOnCall(0); 
		}
	}

	public void onDlgInvite(InviteDialog dialog, NameAddress callee,
			NameAddress caller, String body, Message msg) {
		// TODO Auto-generated method stub
		Log.v("INVITE","8");
		VOIPDroid.setCallState("onDlgInvite");
//		if (body != null && body.length() > 0){
//			SessionDescriptor remote_sdp = new SessionDescriptor(body);
//			SessionDescriptor local_sdp = new SessionDescriptor(VOIPDroid.getSessionDescriptor());
//			SessionDescriptor new_sdp = new SessionDescriptor(remote_sdp
//					.getOrigin(), remote_sdp.getSessionName(), local_sdp
//					.getConnection(), local_sdp.getTime());
//			new_sdp.addMediaDescriptors(local_sdp.getMediaDescriptors());
//			new_sdp = SdpTools.sdpMediaProduct(new_sdp, remote_sdp
//					.getMediaDescriptors());
//			new_sdp = SdpTools.sdpAttirbuteSelection(new_sdp, "rtpmap");
//			local_session = new_sdp.toString();
//		}else {
//			
//		}
		VOIPDroid.incomingCall(caller, body);
	}

	public void onDlgInviteFailureResponse(InviteDialog dialog, int code,
			String reason, Message msg) {
		// TODO Auto-generated method stub
		Log.v("INVITE","9");
		VOIPDroid.setCallState("onDlgInviteFailureResponse");
	}

	public void onDlgInviteProvisionalResponse(InviteDialog dialog, int code,
			String reason, String body, Message msg) {
		// TODO Auto-generated method stub
		Log.v("INVITE","10 - " + String.valueOf(code));
		VOIPDroid.setCallState("onDlgInviteProvisionalResponse");
		Message ack = new Message();
		
		ack.setRecordRoutes(msg.getRecordRoutes());
		ack.addViaHeader(msg.getViaHeader());
		//ack.addViaHeader(new ViaHeader(msg.getRouteHeader().getValue(),));
		Log.v("ROOUTE", msg.getRouteHeader().getValue());
		ack.setFromHeader(new FromHeader(msg.getFromHeader()));
		ack.setCallIdHeader(new CallIdHeader(msg.getCallIdHeader()));
		ack.setToHeader(msg.getToHeader());
//		ContactHeader ch = new ContactHeader(new NameAddress("sip:bedoonandroid@" + 
//				dialog.getSipProvider().getViaAddress()));
//		ack.addContactHeader(ch, false);
//		ack.addContactHeader(new ContactHeader(msg.getToHeader().getNameAddress()), false);
//		ack.addHeader(msg.getHeader("Allow"), false);
//		ack.addHeader(new Header("P-hint","rr-enforced"), false);
		Log.v("MMM",msg.toString());
		Log.v("ACTIONS",ack.toString());
		Log.v("ACTIONS","MESSAGE SENT");
		
		dialog.ackWithAnswer(ack);
		
		Log.v("INVITE","10 FINE");
		
	}

	public void onDlgInviteRedirectResponse(InviteDialog dialog, int code,
			String reason, MultipleHeader contacts, Message msg) {
		// TODO Auto-generated method stub
		Log.v("INVITE","11");
		VOIPDroid.setCallState("onDlgInviteRedirectResponse");
	}

	
	public void onDlgInviteSuccessResponse(InviteDialog dialog, int code,
			String reason, String body, Message msg) {
		// TODO Auto-generated method stub
		Log.v("INVITE","12, onDlgInviteSuccessResponse");
		VOIPDroid.setCallState("Connected succesfully to calle.");
	}

	
	public void onDlgReInvite(InviteDialog dialog, String body, Message msg) {
		// TODO Auto-generated method stub
		Log.v("INVITE","13");
	}

	
	public void onDlgReInviteFailureResponse(InviteDialog dialog, int code,
			String reason, Message msg) {
		// TODO Auto-generated method stub
		Log.v("INVITE","14");
	}

	
	public void onDlgReInviteProvisionalResponse(InviteDialog dialog, int code,
			String reason, String body, Message msg) {
		// TODO Auto-generated method stub
		Log.v("INVITE","15");
	}

	
	public void onDlgReInviteSuccessResponse(InviteDialog dialog, int code,
			String reason, String body, Message msg) {
		// TODO Auto-generated method stub
		Log.v("INVITE","16");
	}

	
	public void onDlgReInviteTimeout(InviteDialog dialog) {
		// TODO Auto-generated method stub
		Log.v("INVITE","17");
	}

	
	public void onDlgTimeout(InviteDialog dialog) {
		// TODO Auto-generated method stub
		Log.v("INVITE","18");
	}

	
	public void onDlgAltRequest(InviteDialog dialog, String method,
			String body, Message msg) {
		// TODO Auto-generated method stub
		Log.v("INVITE","19");
	}

	
	public void onDlgAltResponse(InviteDialog dialog, String method, int code,
			String reason, String body, Message msg) {
		// TODO Auto-generated method stub
		Log.v("INVITE","20");
	}

	
	public void onDlgNotify(InviteDialog dialog, String event,
			String sipfragment, Message msg) {
		// TODO Auto-generated method stub
		Log.v("INVITE","21");
	}

	
	public void onDlgRefer(InviteDialog dialog, NameAddress refer_to,
			NameAddress referred_by, Message msg) {
		// TODO Auto-generated method stub
		Log.v("INVITE","22");
	}

	
	public void onDlgReferResponse(InviteDialog dialog, int code,
			String reason, Message msg) {
		// TODO Auto-generated method stub
		Log.v("INVITE","23");
	}

}
