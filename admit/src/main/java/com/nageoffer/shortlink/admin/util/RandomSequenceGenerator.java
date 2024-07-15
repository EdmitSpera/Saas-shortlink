package com.nageoffer.shortlink.admin.util;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

/**
 * 生成六位随机序列工具类
 */
public final class RandomSequenceGenerator {

    // 可用的字符和数字
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SEQUENCE_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    /**
     * 生成一个包含数字和字符的六位随机序列
     *
     * @return 随机序列
     */
    public static String generateRandomSequence() {
        StringBuilder sequence = new StringBuilder(SEQUENCE_LENGTH);
        for (int i = 0; i < SEQUENCE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sequence.append(CHARACTERS.charAt(index));
        }
        return sequence.toString();
    }

    /**
     * 测试生成指定数量的随机序列并计算重复率
     * @param count 生成的序列数量
     */
    public static void testRandomSequences(int count) {
        Set<String> sequences = new HashSet<>();
        int duplicates = 0;

        for (int i = 0; i < count; i++) {
            String sequence = generateRandomSequence();
            System.out.println(sequence);
            if (!sequences.add(sequence)) {
                duplicates++;
            }
        }

        double duplicateRate = (double) duplicates / count * 100;
        System.out.println("生成的序列总数: " + count);
        System.out.println("重复的序列数量: " + duplicates);
        System.out.println("重复率: " + String.format("%.2f", duplicateRate) + "%");
    }


    public static void main(String[] args) {
        testRandomSequences(10000000);
    }
}
