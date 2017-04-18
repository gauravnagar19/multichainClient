package multichainClient;

import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.Cipher;
 
public class XXC_KeyGen {
	private static final String ALGORITHM = "RSA";
	
	private static KeyPair generatedKeyPair;
	private static String SHAString = null;
	
	public XXC_KeyGen(String path, char userType, String id, String task){

		if (task == "new"){
		try {
			
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);

			keyGen.initialize(2048);
			generatedKeyPair = keyGen.genKeyPair();

			//System.out.println("Generated Key Pair");
			//dumpKeyPair(generatedKeyPair);
			SHAString = getStringFromSHA256(id);
			String filenamePrefix = generateFilenamePrefix(userType);
			SaveKeyPair(path, filenamePrefix , generatedKeyPair);
	
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}	
		}
		else if (task == "encrypt"){
		try {
			
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);

			keyGen.initialize(1024);
			KeyPair generatedKeyPair = keyGen.genKeyPair();

			KeyPair loadedKeyPair = LoadKeyPair(path, ALGORITHM);
			
	        byte[] publicKey = loadedKeyPair.getPublic().getEncoded();
	        byte[] privateKey = loadedKeyPair.getPrivate().getEncoded();

	        byte[] encryptedData = encrypt(publicKey,
	                "hi this is Visruth here".getBytes());
	        
	        System.out.println(new String(encryptedData));
	        
	        byte[] decryptedData = decrypt(privateKey, encryptedData);

	        System.out.println(new String(decryptedData));
	        
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}	
		}
		
	}
	
	private String generateFilenamePrefix(char userType){
		return( Character.toString(userType)+"-");
	}
	
	public String getPublicKey(){
		Base64.Encoder encoder = Base64.getEncoder();
		return(new String(encoder.encodeToString(generatedKeyPair.getPublic().getEncoded())));
	}
	public byte[] getPublicKeyBytes(){
		return generatedKeyPair.getPublic().getEncoded();
	}	
	public byte[] getPrivateKeyBytes(){
		return generatedKeyPair.getPrivate().getEncoded();
	}	
	public String getPrivateKey(){
		Base64.Encoder encoder = Base64.getEncoder();
		return(new String(encoder.encodeToString(generatedKeyPair.getPrivate().getEncoded())));
	}
	public String getSHAString(){
		return(new String((SHAString)));
	
	}	
    public byte[] encrypt(byte[] publicKey, byte[] inputData)
            throws Exception {

        PublicKey key = KeyFactory.getInstance(ALGORITHM)
                .generatePublic(new X509EncodedKeySpec(publicKey));

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.PUBLIC_KEY, key);

        byte[] encryptedBytes = cipher.doFinal(inputData);

        return encryptedBytes;
    }

    public byte[] decrypt(byte[] privateKey, byte[] inputData)
            throws Exception {

        PrivateKey key = KeyFactory.getInstance(ALGORITHM)
                .generatePrivate(new PKCS8EncodedKeySpec(privateKey));

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.PRIVATE_KEY, key);

        byte[] decryptedBytes = cipher.doFinal(inputData);

        return decryptedBytes;
    }	
	
	private void dumpKeyPair(KeyPair keyPair) {
		PublicKey pub = keyPair.getPublic();
		System.out.println("Public Key: " + getHexString(pub.getEncoded()));
 
		PrivateKey priv = keyPair.getPrivate();
		System.out.println("Private Key: " + getHexString(priv.getEncoded()));
		
		
		Base64.Encoder encoder = Base64.getEncoder();
        System.out.println("privateKey: " + encoder.encodeToString(priv.getEncoded()));
        System.out.println("publicKey: " + encoder.encodeToString(pub.getEncoded()));
	}
 
	private String getHexString(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}
 
	private void SaveKeyPair(String path, String filenamePrefix, KeyPair keyPair) throws IOException {
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
 
		// Store Public Key.
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
				publicKey.getEncoded());
		FileOutputStream fos = new FileOutputStream(path + "/"+"public_"+filenamePrefix+SHAString+".key");
		fos.write(x509EncodedKeySpec.getEncoded());
		fos.close();
 
		// Store Private Key.
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
				privateKey.getEncoded());
		fos = new FileOutputStream(path + "/"+"private_"+filenamePrefix+SHAString+".key");
		fos.write(pkcs8EncodedKeySpec.getEncoded());
		fos.close();
	}
 
	private KeyPair LoadKeyPair(String path, String algorithm)
			throws IOException, NoSuchAlgorithmException,
			InvalidKeySpecException {
		// Read Public Key.
		File filePublicKey = new File(path + "/public.key");
		FileInputStream fis = new FileInputStream(path + "/public.key");
		byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
		fis.read(encodedPublicKey);
		fis.close();
 
		// Read Private Key.
		File filePrivateKey = new File(path + "/private.key");
		fis = new FileInputStream(path + "/private.key");
		byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
		fis.read(encodedPrivateKey);
		fis.close();
 
		// Generate KeyPair.
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				encodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
 
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
				encodedPrivateKey);
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
 
		return new KeyPair(publicKey, privateKey);
	}

	private static final char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String byteArray2Hex(byte[] bytes) {
	    StringBuffer sb = new StringBuffer(bytes.length * 2);
	    for(final byte b : bytes) {
	        sb.append(hex[(b & 0xF0) >> 4]);
	        sb.append(hex[b & 0x0F]);
	    }
	    return sb.toString();
	}
	
	public String getStringFromSHA256(String stringToEncrypt) throws NoSuchAlgorithmException {
	    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
	    Instant instant = Instant.now ();
	    String newStringToEncrpt = instant.toString();
	    messageDigest.update(stringToEncrypt.getBytes());
	    return byteArray2Hex(messageDigest.digest());
	}
	
}