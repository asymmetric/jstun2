package it.unibo.cs.voipdroid.tools;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class ProfileHandler extends DefaultHandler {
	// ===========================================================
	// Fields
	// ===========================================================

	private boolean in_data = false;
	private boolean in_subscriber_profile = false;
	private boolean in_profile = false;
	private boolean in_full_name = false;
	private boolean in_gizmo_name = false;
	private boolean in_state = false;
	private boolean in_city = false;
	private boolean in_country = false;
	private boolean in_homepage_url = false;
	private boolean in_sip_uri = false;
	private boolean in_language = false;
	private boolean in_sex = false;
	private boolean in_birth = false;
	private boolean in_description = false;
	private boolean in_md5 = false;
	private boolean in_err = false;
	private boolean in_msg = false;

	private ParsedProfileDataSet myParsedProfileDataSet;
	StringBuffer string = new StringBuffer();

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public ParsedProfileDataSet getParsedData() {
         return this.myParsedProfileDataSet;
    } 
	// ===========================================================
	// Methods
	// ===========================================================
	@Override
	public void startDocument() throws SAXException {
		this.myParsedProfileDataSet = new ParsedProfileDataSet();
	}

	@Override
	public void endDocument() throws SAXException {
		// Nothing to do
	}

	/**
	 * Gets be called on opening tags like: <tag> Can provide attribute(s), when
	 * xml was like: <tag attribute="attributeValue">
	 */
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		if (localName.equals("data")) {
			this.in_data = true;
		} else if (localName.equals("subscriber_profile")) {
			this.in_subscriber_profile = true;
		} else if (localName.equals("profile")) {
			this.in_profile = true;
		} else if (localName.equals("gizmo_name")) {
			this.in_gizmo_name = true;
		} else if (localName.equals("full_name")) {
			this.in_full_name = true;
		} else if (localName.equals("state")) {
			this.in_state = true;
		} else if (localName.equals("city")) {
			this.in_city = true;
		} else if (localName.equals("country")) {
			this.in_country = true;
		} else if (localName.equals("homepage_url")) {
			this.in_homepage_url = true;
		} else if (localName.equals("sip_uri")) {
			this.in_sip_uri = true;
		} else if (localName.equals("language")) {
			this.in_language = true;
		} else if (localName.equals("sex")) {
			this.in_sex = true;
		} else if (localName.equals("birthdatetime")) {
			this.in_birth = true;
		} else if (localName.equals("description")) {
			this.in_description = true;
		} else if (localName.equals("md5checksum")) {
			this.in_md5 = true;
		} else if (localName.equals("msg")) {
			this.in_msg = true;
		}else if (localName.equals("err")) {
			this.in_err = true;
		}
	}

	/**
	 * Gets be called on closing tags like: </tag>
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (localName.equals("data")) {
			this.in_data = false;
		} else if (localName.equals("subscriber_profile")) {
			this.in_subscriber_profile = false;
		} else if (localName.equals("profile")) {
			this.in_profile = false;
		} else if (localName.equals("gizmo_name")) {
			this.in_gizmo_name = false;
		} else if (localName.equals("full_name")) {
			this.in_full_name = false;
		} else if (localName.equals("state")) {
			this.in_state = false;
		} else if (localName.equals("city")) {
			this.in_city = false;
		} else if (localName.equals("country")) {
			this.in_country = false;
		} else if (localName.equals("homepage_url")) {
			this.in_homepage_url = false;
		} else if (localName.equals("sip_uri")) {
			this.in_sip_uri = false;
		} else if (localName.equals("language")) {
			this.in_language = false;
		} else if (localName.equals("sex")) {
			this.in_sex = false;
		} else if (localName.equals("birthdatetime")) {
			this.in_birth = false;
		} else if (localName.equals("description")) {
			this.in_description = false;
		} else if (localName.equals("md5checksum")) {
			this.in_md5 = false;
		} else if (localName.equals("msg")) {
			this.in_msg = false;
		}else if (localName.equals("err")) {
			this.in_err = false;
		}
	}

	/**
	 * Gets be called on the following structure: <tag>characters</tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		
		if(this.in_full_name) {
			myParsedProfileDataSet.setFullName(new String(ch,start,length));
		}
		if (this.in_gizmo_name) {
			myParsedProfileDataSet.setGizmoName(new String(ch,start,length));
		}
		if(this.in_city) {
			myParsedProfileDataSet.setCity(new String(ch,start,length));
		}
		if (this.in_state) {
			myParsedProfileDataSet.setState(new String(ch,start,length));
		}
		if(this.in_country) {
			myParsedProfileDataSet.setCountry(new String(ch,start,length));
		}
		if (this.in_homepage_url) {
			myParsedProfileDataSet.setHomepage_url(new String(ch,start,length));
		}
		if(this.in_sip_uri) {
			myParsedProfileDataSet.setSip_uri(new String(ch,start,length));
		}
		if (this.in_language) {
			myParsedProfileDataSet.setLanguage(new String(ch,start,length));
		}
		if(this.in_sex) {
			myParsedProfileDataSet.setSex(new String(ch,start,length));
		}
		if (this.in_birth) {
			myParsedProfileDataSet.setBirth(new String(ch,start,length));
		}
		if (this.in_description) {
			string.append(new String(ch,start,length));
			myParsedProfileDataSet.setDescription(string.toString());
		}
		if (this.in_md5) {
			myParsedProfileDataSet.setMd5(new String(ch,start,length));
		}
		if (this.in_err) {
			int i = Integer.parseInt(new String(ch,start,length)); 
			myParsedProfileDataSet.setErr(i);
		}
		if (this.in_msg) {
			myParsedProfileDataSet.setMsg(new String(ch, start, length));
		}
	}
}
