package multichainClient;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.common.hash.Hashing;

public class multichainApp {
	public static void main(String[] args) {
		List<String> params = new ArrayList<String>();
		List<Object> params1 = new ArrayList<Object>();
		Operations op = new Operations();
		RPCClient client = new RPCClient();
		Scanner scanner = new Scanner(System.in);
		char userType;
		
		String filePath = "C:\\Users\\gaura\\OneDrive - University of South Florida\\Gaurav\\Projects\\Blockchain";
		String filenamePrefix = "Unknown";

		while (1 == 1) {
			System.out.println("1. Register: ");
			System.out.println("2. Store certificate: ");
			System.out.println("3. Provide access certificate: ");
			System.out.println("4. Access certificate using your private key: ");
			System.out.println("5. Check User: ");
			System.out.println("6. Exit");
			char taskType = scanner.next().charAt(0);
			if (taskType == '1') {
				System.out.println("Are you a Course Provider / Employer / Student / Instructor (C/E/S/I)): ");

				userType = scanner.next().charAt(0);
				op.registerUser(filePath, userType);
			}

			if (taskType == '2') {
				System.out.println("Are you a Course Provider / Employer / Student / Instructor (C/E/S/I)): ");

				userType = scanner.next().charAt(0);
				op.storeCertificate(filePath, userType);
			}
			if (taskType == '3') {
				//op.checkAccessCertificate(userType);
				//op.provideCertificate(userType);
			}
			if (taskType == '4') {
				//op.checkAccessCertificate(userType);
			}
			if (taskType == '5') {
				System.out.println("Are you a Course Provider / Employer / Student / Instructor (C/E/S/I)): ");
				userType = scanner.next().charAt(0);
				System.out.println("Enter User ID:");
				String userID = scanner.next();
				op.checkUser(userType,userID);
			}
			if (taskType == '6') {
				return;
			}
		}

	}
}
