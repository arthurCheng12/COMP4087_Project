package com.company;

import java.security.KeyPair;
import java.security.PublicKey;
import java.security.PrivateKey;
import static com.company.ECDSAUtils.*;

public class User {

    private double amount;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private KeyPair keyPair = getKeyPair();

    public User() throws Exception {
        this.amount = 0;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = this.amount + amount;
    }

    public PublicKey getPublicKey() {
        publicKey = keyPair.getPublic();
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        privateKey = keyPair.getPrivate();
        return privateKey;
    }
}