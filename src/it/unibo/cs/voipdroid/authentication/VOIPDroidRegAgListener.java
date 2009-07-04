package it.unibo.cs.voipdroid.authentication;

import it.unibo.cs.voipdroid.VOIPDroid;

import org.zoolu.sip.address.NameAddress;


import android.util.Log;

public class VOIPDroidRegAgListener implements RegisterAgentListener {

	public void onUaRegistrationFailure(RegisterAgent ra, NameAddress target,
			NameAddress contact, String result) {
		VOIPDroid.setSipState("Registration failed, " + result);
		VOIPDroid.logged = 2;
		Log.v("INVITE","REG FAILED");
	}

	public void onUaRegistrationSuccess(RegisterAgent ra, NameAddress target,
			NameAddress contact, String result) {

		if (VOIPDroid.registration) {
			VOIPDroid.setSipState("Connected");
			VOIPDroid.logged = 1;
		}
		else {
			VOIPDroid.setSipState("Disconnected");
			VOIPDroid.logged = 0;
		}
		Log.v("INVITE","REG OK");
	}

}
