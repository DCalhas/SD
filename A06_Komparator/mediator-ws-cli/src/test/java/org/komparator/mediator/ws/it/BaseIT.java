package org.komparator.mediator.ws.it;

import java.io.IOException;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.security.Manager;
import org.komparator.supplier.ws.cli.SupplierClient;

public class BaseIT {

	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;

	protected static MediatorClient mediatorClient;
	protected static SupplierClient SupplierClient1;
	protected static SupplierClient SupplierClient2;
	protected static SupplierClient SupplierClient3;

	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		testProps = new Properties();
		try {
			testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Loaded test properties:");
			System.out.println(testProps);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}

		String uddiEnabled = testProps.getProperty("uddi.enabled");
		String uddiURL = testProps.getProperty("uddi.url");
		String wsName = testProps.getProperty("ws.name");
		String wsURL = testProps.getProperty("ws.url");

		if ("true".equalsIgnoreCase(uddiEnabled)) {
			mediatorClient = new MediatorClient(uddiURL, wsName);
			SupplierClient1 = new SupplierClient(uddiURL, "A06_Supplier1");
			SupplierClient2 = new SupplierClient(uddiURL, "A06_Supplier2");
			SupplierClient3 = new SupplierClient(uddiURL, "A06_Supplier3");
		} else {
			//TODO Complete if not using UDDI, not needed for now
		}

	}

	@AfterClass
	public static void cleanup() {
	}

}
