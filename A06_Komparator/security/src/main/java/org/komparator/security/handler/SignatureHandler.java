package org.komparator.security.handler;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import java.io.FileNotFoundException;
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
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.komparator.security.CryptoUtil;
import org.komparator.security.Manager;
import org.w3c.dom.NodeList;

import pt.ulisboa.tecnico.sdis.ws.cli.CAClient;
import pt.ulisboa.tecnico.sdis.ws.cli.CAClientException;

/**
 * This SOAPHandler outputs the contents of inbound and outbound messages.
 */

public class SignatureHandler implements SOAPHandler<SOAPMessageContext> {
	
	final static String algorithm = "SHA256withRSA";

	@Override
	public void close(MessageContext arg0) {	
	}

	@Override
	public boolean handleFault(SOAPMessageContext arg0) {
		return sign(arg0);
	}
	
	private boolean sign(SOAPMessageContext smc) {
		System.out.println("SIGNATUREHANDLER: Handling message.");

		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		try{
			if(outboundElement.booleanValue()){
				
				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart soapPart = msg.getSOAPPart();
				SOAPEnvelope soapEnvelope = soapPart.getEnvelope();

				// add header
				SOAPHeader soapHeader = soapEnvelope.getHeader();
				if (soapHeader == null)
					soapHeader = soapEnvelope.addHeader();

				// add header element (name, namespace prefix, namespace)
				Name name = soapEnvelope.createName("signature", "w", "http://ws.supplier.komparator.org/");
				SOAPHeaderElement element = soapHeader.addHeaderElement(name);

				// add header element value
				
				String body = "";
				
				
				Iterator it = smc.getMessage().getSOAPBody().getChildElements();
				while(it.hasNext()){
					Node node = (Node) it.next();
					NodeList nodes = node.getChildNodes();
					for(int i = 0; i < nodes.getLength(); i++) {
						body += nodes.item(i).getTextContent();
					}
				}
				
				Iterator children = smc.getMessage().getSOAPHeader().getChildElements();
				
				while(children.hasNext()) {
					Node node = (Node) children.next();
					if(node.getLocalName().equals("timestamp")){
						body += node.getValue();
					}
				}
				
				System.out.println("WEB SERVICE NAME: " + Manager.wsName);
				
								
				String sender = Manager.wsName;

				if(!Manager.wsName.equals("")) {
					PrivateKey privateKey = null;
					try {
						System.out.println("Getting private key from " + sender + ".jks");
						privateKey = CryptoUtil.getPrivateKeyFromKeyStoreResource(sender + ".jks", "HYGxNCtH".toCharArray(), Manager.wsName.toLowerCase(), "HYGxNCtH".toCharArray());
					} catch (UnrecoverableKeyException | FileNotFoundException | KeyStoreException e) {
						System.out.println("File not found");
						throw new RuntimeException();
					}
					
					System.out.println("Making digital signature");
					
					byte[] digitalSignature = CryptoUtil.makeDigitalSignature(algorithm, privateKey, parseBase64Binary(body));
					
					if(digitalSignature != null) System.out.println("Made digital signature with success");
					
					
					String encodedSignature = printBase64Binary(digitalSignature);
					element.addTextNode(encodedSignature);
				}
				return true;
			}
			
			else{ 
				
				System.out.println("INBOUND INICIO:");
				
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
				Name nameSignature = soapEnvelope.createName("signature", "w", "http://ws.supplier.komparator.org/");
				Name wsName = soapEnvelope.createName("wsSenderName", "w", "http://ws.supplier.komparator.org/");
				
				Iterator it = soapHeader.getChildElements();
				// check header element
				
				String signature = "";
				String name = "";
				
				while(it.hasNext()) {
					Node node = (Node) it.next();
					if(node.getLocalName().equals(wsName.getLocalName()))
						name = node.getTextContent();
					if(node.getLocalName().equals(nameSignature.getLocalName()))
						signature = node.getTextContent();
						
				}
				
				byte [] digitalSignature = parseBase64Binary(signature);
				
				System.out.println("BODY");
				System.out.println("Signature: " + signature);
				
				
				String encodedBody = "";
				
				Iterator itbody = smc.getMessage().getSOAPBody().getChildElements();
				while(itbody.hasNext()){
					Node node = (Node) itbody.next();
					NodeList nodes = node.getChildNodes();
					for(int i = 0; i < nodes.getLength(); i++) {
						encodedBody += nodes.item(i).getTextContent();
					}
				}
				
				Iterator children = smc.getMessage().getSOAPHeader().getChildElements();
				
				while(children.hasNext()) {
					Node node = (Node) children.next();
					if(node.getLocalName().equals("timestamp")){
						encodedBody += node.getValue();
					}
				}
				
				System.out.println("BODY ENCODED:" + encodedBody);
							
				
				CAClient ca = new CAClient("http://sec.sd.rnl.tecnico.ulisboa.pt:8081/ca");
				String resultCertificate = ca.getCertificate(name);
				
				System.out.println("Certificate file: " + resultCertificate);
				PublicKey publicKey = null;
				try {
					Certificate certificate = CryptoUtil.getX509CertificateFromPEMString(resultCertificate);
					Certificate caCertificate = CryptoUtil.getX509CertificateFromResource("ca.cer");
					
					PublicKey publicKeyCA = CryptoUtil.getPublicKeyFromCertificate(caCertificate);
					System.out.println("Verifying signature from CA Web Service");
					if(!CryptoUtil.verifySignedCertificate(certificate, publicKeyCA)) {
						throw new RuntimeException("CA Certificate is invalid");
					} 
					System.out.println("Signature from CA Web Service is valid");
					publicKey = CryptoUtil.getPublicKeyFromCertificate(certificate);
				} catch (CertificateException e) {
					throw new RuntimeException("CertificateException: failed to get Certificate");
				} catch (IOException e) {
					throw new RuntimeException("IOException: failed to access Certificate file");
				}				
				
				System.out.println("Verifying signature from " + name);
				if(CryptoUtil.verifyDigitalSignature(algorithm, publicKey, parseBase64Binary(encodedBody), digitalSignature))
					return true;
				
				throw new RuntimeException("Failed to verify signature from " + name);
			}
		} catch (SOAPException se) {
			throw new RuntimeException("SOAPException caught in SignatureHandler");
		} catch (CAClientException e) {
			throw new RuntimeException("CAClientException: error instantiating CAClient");
		}
	}
	
	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		return sign(smc);
	}
	

	@Override
	public Set<QName> getHeaders() {
		return null;
	}

}
