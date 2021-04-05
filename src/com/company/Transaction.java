package com.company;

public class Transaction {
    public String id;
    public TxIn[] txIns;
    public TxOut[] txOuts;

    public Transaction(String id, TxIn[] txIns, TxOut[] txOuts) {
        this.id = id;
        this.txIns = txIns;
        this.txOuts = txOuts;
    }

    public static String getTransactionId() {
        return "";
    }

    class TxOut {
        public String address;
        public int amount;
        TxOut(String address, int amount) {
            this.address = address;
            this.amount = amount;
        }
    }

    class TxIn {
        public String txOutId;
        public int txOutIndex;
        public String signature;
    }
}
