package com.calmkin.NIO.practice;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author CalmKin
 * @description 多级目录拷贝功能实现
 * @version 1.0
 * @date 2024/4/10 20:18
 */
public class FilesCopy {


    public static void main(String[] args) throws IOException {
        String source = "D:\\origin";
        String target = "D:\\target";

        Files.walk(Paths.get(source)).forEach(path -> {
            // 把原文件的路径前缀替换掉
            String targetName = path.toString().replace(source,target);
            // 如果是目录，走创建目录流程
            try {
                if(Files.isDirectory(path))
                {
                    Files.createDirectory(Paths.get(targetName));
                }
                // 如果是文件，走拷贝
                else if(Files.isRegularFile(path)){
                    Files.copy(path, Paths.get(targetName));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        });


    }
}
