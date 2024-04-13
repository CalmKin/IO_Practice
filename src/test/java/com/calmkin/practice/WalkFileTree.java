package com.calmkin.practice;


import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author CalmKin
 * @description 文件树遍历
 * @version 1.0
 * @date 2024/4/10 19:35
 */
public class WalkFileTree {
    public static void main(String[] args) throws IOException {
        AtomicInteger fileCount = new AtomicInteger();
        AtomicInteger dirCount = new AtomicInteger();

        Files.walkFileTree(Paths.get("C:\\Users\\86158\\Desktop\\图片\\壁纸"), new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

                dirCount.getAndIncrement();

                System.out.println("文件夹======》" + dir.toString());

                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                int fileId = fileCount.getAndIncrement();
                String fileName = file.toString();

                // 判断文件类型
                if(fileName.endsWith(".png"))
                {
                    int idx = fileName.indexOf('.');

                    String prefix = "电脑壁纸-" + fileId;

                    // 生成新文件名，替换后缀
                    String newFileName = prefix + ".png";

                    // 解析文件路径
                    Path newFilePath = file.getParent().resolve(newFileName);

                    // 重命名文件（替换已经存在的）
                    Files.move(file, newFilePath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Renamed: " + file + " to " + newFilePath);
                }


                return super.visitFile(file, attrs);
            }
        });

        System.out.println("文件夹数量:" + dirCount.get());
        System.out.println("文件数量:" + fileCount.get());

    }
}
