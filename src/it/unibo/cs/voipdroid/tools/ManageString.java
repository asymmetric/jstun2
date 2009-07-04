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
package it.unibo.cs.voipdroid.tools;

import android.util.Log;

public class ManageString {
	public static String SubStringContact(String contact) {
		String contact_url = null;
		if (!contact.endsWith(">")){
			int index;
			index = contact.lastIndexOf(';');
			contact_url = contact.substring(0, index);
			Log.v("CONTACT", contact_url );
		}
		return contact_url;
	}
	
	public static String SubStringHost(String contact) {
		String host = null;
		int index;
		int index2;
		index = contact.indexOf('@') + 1;
		index2 = contact.lastIndexOf(':');
		host= contact.substring(index, index2);
		Log.v("CONTACT", host );
		return host;
	}
	
	public static int SubStringPort(String contact) {
		int port = 0;
		String portString = null;
		int index;
		int index2;
		index = contact.lastIndexOf(':') + 1;
		index2 = contact.indexOf(';');
		portString = contact.substring(index, index2);
		port = Integer.parseInt(portString);
		Log.v("CONTACT", String.valueOf(port));
		return port;
	}
	
	public static String SubStringJid(String jid) {
		int index = jid.indexOf('@');
		String gizmoname = jid.substring(0, index);
		return gizmoname;
	}
}
