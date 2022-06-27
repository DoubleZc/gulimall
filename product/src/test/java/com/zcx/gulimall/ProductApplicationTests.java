package com.zcx.gulimall;

import com.aliyun.oss.*;
import com.zcx.gulimall.product.ProductApplication;
import com.zcx.gulimall.product.entity.BrandEntity;
import com.zcx.gulimall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

import java.io.FileInputStream;
import java.io.InputStream;

@SpringBootTest(classes = ProductApplication.class)
class ProductApplicationTests {

    @Autowired
    private BrandService brandService;


    @Autowired
    private OSSClient ossClient;

    @Test
    void contextLoads() throws Exception {


        // 填写Bucket名称，例如examplebucket。
        String bucketName = "guli-zcx";
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        String objectName = "test1.jpg";
        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        String filePath = "H:\\Java课程\\谷粒商城\\课件和文档\\基础篇\\资料\\pics\\2b1837c6c50add30.jpg";

        try {
            InputStream inputStream = new FileInputStream(filePath);
            // 创建PutObject请求。
            ossClient.putObject(bucketName, objectName, inputStream);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        System.out.println("上传完成");


    }

}
