package ru.nsu.kuklin.substring;

import java.lang.Iterable;
import java.lang.Readable;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Stub docs.
 */
public class App {
    /**
     * Returns an iterable over matches found in a file.
     * Each number is a start index of a match
     */
    public static Iterable<Long> findSubstrings(InputStream stream, String pattern) {
        return new Iterable<Long>() {
            public Iterator<Long> iterator() {
                try {
                    return new SubstringIterator(stream, pattern);
                } catch (Exception e) {
                    return null;
                }
            }
        };
    }

    private static class SubstringIterator implements Iterator<Long> {
        public SubstringIterator(InputStream stream, String pattern)
                throws UnsupportedEncodingException  {
            this.pattern = pattern;
            this.file = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

            // For a moment, there could be one more element
            this.current = new CircularQueue<>(pattern.length() + 1);
            this.nextFound = -1;
            this.currentPos = 0;

            this.base = 19;

            this.currentHash = 0;
            patternHash = 0;
            biggestPower = 1;
            for (int i = 0; i < pattern.length(); i++) {
                patternHash = patternHash * base + pattern.charAt(i);
                biggestPower *= base;
            }
        }

        public boolean hasNext() {
            if (nextFound >= 0) {
                return true;
            }
            do {
                Begin:
                try {
                    int next = file.read();
                    if (next == -1) {
                        return false;
                    }

                    currentHash = currentHash * base + next;
                    assert current.push(next) : "Failed to push element";
                    if (current.size() > pattern.length()) {
                        currentPos++;
                        currentHash -= current.pop() * biggestPower;
                    } else if (current.size() < pattern.length()) {
                        // Skip to next iteration without checking equality
                        // bc current is not ready
                        break Begin;
                    }
                } catch (IOException e) {
                    return false;
                }
            } while (currentHash != patternHash || !compare());
            assert !(compare() && currentHash != patternHash) 
                : "Different hashes for equal strings";

            nextFound = currentPos;
            return true;
        }

        public Long next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            var res = nextFound;
            nextFound = -1;
            return res;
        }

        private boolean compare() {
            assert current.size() == pattern.length() 
                : "Current is of different length than pattern";
            for (int i = 0; i < pattern.length(); i++) {
                if (current.getAt(i) != pattern.charAt(i)) {
                    return false;
                }
            }
            return true;
        }

        private int base;
        private int biggestPower;

        private int currentHash;
        private int patternHash;
        private long currentPos;
        // ArrayDeque's remove is linear which is not acceptable for this algorithm
        private CircularQueue<Integer> current;

        private long nextFound;

        private String pattern;
        private BufferedReader file;
    }
}
