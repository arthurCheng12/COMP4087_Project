package com.company;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Vector;

public class utils {
    public static String SHA256(String data){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
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

    public static String getMerkleRoot(Vector<Transaction> transactions) {
        int count = transactions.size();

        Vector<String> previousTreeLayer = new Vector<String>();
        for(Transaction transaction : transactions) {
//            previousTreeLayer.add(transaction.getTransactionId());
        }
//        List<String> treeLayer = previousTreeLayer;
//
//        while(count > 1) {
//            treeLayer = new ArrayList<String>();
//            for(int i=1; i < previousTreeLayer.size(); i+=2) {
//                treeLayer.add(applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
//            }
//            count = treeLayer.size();
//            previousTreeLayer = treeLayer;
//        }
//
//        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
//        return merkleRoot;
        return "";
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

    public static boolean hashMatchesDifficulty(String hash, int difficulty) {
        String hashInBinary = hexToBinary(hash);
        String requiredPrefix = repeat("0", difficulty);
        return hashInBinary.startsWith(requiredPrefix);
    }
}
