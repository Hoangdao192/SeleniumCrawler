package com.hoangdao.seleniumcrawler.demo.util;

import java.io.*;

public class FileHelper {

    public static File save(byte[] bytes, File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(
                file
        );
        fileOutputStream.write(bytes);
        return file;
    }

    public static File save(String content, File file) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(
                new FileWriter(file)
        );
        bufferedWriter.write(content);
        bufferedWriter.close();
        return file;
    }

}
