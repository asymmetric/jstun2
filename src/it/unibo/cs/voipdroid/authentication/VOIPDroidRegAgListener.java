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
