package com.company;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class Block {
    public int index;
    public double timestamp;
    public String hash;
    public String previousHash;
    public String data;
    public int difficulty;
    public int nonce;


    public Vector<Transaction> transactions;
    public String minerAddress;

    public Block(int index, double timestamp, String previousHash, String data, Transaction transaction, int difficulty) {
        this.index = index;
        this.timestamp = timestamp;
        this.hash = calculateHash(this.index, this.timestamp, this.previousHash, this.data, this.nonce);
        this.previousHash = previousHash;
        this.data = data;


        this.difficulty = difficulty;
        this.nonce = ThreadLocalRandom.current().nextInt();

    }

    public String calculateHash(int index, double timestamp, String previousHash, String data, int nonce) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String blockInformation = (index + previousHash + timestamp + data + nonce);
            byte[] hash = digest.digest(blockInformation.getBytes(StandardCharsets.UTF_8));
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Ben
    public Block(String minerAddress, int index, String hash, String previousHash, double timestamp, String data) {
        this.minerAddress = minerAddress;
        this.index = index;
        this.timestamp = timestamp;
        this.hash = hash;
        this.previousHash = previousHash;
        this.data = data;

        this.transactions = new Vector<Transaction>();
//        Transaction coinbaseTrans = Transaction.genConinbaseTrans();
//        transactions.add(coinbaseTrans);

//        this.difficulty = difficulty;
//        this.nonce = ThreadLocalRandom.current().nextInt();
    }

    // Ben
    public Block(int index, String hash, String previousHash, double timestamp, String data, int difficulty, int nonce) {
        this.index = index;
        this.timestamp = timestamp;
        this.hash = hash;
        this.previousHash = previousHash;
        this.data = data;
        this.difficulty = difficulty;
        this.nonce = nonce;

//        this.transaction = transaction;
        this.transactions = new Vector<Transaction>();
//        Transaction coinbaseTrans = Transaction.genConinbaseTrans();
//        transactions.add(coinbaseTrans);
    }
}
