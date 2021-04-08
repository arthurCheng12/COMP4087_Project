package com.company;

import sun.misc.BASE64Encoder;

import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

public class Main {
    static final int BLOCK_GENERATION_INTERVAL = 30;
    static final int DIFFICULTY_ADJUSTMENT_INTERVAL = 8;
    static final int COINBASE_AMOUNT = 50;
    static Vector<Block> chain = new Vector<Block>();

    public static void main(String[] args) throws Exception{
        // write your code here
        System.out.println("Block Chain Start!");

        Transaction transaction;
        String blockData;

        transaction = createTransaction("127.0.0.1:3000", 50, "127.0.0.1:3000", 5);
        blockData = "This is the gene block";
        Block block = new Block(0, new Date().getTime() / 1000, "th1515f1r5t810ckh45h", blockData, transaction, DIFFICULTY_ADJUSTMENT_INTERVAL);
        chain.add(block);

        transaction = createTransaction("127.0.0.1:3000", 45, "127.0.0.1:3001", 5);
        blockData = "Gun";
        block = findBlock(blockData, transaction, chain, DIFFICULTY_ADJUSTMENT_INTERVAL, BLOCK_GENERATION_INTERVAL);
        if (isValidNewBlock(block, chain.lastElement())) {
            MintCoinTransaction();
            chain.add(block);
        }

        transaction = createTransaction("127.0.0.1:3000", 40,"127.0.0.1:3002", 5);
        blockData = "420";
        block = findBlock(blockData, transaction, chain, DIFFICULTY_ADJUSTMENT_INTERVAL, BLOCK_GENERATION_INTERVAL);
        if (isValidNewBlock(block, chain.lastElement())) {
            MintCoinTransaction();
            chain.add(block);
        }

        printBlockChain(chain);

        // a user have a address, and keep mining block
//        User user1 = new User()
    }

    public static Transaction createTransaction(String txOutId, int txOutIndex, String address, double amount) throws Exception{
        Transaction.TxOut txOut = new Transaction.TxOut(address, amount);
        String data = address + amount;
        KeyPair keyPair = RSASignUtils.generateKeyPair();
        PublicKey pubKey = keyPair.getPublic();
        PrivateKey priKey = keyPair.getPrivate();
        byte[] signInfo = RSASignUtils.sign(data.getBytes(), priKey);
        String signature = new BASE64Encoder().encode(signInfo);;
        Transaction.TxIn txIn = new Transaction.TxIn(txOutId, txOutIndex, signature);

        boolean verify = RSASignUtils.verify(data.getBytes(), signInfo, pubKey);

        if (verify) {
            System.out.println("Verify Status " + verify);
        } else {
            System.out.println("Not verified ");
        }
        return new Transaction(txIn, txOut);

    }

    public static int getAdjustedDifficulty(Block latestBlock, Vector<Block> chain) {
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

    public static int getDifficulty(Vector<Block> chain) {
        Block lastBlock = chain.lastElement();
        if (lastBlock.index % DIFFICULTY_ADJUSTMENT_INTERVAL == 0 && lastBlock.index != 0) {
            return getAdjustedDifficulty(lastBlock, chain);
        } else {
            return lastBlock.difficulty;
        }
    }

    public static Block findBlock(String blockData, Transaction transaction, Vector<Block> chain, int DIFFICULTY_ADJUSTMENT_INTERVAL, int BLOCK_GENERATION_INTERVAL) {
        while (true) {
            int difficulty = getDifficulty(chain);
            Block newBlock = generateNextBlock(blockData, transaction, chain, difficulty);
            if (hashMatchesDifficulty(newBlock.hash, difficulty)) {
                return newBlock;
            }
        }
    }


    // Ben
    public static Block generateNextBlock(String blockData, String miner){
        Block previousBlock = chain.lastElement();
        int nextIndex = previousBlock.index + 1;
        double nextTimestamp = new Date().getTime() / 1000;
        String nextHash = HashUtils.SHA256((nextIndex + previousBlock.hash + nextTimestamp + blockData));
//        Block newBlock = new Block(nextIndex, nextTimestamp, previousBlock.hash, blockData, transaction, difficulty);
        Block newBlock = new Block(miner, nextIndex, nextHash, previousBlock.hash, nextTimestamp, blockData);

        return newBlock;
    }

    // Ben
    public static Block miningBlock(int index, String previousHash, double timestamp, String data, int difficulty) {
        int nonce = 0;
        while (true) {
//            final String hash = calculateHash(index, previousHash, timestamp, data, difficulty, nonce);
            final String hash = HashUtils.SHA256((index + previousHash + timestamp + data + nonce));
//            Block newBlock = generateNextBlock(blockData, transaction, chain, difficulty);
            if (hashMatchesDifficulty(hash, difficulty)) {
                int newBlockDifficulty = getDifficulty(chain);
                Block newBlock = new Block(index, hash, previousHash, timestamp, data, newBlockDifficulty, nonce);
                return newBlock;
            }
            nonce++;
        }
    }

    public static Block generateNextBlock(String blockData, Transaction transaction, Vector<Block> chain, int difficulty) {
        Block previousBlock = chain.lastElement();
        int nextIndex = previousBlock.index + 1;
        double nextTimestamp = new Date().getTime() / 1000;
        return new Block(nextIndex, nextTimestamp, previousBlock.hash, blockData, transaction, difficulty);
    }

    public static boolean isValidNewBlock(Block newBlock, Block previousBlock) {
        if (previousBlock.index + 1 != newBlock.index) {
            System.out.println("invalid index");
            return false;
        } else if (previousBlock.hash != newBlock.previousHash) {
            System.out.println("invalid previoushash");
            return false;
        } else if (!hashForBlock(newBlock).equals(newBlock.hash)) {
            System.out.println("invalid hash");
            return false;
        } else return true;
    }

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

    public static boolean hashMatchesDifficulty(String hash, int difficulty) {
        String hashInBinary = hexToBinary(hash);
        String requiredPrefix = repeat("0", difficulty);
        return hashInBinary.startsWith(requiredPrefix);
    }

    public static String hexToBinary(String hex) {
        String binary = "";
        hex = hex.toUpperCase();

        // initializing the HashMap class
        HashMap<Character, String> hashMap = new HashMap<Character, String>();

        // storing the key value pairs
        hashMap.put('0', "0000");
        hashMap.put('1', "0001");
        hashMap.put('2', "0010");
        hashMap.put('3', "0011");
        hashMap.put('4', "0100");
        hashMap.put('5', "0101");
        hashMap.put('6', "0110");
        hashMap.put('7', "0111");
        hashMap.put('8', "1000");
        hashMap.put('9', "1001");
        hashMap.put('A', "1010");
        hashMap.put('B', "1011");
        hashMap.put('C', "1100");
        hashMap.put('D', "1101");
        hashMap.put('E', "1110");
        hashMap.put('F', "1111");

        int i;
        char ch;
        for (i = 0; i < hex.length(); i++) {
            ch = hex.charAt(i);
            if (hashMap.containsKey(ch))
                binary += hashMap.get(ch);
            else {
                binary = "Invalid Hexadecimal String";
                return binary;
            }
        }
        return binary;
    }

    public static String repeat(String str, int difficulty) {
        String result = "";
        for (int i = 0; i < difficulty; i++) {
            result += str;
        }
        return result;
    }

    public static void printBlockChain(Vector<Block> chain) {
        for (int i = 0; i < chain.size(); i++) {
            Block block = chain.get(i);
            System.out.println("index: " + block.index);
            System.out.println("timestamp: " + block.timestamp);
            System.out.println("data: " + block.data);
//            System.out.println("transactionID: " + block.transaction.id);
            System.out.println("previousHash: " + block.previousHash);
            System.out.println("hash: " + block.hash);
            System.out.println("------------------------------------------------------------------------");
        }
    }

    public static void MintCoinTransaction() {
        // Mint Coin Transaction
        Transaction.TxOut txout = new Transaction.TxOut("127.0.0.1:3001", 50);
    }

}
