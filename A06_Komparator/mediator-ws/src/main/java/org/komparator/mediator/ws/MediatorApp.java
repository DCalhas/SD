package org.komparator.mediator.ws;

import java.io.File;
import java.util.Date;
import java.util.Timer;

import org.komparator.security.Manager;

public class MediatorApp {

	public static void main(String[] args) throws Exception {
		// Check arguments
		if (args.length == 0 || args.length == 2) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + MediatorApp.class.getName() + " wsURL OR uddiURL wsName wsURL");
			return;
		}
		String uddiURL = null;
		String wsName = null;
		String wsURL = null;

   		// Create server implementation object, according to options
		MediatorEndpointManager endpoint = null;
		if (args.length == 1) {
			wsURL = args[0];
			endpoint = new MediatorEndpointManager(wsURL);
		} else if (args.length >= 3) {
			uddiURL = args[0];
			wsName = args[1];
			wsURL = args[2];
			
			Manager.wsName = wsName;
			Manager.wsURL = wsURL;
			
			System.out.println("MANAGER WEB SERVICE NAME: " + Manager.wsName);
			endpoint = new MediatorEndpointManager(uddiURL, wsName, wsURL);
			endpoint.setVerbose(true);
		}
		
		LifeProof life = null;
		Timer timer = null;
		try {
			endpoint.start();
			// creating timer task, timer
			timer = new Timer();
	   
	   		life = new LifeProof(endpoint);
	      
	   		// scheduling the task at fixed rate
	   
	   		timer.scheduleAtFixedRate(life, new Date(), 5000);

			endpoint.awaitConnections();
		} finally {
			timer.cancel();
			life.cancel();
			endpoint.stop();
		}

	}

}
