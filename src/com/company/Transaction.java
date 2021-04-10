package com.company;
import sun.misc.BASE64Encoder;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static com.company.ECDSAUtils.*;
import static com.company.utils.*;
import com.google.gson.annotations.Expose;

public class Transaction {

    @Expose
    public String id;

    // him
//    public TxIn txIns;
//    public TxOut txOuts;

    // Ben
    @Expose
    public String signature;

    @Expose(serialize = false)
    public PublicKey sender;
    @Expose(serialize = false)
    public PublicKey reciepient;

    @Expose
    public double amount;
    @Expose
    public ArrayList<TxIn> txIns = new ArrayList<TxIn>();
    @Expose
    public ArrayList<TxOut> txOuts = new ArrayList<TxOut>();


    private static int sequence = 0; //A rough count of how many transactions have been generated

    // him
//    public Transaction(TxIn txIns, TxOut txOuts) throws Exception {
//        this.txIns = txIns;
//        this.txOuts = txOuts;
//        this.id = getTransactionId(this);
//    }

    // ben
    public Transaction(PublicKey sender, PublicKey reciepient , double amount, ArrayList<TxIn> txIns) {
        this.sender = sender;
        this.reciepient = reciepient;
        this.amount = amount;
        this.txIns = txIns;
    }

    // him
//    public static String getTransactionId(Transaction transaction) {
//        TxIn txIn = transaction.txIns;
//        String txInContent = txIn.txOutId + txIn.txOutIndex;
//        txInContent.replace(" ", "");
//
//        TxOut txOut = transaction.txOuts;
//        String txOutContent = txOut.address + txOut.amount;
//        txOutContent.replace(" ", "");
//
//        return hash(txInContent, txOutContent);
//    }
//
//    public static String hash(String txInContent, String txOutContent ) {
//        try {
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            String blockInformation = (txInContent + txOutContent);
//            byte[] hash = digest.digest(blockInformation.getBytes(StandardCharsets.UTF_8));
//            StringBuffer hexString = new StringBuffer();
//            for (int i = 0; i < hash.length; i++) {
//                String hex = Integer.toHexString(0xff & hash[i]);
//                if (hex.length() == 1) {
//                    hexString.append('0');
//                }
//                hexString.append(hex);
//            }
//            return hexString.toString();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public TxIn getTxIns () {
//        return txIns;
//    }
//
//    public TxOut getTxOuts () {
//        return txOuts;
//    }
//
//    public static String genSignature(PrivateKey privateKey, String data) throws Exception {
//        String sign = signECDSA(privateKey, data);
//        return sign;
//    }
//
//    public static boolean verifySignature(PublicKey publicKey, String sign, String data) {
//        return verifyECDSA(publicKey, sign, data);
//    }

    // Ben
    public void genSignature(PrivateKey privateKey){
        try {
            String data = kCov(this.sender) + kCov(this.reciepient) + amount;
            this.signature = signECDSA(privateKey, data);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
//        return sign;
    }

    public boolean verifySignature() {
        String data = kCov(this.sender) + kCov(this.reciepient) + amount;
        return verifyECDSA(sender, signature, data);
    }

    public String getTransactionId() {
        sequence++;
        return calculateHash(kCov(sender) + kCov(reciepient) + amount + sequence);
    }

    public boolean processTransaction() {
        if(verifySignature() == false) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        for(TxIn i : txIns) {
            i.UTXO = Main.UTXOs.get(i.txOutId);
        }

        double leftOver = getInputsAmount() - amount;

        id = getTransactionId();
        txOuts.add(new TxOut( this.reciepient, amount, id)); //send value to recipient
        txOuts.add(new TxOut( this.sender, leftOver, id)); //send the left over 'change' back to sender

        //Add outputs to Unspent list
        for(TxOut o : txOuts) {
            Main.UTXOs.put(o.txOutId , o);
        }

        //Remove transaction inputs from UTXO lists as spent:
        for(TxIn i : txIns) {
            if(i.UTXO == null) continue;
            Main.UTXOs.remove(i.UTXO.txOutId);
        }

        return true;
    }

    public float getInputsAmount() {
        float total = 0;
        for(TxIn i : txIns) {
            if(i.UTXO == null) continue;
            total += i.UTXO.amount;
        }
        return total;
    }




    // him
//    static class TxIn {
//        public String txOutId;
//        public int txOutIndex;
//        public String signature;
//
//        public TxIn() {
//
//        }
//
//        public TxIn(String txOutId, int txOutIndex, String signature) throws Exception {
//            this.txOutId = txOutId;
//            this.txOutIndex = txOutIndex;
//            this.signature = signature;
//        }
//
//        public void setTxOutId(String txOutId) {
//            this.txOutId = txOutId;
//        }
//
//        public void setTxOutIndex(int txOutIndex) {
//            this.txOutIndex = txOutIndex;
//        }
//
//        public void setSignature(String signature) {
//            this.signature = signature;
//        }
//
//        public String getTxOutId() {
//            return txOutId;
//        }
//
//        public int getTxOutIndex() {
//            return txOutIndex;
//        }
//
//        public String getSignature() {
//            return signature;
//        }
//    }
//
//    static class TxOut {
//        public String address;
//        public double amount;
//
//        public TxOut() {
//
//        }
//
//        public TxOut(String address, double amount) {
//            this.address = address;
//            this.amount = amount;
//        }
//
//        public void setAddress(String address) {
//            this.address = address;
//        }
//
//        public String getAddress() {
//            return address;
//        }
//
//        public void setAmount(double amount) {
//            this.amount = amount;
//        }
//
//        public double getAmount() {
//            return amount;
//        }
//
//    }
}