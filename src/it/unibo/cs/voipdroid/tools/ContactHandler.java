package it.unibo.cs.voipdroid.tools;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class ContactHandler extends DefaultHandler {
	// ===========================================================
	// Fields
	// ===========================================================

	private boolean in_data = false;
	private boolean in_get_contacts = false;
	private boolean in_add_contact = false;
	private boolean in_contacts = false;
	private boolean in_contact = false;
	private boolean in_nick = false;
	private boolean in_jid = false;
	private boolean in_md5 = false;
	private boolean in_err = false;
	private boolean in_msg = false;
	StringBuffer nick;

	private ParsedContactDataSet myParsedContactDataSet;
	
    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public String getParsedMd5() {
         return this.myParsedContactDataSet.getMd5();
    } 

	// ===========================================================
	// Methods
	// ===========================================================
	@Override
	public void startDocument() throws SAXException {
		this.myParsedContactDataSet = new ParsedContactDataSet();
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
		} else if (localName.equals("get_contacts")) {
			this.in_get_contacts = true;
		} else if (localName.equals("contacts")) {
			this.in_contacts = true;
		} else if (localName.equals("contact")) {
			this.in_contact = true;
		} else if (localName.equals("nick")) {
			this.in_nick = true;
			nick = new StringBuffer();
		} else if (localName.equals("jid")) {
			this.in_jid = true;
		} else if (localName.equals("add_contact")) {
			this.in_add_contact = true;
		} else if (localName.equals("msg")) {
			this.in_msg = true;
		}else if (localName.equals("err")) {
			this.in_err = true;
		}else if (localName.equals("md5checkSum")) {
			this.in_md5 = true;
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
		} else if (localName.equals("get_contacts")) {
			this.in_get_contacts = false;
		} else if (localName.equals("contacts")) {
			this.in_contacts = false;
		} else if (localName.equals("contact")) {
			this.in_contact = false;
		} else if (localName.equals("nick")) {
			this.in_nick = false;
		} else if (localName.equals("jid")) {
			this.in_jid = false;
		} else if (localName.equals("add_contact")) {
			this.in_add_contact = false;
		} else if (localName.equals("msg")) {
			this.in_msg = false;
		} else if (localName.equals("err")) {
			this.in_err = false;
		} else if (localName.equals("md5checkSum")) {
			this.in_md5 = false;
		}
	}

	/**
	 * Gets be called on the following structure: <tag>characters</tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		if (this.in_nick) {
			nick.append(new String(ch, start, length));
			myParsedContactDataSet.setNick(nick.toString());
			Log.v("CONTACTS", new String(ch, start, length));
		}
		if (this.in_jid) {
			myParsedContactDataSet.setJid(new String(ch, start, length));
		}
		if (this.in_err) {
			int i = Integer.parseInt(new String(ch,start,length)); 
			myParsedContactDataSet.setErr(i);
		}
		if (this.in_msg) {
			myParsedContactDataSet.setMsg(new String(ch, start, length));
		}
		
		if (this.in_md5) {
			myParsedContactDataSet.setMd5(new String(ch, start, length));
		}
	}
}
