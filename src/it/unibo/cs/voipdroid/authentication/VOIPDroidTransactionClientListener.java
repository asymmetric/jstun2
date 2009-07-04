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