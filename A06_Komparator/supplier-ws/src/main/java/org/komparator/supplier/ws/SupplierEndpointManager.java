package org.komparator.supplier.ws;

import java.io.IOException;

import javax.xml.ws.Endpoint;

import org.komparator.security.Manager;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;


/** End point manager */
public class SupplierEndpointManager {

	/** Web Service location to publish */
	private String wsURL = null;
	private String uddiURL = null;
	private String wsName = null;
	private UDDINaming uddiNaming = null;

	/** Port implementation */
	private SupplierPortImpl portImpl = new SupplierPortImpl(this);

// TODO
//	/** Obtain Port implementation */
//	public SupplierPortType getPort() {
//		return portImpl;
//	}

	/** Web Service end point */
	private Endpoint endpoint = null;

	/** output option **/
	private boolean verbose = true;

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/** constructor with provided web service URL */
	public SupplierEndpointManager(String wsURL) {
		if (wsURL == null)
			throw new NullPointerException("Web Service URL cannot be null!");
		this.wsURL = wsURL;
	}
	
	/** constructor with UDDI address 
	 * @throws UDDINamingException */
	public SupplierEndpointManager(String wsURL, String uddiURL, String wsName) throws UDDINamingException {
		if (wsURL == null || uddiURL == null || wsName == null)
			throw new NullPointerException("Web Service URL, UDDI URL or Service name cannot be null!");
		this.wsURL = wsURL;
		this.uddiURL = uddiURL;
		this.wsName = wsName;
		uddiNaming = new UDDINaming(this.uddiURL);
		System.out.println("Hello, I'm " + this.wsName);
		uddiNaming.rebind(this.wsName, this.wsURL);
		Manager.wsName = wsName;
	}

	/* end point management */

	public void start() throws Exception {
		try {
			// publish end point
			endpoint = Endpoint.create(this.portImpl);
			if (verbose) {
				System.out.printf("Starting %s%n", wsURL);
			}
			endpoint.publish(wsURL);
		} catch (Exception e) {
			endpoint = null;
			if (verbose) {
				System.out.printf("Caught exception when starting: %s%n", e);
				e.printStackTrace();
			}
			throw e;
		}
	}

	public void awaitConnections() {
		if (verbose) {
			System.out.println("Awaiting connections");
			System.out.println("Press enter to shutdown");
		}
		try {
			System.in.read();
		} catch (IOException e) {
			if (verbose) {
				System.out.printf("Caught i/o exception when awaiting requests: %s%n", e);
			}
		}
	}

	public void stop() throws Exception {
		try {
			if (endpoint != null) {
				// stop end point
				endpoint.stop();
				if (verbose) {
					System.out.printf("Stopped %s%n", wsURL);
				}
			}
		} catch (Exception e) {
			if (verbose) {
				System.out.printf("Caught exception when stopping: %s%n", e);
			}
		}
		this.uddiNaming.unbind(this.wsName);
		this.portImpl = null;
	}

}
