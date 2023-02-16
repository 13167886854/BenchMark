package com.example.benchmark.utils;



import com.chinamobile.bcop.api.sdk.auth.signature.ISignature;
import com.chinamobile.bcop.api.sdk.auth.signature.v2.Credential;
import com.chinamobile.bcop.api.sdk.auth.signature.v2.SignatureGenerator;
import com.chinamobile.bcop.api.sdk.constant.HttpMethod;
import com.example.benchmark.data.MobileCloud;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;

/**
 * 通过 accessKey 和 secretKey，远程获取移动云手机的参数信息
 */
public class MobileCloudRequestUtils {

    public String generateSignature(final String path, final HttpMethod method, final Map<String, Object> paramMap, final String accessKey, final String secretKey) {
        System.out.println("签名地址path：" + path);
        final ISignature signature = new SignatureGenerator();
        final Credential credential = new Credential();
        credential.setAccessKey(accessKey);
        credential.setSecretKey(secretKey);

        final String url = signature.doSignature(path, credential, method.getMethod(), paramMap);
        System.out.println("签名后的参数：" + MobileCloud.baseUrl + url);
        return MobileCloud.baseUrl  + url;
    }

    public String httpPost(String path, Object obj, final String accessKey, final String secretKey) {

        if (obj == null) {
            obj = new Object();
        }

        final String jsonStr = JSONUtil.toJsonStr(obj);
        System.out.println("https post request, url = " + path + "请求参数：" + jsonStr);
        path = generateSignature(path, HttpMethod.POST, new HashMap<>(), accessKey, secretKey);
        final String result = HttpRequest.post(path)
                .header("Content-Type", "application/json").header("Content-Length", "3456").
                        body(jsonStr).timeout(60000).execute().body();
        return result;
    }


}
