package com.company;

import sun.misc.BASE64Encoder;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
    public static HashMap<String, TxOut> UTXOs = new HashMap<String, TxOut>();
    public static HashMap<String, User> Users = new HashMap<String, User>();

    static User coinbase;




    public static void main(String[] args) throws Exception{
        // write your code here
        System.out.println("Block Chain Start!");

        coinbase = new User();
        User alice = new User();
        User bob = new User();
        User peter = new User();
        User ben = new User();

        Users.put("alice", alice);
        Users.put("bob", bob);
        Users.put("peter", peter);
        Users.put("ben", ben);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        int operation = 0;
        while (operation != 6) {
            System.out.println("Please input operation :");
            System.out.println("1 : Create Block Chain");
            System.out.println("2 : Mining Block ");
            System.out.println("3 : Create Transaction ");
            System.out.println("4 : Print User State ");
            System.out.println("5 : Print Block Chain ");
            System.out.println("6 : Exit ");

            operation = Integer.parseInt(reader.readLine());
            switch(operation){
                case 1 :
                    Transaction genesisTransaction = new Transaction(coinbase.publicKey, alice.publicKey, 100, null);
                    genesisTransaction.genSignature(coinbase.privateKey);

                    genesisTransaction.id = "0";
                    genesisTransaction.txOuts.add(new TxOut(genesisTransaction.reciepient, genesisTransaction.amount, genesisTransaction.id));
                    UTXOs.put(genesisTransaction.txOuts.get(0).txOutId, genesisTransaction.txOuts.get(0));

                    System.out.println("Creating and Mining Genesis block... ");
                    Block genesisBlock = createGenesisBlock();
                    genesisBlock.addTransaction(genesisTransaction);
                    chain.add(genesisBlock) ;
                    break;
                case 2 :
                    System.out.print("Input miner name : ");
                    String minerName = reader.readLine();
//                    chain.add(findBlock(ben.publicKey, minerName + " find a block"));
                    try {
                        User miner = Users.get(minerName);
//                        System.out.print(miner.publicKey);
                        chain.add(findBlock(miner.publicKey, minerName + " find a block"));
                    }catch (Exception e){
                        System.out.print(e);
                        System.out.print("User not find");
                    }
                    break;
                case 3 :
                    System.out.print("Input sender : ");
                    String senderName = reader.readLine();
                    System.out.print("Input recipient : ");
                    String recipientName = reader.readLine();
                    System.out.print("Input Amount : ");
                    double amount = Integer.parseInt( reader.readLine());

                    try {
                        User sender = Users.get(senderName);
                        User recipient = Users.get(recipientName);

                        Block previousBlock = chain.get(chain.size() - 1);
                        previousBlock.addTransaction(sender.sendFunds(recipient.publicKey, amount));

                        chain.set(chain.size() - 1, previousBlock);
                    }catch (Exception e){
                        System.out.print("User not find");
                    }

                    break;
                case 4 :
                    System.out.println("Alice Balance : " + alice.getBalance() + " public key : " + kCov(alice.publicKey));
                    System.out.println("Bob Balance   : " + bob.getBalance() + " public key : " + kCov(bob.publicKey));
                    System.out.println("Peter Balance : " + peter.getBalance() + " public key : " + kCov(peter.publicKey));
                    System.out.println("Ben Balance : " + ben.getBalance() + " public key : " + kCov(ben.publicKey));
                    break;
                case 5 :
                    printBlockChain();
                    break;
                case 6 :
                    break;
                case 7 :
                    alice.sendFunds(ben.publicKey, 20);
                    break;
                default :
                    System.out.println("Unknow operation, please input again");
            }
            System.out.println("");
        }
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



//        chain.add(createGenesisBlock());
//        printBlockChain();
//        for (int i = 0; i <= 20; i++) {
//            chain.add(findBlock(kCov(alice.getPublicKey()), "this " + i + " block"));
//        }
//        printBlockChain();


        // Testing
         System.out.println("Alice Amount : " + alice.getBalance());
         System.out.println("Bob Amount : " + bob.getBalance());
         System.out.println("Peter Amount : " + peter.getBalance());
//
        printBlockChain();

        // a user have a address, and keep mining block
        //        User user1 = new User()
    }

//    public static Block createGenesisBlock() {
//        return new Block(0, "\"Everything starts from here!\"", "0");
//    }

    // him
//    public static Transaction createTransaction(String txOutId, int txOutIndex, PrivateKey privateKey, String address, double amount) throws Exception{
//        Transaction.TxIn txIn = new Transaction.TxIn();
//        Transaction.TxOut txOut = new Transaction.TxOut();
//        Transaction transaction = new Transaction(txIn, txOut);
//
//        txIn.setTxOutId(txOutId);
//        txIn.setTxOutIndex(txOutIndex);
//        txIn.setSignature(transaction.genSignature(privateKey, txIn.getTxOutId() + txOutIndex));
//        //System.out.println("Line 64 Signature : " + txIn.getSignature());
//        txOut.setAddress(address);
//        //System.out.println("Line 66 Address : " + txOut.getAddress());
//        txOut.setAmount(amount);
//        //System.out.println("Line 68 Amount : " + txOut.getAmount());
//
//        return transaction;
//    }

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
        System.out.println("Genesis Block is created wish hash : " + hash);

        return new Block(kCov(coinbase.publicKey), index, hash, previousBlockHash, timestamp, blockData, DIFFICULTY_ADJUSTMENT_INTERVAL, 0);
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
    public static Block findBlock(PublicKey miner, String data) {
        int nonce = 0;
        while (true) {
            int difficulty = getDifficulty(chain);
//            System.out.println(difficulty);
            Block newBlock = generateNextBlock(kCov(miner), data, difficulty, nonce);
            if (hashMatchesDifficulty(newBlock.hash, difficulty)) {
                // create Coinbase Transaction for block
                newBlock.addTransaction(createCoinBaseTransaction(miner));;
                System.out.println("Mined A Block wish hash : " + newBlock.hash + " new block difficulty :" + newBlock.difficulty);
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
        String blockchainJson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create().toJson(chain);
        System.out.println(blockchainJson);
        double blockHeight = chain.size();
        System.out.println("Block Height : " + blockHeight);

        System.out.println("UTXOs as follow : ");
        for(Map.Entry<String, TxOut> item: UTXOs.entrySet()) {
            TxOut UTXO = item.getValue();
            System.out.println("id : " + UTXO.txOutId);
            System.out.println("Amount : " + UTXO.amount);
            System.out.println("Recipient public key : " + kCov(UTXO.reciepient) + "\n");
        }
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

    public static Transaction createCoinBaseTransaction(PublicKey miner){
        try {
            Transaction coinBasetransaction = new Transaction(coinbase.publicKey, miner, 50, null);
            coinBasetransaction.genSignature(coinbase.privateKey);
            coinBasetransaction.id = "0";
            coinBasetransaction.txOuts.add(new TxOut(coinBasetransaction.reciepient, coinBasetransaction.amount, coinBasetransaction.id));
            UTXOs.put(coinBasetransaction.txOuts.get(0).txOutId, coinBasetransaction.txOuts.get(0));

            return coinBasetransaction;
        }catch (Exception e){
            return null;
        }
    }

}
