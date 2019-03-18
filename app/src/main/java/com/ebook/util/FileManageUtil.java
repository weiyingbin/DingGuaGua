package com.ebook.util;

import android.util.Log;

import com.ebook.R;
import com.ebook.model.Const;
import com.ebook.model.Directory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManageUtil {
    private List<Directory> directories;
    private List<File> fileList = new ArrayList<>();

    /**
     * 递归遍历出目录下面所有文件
     *
     * @param pathName 需要遍历的目录，必须以"/"开始和结束
     * @return
     * @throws IOException
     */
    public List<Directory> list(String pathName) {
        directories = new ArrayList<>();
        if (pathName.startsWith("/") && pathName.endsWith("/")) {
            final String directory = pathName;
            //更换目录到当前目录
            File[] files = new File(directory).listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    Directory directory1 = new Directory();
                    if (file.isFile() && isOfficeFile(file) || isTxt(file)) {
                        directory1.setName(file.getName());
                        directory1.setPath(directory + file.getName());
                        directory1.setType(1);
                    } else if (file.isDirectory()) {
                        directory1.setName(file.getName());
                        directory1.setPath(directory + file.getName() + "/");
                        directory1.setType(2);
                    }
                    directories.add(directory1);
                }
            }
        }
        return directories;
    }

    /**
     * 扫描office文件
     *
     * @param path 扫描路径
     */
    public List<File> scanOfficeFile(String path) {
        if (path.startsWith("/") && path.endsWith("/")) {
            final String directory = path;
            File[] files = new File(directory).listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file.isFile() && isOfficeFile(file) || isTxt(file)) {//判断是office文件
                        fileList.add(file);
                    } else if (file.isDirectory()) {
                        scanOfficeFile(file + "/");
                    }
                    Log.d("file", "scanOfficeFile: " + file.getPath());
                }
            }
        }
        return fileList;
    }

    //获取上一级文件夹路径，没有则返回空
    public String getParentDirectory(String path) {
        if (path != null && !path.equals("")) {
            File file = new File(path);
            String fileParent = file.getParent();
            return fileParent;
        } else {
            return null;
        }
    }

    /**
     * 是否是office文件
     *
     * @return
     */
    public boolean isOfficeFile(File file) {
        if (file.getName().endsWith(".pdf")) {
            return true;
        }
        if (file.getName().endsWith(".doc")) {
            return true;
        }
        if (file.getName().endsWith(".docx")) {
            return true;
        }
        if (file.getName().endsWith(".ppt")) {
            return true;
        }
        if (file.getName().endsWith(".pptx")) {
            return true;
        }
        if (file.getName().endsWith(".xls")) {
            return true;
        }
        if (file.getName().endsWith(".xlsx")) {
            return true;
        }
        return false;
    }

    /**
     * 是否是txt文件
     *
     * @param file
     * @return
     */
    public boolean isTxt(File file) {
        if (file.getName().endsWith(".txt")) {
            return true;
        }
        return false;
    }
}
