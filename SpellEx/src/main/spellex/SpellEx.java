package main.spellex;

import java.util.*;

public class SpellEx {
    
    // Note: Not quite as space-conscious as a Bloom Filter,
    // nor a Trie, but since those aren't in the JCF, this map 
    // will get the job done for simplicity of the assignment
    private Map<String, Integer> dict;
    
    // For your convenience, you might need this array of the
    // alphabet's letters for a method
    private static final char[] LETTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    /**
     * Constructs a new SpellEx spelling corrector from a given
     * "dictionary" of words mapped to their frequencies found
     * in some corpus (with the higher counts being the more
     * prevalent, and thus, the more likely to be suggested)
     * @param words The map of words to their frequencies
     */
    public SpellEx(Map<String, Integer> words) {
        dict = new HashMap<>(words);
    }
    
    /**
     * Returns the edit distance between the two input Strings
     * s0 and s1 based on the minimal number of insertions, deletions,
     * replacements, and transpositions required to transform s0
     * into s1
     * @param s0 A "start" String
     * @param s1 A "destination" String
     * @return The minimal edit distance between s0 and s1
     */
    public static int editDistance (String s0, String s1) {
        if (s0.equals(s1)) {
            return 0;
        }
        int row = s0.length();
        int col = s1.length();
        
        int memoTable[][] = new int[row + 1][col + 1];
        
        for (int i = 0; i < row + 1; i++) {
            memoTable[i][0] = i;
        }
        for (int i = 0; i < col + 1; i++) {
            memoTable[0][i] = i;
        }

        for (int r = 1; r < row + 1; r++) {
            for (int c = 1; c < col + 1; c++) {
                ArrayList<Integer> edits = new ArrayList<>();
                edits.add(memoTable[r][c - 1] + 1);
                edits.add(memoTable[r - 1][c] + 1);
                edits.add(memoTable[r - 1][c - 1]);
                
                if (s0.charAt(r - 1) != s1.charAt(c - 1)) {
                    edits.remove(2);
                    edits.add(memoTable[r - 1][c - 1] + 1);
                }

                if (r >= 2 && c >= 2) {
                    if (s0.charAt(r - 1) == s1.charAt(c - 2) && s1.charAt(c - 1) == s0.charAt(r - 2)) {
                        edits.add(memoTable[r - 2][c - 2] + 1);
                    }
                }
                Collections.sort(edits);
                memoTable[r][c] = edits.get(0);
            }
        }
        return memoTable[row][col];
    }
    
    /**
     * Returns the n closest words in the dictionary to the given word,
     * where "closest" is defined by:
     * <ul>
     *   <li>Minimal edit distance (with ties broken by:)</li>
     *   <li>Largest count / frequency in the dictionary (with ties broken by:)</li>
     *   <li>Ascending alphabetic order</li>
     * </ul>
     * @param word The word we are comparing against the closest in the dictionary
     * @param n The number of least-distant suggestions desired
     * @return A set of up to n suggestions closest to the given word
     */
    public Set<String> getNLeastDistant (String word, int n) {
        PriorityQueue<dictWord> leastDist = new PriorityQueue<>();
        Set<String> leastDistSet = new HashSet<>();
        
        for (Map.Entry<String, Integer> dictEntry : dict.entrySet()) {
            int editDist = editDistance(word, dictEntry.getKey());
            dictWord current = new dictWord(dictEntry.getKey(), editDist, dictEntry.getValue());
            leastDist.add(current);
        }
        while (n > 0) {
            if (leastDist.size() > 0) {
                leastDistSet.add(leastDist.poll().word);
            }
            n--;
        }
        return leastDistSet;
    }
    
    /**
     * Returns the set of n most frequent words in the dictionary to occur with
     * edit distance distMax or less compared to the given word. Ties in
     * max frequency are broken with ascending alphabetic order.
     * @param word The word to compare to those in the dictionary
     * @param n The number of suggested words to return
     * @param distMax The maximum edit distance (inclusive) that suggested / returned 
     * words from the dictionary can stray from the given word
     * @return The set of n suggested words from the dictionary with edit distance
     * distMax or less that have the highest frequency.
     */
    public Set<String> getNBestUnderDistance (String word, int n, int distMax) {
        PriorityQueue<dictWord> mostFreq = new PriorityQueue<>();
        Set<String> oldWords = new HashSet<>();
        Set<String> allWords = new HashSet<>();
        Set<String> newWords = new HashSet<>();
        Set<String> nMostFreq = new HashSet<>();
        
        getWordSet(word, oldWords);
        if (distMax == 2) {
            for (String newWord : oldWords) {
                getWordSet(newWord, newWords);
            }
        }
        allWords.addAll(oldWords);
        allWords.addAll(newWords);
        for (String checkWord : allWords) {
            if (dict.containsKey(checkWord)) {
                dictWord isWord = new dictWord(checkWord, 0, dict.get(checkWord));
                mostFreq.add(isWord);
            }
        }
        while (n > 0) {
            if (mostFreq.size() > 0) {
                nMostFreq.add(mostFreq.poll().word);
            }
            n--;
        }
        return nMostFreq;
    }
    
    /**
     * dictWord private static nested class that is used in getNLeastDistant to 
     * construct the set of closest words to the one misspelled.
     */
    private static class dictWord implements Comparable<dictWord> {
        
        String word;
        int editDist;
        int frequency;
        
        /**
         * Constructs a new dictWord to be used in the priority queue.
         * 
         * @param word The string from the dictionary to be checked.
         * @param editDist The distance from the dictionary word to the misspelled one.
         * @param frequency The amount of times the word appears in the dictionary.
         */
        dictWord (String word, int editDist, int frequency) {
            this.word = word;
            this.editDist = editDist;
            this.frequency = frequency;
        }
        
        /**
         * Override method that returns a negative number, 0, or a positive number depending
         * on which dictWord has higher priority in the queue. Ties are broken first based off
         * frequency and next off of ascending alphabetical order.
         * 
         * @return A negative number, 0, or a positive number.
         */
        @Override
        public int compareTo (dictWord other) {
            if (this.editDist != other.editDist) {
                return this.editDist - other.editDist;
            }
            else if (this.frequency != other.frequency) {
                return other.frequency - this.frequency;
            }
            else {
                return this.word.compareTo(other.word);
            }
        }       
    }   
    
    /**
     * A method to calculate the set of words made from insertion, deletion
     * replacement and transposition.
     * 
     * @param word The word to generate the set from
     * @param wordSet The set of possible words
     * @return The set of possible words generated from the given
     */
    private Set<String> getWordSet (String word, Set<String> wordSet) {
        for (int i = 0; i <= word.length(); i++) {
            String front = word.substring(0, i);
            String back = word.substring(i);
            for (char letter : LETTERS) {
                wordSet.add(front + letter + back);
            }   
        }
        for (int i = 0; i < word.length(); i++) {
            for (char letter : LETTERS) {
                wordSet.add(word.replace(word.charAt(i), letter));
            } 
            if (i > 0) {
                char[] wordArr = word.toCharArray();
                char temp = wordArr[i];
                wordArr[i] = wordArr[i - 1];
                wordArr[i - 1] = temp;
                wordSet.add(wordArr.toString());
            }
            wordSet.add(word.substring(0, i) + word.substring(i + 1));
        }
        return wordSet;
    }
}

