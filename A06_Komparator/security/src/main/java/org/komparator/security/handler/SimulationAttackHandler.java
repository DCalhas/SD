package org.komparator.security.handler;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;


import javax.xml.soap.SOAPException;

import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;


import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class SimulationAttackHandler implements SOAPHandler<SOAPMessageContext> {

	@Override
	public void close(MessageContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		return attack(context);
	}
	
	public boolean attack(SOAPMessageContext smc) {

		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		System.out.println("SIMULATIONATTACKHANDLER: Handling message.");
		
		
		if(outboundElement.booleanValue()){

			QName opn = (QName) smc.get(MessageContext.WSDL_OPERATION);
			System.out.println("Operation: " + opn.getLocalPart());
			if(opn.getLocalPart().equals("getProduct")) {
				System.out.println("Analising SOAP Body");
				try {
					Iterator it = smc.getMessage().getSOAPBody().getChildElements();
					while(it.hasNext()) {
						Node parent = (Node) it.next();
						System.out.println(parent.getLocalName());
						NodeList listResponse = parent.getChildNodes();
						if(listResponse.getLength() > 0) {
							NodeList attributes = listResponse.item(0).getChildNodes();
							if(attributes.item(0).getTextContent().equals("ATTACK")) {
								//changing price
								attributes.item(3).setTextContent("0");
							}	
						}
					}
				} catch (SOAPException e) {
					throw new RuntimeException("SOAPException caught in CreditCardHandler");
				}
			}
			return true;
		}
		
		else{ 
			return true;
		}
	}

	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		return attack(smc);
	}


	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
