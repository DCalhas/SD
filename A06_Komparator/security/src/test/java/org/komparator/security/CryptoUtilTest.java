package org.komparator.security;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

import javax.crypto.*;
import java.util.*;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import org.junit.*;
import static org.junit.Assert.*;

public class CryptoUtilTest {

	/** Asymmetric cryptography algorithm. */
	private static final String ASYM_ALGO = "RSA";
	/** Asymmetric cryptography key size. */
	private static final int ASYM_KEY_SIZE = 2048;
	/**
	 * Asymmetric cipher: combination of algorithm, block processing, and
	 * padding.
	 */
	private static final String ASYM_CIPHER = "RSA/ECB/PKCS1Padding";
    // static members
	
	private final String plainText = "This is the plain text!";
	/** Plain text bytes. */
	private final byte[] plainBytes = plainText.getBytes();
	
	private KeyPairGenerator keyGen;
	private KeyPair keyPair;
	
	private PrivateKey privateKey;
	private PublicKey publicKey;
	
	final static String CERTIFICATE = "example.cer";

	final static String KEYSTORE = "example.jks";
	final static String KEYSTORE_PASSWORD = "1nsecure";

	final static String KEY_ALIAS = "example";
	final static String KEY_PASSWORD = "ins3cur3";

	/** Digital signature algorithm. */
	private static final String SIGNATURE_ALGO = "SHA256withRSA";

    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() {
        // runs once before all tests in the suite
    }

    @AfterClass
    public static void oneTimeTearDown() {
        // runs once after all tests in the suite
    }

    // members

    // initialization and clean-up for each test
    @Before
    public void setUp() throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, CertificateException, IOException {
		privateKey = CryptoUtil.getPrivateKeyFromKeyStoreResource(KEYSTORE,
				KEYSTORE_PASSWORD.toCharArray(), KEY_ALIAS, KEY_PASSWORD.toCharArray());
		publicKey = CryptoUtil.getX509CertificateFromResource(CERTIFICATE).getPublicKey();
    }

    @After
    public void tearDown() {
        // runs after each test
    }

    // tests
    @Test
    public void success() {
        CryptoUtil crypto = new CryptoUtil();
		
        byte[] dataCiphered = crypto.asymCipher(plainBytes, publicKey);
        
        System.out.println(new String(dataCiphered));
        
        assertEquals(plainText, new String(crypto.asymDecipher(dataCiphered, privateKey)));
    }
    
    @Test
    public void successCipherPrivate() {
        CryptoUtil crypto = new CryptoUtil();
		
        byte[] dataCiphered = crypto.asymCipher(plainBytes, privateKey);
        
        System.out.println(new String(dataCiphered));
        
        assertEquals(plainText, new String(crypto.asymDecipher(dataCiphered, publicKey)));
    }
    
    @Test
	public void testSignature() throws Exception {
		System.out.print("TEST ");
		System.out.print(SIGNATURE_ALGO);
		System.out.print(" digital signature");
		System.out.println(" with public key in X509 certificate and private key in JDK keystore");

		System.out.print("Text: ");
		System.out.println(plainText);
		System.out.print("Bytes: ");
		System.out.println(printHexBinary(plainBytes));

		// make digital signature
		System.out.println("Signing ...");
		byte[] digitalSignature = CryptoUtil.makeDigitalSignature(SIGNATURE_ALGO, privateKey, plainBytes);
		assertNotNull(digitalSignature);

		// verify the signature
		System.out.println("Verifying ...");
		boolean result = CryptoUtil.verifyDigitalSignature(SIGNATURE_ALGO, publicKey, plainBytes, digitalSignature);
		assertTrue(result);

		// data modification ...
		plainBytes[3] = 12;
		System.out.println("Tampered bytes: (look closely around the 7th hex character)");
		System.out.println(printHexBinary(plainBytes));
		System.out.println("      ^");

		// verify the signature
		System.out.println("Verifying ...");
		boolean resultAfterTamper = CryptoUtil.verifyDigitalSignature(SIGNATURE_ALGO, publicKey, plainBytes,
				digitalSignature);
		assertFalse(resultAfterTamper);
	}
}
