package com.pinyougou.manager.controller;

import com.pinyougou.entity.Result;
import com.pinyougou.utils.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: wangyilong
 * @Date: 2019/11/13 0013
 * @Description:
 */
@RestController
public class UploadController {

    @Value("${FILE_SERVICE_URL}")
    private String FILE_SERVICE_URL;

    @RequestMapping("/upload")
    public Result uploadFile(MultipartFile file) throws Exception {
        try {
            //1 获取文件名
            String fileName = file.getOriginalFilename();
            //2 获取文件的后缀
            String extName = fileName.substring(fileName.lastIndexOf("."));

            //3 调用文件上传组件
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            // 3.1 上传文件  返回文件的url地址
            String url = fastDFSClient.uploadFile(file.getBytes(), extName);
            //3.2 拼接完整的服务器的url地址
            url = FILE_SERVICE_URL+url;
            //4 返回url到前台
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败!");
        }
    }

}
