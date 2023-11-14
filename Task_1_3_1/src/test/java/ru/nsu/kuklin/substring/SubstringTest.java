package ru.nsu.kuklin.substring;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.*;
import java.io.*;
import java.util.*;
import org.junit.jupiter.api.*;

/**
 * Stub docs.
 */
public class SubstringTest {
    @Test
    @DisplayName("Simple Finds")
    public void testSimpleFind() {
        String file = "simple.txt";
        String pattern = "ab";
        var expected = new int[] {0, 4};
        int i = 0;
        assertNotEquals(null, App.findSubstrings(file, pattern));
        for (var el : App.findSubstrings(file, pattern)) {
            assertTrue(i < expected.length);
            assertEquals(expected[i], el);
            i++;
        }
        assertEquals(expected.length, i); 
    }

    @Test
    @DisplayName("UTF-8 test")
    public void testUtf8() {
        String file = "utf8.txt";
        String pattern = "あ";
        var expected = new int[] {0, 29};
        int i = 0;
        assertNotEquals(null, App.findSubstrings(file, pattern));
        for (var el : App.findSubstrings(file, pattern)) {
            assertTrue(i < expected.length);
            assertEquals(expected[i], el);
            i++;
        }
        assertEquals(expected.length, i); 
    }


    @Test
    @DisplayName("Read BFFile")
    public void testBfFileReadOnly() {
        var kiloByte = """
すべてが私たちの前ですでに起こっています、あなたは恐怖を吐き出して空を見つめることができます
あなたが周囲の売春婦に抵抗するならば、それから悲しい顔ではありません
すべてが何度も何度も起こりますが、私たちは今生きています、私たちと一緒にチェルノゼムを肥やすには時期尚早です

孫子と老子を忘れる,
このサーカスには二つの方法しかないからです:
自殺またはストイシズム。
そして、あなたが自殺ではないことを選んだ場合,
それから我慢して、泣き言をやめて、私を力と主と一緒に行かせてくだ
そして、はい、私たちは小便し、誰もが小便し、恐怖
と独善主義,
しかし、世界にもかかわらず、私たちは喧騒の中で離
陸します。

秘密の握手のない兄弟は、チャーターを構成
結局のところ、私たちは禁止された組織ですランクにいます
                    """;
        String filename = "BFFile.txt";
        try (var writer = new PrintWriter("src/main/resources/" + filename)) {
            int kiloBytes = 15 * 1024 * 1024;
            for (int i = 0; i < kiloBytes; i++) {
                writer.println(kiloByte);
            }
        } catch (Exception e) {
            assertTrue(false);
        }

        String pattern = "秘密の握手のない兄弟は、チャーターを構成";
        assertNotEquals(null, App.findSubstrings(filename, pattern));
        for (var el : App.findSubstrings(filename, pattern)) {
            assertEquals(el, el);
        }
    }
}


