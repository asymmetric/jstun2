package it.unibo.cs.voipdroid.authentication;


import org.zoolu.sip.address.NameAddress;


/** Listener of RegisterAgent */
public interface RegisterAgentListener {
	
   /** When a UA has been successfully (un)registered. */
   public void onUaRegistrationSuccess(RegisterAgent ra, NameAddress target, NameAddress contact, String result);

   /** When a UA failed on (un)registering. */
   public void onUaRegistrationFailure(RegisterAgent ra, NameAddress target, NameAddress contact, String result);

}
