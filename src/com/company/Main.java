package com.company;

import java.util.Date;

public class Main {

    public static void main(String[] args) {
	// write your code here
        System.out.println("Hello World!");
    }

    public Block generateNextBlock(String blockData) {
        Block previousBlock = getLatestBlock();
        int nextIndex = previousBlock.index + 1;
        double nextTimestamp = new Date().getTime() / 1000;
        return new Block(nextIndex, nextTimestamp, previousBlock.hash, blockData);
    }

    public Block getLatestBlock() {
        return new Block();
    }

}
