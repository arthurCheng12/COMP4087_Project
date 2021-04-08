package com.company;
import sun.misc.BASE64Encoder;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class Transaction {
    public String id;
    public TxIn txIns;
    public TxOut txOuts;

    public Transaction(TxIn txIns, TxOut txOuts) {
        this.txIns = txIns;
        this.txOuts = txOuts;
        this.id = getTransactionId(this);
    }

    public static String getTransactionId(Transaction transaction) {
        TxIn txIn = transaction.txIns;
        String txInContent = txIn.txOutId + txIn.txOutIndex;
        txInContent.replace(" ", "");

        TxOut txOut = transaction.txOuts;
        String txOutContent = txOut.address + txOut.amount;
        txOutContent.replace(" ", "");

        return hash(txInContent, txOutContent);
    }

    public static String hash(String txInContent, String txOutContent ) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String blockInformation = (txInContent + txOutContent);
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

    static class TxOut {
        public String address;
        public double amount;
        TxOut(String address, double amount) {
            this.address = address;
            this.amount = amount;
        }
    }

    static class TxIn {
        public String txOutId;
        public int txOutIndex;
        public String signature;

        public TxIn(String txOutId, int txOutIndex) throws Exception {
            this.txOutId = txOutId;
            this.txOutIndex = txOutIndex;
            this.signature = genRSASign(txOutId, txOutIndex);
        }

        public String genRSASign(String txOutId, int txOutIndex) throws Exception {
            KeyPair keyPair = RSASignUtils.generateKeyPair();
            PublicKey pubKey = keyPair.getPublic();
            PrivateKey priKey = keyPair.getPrivate();

            String data = txOutId + txOutIndex;
            byte[] signInfo = RSASignUtils.sign(data.getBytes(), priKey);
            boolean verify = RSASignUtils.verify(data.getBytes(), signInfo, pubKey);

            System.out.println("Line 84 - Public Key : " + pubKey);
            System.out.println("Line 85 - Private Key : " + priKey);

            if (verify) {
                // System.out.println("Line 84 Verify! \n : " + new BASE64Encoder().encode(signInfo));
                return new BASE64Encoder().encode(signInfo);
            } else {
                // System.out.println("Line 87 Not Verify!");
                return "No RSA Sign";
            }

        }


    }

}
