package com.example.tutorial.client;

import com.r3.conclave.client.EnclaveClient;
import com.r3.conclave.client.web.WebEnclaveTransport;
import com.r3.conclave.common.EnclaveConstraint;
import com.r3.conclave.common.InvalidEnclaveException;
import com.r3.conclave.mail.EnclaveMail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ReverseEnclaveClient {
    private static String DESCRIPTION = "Simple client that communicates with the ReverseEnclave using the web host.";
    private static String USAGE_MESSAGE = "Usage: reverse-client ENCLAVE_CONSTRAINT STRING_TO_REVERSE\n" +
            "  ENCLAVE_CONSTRAINT: Enclave constraint which determines the enclave's identity and whether it's " +
            "acceptable to use.\n" +
            "  STRING_TO_REVERSE: The string to send to the enclave to reverse.";

    private static String REVERSE_HOST_URL = "http://localhost:8080";

    public static void main(String... args) throws IOException, InvalidEnclaveException {
        if(args.length != 2){
            System.out.println(DESCRIPTION);
            System.out.println(USAGE_MESSAGE);
        }
        
        EnclaveConstraint constraint = EnclaveConstraint.parse(args[0]);
        String stringToReverse = args[1];
        
        callEnclave(constraint, stringToReverse);
    }

    private static void callEnclave(EnclaveConstraint constraint, String stringToReverse) throws IOException, InvalidEnclaveException {
        try (WebEnclaveTransport transport = new WebEnclaveTransport(REVERSE_HOST_URL);
            EnclaveClient client = new EnclaveClient(constraint)){

            // Connect to the host and send the string to reverse
            client.start(transport);
            byte[] requestMailBody = stringToReverse.getBytes(StandardCharsets.UTF_8);
            EnclaveMail responseMail = client.sendMail(requestMailBody);

            //Parse and print out the response
            String responseString = (responseMail != null) ? new String(responseMail.getBodyAsBytes()) : null;
            System.out.println("Reversing `" + stringToReverse + "` gives `" + responseString + "`");


        }


    }


}