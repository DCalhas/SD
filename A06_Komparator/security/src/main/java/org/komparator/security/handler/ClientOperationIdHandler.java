package org.komparator.security.handler;

import java.text.SimpleDateFormat;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.komparator.security.Manager;


/**
 * This SOAPHandler outputs the contents of inbound and outbound messages.
 */
public class ClientOperationIdHandler implements SOAPHandler<SOAPMessageContext> {

	//
	// Handler interface implementation
	//
	
	/**
	 * Gets the header blocks that can be processed by this Handler instance. If
	 * null, processes all.
	 */
	@Override
	public Set<QName> getHeaders() {
		return null;
	}
	
	public boolean operationId(SOAPMessageContext smc) {
		System.out.println("CLIENTOPERATIONIDHANDLER: Handling message.");

		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		try {
			if (outboundElement.booleanValue()) {				
				System.out.println("MANAGER WSNAME ----- " + Manager.wsName);
				QName opn = (QName) smc.get(MessageContext.WSDL_OPERATION);
				System.out.println("Operation: " + opn.getLocalPart());
		        if (!opn.getLocalPart().equals("buyCart") && !opn.getLocalPart().equals("addToCart")) {return true; }
				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart soapPart = msg.getSOAPPart();
				SOAPEnvelope soapEnvelope = soapPart.getEnvelope();

				// add header
				SOAPHeader soapHeader = soapEnvelope.getHeader();
				if (soapHeader == null)
					soapHeader = soapEnvelope.addHeader();

				// add header element (name, namespace prefix, namespace)
				Name name = soapEnvelope.createName("operationId", "w", "http://ws.security.komparator.org/");
				SOAPHeaderElement element = soapHeader.addHeaderElement(name);
				System.out.println("OperationId is: " + String.valueOf(Manager.operationId));
				element.addTextNode(String.valueOf(Manager.operationId));
				return true;
					
			} else {
				System.out.println("IDENTIFICATION HANDLER INBOUND");
				return true;
			}
		} catch(SOAPException e) {
			throw new RuntimeException("SOAPException caugh in IdentificationHandlert");
		}
	}
	
	/**
	 * The handleMessage method is invoked for normal processing of inbound and
	 * outbound messages.
	 */
	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		return operationId(smc);
	}

	/** The handleFault method is invoked for fault message processing. */
	@Override
	public boolean handleFault(SOAPMessageContext smc) {
		return operationId(smc);
	}

	/**
	 * Called at the conclusion of a message exchange pattern just prior to the
	 * JAX-WS runtime dispatching a message, fault or exception.
	 */
	@Override
	public void close(MessageContext messageContext) {
		// nothing to clean up
	}

	/** Date formatter used for outputting timestamps in ISO 8601 format */
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	/**
	 * Check the MESSAGE_OUTBOUND_PROPERTY in the context to see if this is an
	 * outgoing or incoming message. Write a brief message to the print stream
	 * and output the message. The writeTo() method can throw SOAPException or
	 * IOException
	 */

}