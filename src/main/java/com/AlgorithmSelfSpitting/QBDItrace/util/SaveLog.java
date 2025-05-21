package com.AlgorithmSelfSpitting.QBDItrace.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class SaveLog {
    /**
     * 按行存储传入的一行内容到指定文件，多次调用可实现日志追加
     *
     * @param lineContent 要存储的一行内容
     * @param filePath 文件路径
     */
    public static void storeContentByLine(String lineContent, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(lineContent);
            writer.newLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
