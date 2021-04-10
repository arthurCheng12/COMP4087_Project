package com.company;

import java.security.KeyPair;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.company.ECDSAUtils.*;


public class User {

//    private double balance;
    public  PublicKey publicKey;
    public  PrivateKey privateKey;
    public  KeyPair keyPair = getKeyPair();

    // ben
    public HashMap<String, TxOut> UTXOs = new HashMap<String, TxOut>();

    // Him
//    public User(double balance) throws Exception {
//        this.balance = balance;
//    }
//
//    public User() throws Exception {
//        this.balance = 0;
//    }
//
//    public double getBalance() {
//        return balance;
//    }
//
//    public void addBalance(double amount) {
//        this.balance += amount;
//    }
//
//    public void minusAmount(double amount) {
//        this.balance -= amount;
//    }
//
//    public PublicKey getPublicKey() {
//        publicKey = keyPair.getPublic();
//        return publicKey;
//    }
//
//    public PrivateKey getPrivateKey() {
//        privateKey = keyPair.getPrivate();
//        return privateKey;
//    }


    public User() throws Exception {
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    public double getBalance() {
        double total = 0;
        for (Map.Entry<String, TxOut> item: Main.UTXOs.entrySet()){
            TxOut UTXO = item.getValue();
            if(UTXO.isMine(publicKey)) { //if output belongs to me ( if coins belong to me )
                UTXOs.put(UTXO.txOutId, UTXO); //add it to our list of unspent transactions.
                total += UTXO.amount ;
            }
        }
        return total;
    }

    public Transaction sendFunds(PublicKey recipient, double amount) {
        if(getBalance() < amount) {
            System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }
        ArrayList<TxIn> inputs = new ArrayList<TxIn>();

        float total = 0;
        for (Map.Entry<String, TxOut> item: UTXOs.entrySet()){
            TxOut UTXO = item.getValue();
            total += UTXO.amount;
            inputs.add(new TxIn(UTXO.txOutId));
            if(total > amount) break;
        }

        Transaction newTransaction = new Transaction(publicKey, recipient , amount, inputs);
        newTransaction.genSignature(privateKey);

        for(TxIn input: inputs){
            UTXOs.remove(input.txOutId);
        }

        return newTransaction;
    }

}