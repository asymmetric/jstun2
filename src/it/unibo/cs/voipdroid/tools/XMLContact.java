package it.unibo.cs.voipdroid.tools;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.util.Log;

public class XMLContact {

	public static ParsedProfileDataSet profile;
	public static String contactMD5;
	
	public static boolean getContacts(String username, String password) {
		HttpGet get = new HttpGet(
				"https://g5data04.gizmo5.com/xmlfeed/app?class=Subscriber;proc=getContacts;skip_redirect=1;xmlfeed_version=1;partnerId=0;raw=1;username="
						+ username + ";password=" + password);
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
			// int status = httpclient.executeMethod(get);
			HttpResponse response = httpclient.execute(get);
			HttpEntity entity = response.getEntity();
			Log.v("httpGet", "Login form get: " + response.getStatusLine());
			int status = response.getStatusLine().getStatusCode();

			if (status != 404) {
				try {

					/* Get a SAXParser from the SAXPArserFactory. */
					SAXParserFactory spf = SAXParserFactory.newInstance();
					SAXParser sp = spf.newSAXParser();
					/* Get the XMLReader of the SAXParser we created. */
					XMLReader xr = sp.getXMLReader();
					/*
					 * Create a new ContentHandler and apply it to the
					 * XML-Reader
					 */
					ContactHandler myContactHandler = new ContactHandler();
					xr.setContentHandler(myContactHandler);
					/* Parse the xml-data from our URL. */
					xr.parse(new InputSource(entity.getContent()));
					/* Parsing has finished. */

				} catch (Exception e) {
					/* Display any Error to the GUI. */
					Log.v("PARSECONTACT", "Error: " + e.getMessage());
					return false;
				}
			} else {
				Log.v("404", "Error: no resource in the given URL");
				return false;
			}
			if (entity != null) {
				entity.consumeContent();
			}

		} catch (Exception e) {
			Log.e("Error:", e.getMessage());
		} finally {
			get = null;
		}
		return true;
	}
	
	public static boolean getContactsMD5(String username, String password) {
		HttpGet get = new HttpGet(
				"https://g5data04.gizmo5.com/xmlfeed/app?class=Subscriber;proc=getContacts;skip_redirect=1;xmlfeed_version=1;partnerId=0;raw=1;username="
						+ username + ";password=" + password + ";md5=1;md5Only=1");
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
			// int status = httpclient.executeMethod(get);
			HttpResponse response = httpclient.execute(get);
			HttpEntity entity = response.getEntity();
			Log.v("httpGet", "Login form get: " + response.getStatusLine());
			int status = response.getStatusLine().getStatusCode();

			if (status != 404) {
				try {

					/* Get a SAXParser from the SAXPArserFactory. */
					SAXParserFactory spf = SAXParserFactory.newInstance();
					SAXParser sp = spf.newSAXParser();
					/* Get the XMLReader of the SAXParser we created. */
					XMLReader xr = sp.getXMLReader();
					/*
					 * Create a new ContentHandler and apply it to the
					 * XML-Reader
					 */
					ContactHandler myContactHandler = new ContactHandler();
					xr.setContentHandler(myContactHandler);
					/* Parse the xml-data from our URL. */
					xr.parse(new InputSource(entity.getContent()));
					/* Parsing has finished. */
					contactMD5 = myContactHandler.getParsedMd5();
					
				} catch (Exception e) {
					/* Display any Error to the GUI. */
					Log.v("PARSECONTACT", "Error: " + e.getMessage());
					return false;
				}
			} else {
				Log.v("404", "Error: no resource in the given URL");
				return false;
			}
			if (entity != null) {
				entity.consumeContent();
			}

		} catch (Exception e) {
			Log.e("Error:", e.getMessage());
		} finally {
			get = null;
		}
		return true;
	}
	
	public static void addContact(String username, String password, String jid, String nick) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		
		HttpPost post = new HttpPost("https://g5data04.gizmo5.com/xmlfeed/app?");
        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("class", "Subscriber"));
        nvps.add(new BasicNameValuePair("proc", "addContact"));
        nvps.add(new BasicNameValuePair("skip_redirect", "1"));
        nvps.add(new BasicNameValuePair("xmlfeed_version", "1"));
        nvps.add(new BasicNameValuePair("partnerId", "0"));
        nvps.add(new BasicNameValuePair("raw", "1"));
        nvps.add(new BasicNameValuePair("username", username));
        nvps.add(new BasicNameValuePair("password", password));
        nvps.add(new BasicNameValuePair("jid", jid));
        nvps.add(new BasicNameValuePair("nickName", nick));

        try {
			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        
        // Post, check and show the result (not really spectacular, but works):
        HttpResponse response = httpclient.execute(post);
        HttpEntity entity = response.getEntity(); 
        
        Log.v("httpPost", "Login form get: " + response.getStatusLine());
		try {

			/* Get a SAXParser from the SAXPArserFactory. */
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			/* Get the XMLReader of the SAXParser we created. */
			XMLReader xr = sp.getXMLReader();
			/*
			 * Create a new ContentHandler and apply it to the
			 * XML-Reader
			 */
			ContactHandler myContactHandler = new ContactHandler();
			xr.setContentHandler(myContactHandler);
			/* Parse the xml-data from our URL. */
			xr.parse(new InputSource(entity.getContent()));
			/* Parsing has finished. */

		} catch (Exception e) {
			/* Display any Error to the GUI. */
			Log.v("PARSE", "Error: " + e.getMessage());
		}
        if (entity != null) {
            entity.consumeContent();
        } 
        
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			Log.e("ERRORE HTTP", e.getMessage());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			Log.e("ERRORE HTTP", e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("ERRORE HTTP", e.getMessage());
		}
	}

	public static void removeContact(String username, String password, String jid) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		
		HttpPost post = new HttpPost("https://g5data04.gizmo5.com/xmlfeed/app?");
        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("class", "Subscriber"));
        nvps.add(new BasicNameValuePair("proc", "removeContact"));
        nvps.add(new BasicNameValuePair("skip_redirect", "1"));
        nvps.add(new BasicNameValuePair("xmlfeed_version", "1"));
        nvps.add(new BasicNameValuePair("partnerId", "0"));
        nvps.add(new BasicNameValuePair("raw", "1"));
        nvps.add(new BasicNameValuePair("username", username));
        nvps.add(new BasicNameValuePair("password", password));
        nvps.add(new BasicNameValuePair("jid", jid));

        try {
			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        
        // Post, check and show the result (not really spectacular, but works):
        HttpResponse response = httpclient.execute(post);
        HttpEntity entity = response.getEntity(); 
        
        Log.v("httpPost", "Login form get: " + response.getStatusLine());
		try {

			/* Get a SAXParser from the SAXPArserFactory. */
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			/* Get the XMLReader of the SAXParser we created. */
			XMLReader xr = sp.getXMLReader();
			/*
			 * Create a new ContentHandler and apply it to the
			 * XML-Reader
			 */
			ContactHandler myContactHandler = new ContactHandler();
			xr.setContentHandler(myContactHandler);
			/* Parse the xml-data from our URL. */
			xr.parse(new InputSource(entity.getContent()));
			/* Parsing has finished. */

		} catch (Exception e) {
			/* Display any Error to the GUI. */
			Log.v("PARSE", "Error: " + e.getMessage());
		}
        if (entity != null) {
            entity.consumeContent();
        } 
        
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			Log.e("ERRORE HTTP", e.getMessage());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			Log.e("ERRORE HTTP", e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("ERRORE HTTP", e.getMessage());
		}
	}
	
	public static void getProfile(String username, String password, String jid) {

		HttpGet get = new HttpGet(
				"https://g5data04.gizmo5.com/xmlfeed/app?class=Subscriber;proc=getProfile;skip_redirect=1;xmlfeed_version=1;partnerId=0;raw=1;username="
						+ username + ";password=" + password + ";jid=" + jid);
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
			// int status = httpclient.executeMethod(get);
			HttpResponse response = httpclient.execute(get);
			HttpEntity entity = response.getEntity();
			Log.v("httpGet", "Profile form get: " + response.getStatusLine());
			int status = response.getStatusLine().getStatusCode();

			if (status != 404) {
				try {

					/* Get a SAXParser from the SAXPArserFactory. */
					SAXParserFactory spf = SAXParserFactory.newInstance();
					SAXParser sp = spf.newSAXParser();
					/* Get the XMLReader of the SAXParser we created. */
					XMLReader xr = sp.getXMLReader();
					/*
					 * Create a new ContentHandler and apply it to the
					 * XML-Reader
					 */
					ProfileHandler myProfileHandler = new ProfileHandler();
					xr.setContentHandler(myProfileHandler);
					/* Parse the xml-data from our URL. */
					xr.parse(new InputSource(entity.getContent()));
					/* Parsing has finished. */
					profile = myProfileHandler.getParsedData();

				} catch (Exception e) {
					/* Display any Error to the GUI. */
					Log.v("PARSEPROFILE", "Error: " + e.getMessage());
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
	}
	
	public static void getProfileMD5(String username, String password, String jid) {

		HttpGet get = new HttpGet(
				"https://g5data04.gizmo5.com/xmlfeed/app?class=Subscriber;proc=getProfile;skip_redirect=1;xmlfeed_version=1;partnerId=0;raw=1;username="
						+ username + ";password=" + password + ";jid=" + jid + ";md5only=1");
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
			// int status = httpclient.executeMethod(get);
			HttpResponse response = httpclient.execute(get);
			HttpEntity entity = response.getEntity();
			Log.v("httpGet", "Profile form get: " + response.getStatusLine());
			int status = response.getStatusLine().getStatusCode();

			if (status != 404) {
				try {

					/* Get a SAXParser from the SAXPArserFactory. */
					SAXParserFactory spf = SAXParserFactory.newInstance();
					SAXParser sp = spf.newSAXParser();
					/* Get the XMLReader of the SAXParser we created. */
					XMLReader xr = sp.getXMLReader();
					/*
					 * Create a new ContentHandler and apply it to the
					 * XML-Reader
					 */
					ProfileHandler myProfileHandler = new ProfileHandler();
					xr.setContentHandler(myProfileHandler);
					/* Parse the xml-data from our URL. */
					xr.parse(new InputSource(entity.getContent()));
					/* Parsing has finished. */
					profile = myProfileHandler.getParsedData();

				} catch (Exception e) {
					/* Display any Error to the GUI. */
					Log.v("PARSEMD5", "Error: " + e.getMessage());
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
	}
	
	public static ParsedProfileDataSet getParsedProfile() {
		return profile;
	}
	
	public static String getParsedContactMD5() {
		return contactMD5;
	}
}
