package com.company;

import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

public class Main {

    public static void main(String[] args) {
	// write your code here
        System.out.println("Program Start!");
        Vector<Block> blockChain = new Vector<Block>();

        Block block0 = new Block(0, new Date().getTime() / 1000, "", "This is first block");
        blockChain.add(block0);

        Block block1 = generateNextBlock("Arthur -> Ben, 5, Cola", blockChain);
        blockChain.add(block1);

        Block block2 = generateNextBlock("Ben -> Cecilia, 2, Pen", blockChain);
        blockChain.add(block2);

        for (int i = 0; i < blockChain.size(); i++) {
            Block block  = blockChain.get(i);
            System.out.println("index: " + block.index + ", timestamp: " + block.timestamp + ", previousHash: " + block.previousHash + ", data: " + block.data);
        }
    }

    public static Block generateNextBlock(String blockData, Vector<Block> blockChain) {
        Block previousBlock = getLatestBlock(blockChain);
        int nextIndex = previousBlock.index + 1;
        double nextTimestamp = new Date().getTime() / 1000;
        return new Block(nextIndex, nextTimestamp, previousBlock.hash, blockData);
    }

    public static Block getLatestBlock(Vector<Block> blockChain) {
        return blockChain.lastElement();
    }

}
