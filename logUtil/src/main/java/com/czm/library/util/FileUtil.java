package com.czm.library.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by czm.
 */
public class FileUtil {

    private static String TAG = "FileUtil";
    private static final String CRASH = "Crash";
    private static final String INFO = "Info";

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return 删除成功返回true，否则返回false
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            // 递归删除目录中的子目录下
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }


    /**
     * 读取File中的内容
     *
     * @param file 请务必保证file文件已经存在
     * @return file中的内容
     */
    public static String getText(File file) {
        if (!file.exists()) {
            return null;
        }
        StringBuilder text = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }

        } catch (IOException e) {
            android.util.Log.e(TAG, e.toString());
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return text.toString();
    }

    /**
     * 遍历获取Log文件夹下的所有crash文件
     *
     * @param logdir 从哪个文件夹下找起
     * @return 返回crash文件列表
     */
    public static ArrayList<File> getCrashList(File logdir) {
        ArrayList<File> crashFileList = new ArrayList<>();
        findFiles(logdir.getAbsolutePath(), crashFileList, CRASH);
        return crashFileList;
    }

    public static ArrayList<File> getLogInfoList(File logdir){
        ArrayList<File> crashFileList = new ArrayList<>();
        findFiles(logdir.getAbsolutePath(), crashFileList,INFO);
        return crashFileList;
    }


    /**
     * 将指定文件夹中满足要求的文件存储到list集合中
     *
     * @param f
     * @param list
     */

    /**
     * 递归查找文件
     *
     * @param baseDirName 查找的文件夹路径
     * @param fileList    查找到的文件集合
     */
    public static void findFiles(String baseDirName, List<File> fileList, String filter) {

        if (filter==null) {
            return;
        }

        File baseDir = new File(baseDirName);       // 创建一个File对象
        if (!baseDir.exists() || !baseDir.isDirectory()) {  // 判断目录是否存在
            Logs.e(TAG, "文件查找失败：" + baseDirName + "不是一个目录！");
        }
        String tempName;
        //判断目录是否存在
        File tempFile;
        File[] files = baseDir.listFiles();
        for (File file : files) {
            tempFile = file;
            if (tempFile.isDirectory()) {
                findFiles(tempFile.getAbsolutePath(), fileList, filter);
            } else if (tempFile.isFile()) {
                tempName = tempFile.getName();

                //判断上传日志等级
                if (StringUtils.isEquals(filter,CRASH)) {
                    if (tempName.contains(filter)) {
                        // 匹配成功，将文件名添加到结果集
                        fileList.add(tempFile.getAbsoluteFile());
                    }
                }else if (StringUtils.isEquals(filter,INFO)){
                    fileList.add(tempFile.getAbsoluteFile());
                }


            }
        }
    }



    /**
     * 获取文件夹的大小
     *
     * @param directory 需要测量大小的文件夹
     * @return 返回文件夹大小，单位byte
     */
    public static long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }

    public static File createFile(File zipdir, File zipfile) {
        if (!zipdir.exists()) {
            boolean result = zipdir.mkdirs();
            Logs.d("TAG", "zipdir.mkdirs() = " + result);
        }
        if (!zipfile.exists()) {
            try {
                boolean result = zipfile.createNewFile();
                Logs.d("TAG", "zipdir.createNewFile() = " + result);
            } catch (IOException e) {
                e.printStackTrace();
                android.util.Log.e("TAG", e.getMessage());
            }
        }
        return zipfile;
    }


    public static boolean deleteOldestFile(File directory)
    {

        Logs.e("执行了删除旧文件操作");
        File[] files = directory.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2)
            {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }});

        if (FileUtil.deleteDir(files[0])) {
            Logs.e("删除旧文件成功");
            return true;
        }else {
            Logs.e("删除旧文件失败");
            return false;

        }
    }
}
