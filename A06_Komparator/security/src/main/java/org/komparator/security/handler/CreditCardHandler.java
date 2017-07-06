package org.komparator.security.handler;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import org.komparator.security.CryptoUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pt.ulisboa.tecnico.sdis.ws.cli.CAClient;
import pt.ulisboa.tecnico.sdis.ws.cli.CAClientException;

/**
 * This SOAPHandler outputs the contents of inbound and outbound messages.
 */
public class CreditCardHandler implements SOAPHandler<SOAPMessageContext> {

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

	/**
	 * The handleMessage method is invoked for normal processing of inbound and
	 * outbound messages.
	 */
	public boolean crypt(SOAPMessageContext smc) {
		System.out.println("CREDITCARDHANDLER: Handling message.");

		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		CryptoUtil crypto = new CryptoUtil();
		
		try {
			if (outboundElement.booleanValue()) {
				System.out.println("Writing header in outbound SOAP message...");

				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart soapPart = msg.getSOAPPart();
				SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
				SOAPBody soapBody = soapEnvelope.getBody();

				// add header
				SOAPHeader soapHeader = soapEnvelope.getHeader();
				if (soapHeader == null)
					soapHeader = soapEnvelope.addHeader();

				QName opn = (QName) smc.get(MessageContext.WSDL_OPERATION);
				System.out.println("Operation: " + opn.getLocalPart());
		        if (!opn.getLocalPart().equals("buyCart")) {return true; }

				// add body value
				NodeList children = soapBody.getFirstChild().getChildNodes();
				
				for(int i=0; i < children.getLength(); i++){
					Node argument = children.item(i);
					System.out.println("NodeTag: " + argument.getNodeName());
					if(argument.getNodeName().equals("creditCardNr")){
						String secretArgument = argument.getTextContent();
						System.out.println("Data to cipher: " + secretArgument);
						byte[] dataToCipher = parseBase64Binary(secretArgument);
						
						CAClient ca = new CAClient("http://sec.sd.rnl.tecnico.ulisboa.pt:8081/ca");
						String result = ca.getCertificate("A06_Mediator");
						
						Certificate certificate = CryptoUtil.getX509CertificateFromPEMString(result);
						Certificate caCertificate = CryptoUtil.getX509CertificateFromResource("ca.cer");
						PublicKey publicKeyCA = CryptoUtil.getPublicKeyFromCertificate(caCertificate);
						if(!CryptoUtil.verifySignedCertificate(certificate, publicKeyCA)) { 
							throw new RuntimeException("CA Certificate is invalid");
						}
						
						PublicKey publicKey = CryptoUtil.getPublicKeyFromCertificate(certificate);
						byte [] cipheredArgument = crypto.asymCipher(dataToCipher, publicKey);
						
						
						String encodedSecretArgument = printBase64Binary(cipheredArgument);
						argument.setTextContent(encodedSecretArgument);
						msg.saveChanges();
						
						return true;
					}
				}
				return true;


			} else {
				System.out.println("Reading header in inbound SOAP message...");

				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart soapPart = msg.getSOAPPart();
				SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
				SOAPBody soapBody = soapEnvelope.getBody();

				// add header
				SOAPHeader soapHeader = soapEnvelope.getHeader();
				if (soapHeader == null)
					soapHeader = soapEnvelope.addHeader();

				QName opn = (QName) smc.get(MessageContext.WSDL_OPERATION);
		        if (!opn.getLocalPart().equals("buyCart")) {return true; }
				
				// add body value
				Iterator it = soapBody.getChildElements();
				while(it.hasNext()){
					Node node = (Node) it.next();
					NodeList nodes = node.getChildNodes();
					for(int i=0; i < nodes.getLength(); i++){
						Node argument = nodes.item(i);
						if(argument.getNodeName().equals("creditCardNr")){
							
							byte [] cipheredData = parseBase64Binary(argument.getTextContent());
							PrivateKey privateKey = CryptoUtil.getPrivateKeyFromKeyStoreResource("A06_Mediator.jks", "HYGxNCtH".toCharArray(), "a06_mediator", "HYGxNCtH".toCharArray());
							
							byte [] decipheredData = crypto.asymDecipher(cipheredData, privateKey);
							String decodedData = printBase64Binary(decipheredData);
							argument.setTextContent(decodedData);
							System.out.println("Data decifrada: " + decodedData);
							msg.saveChanges();
							
							return true;
						}
					}
				}
			}
		} catch (SOAPException se) {
			throw new RuntimeException("SOAPException caught in CreditCardHandler");
		} catch (CAClientException cace) {
			throw new RuntimeException("CAClientException: error instantiating CAClient");
		}
		catch (CertificateException ce) {
			throw new RuntimeException("CertificateException: failed to get Certificate");
		}
		catch (IOException ioe) {
			throw new RuntimeException("IOException: failed to access Certificate file");
		}
		catch (UnrecoverableKeyException uke) {
			throw new RuntimeException("UnrecoverableKeyException: unable get private key from KeyStoreResource");
		}
		catch (KeyStoreException kse) {
			throw new RuntimeException("KeyStoreException: failed to get private key from KeyStoreResource");
		}
		return true;
	}
	
	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		return crypt(smc);
	}

	/** The handleFault method is invoked for fault message processing. */
	@Override
	public boolean handleFault(SOAPMessageContext smc) {
		return crypt(smc);
	}

	/**
	 * Called at the conclusion of a message exchange pattern just prior to the
	 * JAX-WS runtime dispatching a message, fault or exception.
	 */
	@Override
	public void close(MessageContext messageContext) {
		// nothing to clean up
	}


	/**
	 * Check the MESSAGE_OUTBOUND_PROPERTY in the context to see if this is an
	 * outgoing or incoming message. Write a brief message to the print stream
	 * and output the message. The writeTo() method can throw SOAPException or
	 * IOException
	 */
}
