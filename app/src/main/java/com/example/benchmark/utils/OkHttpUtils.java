/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * OkHttpUtils
 *
 * @version 1.0
 * @since 2023/3/7 17:26
 */
public class OkHttpUtils {
    private static final String TAG = "MyOkHttpUtils";
    private static volatile OkHttpClient okHttpClient = null;
    private static volatile Semaphore semaphore = null;

    private Map<String, String> headerMap;
    private Map<String, String> paramMap;
    private String url;
    private Request.Builder request;

    /**
     * 初始化okHttpClient，并且允许https访问
     */
    private OkHttpUtils() {
        if (okHttpClient == null) {
            synchronized (OkHttpUtils.class) {
                if (okHttpClient == null) {
                    TrustManager[] trustManagers = buildTrustManagers();
                    okHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .sslSocketFactory(createSSLSocketFactory(trustManagers),
                                    trustManagers[0] instanceof X509TrustManager
                                            ? (X509TrustManager) trustManagers[0] : null)
                            .hostnameVerifier((hostName, session) -> true)
                            .retryOnConnectionFailure(true)
                            .build();
                    addHeader("User-Agent",
                            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) "
                                    + "AppleWebKit/537.36 (KHTML, like Gecko) "
                                    + "Chrome/63.0.3239.132 Safari/537.36");
                }
            }
        }
    }

    /**
     * getSemaphoreInstance
     *
     * @return java.util.concurrent.Semaphore
     * @date 2023/3/8 09:11
     */
    private static Semaphore getSemaphoreInstance() {
        // 只能1个线程同时访问
        synchronized (OkHttpUtils.class) {
            if (semaphore == null) {
                semaphore = new Semaphore(0);
            }
        }
        return semaphore;
    }

    /**
     * builder
     *
     * @return com.example.benchmark.utils.OkHttpUtils
     * @date 2023/3/8 09:11
     */
    public static OkHttpUtils builder() {
        return new OkHttpUtils();
    }

    /**
     * url
     *
     * @param url description
     * @return com.example.benchmark.utils.OkHttpUtils
     * @date 2023/3/8 09:11
     */
    public OkHttpUtils url(String url) {
        this.url = url;
        return this;
    }

    /**
     * addParam
     *
     * @param key   description
     * @param value description
     * @return com.example.benchmark.utils.OkHttpUtils
     * @date 2023/3/8 09:11
     */
    public OkHttpUtils addParam(String key, String value) {
        if (paramMap == null) {
            paramMap = new LinkedHashMap<>(16);
        }
        paramMap.put(key, value);
        return this;
    }

    /**
     * addHeader
     *
     * @param key   description
     * @param value description
     * @return com.example.benchmark.utils.OkHttpUtils
     * @date 2023/3/8 09:11
     */
    public OkHttpUtils addHeader(String key, String value) {
        if (headerMap == null) {
            headerMap = new LinkedHashMap<>(16);
        }
        headerMap.put(key, value);
        return this;
    }

    /**
     * get
     *
     * @return com.example.benchmark.utils.OkHttpUtils
     * @date 2023/3/8 09:11
     */
    public OkHttpUtils get() {
        request = new Request.Builder().get();
        StringBuilder urlBuilder = new StringBuilder(url);
        if (paramMap != null) {
            urlBuilder.append("?");
            try {
                for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                    urlBuilder.append(URLEncoder.encode(entry.getKey(), "utf-8"))
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), "utf-8"))
                            .append("&");
                }
            } catch (UnsupportedEncodingException ex) {
                Log.e(TAG, "UnsupportedEncodingException: " + ex);
            }
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }
        request.url(urlBuilder.toString());
        return this;
    }

    /**
     * post
     *
     * @param isJsonPost description
     * @return com.example.benchmark.utils.OkHttpUtils
     * @date 2023/3/8 09:11
     */
    public OkHttpUtils post(boolean isJsonPost) {
        RequestBody requestBody;
        if (isJsonPost) {
            String json = "";
            if (paramMap != null) {
                json = JSON.toJSONString(paramMap);
            }
            requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        } else {
            FormBody.Builder formBody = new FormBody.Builder();
            if (paramMap != null) {
                paramMap.forEach(formBody::add);
            }
            requestBody = formBody.build();
        }
        request = new Request.Builder().post(requestBody).url(url);
        return this;
    }

    /**
     * sync
     *
     * @return java.lang.String
     * @date 2023/3/8 09:11
     */
    public String sync() {
        setHeader(request);
        try {
            Response response = okHttpClient.newCall(request.build()).execute();
            if (response.body() != null) {
                return response.body().toString();
            }
        } catch (IOException ex) {
            Log.e(TAG, "sync: ", ex);
            return "请求失败：" + ex.getMessage();
        }
        return "未收到数据";
    }

    /**
     * async
     *
     * @return java.lang.String
     * @date 2023/3/8 09:11
     */
    public String async() {
        StringBuilder buffer = new StringBuilder("");
        setHeader(request);
        okHttpClient.newCall(request.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException ex) {
                buffer.append("请求出错：").append(ex.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() != null) {
                    buffer.append(response.body().string());
                    getSemaphoreInstance().release();
                }
            }
        });
        try {
            getSemaphoreInstance().acquire();
        } catch (InterruptedException ex) {
            Log.e(TAG, "async: ", ex);
        }
        return buffer.toString();
    }

    /**
     * async
     *
     * @param callBack description
     * @date 2023/3/8 09:11
     */
    public void async(ICallBack callBack) {
        setHeader(request);
        okHttpClient.newCall(request.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException ex) {
                callBack.onFailure(call, ex.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() != null) {
                    callBack.onSuccessful(call, response.body().string());
                }
            }
        });
    }

    /**
     * setHeader
     *
     * @param request description
     * @return void
     * @throws null
     * @date 2023/3/8 09:12
     */
    private void setHeader(Request.Builder request) {
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * createSSLSocketFactory
     *
     * @param trustAllCerts description
     * @return javax.net.ssl.SSLSocketFactory
     * @throws null
     * @date 2023/3/8 09:12
     */
    private static SSLSocketFactory createSSLSocketFactory(TrustManager[] trustAllCerts) {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException ex) {
            Log.e(TAG, "createSSLSocketFactory: " + ex);
        }
        return ssfFactory;
    }

    /**
     * buildTrustManagers
     *
     * @return javax.net.ssl.TrustManager[]
     * @throws null
     * @date 2023/3/8 09:12
     */
    private static TrustManager[] buildTrustManagers() {
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        for (X509Certificate cert : chain) {
                            // Make sure that it hasn't expired.
                            try {
                                cert.checkValidity();
                            } catch (CertificateExpiredException | CertificateNotYetValidException ex) {
                                Log.e(TAG, "checkServerTrusted: " + ex.toString());
                            }
                        }

                        // 检查所有证书
                        try {
                            TrustManagerFactory factory = TrustManagerFactory.getInstance("X509");
                            if (null instanceof KeyStore) {
                                factory.init((KeyStore) null);
                            }
                            for (TrustManager trustManager : factory.getTrustManagers()) {
                                if (trustManager instanceof X509TrustManager) {
                                    ((X509TrustManager) trustManager).checkServerTrusted(chain, authType);
                                }
                            }
                        } catch (NoSuchAlgorithmException | CertificateException | KeyStoreException ex) {
                            Log.e(TAG, "checkServerTrusted: " + ex.toString());
                        }

                        // 获取网络中的证书信息
                        X509Certificate certificate = chain[0];
                        // 证书拥有者
                        String subject = certificate.getSubjectDN().getName();
                        // 证书颁发者
                        String issuer = certificate.getIssuerDN().getName();
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
        };
    }

    /**
     * @return
     * @throws null
     * @date 2023/3/8 09:12
     */
    public interface ICallBack {
        /**
         * onSuccessful
         *
         * @param call description
         * @param data description
         * @return void
         * @throws null
         * @date 2023/3/8 09:12
         */
        void onSuccessful(Call call, String data);

        /**
         * onFailure
         *
         * @param call     description
         * @param errorMsg description
         * @return void
         * @throws null
         * @date 2023/3/8 09:12
         */
        void onFailure(Call call, String errorMsg);
    }
}