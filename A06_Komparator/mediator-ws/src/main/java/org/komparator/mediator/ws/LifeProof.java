package org.komparator.mediator.ws;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.mediator.ws.cli.MediatorClientException;
import org.komparator.security.Manager;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;

public class LifeProof extends TimerTask {
	
	public static MediatorEndpointManager port;
   
   public static Date stampAlive = null;
   
   public boolean secondRunning = false;
   
   private MediatorClient client = null;
  
   public LifeProof(MediatorEndpointManager port) {
	   this.port = port;
	   if(isPrimary())
		   try {
			   client = new MediatorClient("http://localhost:8072/mediator-ws/endpoint");
		   } catch (MediatorClientException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		   }
		   
   }
   
   public void run() {
	   if(isPrimary()) {		   
		   client.imAlive();
	   } else if(stampAlive != null) {
		   UDDINaming uddiNaming = null;
		   long difference = (new Date()).getTime() - stampAlive.getTime();
		   if(difference > 8000 && !secondRunning) {
			   try {
				   port.setUDDIurl(port.getUDDIBackup());
				   port.publishToUDDI();
				   secondRunning = true;
			   } catch (Exception e) {
				   // TODO Auto-generated catch block
				   e.printStackTrace();
			   }
		   }
	   }   
   }
   
   public boolean isPrimary() { return port.getWsURL().contains("8071"); }
}