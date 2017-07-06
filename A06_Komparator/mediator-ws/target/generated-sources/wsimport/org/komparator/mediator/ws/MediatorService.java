
package org.komparator.mediator.ws;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.10
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "MediatorService", targetNamespace = "http://ws.mediator.komparator.org/", wsdlLocation = "file:/home/david/A06-Komparator/mediator-ws/src/main/resources/mediator.1_0.wsdl")
public class MediatorService
    extends Service
{

    private final static URL MEDIATORSERVICE_WSDL_LOCATION;
    private final static WebServiceException MEDIATORSERVICE_EXCEPTION;
    private final static QName MEDIATORSERVICE_QNAME = new QName("http://ws.mediator.komparator.org/", "MediatorService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("file:/home/david/A06-Komparator/mediator-ws/src/main/resources/mediator.1_0.wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        MEDIATORSERVICE_WSDL_LOCATION = url;
        MEDIATORSERVICE_EXCEPTION = e;
    }

    public MediatorService() {
        super(__getWsdlLocation(), MEDIATORSERVICE_QNAME);
    }

    public MediatorService(WebServiceFeature... features) {
        super(__getWsdlLocation(), MEDIATORSERVICE_QNAME, features);
    }

    public MediatorService(URL wsdlLocation) {
        super(wsdlLocation, MEDIATORSERVICE_QNAME);
    }

    public MediatorService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, MEDIATORSERVICE_QNAME, features);
    }

    public MediatorService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public MediatorService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns MediatorPortType
     */
    @WebEndpoint(name = "MediatorPort")
    public MediatorPortType getMediatorPort() {
        return super.getPort(new QName("http://ws.mediator.komparator.org/", "MediatorPort"), MediatorPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns MediatorPortType
     */
    @WebEndpoint(name = "MediatorPort")
    public MediatorPortType getMediatorPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://ws.mediator.komparator.org/", "MediatorPort"), MediatorPortType.class, features);
    }

    private static URL __getWsdlLocation() {
        if (MEDIATORSERVICE_EXCEPTION!= null) {
            throw MEDIATORSERVICE_EXCEPTION;
        }
        return MEDIATORSERVICE_WSDL_LOCATION;
    }

}