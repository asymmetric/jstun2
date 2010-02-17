package de.javawi.jstun.attribute;

import de.javawi.jstun.attribute.exception.MessageAttributeException;
import de.javawi.jstun.attribute.exception.MessageAttributeParsingException;
import de.javawi.jstun.util.Address;
import de.javawi.jstun.util.IPv4Address;
import de.javawi.jstun.util.Utility;
import de.javawi.jstun.util.UtilityException;
import de.javawi.jstun.util.Address.Family;

public class MappedAddress extends AbstractMappedAddress {

	public MappedAddress() {
		super(MessageAttributeType.MappedAddress);
	}

	public MappedAddress(byte[] data) throws MessageAttributeParsingException {
		this();
		parseData(data);
	}

	@Override
	protected void parseData(byte[] data) throws MessageAttributeParsingException {
		try {
			if (data.length < 8) { // TODO why 8?
				throw new MessageAttributeParsingException("Data array too short");
			}

			// Get the IP family directly from the packet
			int familyInt = Utility.oneByteToInteger(data[1]);

			if (familyInt == Address.IPv4) {

				byte[] addressArray = new byte[4];
				System.arraycopy(data, 4, addressArray, 0, 4);

				// store it in the local vars
				this.address = new IPv4Address(addressArray);
				this.family = Family.IPv4;

			} else if (familyInt == Address.IPv6) {
				; // TODO implement
			} else
				throw new MessageAttributeParsingException("Family " + familyInt
						+ " is not supported");

			// Geth the port from the packet
			byte[] portArray = new byte[2];
			System.arraycopy(data, 2, portArray, 0, 2);

			// store it
			this.port = Utility.twoBytesToInteger(portArray);

		} catch (UtilityException ue) {
			throw new MessageAttributeParsingException("Parsing error");
		} catch (MessageAttributeException mae) {
			throw new MessageAttributeParsingException("Port parsing error");
		}
	}
}
