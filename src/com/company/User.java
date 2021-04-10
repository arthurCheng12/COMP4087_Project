package com.company;

import java.security.KeyPair;
import java.security.PublicKey;
import java.security.PrivateKey;
import static com.company.ECDSAUtils.*;

public class User {

    private double balance;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private KeyPair keyPair = getKeyPair();

    public User(double balance) throws Exception {
        this.balance = balance;
    }

    public User() throws Exception {
        this.balance = 0;
    }

    public double getBalance() {
        return balance;
    }

    public void addBalance(double amount) {
        this.balance += amount;
    }

    public void minusAmount(double amount) {
        this.balance -= amount;
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