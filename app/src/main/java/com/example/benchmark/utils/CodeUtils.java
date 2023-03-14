/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * CodeUtils
 *
 * @version 1.0
 * @since 2023/3/7 17:24
 */
public final class CodeUtils {
    /**
     * 默认宽度要求
     */
    public static final int DEFAULT_REQ_WIDTH = 480;

    /**
     * 默认高度要求
     */
    public static final int DEFAULT_REQ_HEIGHT = 640;

    private CodeUtils() {
        throw new AssertionError();
    }

    /**
     * 生成二维码
     *
     * @param content   二维码的内容
     * @param heightPix 二维码的高
     * @return Bitmap
     */
    public static Bitmap createQRCode(String content, int heightPix) {
        return createQRCode(content, heightPix, null);
    }

    /**
     * 生成二维码
     *
     * @param content   二维码的内容
     * @param heightPix 二维码的高
     * @param codeColor 二维码的颜色
     * @return Bitmap
     */
    public static Bitmap createQRCode(String content, int heightPix, int codeColor) {
        return createQRCode(content, heightPix, null, codeColor);
    }

    /**
     * 生成我二维码
     *
     * @param content   二维码的内容
     * @param heightPix 二维码的高
     * @param logo      logo大小默认占二维码的20%
     * @return Bitmap
     */
    public static Bitmap createQRCode(String content, int heightPix, Bitmap logo) {
        return createQRCode(content, heightPix, logo, Color.BLACK);
    }

    /**
     * 生成我二维码
     *
     * @param content   二维码的内容
     * @param heightPix 二维码的高
     * @param logo      logo大小默认占二维码的20%
     * @param codeColor 二维码的颜色
     * @return Bitmap
     */
    public static Bitmap createQRCode(String content, int heightPix, Bitmap logo, int codeColor) {
        return createQRCode(content, heightPix, logo, 0.2f, codeColor);
    }

    /**
     * 生成二维码
     *
     * @param content   二维码的内容
     * @param heightPix 二维码的高
     * @param logo      二维码中间的logo
     * @param ratio     logo所占比例 因为二维码的最大容错率为30%，所以建议ratio的范围小于0.3
     * @return Bitmap
     */
    public static Bitmap createQRCode(String content, int heightPix, Bitmap logo,
                                        @FloatRange(from = 0.0f, to = 1.0f) float ratio) {
        // 配置参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

        // 容错级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        // 设置空白边距的宽度 default is 4
        hints.put(EncodeHintType.MARGIN, 1);
        return createQRCode(content, heightPix, logo, ratio, hints);
    }

    /**
     * 生成二维码
     *
     * @param content   二维码的内容
     * @param heightPix 二维码的高
     * @param logo      二维码中间的logo
     * @param ratio     logo所占比例 因为二维码的最大容错率为30%，所以建议ratio的范围小于0.3
     * @param codeColor 二维码的颜色
     * @return Bitmap
     */
    public static Bitmap createQRCode(String content, int heightPix, Bitmap logo,
                                        @FloatRange(from = 0.0f, to = 1.0f) float ratio, int codeColor) {
        // 配置参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

        // 容错级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        // 设置空白边距的宽度 default is 1
        hints.put(EncodeHintType.MARGIN, 1);
        int[] combinedInt = new int[2];
        combinedInt[0] = heightPix;
        combinedInt[1] = codeColor;
        return createQRCode(content, combinedInt, logo, ratio, hints);
    }

    /**
     * createQRCode
     *
     * @param content   description
     * @param heightPix description
     * @param logo      description
     * @param ratio     description
     * @param hints     description
     * @return android.graphics.Bitmap
     * @date 2023/3/9 16:21
     */
    public static Bitmap createQRCode(String content, int heightPix, Bitmap logo,
                                        @FloatRange(from = 0.0f, to = 1.0f) float ratio,
                                        Map<EncodeHintType, ?> hints) {
        int[] combinedInt = new int[2];
        combinedInt[0] = heightPix;
        combinedInt[1] = Color.BLACK;
        return createQRCode(content, combinedInt, logo, ratio, hints);
    }

    /**
     * 生成二维码
     *
     * @param content   二维码的内容
     * @param combinedInt   heightPix、codeColor
     * @param logo      二维码中间的logo
     * @param ratio     logo所占比例 因为二维码的最大容错率为30%，所以建议ratio的范围小于0.3
     * @param hints     提示
     * @return Bitmap
     */
    public static Bitmap createQRCode(String content, int[] combinedInt, Bitmap logo,
                                        @FloatRange(from = 0.0f, to = 1.0f) float ratio,
                                        Map<EncodeHintType, ?> hints) {
        try {
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(content,
                    BarcodeFormat.QR_CODE, combinedInt[0], combinedInt[0], hints);
            int[] pixels = new int[combinedInt[0] * combinedInt[0]];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < combinedInt[0]; y++) {
                for (int x = 0; x < combinedInt[0]; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * combinedInt[0] + x] = combinedInt[1];
                    } else {
                        pixels[y * combinedInt[0] + x] = Color.WHITE;
                    }
                }
            }
            // 生成二维码图片的格式
            Bitmap bitmap = Bitmap.createBitmap(combinedInt[0], combinedInt[0], Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, combinedInt[0], 0, 0, combinedInt[0], combinedInt[0]);
            if (logo != null) {
                bitmap = addLogo(bitmap, logo, ratio);
            }
            return bitmap;
        } catch (WriterException ex) {
            Log.e("WriterException", "createQRCode: " + ex);
        }
        return Bitmap.createBitmap(combinedInt[0], combinedInt[0], Bitmap.Config.ARGB_8888);
    }

    /**
     * 在二维码中间添加Logo图案
     *
     * @param src   src
     * @param logo  logo
     * @param ratio logo所占比例 因为二维码的最大容错率为30%，所以建议ratio的范围小于0.3
     * @return Bitmap
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo,
                                    @FloatRange(from = 0.0f, to = 1.0f) float ratio) {
        if (src == null) {
            return src;
        }
        if (logo == null) {
            return src;
        }
        // 获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();
        if (srcWidth == 0 || srcHeight == 0) {
            return src;
        }
        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }
        // logo大小为二维码整体大小
        BigDecimal scrWidthB = BigDecimal.valueOf(srcWidth);
        BigDecimal ratioB = BigDecimal.valueOf(ratio);
        BigDecimal logoWidthB = BigDecimal.valueOf(logoWidth);
        BigDecimal scaleFactorB = scrWidthB.multiply(ratioB).divide(logoWidthB,BigDecimal.ROUND_CEILING);
        float scaleFactor = scaleFactorB.floatValue();
        Bitmap bitmap;
        bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(src, 0, 0, null);
        canvas.scale(scaleFactor, scaleFactor,
                srcWidth / 2, srcHeight / 2);
        canvas.drawBitmap(logo,
                (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
        canvas.save();
        canvas.restore();

        return bitmap;
    }

    /**
     * 解析二维码图片
     *
     * @param bitmapPath 需要解析的图片路径
     * @return String
     */
    public static String parseQRCode(String bitmapPath) {
        Result result = parseQRCodeResult(bitmapPath);
        if (result != null) {
            return result.getText();
        }
        return "null";
    }

    /**
     * 解析二维码图片
     *
     * @param bitmapPath 需要解析的图片路径
     * @return Result
     */
    public static Result parseQRCodeResult(String bitmapPath) {
        return parseQRCodeResult(bitmapPath, DEFAULT_REQ_WIDTH, DEFAULT_REQ_HEIGHT);
    }

    /**
     * 解析二维码图片
     *
     * @param bitmapPath 需要解析的图片路径
     * @param reqWidth   请求目标宽度，如果实际图片宽度大于此值，会自动进行压缩处理，
     *                   当 reqWidth 和 reqHeight都小于或等于0时，则不进行压缩处理
     * @param reqHeight  请求目标高度，如果实际图片高度大于此值，会自动进行压缩处理，
     *                   当 reqWidth 和 reqHeight都小于或等于0时，则不进行压缩处理
     * @return Result
     */
    public static Result parseQRCodeResult(String bitmapPath, int reqWidth, int reqHeight) {
        return parseCodeResult(bitmapPath, reqWidth, reqHeight, DecodeFormatManager.QR_CODE_HINTS);
    }

    /**
     * 解析一维码/二维码图片
     *
     * @param bitmapPath 需要解析的图片路径
     * @return String
     */
    public static String parseCode(String bitmapPath) {
        return parseCode(bitmapPath, DecodeFormatManager.ALL_HINTS);
    }

    /**
     * 解析一维码/二维码图片
     *
     * @param bitmapPath 需要解析的图片路径
     * @param hints      解析编码类型
     * @return String
     */
    public static String parseCode(String bitmapPath, Map<DecodeHintType, Object> hints) {
        Result result = parseCodeResult(bitmapPath, hints);
        if (result != null) {
            return result.getText();
        }
        return "null";
    }

    /**
     * 解析二维码图片
     *
     * @param bitmap 解析的图片
     * @return String
     */
    public static String parseQRCode(Bitmap bitmap) {
        return parseCode(bitmap, DecodeFormatManager.QR_CODE_HINTS);
    }

    /**
     * 解析一维码/二维码图片
     *
     * @param bitmap 解析的图片
     * @return String
     */
    public static String parseCode(Bitmap bitmap) {
        return parseCode(bitmap, DecodeFormatManager.ALL_HINTS);
    }

    /**
     * parseCode
     *
     * @param bitmap description
     * @param hints  description
     * @return java.lang.String
     * @date 2023/3/9 16:28
     */
    public static String parseCode(Bitmap bitmap, Map<DecodeHintType, Object> hints) {
        Result result = parseCodeResult(bitmap, hints);
        if (result != null) {
            return result.getText();
        }
        return "null";
    }

    /**
     * 解析一维码/二维码图片
     *
     * @param bitmapPath 需要解析的图片路径
     * @param hints      解析编码类型
     * @return Result
     */
    public static Result parseCodeResult(String bitmapPath, Map<DecodeHintType, Object> hints) {
        return parseCodeResult(bitmapPath, DEFAULT_REQ_WIDTH, DEFAULT_REQ_HEIGHT, hints);
    }

    /**
     * 解析一维码/二维码图片
     *
     * @param bitmapPath 需要解析的图片路径
     * @param reqWidth   请求目标宽度，如果实际图片宽度大于此值，会自动进行压缩处理，
     *                   当 reqWidth 和 reqHeight都小于或等于0时，则不进行压缩处理
     * @param reqHeight  请求目标高度，如果实际图片高度大于此值，会自动进行压缩处理，
     *                   当 reqWidth 和 reqHeight都小于或等于0时，则不进行压缩处理
     * @param hints      解析编码类型
     * @return Result
     */
    public static Result parseCodeResult(String bitmapPath, int reqWidth,
                                            int reqHeight, Map<DecodeHintType, Object> hints) {
        return parseCodeResult(compressBitmap(bitmapPath, reqWidth, reqHeight), hints);
    }

    /**
     * 解析一维码/二维码图片
     *
     * @param bitmap 解析的图片
     * @return Result
     */
    public static Result parseCodeResult(Bitmap bitmap) {
        return parseCodeResult(getRGBLuminanceSource(bitmap), DecodeFormatManager.ALL_HINTS);
    }

    /**
     * 解析一维码/二维码图片
     *
     * @param bitmap 解析的图片
     * @param hints  解析编码类型
     * @return Result
     */
    public static Result parseCodeResult(Bitmap bitmap, Map<DecodeHintType, Object> hints) {
        return parseCodeResult(getRGBLuminanceSource(bitmap), hints);
    }

    /**
     * 解析一维码/二维码图片
     *
     * @param source source
     * @param hints  解析编码类型
     * @return Result
     */
    public static Result parseCodeResult(LuminanceSource source,
                                            Map<DecodeHintType, Object> hints) {
        Result result = null;
        MultiFormatReader reader = new MultiFormatReader();
        try {
            reader.setHints(hints);
            if (source != null) {
                result = decodeInternal(reader, source);
                if (result == null) {
                    result = decodeInternal(reader, source.invert());
                }
                if (result == null && source.isRotateSupported()) {
                    result = decodeInternal(reader, source.rotateCounterClockwise());
                }
            }
        } finally {
            reader.reset();
        }
        return result;
    }

    private static Result decodeInternal(MultiFormatReader reader, LuminanceSource source) {
        Result result = null;
        try {
            // 采用HybridBinarizer解析
            result = reader.decodeWithState(new BinaryBitmap(new HybridBinarizer(source)));
        } catch (NotFoundException ex) {
            Log.e("TAG", "decodeInternal: " + ex);
        }
        if (result == null) {
            // 如果没有解析成功，再采用GlobalHistogramBinarizer解析一次
            try {
                result = reader.decodeWithState(
                        new BinaryBitmap(
                                new GlobalHistogramBinarizer(source)));
            } catch (NotFoundException e) {
                Log.e("decodeInternal", "decodeInternal: ", e);
            }
        }
        return result;
    }

    /**
     * compressBitmap
     *
     * @param path      description
     * @param reqWidth  description
     * @param reqHeight description
     * @return android.graphics.Bitmap
     * @date 2023/3/9 16:30
     */
    private static Bitmap compressBitmap(String path, int reqWidth, int reqHeight) {
        if (reqWidth > 0 && reqHeight > 0) {
            // 都大于进行判断是否压缩
            BitmapFactory.Options newOpts = new BitmapFactory.Options();

            // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true; // 获取原始图片大小

            BitmapFactory.decodeFile(path, newOpts); // 此时返回bm为空

            BigDecimal width = new BigDecimal(newOpts.outWidth);
            BigDecimal height = new BigDecimal(newOpts.outHeight);

            // 缩放比，由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            int wSize = 1; // wSize=1表示不缩放
            if (width.intValue() > reqWidth) {
                // 如果宽度大的话根据宽度固定大小缩放
                wSize = (int) (width.intValue() / reqWidth);
            }

            // wSize=1表示不缩放
            int hSize = 1;
            if (height.intValue() > reqHeight) {
                // 如果高度高的话根据宽度固定大小缩放
                hSize = (int) (height.intValue() / reqHeight);
            }
            int size = Math.max(wSize, hSize);
            if (size <= 0) {
                size = 1;
            }
            newOpts.inSampleSize = size; // 设置缩放比例

            // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            newOpts.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(path, newOpts);
        }
        return BitmapFactory.decodeFile(path);
    }

    /**
     * 获取RGBLuminanceSource
     *
     * @param bitmap bitmap
     * @return RGBLuminanceSource
     */
    private static RGBLuminanceSource getRGBLuminanceSource(@NonNull Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(),
                0, 0, bitmap.getWidth(), bitmap.getHeight());
        return new RGBLuminanceSource(width, height, pixels);
    }

    /**
     * createBarCode
     *
     * @param content       description
     * @param desiredWidth  description
     * @param desiredHeight description
     * @return android.graphics.Bitmap
     * @date 2023/3/10 16:35
     */
    public static Bitmap createBarCode(String content, int desiredWidth, int desiredHeight) {
        return createBarCode(content,
                BarcodeFormat.CODE_128,
                desiredWidth,
                desiredHeight,
                null);
    }

    /**
     * 生成条形码
     *
     * @param content       content
     * @param format        format
     * @param desiredWidth  desiredWidth
     * @param desiredHeight desiredHeight
     * @return Bitmap
     */
    public static Bitmap createBarCode(String content, BarcodeFormat format,
                                        int desiredWidth, int desiredHeight) {
        return createBarCode(content, format, desiredWidth, desiredHeight, null);
    }

    /**
     * createBarCode
     *
     * @param content description
     * @param desiredWidth description
     * @param desiredHeight description
     * @param isShowText description
     * @return android.graphics.Bitmap
     * @date 2023/3/14 15:20
     */
    public static Bitmap createBarCode(String content, int desiredWidth,
                                        int desiredHeight, boolean isShowText) {
        int[] combinedInt = new int[4];
        combinedInt[0] = desiredWidth;
        combinedInt[1] = desiredHeight;
        combinedInt[2] = 40;
        combinedInt[3] = Color.BLACK;
        return createBarCode(content, BarcodeFormat.CODE_128, combinedInt,
                null, isShowText);
    }

    /**
     * createBarCode
     *
     * @param content description
     * @param desiredWidth description
     * @param desiredHeight description
     * @param isShowText description
     * @param codeColor description
     * @return android.graphics.Bitmap
     * @date 2023/3/13 15:17
     */
    public static Bitmap createBarCode(String content, int desiredWidth,
                                        int desiredHeight, boolean isShowText, @ColorInt int codeColor) {
        int[] combinedInt = new int[4];
        combinedInt[0] = desiredWidth;
        combinedInt[1] = desiredHeight;
        combinedInt[2] = 40;
        combinedInt[3] = codeColor;
        return createBarCode(content, BarcodeFormat.CODE_128, combinedInt,
                null, isShowText);
    }

    /**
     * 生成条形码
     *
     * @param content       content
     * @param format        format
     * @param desiredWidth  desiredWidth
     * @param desiredHeight desiredHeight
     * @param hints         hints
     * @return Bitmap
     */
    public static Bitmap createBarCode(String content, BarcodeFormat format,
                                        int desiredWidth, int desiredHeight,
                                        Map<EncodeHintType, ?> hints) {
        int[] combinedInt = new int[4];
        combinedInt[0] = desiredWidth;
        combinedInt[1] = desiredHeight;
        combinedInt[2] = 40;
        combinedInt[3] = Color.BLACK;
        return createBarCode(content, format, combinedInt,
                hints, false);
    }

    /**
     * createBarCode
     *
     * @param content description
     * @param format description
     * @param combinedInt description
     * @param hints description
     * @param isShowText description
     * @return android.graphics.Bitmap
     * @date 2023/3/13 15:26
     */
    public static Bitmap createBarCode(String content, BarcodeFormat format, int[] combinedInt,
                                        Map<EncodeHintType, ?> hints,
                                        boolean isShowText) {
        final int white = Color.WHITE;
        final int black = combinedInt[3];
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix result = writer.encode(content, format, combinedInt[0],
                    combinedInt[1], hints);
            int width = result.getWidth();
            int height = result.getHeight();
            int[] pixels = new int[width * height];

            // All are 0, or black, by default
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = result.get(x, y) ? black : white;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            if (isShowText) {
                return addCode(bitmap, content, combinedInt[2], combinedInt[3], combinedInt[2] / 2);
            }
            return bitmap;
        } catch (WriterException e) {
            LogUtils.logW(e.getMessage());
        }
        return Bitmap.createBitmap(100, 100,
                Bitmap.Config.ARGB_8888);
    }

    /**
     * addCode
     *
     * @param src       description
     * @param code      description
     * @param textSize  description
     * @param textColor description
     * @param offset    description
     * @return android.graphics.Bitmap
     * @date 2023/3/10 16:33
     */
    private static Bitmap addCode(Bitmap src, String code, int textSize,
                                    @ColorInt int textColor, int offset) {
        if (TextUtils.isEmpty(code)) {
            return src;
        }
        // 获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        Bitmap bitmap;
        bitmap = Bitmap.createBitmap(srcWidth, srcHeight + textSize + offset * 2,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(src, 0, 0, null);
        TextPaint paint = new TextPaint();
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(code, srcWidth / 2, srcHeight + textSize / 2 + offset, paint);
        canvas.save();
        canvas.restore();

        return bitmap;
    }
}