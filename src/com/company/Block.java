package com.company;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.sql.Timestamp;
import java.security.MessageDigest;

public class Block {
    public int index;
    public double timestamp;
    public String hash;
    public String previousHash;
    public String data;

    public Block() {}

    public Block(int index, double timestamp, String previousHash, String data) {
        this.index = index;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.data = data;
        this.hash = hash(this.index, this.timestamp, this.previousHash, this.data);
    }

    public String hash(int index, double timestamp, String previousHash, String data) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String blockInformation = (index + previousHash + timestamp  + data);
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
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}