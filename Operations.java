package multichainClient;

import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Scanner;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Hex;

public class Operations {
	private String getHexString(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}
	
	public void registerUser(String filePath, char signInAuthType){
		RPCClient client = new RPCClient();
		Scanner scanner = new Scanner(System.in);
		try {
				System.out.println("Enter your ID: ");
				String id = scanner.next();
				XXC_KeyGen keyGen = new XXC_KeyGen(filePath, signInAuthType, id, "new");
				byte[] pubKey = keyGen.getPublicKeyBytes();
				String SHAString = keyGen.getSHAString();
				//System.out.println("10 - "+getHexString(keyGen.getPublicKeyBytes()));
				//System.out.println("11 - "+Hex.encodeHexString(keyGen.getPublicKeyBytes()));
				//System.out.println("12 - "+Hex.encodeHexString(Hex.decodeHex((Hex.encodeHexString(keyGen.getPublicKeyBytes())).toCharArray())));
				//System.out.println("12a - "+Hex.encodeHexString(Hex.decodeHex((Hex.encodeHexString(keyGen.getPublicKeyBytes())).toCharArray())));
				//System.out.println("13 - "+keyGen.getPublicKeyBytes());
				//System.out.println("14 - "+pubKey);
				//System.out.println("15 - "+pubKey.getBytes());
				//System.out.println("16 - "+new String(pubKey.getBytes()));
				//System.out.println("17 - "+Hex.encodeHexString(pubKey.getBytes()));
				//System.out.println("18 - "+Hex.encodeHexString(Hex.decodeHex((Hex.encodeHexString(pubKey.getBytes())).toCharArray())));
				//System.out.println("19 - "+pubKey.getBytes("UTF-8"));
				//System.out.println("20 - "+Base64.getEncoder().encodeToString(pubKey.getBytes("utf-8")));
				//System.out.println("21 - "+Base64.getDecoder().decode(pubKey.getBytes()));
				//System.out.println("22 - "+DatatypeConverter.printHexBinary(keyGen.getPublicKeyBytes()));
				//String g = DatatypeConverter.printHexBinary(keyGen.getPublicKeyBytes());
				//System.out.println("23 - "+DatatypeConverter.parseHexBinary(g));

				System.out.println(client.createStreamKeys("test_pubkeys", signInAuthType +"-" + SHAString, pubKey));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String checkUser(char signInAuthType, String id){
		RPCClient client = new RPCClient();
		Scanner scanner = new Scanner(System.in);
		try {
		String SUID = getStringFromSHA256(id);
		String output = client.getLatestStreamKeyItemsData("test_pubkeys",signInAuthType+"-" +SUID);
		System.out.println("21 - "+output);
		System.out.println("22 - "+Hex.decodeHex(output.toCharArray()));
		if(output == "error") return "error";
		else return output;
		} catch (Exception e) {
		e.printStackTrace();
		return "error";
	}

	}
	
	public void storeCertificate(String filePath, char signInAuthType){
		RPCClient client = new RPCClient();
		Scanner scanner = new Scanner(System.in);
		try {
				System.out.println("Enter your CP ID: ");
				String cpId = scanner.next();
				String cpSHA = getStringFromSHA256(cpId);
				String cpOutput = client.getLatestStreamKeyItemsData("test_pubkeys","C-"+cpSHA);

				System.out.println("Enter Student's ID: ");
				String studentId = scanner.next();
				String studentSHA = getStringFromSHA256(studentId);
				String pubKeyStudent = client.getLatestStreamKeyItemsData("test_pubkeys","S-"+studentSHA);

				if (cpOutput == "error" | pubKeyStudent == "error") {
					System.out.println("ID not found.");
					return;
				}
				else {
					System.out.println("Enter location of certificate:");
					//String datafile = "E:\\NEW_TEST\\chunks\\chunk1.pdf";
					XXC_KeyGen keyGen = new XXC_KeyGen(filePath, signInAuthType, studentId, "new");
					byte[] pubKey = keyGen.getPublicKeyBytes();
					System.out.println("pubKey: "+pubKey);
					Path certFilePath = Paths.get("C:\\Cert_1.pdf");
					byte[] pdfBytes = Files.readAllBytes(certFilePath);
					String SHAPDF = getFileStringFromSHA256(pdfBytes);
					//byte[] encryptedBytesItems = keyGen.encrypt(pubKey, SHAPDF.getBytes());

					//System.out.println("CE-" + SHAPDF);
					//System.out.println(new String(encryptedBytes, "UTF-8"));

					//byte[] privKey = keyGen.getPrivateKeyBytes();
					//byte[] decryptedData = keyGen.decrypt(privKey, encryptedBytes);
			        //System.out.println(new String(decryptedData));
					//String statusTxIdItems = client.createStreamKeys("test_items", studentSHA, encryptedBytesItems);
					String statusTxIdItems = client.createStreamKeys("test_items", studentSHA, SHAPDF.getBytes());
					
					if (statusTxIdItems == "Unsuccessful") {
						System.out.println("Error storing in stream test_items"); 
						return;
						}
					else {
					byte[] privKeyFile = keyGen.getPrivateKeyBytes();
					System.out.println(privKeyFile.length);
					//byte[] privKeyFile = new String("Gaurav").getBytes();
					//byte[] pubKeyStudentBytes = Base64.getDecoder().decode(pubKeyStudent);
					byte[] pubKeyStudentBytes = Hex.decodeHex(pubKeyStudent.toCharArray());
					byte[] encryptedBytesAccess = keyGen.encrypt(pubKeyStudentBytes, privKeyFile);
					//System.out.println(client.createStreamKeys("test_access", cpSHA+"-"+sSHA, encryptedBytesAccess));
					String statusTxIdAccess = client.createStreamKeys("test_access", statusTxIdItems+"-"+studentSHA, encryptedBytesAccess);
					if (statusTxIdItems == "Unsuccessful") {
						System.out.println("Error storing in stream test_access"); 
						return;
						}
					else {
						System.out.println(statusTxIdAccess);
					}
					
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	


	public void checkAccessCertificate(char signInAuthType){
		RPCClient client = new RPCClient();
		Scanner scanner = new Scanner(System.in);
		try {
			System.out.println("Enter Student's ID: ");
			String sId = scanner.next();
			String sSHA = getStringFromSHA256(sId);
			String sOutput = client.getLatestStreamKeyItemsData("test_pubkeys","S-"+sSHA);

			System.out.println("Enter your CP's ID: ");
			String cpId = scanner.next();
			String cpSHA = getStringFromSHA256(cpId);
			String cpOutput = client.getLatestStreamKeyItemsData("test_pubkeys","C-"+cpSHA);

				if (cpOutput == "error" | sOutput == "error") {
					System.out.println("ID not found.");
					return;
				}
				else {
					System.out.println("Checking certificate provided by the Course Provider:");

					Path pdfPath = Paths.get("C:\\Cert_1.pdf");
					byte[] pdfBytes = Files.readAllBytes(pdfPath);
					String SHAPDF = getFileStringFromSHA256(pdfBytes);
					byte[] encryptedBytes = keyGen.encrypt(pubKey, SHAPDF.getBytes());
					
					//System.out.println("CE-" + SHAPDF);
					//System.out.println(new String(encryptedBytes, "UTF-8"));
					
					//byte[] privKey = keyGen.getPrivateKeyBytes();
					//byte[] decryptedData = keyGen.decrypt(privKey, encryptedBytes);
			        //System.out.println(new String(decryptedData));
					System.out.println(client.createStreamKeys("test_items", cpSHA+"-"+sSHA, new String(encryptedBytes, "UTF-8")));
					
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void provideAccessCertificate(char signInAuthType){
		RPCClient client = new RPCClient();
		Scanner scanner = new Scanner(System.in);
		try {
			System.out.println("Enter Student's ID: ");
			String sId = scanner.next();
			String sSHA = getStringFromSHA256(sId);
			String sOutput = client.getLatestStreamKeyItemsData("test_pubkeys","S-"+sSHA);

			System.out.println("Enter your CP's ID: ");
			String cpId = scanner.next();
			String cpSHA = getStringFromSHA256(cpId);
			String cpOutput = client.getLatestStreamKeyItemsData("test_pubkeys","C-"+cpSHA);


				if (cpOutput == "error" | sOutput == "error") {
					System.out.println("ID not found.");
					return;
				}
				else {
					System.out.println("Checking certificate provided by the Course Provider:");

					Path pdfPath = Paths.get("C:\\Cert_1.pdf");
					byte[] pdfBytes = Files.readAllBytes(pdfPath);
					String SHAPDF = getFileStringFromSHA256(pdfBytes);
					byte[] encryptedBytes = keyGen.encrypt(pubKey, SHAPDF.getBytes());
					
					//System.out.println("CE-" + SHAPDF);
					//System.out.println(new String(encryptedBytes, "UTF-8"));
					
					//byte[] privKey = keyGen.getPrivateKeyBytes();
					//byte[] decryptedData = keyGen.decrypt(privKey, encryptedBytes);
			        //System.out.println(new String(decryptedData));
					System.out.println(client.createStreamKeys("test_items", cpSHA+"-"+sSHA, new String(encryptedBytes, "UTF-8")));
					
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	    messageDigest.update(stringToEncrypt.getBytes());
	    return byteArray2Hex(messageDigest.digest());
	}
	
	public String getFileStringFromSHA256(byte[] dataBytes) throws NoSuchAlgorithmException {
	    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
	    messageDigest.update(dataBytes);
	    return byteArray2Hex(messageDigest.digest());
	}	
}