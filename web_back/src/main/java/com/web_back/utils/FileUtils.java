package com.web_back.utils;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class FileUtils {
    public static String getUUID(){
        return UUID.randomUUID().toString();
    }

    public static String getSuffix(String fileOriginName){
        return fileOriginName.substring(fileOriginName.lastIndexOf("."));
    }
    
    public static String getFileName(String fileOriginName){
        return getUUID() + getSuffix(fileOriginName);
    }

    public static String uploadFile(MultipartFile file, String path, List<String> typeArr){
        String fileOriginName=file.getOriginalFilename();
        if(!typeArr.contains(getSuffix(fileOriginName))){
            System.out.println("文件格式不正确");
            return null;
        }

        String fileName=getFileName(fileOriginName);
        File targetFile = new File(path,fileName);

        if(!targetFile.getParentFile().exists()){
            targetFile.getParentFile().mkdir();
        }

        try {
            file.transferTo(targetFile);
            return fileName;
        } catch (Exception e) {	            
            e.printStackTrace();
            return null;
        }
    }
}