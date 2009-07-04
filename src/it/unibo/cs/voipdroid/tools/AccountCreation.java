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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class AccountCreation {
	private static String sessionId;
	private static String accountUrl;
	private static String pngUrl;
	private static String jpgUrl;
	private static String failure;
	private static String messageText;
	private static String username;
	private static String domain;

	public static String getSession() {
		HttpGet get = new HttpGet(
				"https://aps.plugndial.com/dll/app?class=DLL;proc=start;ZoneID=1;PartnerID=0;");
		DefaultHttpClient httpclient = new DefaultHttpClient();
		String result = new String();
		try {
			// int status = httpclient.executeMethod(get);
			HttpResponse response = httpclient.execute(get);
			HttpEntity entity = response.getEntity();
			Log.d("httpGet", "Login form get: " + response.getStatusLine());
			int status = response.getStatusLine().getStatusCode();

			if (status != 404) {
				try {
					Reader reader = new InputStreamReader(entity.getContent());

					int x;
					int count = 0;
					byte by[] = new byte[50]; // TODO perche' 50?
					while ((x = reader.read()) != -1) {
						if (count < 50) {
							by[count] = (byte) x;
							result = result + (char) by[count];
						}
						count++;
						if (count == 50)
							count = 0;
					}
					// Log.v("STRINGA", result);
				} catch (Exception e) {
					/* Display any Error to the GUI. */
					Log.v("PARSESESSION", "Error: " + e.getMessage());
				}
			} else {
				Log.v("404", "Error: no resource in the given URL");
			}
			if (entity != null) {
				entity.consumeContent();
			}

		} catch (Exception e) {
			Log.e("Error:", e.getMessage());
		} finally {
			get = null;
		}
		return result;
	}

	public static Boolean readSession(String session) {
		String str;
		Boolean success = true;
		BufferedReader reader = new BufferedReader(new StringReader(session));

		try {
			while ((str = reader.readLine()) != null) {
				if (str.length() > 0 && success == true) {
					if (str.equals("Success=1")) {
						Log.v("READING", "1");
						continue;
					} else if (str.equals("Success=0")) {
						Log.v("READING", "0");
						continue;
					} else if (str.startsWith("Failure")) {
						Log.v("READING", "FAILURE");
						success = false;
						int index = str.indexOf('=') + 1;
						setFailure(str.substring(index));
						break;
					} else if (str.startsWith("memory:string:SessionId")) {
						Log.v("READING", "SESSIONID");
						int index;
						int index2;
						index = str.indexOf('"') + 1;
						index2 = str.lastIndexOf('"');
						setSessionId(str.substring(index, index2));
						Log.v("READING", getSessionId());
						continue;
					} else if (str
							.startsWith("registry:string:CreateAccountURL")) {
						Log.v("READING", "ACCOUNTURL");
						int index;
						int index2;
						index = str.indexOf('"') + 1;
						index2 = str.lastIndexOf('"');
						setAccountUrl(str.substring(index, index2));
						Log.v("READING", getAccountUrl());
						continue;
					} else if (str.startsWith("registry:string:SecurityJPGURL")) {
						Log.v("READING", "JPG");
						int index;
						int index2;
						index = str.indexOf('"') + 1;
						index2 = str.lastIndexOf('"');
						setJpgUrl(str.substring(index, index2));
						Log.v("READING", getJpgUrl());
						continue;
					} else if (str.startsWith("registry:string:SecurityPNGURL")) {
						Log.v("READING", "PNG");
						int index;
						int index2;
						index = str.indexOf('"') + 1;
						index2 = str.lastIndexOf('"');
						setPngUrl(str.substring(index, index2));
						Log.v("READING", getPngUrl());
						continue;
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}
	
	public static Boolean readResponse(String response) {
		String str;
		boolean success = true;
		BufferedReader reader = new BufferedReader(new StringReader(response));

		try {
			while ((str = reader.readLine()) != null) {
				if (str.length() > 0 && success == true) {
					Log.v("READING", str);
					if (str.equals("Success=1")) {
						Log.v("READING", "1");
						continue;
					} else if (str.equals("Success=0")) {
						Log.v("READING", "0");
						continue;
					} else if (str.startsWith("Failure")) {
						Log.v("READING", "FAILURE");
						success = false;
						int index;
						int index2;
						index = str.indexOf('"') + 1;
						index2 = str.lastIndexOf('"');
						setFailure(str.substring(index, index2));
						break;
					} else if (str.startsWith("MessageText1")) {
						Log.v("READING", "SESSIONID");
						int index;
						int index2;
						index = str.indexOf('"') + 1;
						index2 = str.lastIndexOf('"');
						setMessageText(str.substring(index, index2));
						Log.v("READING", getMessageText());
						continue;
					} else if (str
							.startsWith("memory:string:UsernameString")) {
						Log.v("READING", "Username");
						int index;
						int index2;
						index = str.indexOf('"') + 1;
						index2 = str.lastIndexOf('"');
						setUsername(str.substring(index, index2));
						Log.v("READING", getUsername());
						continue;
					} else if (str.startsWith("memory:string:Domain")) {
						Log.v("READING", "Domain");
						int index;
						int index2;
						index = str.indexOf('"') + 1;
						index2 = str.lastIndexOf('"');
						setDomain(str.substring(index, index2));
						Log.v("READING", getDomain());
						continue;
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}

	public static String getAccount(String accountUrl, String session, 
			String securityID, String firstname, String lastname, String address,
			String email, String username, String password, String eula) {
		HttpGet get = new HttpGet(accountUrl + ";ZoneID=1;PartnerID=0;ApplicationID=VoipDroid;OsType=Android;SessionID="
				+ session + ";SecurityID=" + securityID + ";FirstName=" + firstname 
				+ ";LastName=" + lastname + ";Address=" + address + ";CountryID=1;Password="
				+ password + ";Email=" + email + ";DeviceType=AndroidDevPhone1;UsernameString="
				+ username + ";CallingAppVersion=1.0;InstalledCoreVersion=1.0;EULA=" + eula );
		DefaultHttpClient httpclient = new DefaultHttpClient();
		String result = new String();
		try {
			// int status = httpclient.executeMethod(get);
			HttpResponse response = httpclient.execute(get);
			HttpEntity entity = response.getEntity();
			Log.v("httpGet", "Login form get: " + response.getStatusLine());
			int status = response.getStatusLine().getStatusCode();

			if (status != 404) {
				try {
					Reader reader = new InputStreamReader(entity.getContent());

					int x;
					int count = 0;
					byte by[] = new byte[50];
					while ((x = reader.read()) != -1) {
						if (count < 50) {
							by[count] = (byte) x;
							result = result + (char) by[count];
						}
						count++;
						if (count == 50)
							count = 0;
					}
					// Log.v("STRINGA", result);
				} catch (Exception e) {
					/* Display any Error to the GUI. */
					Log.v("PARSESESSION", "Error: " + e.getMessage());
				}
			} else {
				Log.v("404", "Error: no resource in the given URL");
			}
			if (entity != null) {
				entity.consumeContent();
			}

		} catch (Exception e) {
			Log.e("Error:", e.getMessage());
		} finally {
			get = null;
		}
		return result;
	}

	public static String getSessionId() {
		return sessionId;
	}

	private static void setSessionId(String sessionId) {
		AccountCreation.sessionId = sessionId;
	}

	public static String getAccountUrl() {
		return accountUrl;
	}

	private static void setAccountUrl(String accountUrl) {
		AccountCreation.accountUrl = accountUrl;
	}
	
	public static String getJpgUrl() {
		return jpgUrl;
	}

	private static void setJpgUrl(String jpgUrl) {
		AccountCreation.jpgUrl = jpgUrl;
	}

	public static String getPngUrl() {
		return pngUrl;
	}

	private static void setPngUrl(String pngUrl) {
		AccountCreation.pngUrl = pngUrl;
	}

	public static String getFailure() {
		return failure;
	}

	private static void setFailure(String failure) {
		AccountCreation.failure = failure;
	}

	public static String getMessageText() {
		return messageText;
	}

	private static void setMessageText(String messageText) {
		AccountCreation.messageText = messageText;
	}

	public static String getUsername() {
		return username;
	}

	private static void setUsername(String username) {
		AccountCreation.username = username;
	}

	public static String getDomain() {
		return domain;
	}

	private static void setDomain(String domain) {
		AccountCreation.domain = domain;
	}
}
