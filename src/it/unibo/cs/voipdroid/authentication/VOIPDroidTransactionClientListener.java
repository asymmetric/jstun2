package it.unibo.cs.voipdroid.authentication;

import it.unibo.cs.voipdroid.VOIPDroid;

import org.zoolu.sip.message.Message;
import org.zoolu.sip.transaction.TransactionClient;
import org.zoolu.sip.transaction.TransactionClientListener;

import android.util.Log;


public class VOIPDroidTransactionClientListener implements
		TransactionClientListener {

	public void onTransFailureResponse(TransactionClient tc, Message resp) {
		// TODO Auto-generated method stub
		Log.v("INVITE","onTransFailureResponse");
		VOIPDroid.setCallState("onTransFailureResponse");
	}

	public void onTransProvisionalResponse(TransactionClient tc, Message resp) {
		// TODO Auto-generated method stub
		Log.v("INVITE","onTransProvisionalResponse, resp = " + resp.toString());
		VOIPDroid.setCallState("onTransProvisionalResponse");
	}

	public void onTransSuccessResponse(TransactionClient tc, Message resp) {
		// TODO Auto-generated method stub
		Log.v("INVITE","onTransSuccessResponse");
		VOIPDroid.setCallState("onTransSuccessResponse");
	}

	public void onTransTimeout(TransactionClient tc) {
		// TODO Auto-generated method stub
		Log.v("INVITE","onTransTimeout");
		VOIPDroid.setCallState("onTransTimeout");
	}
}