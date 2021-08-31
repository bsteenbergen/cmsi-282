package main.huffman;

import java.util.*;
import java.io.ByteArrayOutputStream; // Optional

/**
 * Huffman instances provide reusable Huffman Encoding Maps for
 * compressing and decompressing text corpi with comparable
 * distributions of characters.
 */
public class Huffman {
    
    // -----------------------------------------------
    // Construction
    // -----------------------------------------------

    private HuffNode trieRoot;
    // TreeMap chosen here just to make debugging easier
    private TreeMap<Character, String> encodingMap;
    // Character that represents the end of a compressed transmission
    private static final char ETB_CHAR = 23;
    
    /**
     * Creates the Huffman Trie and Encoding Map using the character
     * distributions in the given text corpus
     * @param corpus A String representing a message / document corpus
     *        with distributions over characters that are implicitly used
     *        throughout the methods that follow. Note: this corpus ONLY
     *        establishes the Encoding Map; later compressed corpi may
     *        differ.
     */
    public Huffman (String corpus) {
        PriorityQueue<HuffNode> huffQueue = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> charEntry : getCharFreq(corpus).entrySet()) {
            HuffNode newLeaf = new HuffNode(charEntry.getKey(), charEntry.getValue());
            huffQueue.add(newLeaf);
        }
        
        HuffNode leastFreq1, leastFreq2, newParent;
        while (huffQueue.size() > 1) {
            leastFreq1 = huffQueue.poll();
            leastFreq2 = huffQueue.poll();
            
            newParent = new HuffNode(' ', leastFreq1.count + leastFreq2.count);
            newParent.left = leastFreq1;
            newParent.right = leastFreq2;
            huffQueue.add(newParent);
        }       
        this.trieRoot = huffQueue.poll();
        this.encodingMap = new TreeMap<Character, String>();
        encode(this.trieRoot, "");
    }
    
    /**
     * A recursive method to construct an encoding map from the given node.
     * 
     * @param node The node from which to start encoding
     * @param bit The bitString made from the encoding
     */
    private void encode(HuffNode node, String bit){
        if (node.isLeaf()) {
            encodingMap.put(node.character, bit);
        }
        else {
            encode(node.left, bit + "0");
            encode(node.right, bit + "1");
        }
    }
    
    /**
     * Creates a map which contains a given character from the corpus and the 
     * frequency of which it appears.
     * 
     * @param corpus The corpus from which the character and frequency are obtained
     * @return A TreeMap with the characters and frequencies at which they occur
     */
    private TreeMap<Character, Integer> getCharFreq(String corpus) {
        TreeMap<Character, Integer> charFreq = new TreeMap<>();
        charFreq.put(ETB_CHAR, 1);
        for (char c : corpus.toCharArray()) {
            if (charFreq.containsKey(c)) {
                charFreq.put(c, charFreq.get(c) + 1);
            }
            else {
                charFreq.put(c, 1);
            }
        }
        return charFreq;
    }
    
    // -----------------------------------------------
    // Compression
    // -----------------------------------------------
    
    /**
     * Compresses the given String message / text corpus into its Huffman coded
     * bitstring, as represented by an array of bytes. Uses the encodingMap
     * field generated during construction for this purpose.
     * @param message String representing the corpus to compress.
     * @return {@code byte[]} representing the compressed corpus with the
     *         Huffman coded bytecode. Formatted as:
     *         (1) the bitstring containing the message itself, (2) possible
     *         0-padding on the final byte.
     */
    public byte[] compress (String message) {
        message += ETB_CHAR;
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        String byteString = new String();
        
        for (char c : message.toCharArray()) {
            if (encodingMap.containsKey(c)) {
                byteString += encodingMap.get(c);
            }
        }

        for (int i = 0; i < byteString.length(); i += 8) {
            String substring = byteString.substring(i);
            if (i + 8 > byteString.length()) {
                substring = byteString.substring(i, byteString.length());
            }
            else {
                substring = byteString.substring(i, i + 8);
            }
            
            if (substring.length() == 8) {
                byteOut.write(Integer.parseInt(substring, 2)); 
            }
            else {
                byte parsedByte = Byte.parseByte(substring, 2);
                parsedByte = (byte) (parsedByte << (8 - substring.length()));
                byteOut.write(parsedByte);
            }
        }
        return byteOut.toByteArray();
    }
    
    
    // -----------------------------------------------
    // Decompression
    // -----------------------------------------------
    
    /**
     * Decompresses the given compressed array of bytes into their original,
     * String representation. Uses the trieRoot field (the Huffman Trie) that
     * generated the compressed message during decoding.
     * @param compressedMsg {@code byte[]} representing the compressed corpus with the
     *        Huffman coded bytecode. Formatted as:
     *        (1) the bitstring containing the message itself, (2) possible
     *        0-padding on the final byte.
     * @return Decompressed String representation of the compressed bytecode message.
     */
    public String decompress (byte[] compressedMsg) {
        HuffNode current = this.trieRoot;
        String bitString = byteToString(compressedMsg).replace(' ', '0');
        String decompMsg = new String();
        
        for (int i = 0; i < bitString.length(); i++) {
            if (bitString.charAt(i) == '0') {
                current = current.left;
            }
            else {
                current = current.right;
            }
            if (current.isLeaf()) {
                if (current.character == ETB_CHAR) {
                    return decompMsg;
                }
                decompMsg += current.character;
                current = this.trieRoot;
            }
        }      
        return decompMsg;
    }
    
    /**
     * Converts a given array of bytes into a string for ease of traversing.
     * 
     * @param compressedMsg The byte array to convert
     * @return The string version of the given byte arrays
     */
    private String byteToString (byte[] compressedMsg) {
        String binMsg = new String();
        for (int i = 0; i < compressedMsg.length; i++) {
            int byteToInt = Byte.toUnsignedInt(compressedMsg[i]);
            String compressedStr = Integer.toBinaryString(byteToInt);
            binMsg += String.format("%8s", compressedStr);
        }
        return binMsg;
    }
    
    
    // -----------------------------------------------
    // Huffman Trie
    // -----------------------------------------------
    
    /**
     * Huffman Trie Node class used in construction of the Huffman Trie.
     * Each node is a binary (having at most a left and right child), contains
     * a character field that it represents, and a count field that holds the 
     * number of times the node's character (or those in its subtrees) appear 
     * in the corpus.
     */
    private static class HuffNode implements Comparable<HuffNode> {
        
        HuffNode left, right;
        char character;
        int count;
        
        HuffNode (char character, int count) {
            this.count = count;
            this.character = character;
        }
        
        public boolean isLeaf () {
            return this.left == null && this.right == null;
        }
        
        public int compareTo (HuffNode other) {
            if (this.count != other.count) {
                return this.count - other.count;
            }
            else {
                return Character.compare(this.character, other.character);
            }
        }
        
    }

}
