package org.komparator.supplier.ws.it;

import java.io.IOException;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.komparator.supplier.ws.cli.SupplierClient;

public class EVALBaseIT {

	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;

	protected static SupplierClient client;

	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		testProps = new Properties();
		try {
			testProps.load(EVALBaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Loaded test properties:");
			System.out.println(testProps);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}

		String uddiURL = testProps.getProperty("uddi.url");
		String wsName = testProps.getProperty("ws.name");

		client = new SupplierClient(uddiURL, wsName);

	}

	@AfterClass
	public static void cleanup() {
	}

}
