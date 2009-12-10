package de.javawi.jstun.attribute;

import java.util.logging.Logger;

import de.javawi.jstun.attribute.exception.MessageAttributeParsingException;

public class XORMappedAddress extends MappedXORMapped {

	private static Logger logger = Logger.getLogger("de.javawi.jstun.attribute.XORMappedAddress");

	public XORMappedAddress(int family) {
		super(MessageAttribute.MessageAttributeType.MappedAddress, family);
	}

	public static MessageAttribute parse(byte[] data, int family) throws MessageAttributeParsingException {
		MappedAddress ma = new MappedAddress(family);
		MappedXORMapped.parse(ma, data);
		logger.finer("Message Attribute: Mapped Address parsed: " + ma.toString() + ".");
		return ma;
	}
}
