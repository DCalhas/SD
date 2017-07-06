package org.komparator.security.handler;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.komparator.security.Manager;

/**
 * This SOAPHandler outputs the contents of inbound and outbound messages.
 */
public class MediatorOperationIdHandler implements SOAPHandler<SOAPMessageContext> {

	//
	// Handler interface implementation
	//
	public static final String REQUEST_PROPERTY = "my.request.property";

	/**
	 * Gets the header blocks that can be processed by this Handler instance. If
	 * null, processes all.
	 */
	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	public boolean operationId(SOAPMessageContext smc) {
		System.out.println("MediatorOperationIdHandler: Handling message.");

		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		try {
			if (outboundElement.booleanValue()) {
				return true;
			} else {
				System.out.println("Reading Body SOAP message...");

				// get SOAP envelope header
				SOAPMessage msg = smc.getMessage();
				SOAPPart soapPart = msg.getSOAPPart();
				SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
				SOAPHeader soapHeader = soapEnvelope.getHeader();

				// check header
				if (soapHeader == null) {
					System.out.println("Header not found.");
					return true;
				}

				// get first header element
				Name name = soapEnvelope.createName("operationId", "w", "http://ws.security.komparator.org/");
				Iterator it = soapHeader.getChildElements(name);
				
				// check header element
				if (!it.hasNext()) {
					System.out.println("Header element not found.");
					return true;
				}
				SOAPElement element = (SOAPElement) it.next();

				// get header element value
				String headerValue = element.getValue();

				smc.put(REQUEST_PROPERTY, headerValue);
				smc.setScope(REQUEST_PROPERTY, Scope.APPLICATION);
				
				System.out.println("OperationId is: " + headerValue);
				
				return true;
			}
		} catch (SOAPException e) {
			throw new RuntimeException("SOAPException caught in TimeLimitHandler");
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