package com.example.tutorial.enclave;

import com.r3.conclave.host.AttestationParameters;
import com.r3.conclave.host.EnclaveHost;
import com.r3.conclave.host.EnclaveLoadException;
import com.r3.conclave.host.MailCommand;
import com.r3.conclave.mail.PostOffice;
import com.r3.conclave.mail.MailDecryptionException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReverseEnclaveTest {
    // We start by loading the enclave using EnclaveHost, and passing the class name of the Enclave subclass
    // that we defined in our enclave module.
    private static EnclaveHost enclave;
    private static final List<MailCommand> mailCommands = new ArrayList<>();

    @BeforeAll
    static void startup() throws EnclaveLoadException {
        enclave = EnclaveHost.load();
        enclave.start(new AttestationParameters.DCAP(), null, null, mailCommands::addAll);
    }

    @AfterAll
    static void shutdown() {
        if (enclave != null) {
            enclave.close();
        }
    }


    @Test
    void  reverseString() throws IOException, MailDecryptionException {

        PostOffice postOffice = enclave.getEnclaveInstanceInfo().createPostOffice();
        byte[] encryptedBytes = postOffice.encryptMail("hola".getBytes());
        enclave.deliverMail(encryptedBytes,"test");

        final MailCommand.PostMail reply = (MailCommand.PostMail)mailCommands.get(0);
        final byte[] responseBytes = (reply).getEncryptedBytes();
        final byte[] decryptedBytes = postOffice.decryptMail(responseBytes).getBodyAsBytes();


        String response = new String(Objects.requireNonNull(decryptedBytes));
        assertEquals("aloh", response);

    }
}
