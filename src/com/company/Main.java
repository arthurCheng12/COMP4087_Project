package com.company;

import sun.misc.BASE64Encoder;

import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.ArrayList;
import static com.company.utils.*;
import com.google.gson.GsonBuilder;
import java.security.PublicKey;
import java.security.PrivateKey;

public class Main {
    static final int BLOCK_GENERATION_INTERVAL = 4;
    static final int DIFFICULTY_ADJUSTMENT_INTERVAL = 2;
    static final int COINBASE_AMOUNT = 50;
    static final int TOTAL_COIN_SUPPLY = 21000000;
    static ArrayList<Block> chain = new ArrayList<Block>();
    static User system;

    public static void main(String[] args) throws Exception{
        // write your code here
        System.out.println("Block Chain Start!");

        Transaction transaction;
        String blockData;

        system = new User(TOTAL_COIN_SUPPLY);

        User alice = new User();
        User bob = new User();
        User peter = new User();
//
//        transaction = createTransaction("127.0.0.1:3000", 50, alice.getPrivateKey(), "" + bob.getPublicKey(), 5);
//        if (transaction.verifySignature(alice.getPublicKey(), transaction.getTxIns().getSignature(), transaction.getTxIns().getTxOutId() + transaction.getTxIns().getTxOutIndex())) {
//            bob.addBalance(5);
//        } else {
//        }
//
//        blockData = "This is the gene block";
//        Block block = new Block(0, new Date().getTime() / 1000, "th1515f1r5t810ckh45h", blockData, transaction, DIFFICULTY_ADJUSTMENT_INTERVAL);
//        chain.add(block);
//
//        transaction = createTransaction("127.0.0.1:3000", 45, alice.getPrivateKey(), "" + bob.getPublicKey(),  15);
//        if (transaction.verifySignature(bob.getPublicKey(), transaction.getTxIns().getSignature(), transaction.getTxIns().getTxOutId() + transaction.getTxIns().getTxOutIndex())) {
//            bob.addBalance(15);
//        } else {
//        }
//
//        blockData = "Gun";
//        block = findBlock(blockData, transaction);
//        if (isValidNewBlock(block, chain.lastElement())) {
//            MintCoinTransaction();
//            chain.add(block);
//        }
//
//        transaction = createTransaction("127.0.0.1:3000", 40, peter.getPrivateKey(), "" + alice.getPublicKey(), 40);
//        if (transaction.verifySignature(peter.getPublicKey(), transaction.getTxIns().getSignature(), transaction.getTxIns().getTxOutId() + transaction.getTxIns().getTxOutIndex())) {
//            alice.addBalance(40);
//        } else {
//        }
//
//        blockData = "420";
//        block = findBlock(blockData, transaction);
//        if (isValidNewBlock(block, chain.lastElement())) {
//            MintCoinTransaction();
//            chain.add(block);
//        }



        chain.add(createGenesisBlock());
        printBlockChain();
        for (int i = 0; i <= 20; i++) {
            chain.add(findBlock(kCov(alice.getPublicKey()), "this " + i + " block"));
        }
        printBlockChain();


        // Testing
         System.out.println("Alice Amount : " + alice.getBalance());
         System.out.println("Bob Amount : " + bob.getBalance());
         System.out.println("Peter Amount : " + peter.getBalance());
//
//        printBlockChain();

        // a user have a address, and keep mining block
        //        User user1 = new User()
    }

//    public static Block createGenesisBlock() {
//        return new Block(0, "\"Everything starts from here!\"", "0");
//    }

    public static Transaction createTransaction(String txOutId, int txOutIndex, PrivateKey privateKey, String address, double amount) throws Exception{
        Transaction.TxIn txIn = new Transaction.TxIn();
        Transaction.TxOut txOut = new Transaction.TxOut();
        Transaction transaction = new Transaction(txIn, txOut);

        txIn.setTxOutId(txOutId);
        txIn.setTxOutIndex(txOutIndex);
        txIn.setSignature(transaction.genSignature(privateKey, txIn.getTxOutId() + txOutIndex));
        //System.out.println("Line 64 Signature : " + txIn.getSignature());
        txOut.setAddress(address);
        //System.out.println("Line 66 Address : " + txOut.getAddress());
        txOut.setAmount(amount);
        //System.out.println("Line 68 Amount : " + txOut.getAmount());

        return transaction;
    }

    public static int getAdjustedDifficulty(Block latestBlock, ArrayList<Block> chain) {
        Block prevAdjustmentBlock = chain.get(chain.size() - DIFFICULTY_ADJUSTMENT_INTERVAL);
        int timeExpected = BLOCK_GENERATION_INTERVAL * DIFFICULTY_ADJUSTMENT_INTERVAL;
        double timeTaken = latestBlock.timestamp - prevAdjustmentBlock.timestamp;
        if (timeTaken < timeExpected / 2) {
            return prevAdjustmentBlock.difficulty + 1;
        } else if (timeTaken > timeExpected * 2) {
            return prevAdjustmentBlock.difficulty - 1;
        } else {
            return prevAdjustmentBlock.difficulty;
        }
    }

    public static int getDifficulty(ArrayList<Block> chain) {
        Block lastBlock = chain.get(chain.size() - 1);
        if (lastBlock.index % DIFFICULTY_ADJUSTMENT_INTERVAL == 0 && lastBlock.index != 0) {
            return getAdjustedDifficulty(lastBlock, chain);
        } else {
            return lastBlock.difficulty;
        }
    }


//    public static Block findBlock(String blockData, Transaction transaction) {
//        while (true) {
//            int difficulty = getDifficulty(chain);
//            Block newBlock = generateNextBlock(blockData, transaction, chain, difficulty);
//            if (hashMatchesDifficulty(newBlock.hash, difficulty)) {
//                return newBlock;
//            }
//        }
//    }

    // Ben
    public static Block createGenesisBlock(){
        int index = 0;
        double timestamp = new Date().getTime() / 1000;
        String previousBlockHash = "0";
        String blockData = "This is first block";
        String hash = calculateHash(index + previousBlockHash + timestamp + blockData);

        return new Block(kCov(system.getPublicKey()), index, hash, previousBlockHash, timestamp, blockData, DIFFICULTY_ADJUSTMENT_INTERVAL, 0);
    }

    // Ben
    public static Block generateNextBlock(String miner, String blockData, int difficulty, int nonce){
        Block previousBlock = chain.get(chain.size() - 1);
        int nextIndex = previousBlock.index + 1;
        double nextTimestamp = new Date().getTime() / 1000;
        String nextHash = calculateHash(nextIndex + previousBlock.hash + nextTimestamp + blockData + nonce);
        Block newBlock = new Block(miner , nextIndex, nextHash, previousBlock.hash, nextTimestamp, blockData, difficulty, nonce);

        return newBlock;
    }

    // Ben
    public static Block findBlock(String miner, String data) {
        int nonce = 0;
        while (true) {
            int difficulty = getDifficulty(chain);
//            System.out.println(difficulty);
            Block newBlock = generateNextBlock(miner, data, difficulty, nonce);
            if (hashMatchesDifficulty(newBlock.hash, difficulty)) {
                // create Coinbase Transaction for block
                newBlock.addTransaction(createCoinBaseTransaction(miner));;
                System.out.println("Total block : " + chain.size() + " new block difficulty :" + difficulty);
                return newBlock;
            }
            nonce++;
        }
    }

//    public static Block generateNextBlock(String blockData, Transaction transaction, Vector<Block> chain, int difficulty) {
//        Block previousBlock = chain.lastElement();
//        int nextIndex = previousBlock.index + 1;
//        double nextTimestamp = new Date().getTime() / 1000;
//        return new Block(nextIndex, nextTimestamp, previousBlock.hash, blockData, transaction, difficulty);
//    }

    public static String hashForBlock(Block block) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String blockInformation = block.index + block.previousHash + block.timestamp + block.data + block.nonce;
            byte[] hash = digest.digest(blockInformation.getBytes(StandardCharsets.UTF_8));
            StringBuffer hexString = new StringBuffer();

            for(int i = 0; i < hash.length; ++i) {
                String hex = Integer.toHexString(255 & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception var7) {
            throw new RuntimeException(var7);
        }
    }


    public static void printBlockChain() {
        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(chain);
        System.out.println(blockchainJson);
        double releasedCoins = TOTAL_COIN_SUPPLY - system.getBalance();
        double blockHeight = chain.size();
        System.out.println("Block Height : " + blockHeight);
        System.out.println("Number of Released Coins : " + releasedCoins);

//        for (int i = 0; i < chain.size(); i++) {
//            Block block = chain.get(i);
//            System.out.println("index: " + block.index);
//            System.out.println("timestamp: " + block.timestamp);
//            System.out.println("data: " + block.data);
////            System.out.println("transactionID: " + block.transaction.id);
//            System.out.println("previousHash: " + block.previousHash);
//            System.out.println("hash: " + block.hash);
//            System.out.println("------------------------------------------------------------------------");
//        }
    }

    public static Transaction createCoinBaseTransaction(String miner){
        try {
            Transaction coinBasetransaction = createTransaction("give 50 coins to address 0xabc", 1, system.getPrivateKey(), miner, 50);
            return coinBasetransaction;
        }catch (Exception e){
            return null;
        }
    }

}
