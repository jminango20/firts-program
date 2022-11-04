package com.example.tutorial.enclave;

import com.r3.conclave.enclave.Enclave;
import com.r3.conclave.mail.EnclaveMail;

/**
 * Simply reverses the bytes that are passed in.
 */
public class ReverseEnclave extends Enclave {

    private String reverse(String input) {
        var builder = new StringBuilder(input.length());
        for (var i = input.length() - 1; i >= 0; i--) {
            builder.append(input.charAt(i));
        }
        return builder.toString();
    }

    @Override
    protected void receiveMail(EnclaveMail mail, String routingHint) {
        // First, decode mail body as a String.
        var stringToReverse = new String(mail.getBodyAsBytes());
        // Reverse it and re-encode to UTF-8 to send back.
        var reversedEncodedString = reverse(stringToReverse).getBytes();
        // Get the post office object for responding back to this mail and use it to encrypt our response.
        var responseBytes = postOffice(mail).encryptMail(reversedEncodedString);
        postMail(responseBytes, routingHint);
    }
}