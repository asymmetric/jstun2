package it.unibo.cs.voipdroid.authentication;

import org.zoolu.tools.MD5;

public class CalculateResponse {
	
	static String username = null;
	static String passwd = null;
	static String realm = null;
	static String algorithm = "md5";
	static String nonce = null;
	static String cnonce = null;
	static String qop = "auth";
	static String nc = "00000001";
	static String method = "REGISTER";
	static String uri = null;
	static String body = null;
	
	public static String calculate(String uname, String pwd, String Nonce, 
			String cNonce, String URI) {
		username = uname;
		passwd = pwd;
		nonce = Nonce;
		cnonce = cNonce;
		uri = URI;

		// Prova
		String a1 = username + ":" + realm + ":" + passwd;
		String a2 = method + ":" + uri;
		String first = HEX(MD5(a1));
		String second = HEX(MD5(a2));
		String finale = first + ":" + nonce + ":" + second;
		return HEX(MD5(finale));
		
//		return getResponse();
	}
	
	public static String getResponse() {
		
		
		
		String secret = HEX(MD5(A1()));
		StringBuffer sb = new StringBuffer();
		if (nonce != null)
			sb.append(nonce);
		sb.append(":");
		if (qop != null) {
			if (nc != null)
				sb.append(nc);
			sb.append(":");
			if (cnonce != null)
				sb.append(cnonce);
			sb.append(":");
			sb.append(qop);
			sb.append(":");
		}
		sb.append(HEX(MD5(A2())));
		String data = sb.toString();
		return HEX(KD(secret, data));
	}

	private static byte[] A1() {
		StringBuffer sb = new StringBuffer();
		if (username != null)
			sb.append(username);
		sb.append(":");
		if (realm != null)
			sb.append(realm);
		sb.append(":");
		if (passwd != null)
			sb.append(passwd);

		if (algorithm == null || !algorithm.equalsIgnoreCase("MD5-sess")) {
			return sb.toString().getBytes();
		} else {
			StringBuffer sb2 = new StringBuffer();
			sb2.append(":");
			if (nonce != null)
				sb2.append(nonce);
			sb2.append(":");
			if (cnonce != null)
				sb2.append(cnonce);
			return cat(MD5(sb.toString()), sb2.toString().getBytes());
		}
	}
	
	private static byte[] MD5(byte[] bb) {
		return MD5.digest(bb);
	}
	
	private static byte[] MD5(String bb) {
		return MD5.digest(bb);
	}
	
	private static String A2() {
		StringBuffer sb = new StringBuffer();
		sb.append(method);
		sb.append(":");
		if (uri != null)
			sb.append(uri);

		if (qop != null && qop.equalsIgnoreCase("auth-int")) {
			sb.append(":");
			if (body == null)
				sb.append(HEX(MD5("")));
			else
				sb.append(HEX(MD5(body)));
		}
		return sb.toString();
	}
	
	private static byte[] cat(byte[] a, byte[] b) {
		int len = a.length + b.length;
		byte[] c = new byte[len];
		for (int i = 0; i < a.length; i++)
			c[i] = a[i];
		for (int i = 0; i < b.length; i++)
			c[i + a.length] = b[i];
		return c;
	}
	
	private static String HEX(byte[] bb) {
		return MD5.asHex(bb);
	}
	
	private static byte[] KD(String secret, String data) {
		StringBuffer sb = new StringBuffer();
		sb.append(secret).append(":").append(data);
		return MD5(sb.toString());
	}

}
