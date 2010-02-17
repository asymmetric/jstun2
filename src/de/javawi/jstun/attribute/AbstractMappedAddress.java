package de.javawi.jstun.attribute;

import de.javawi.jstun.attribute.exception.MessageAttributeException;
import de.javawi.jstun.attribute.exception.MessageAttributeParsingException;
import de.javawi.jstun.util.Address;
import de.javawi.jstun.util.Utility;
import de.javawi.jstun.util.UtilityException;

public abstract class AbstractMappedAddress extends AbstractMessageAttribute {

	int port;
	Address address;
	Address.Family family;

	final static int HEADER_LENGTH = 4;
	final static int IPv4_LENGTH = 4;

	public AbstractMappedAddress(MessageAttributeType type) {
		super(type);
	}

	public AbstractMappedAddress(MessageAttributeType type, byte[] data,
			Address address, int port) throws MessageAttributeParsingException {
		super(type);
		this.address = address;
		this.port = port;

		parseData(data);
	}
	
	abstract protected void parseData(byte[] data) throws MessageAttributeParsingException;

	public int getPort() {
		return port;
	}

	public Address getAddress() {
		return address;
	}

	public void setPort(int port) throws MessageAttributeException {
		if ((port > 65536) || (port < 0)) {
			throw new MessageAttributeException("Port value " + port + " out of range.");
		}
		this.port = port;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	@Override
	public byte[] getBytes() throws UtilityException {
		// 4 common bytes header + 4B own header + 4B address
		byte[] result = new byte[COMMONHEADERSIZE + HEADER_LENGTH + IPv4_LENGTH]; // TODO this should be variable
		// message attribute header
		// type
		System.arraycopy(Utility.integerToTwoBytes(typeToInteger(type)), 0, result, 0, 2);
		// length
		// TODO should be variable
		System.arraycopy(Utility.integerToTwoBytes(HEADER_LENGTH + IPv4_LENGTH), 0, result, 2, 2);

		// mappedaddress header
		// padding
		result[4] = 0x0;
		// family
		result[5] = Utility.integerToOneByte(family.getEncoding());

		// port
		System.arraycopy(Utility.integerToTwoBytes(port), 0, result, 6, 2);
		// address
		System.arraycopy(address.getBytes(), 0, result, 8, 4);

		return result;
	}

	public String toString() {
		return "Address " + address.toString() + ", Port " + port;
	}

}
