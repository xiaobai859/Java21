package com.pinyougou.manager.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pinyougou.utils.FastDFSClient;

import entity.Result;

@RestController
public class UploadController {

  @Value("${FILE_SERVER_URL}")
  private String FILE_SERVER_URL;

  @RequestMapping("/upload")
  public Result upload(MultipartFile file) {
    String filename = file.getOriginalFilename(); // 获取文件全名称
    String extName = filename.substring(filename.lastIndexOf(".") + 1); // 获取文件扩展名

    try {
      FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
      String path = fastDFSClient.uploadFile(file.getBytes(), extName);
      String url = FILE_SERVER_URL + path;
      return new Result(true, url);
    } catch (Exception e) {
      e.printStackTrace();
      return new Result(false, "上传失败");
    }


  }

}
