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

import java.util.HashMap;
import java.util.Map;


/**
 * CodeUtils
 *
 * @version 1.0
 * @since 2023/3/7 17:24
 */
public final class CodeUtils {
    // 默认宽度要求
    public static final int DEFAULT_REQ_WIDTH = 480;

    // 默认高度要求
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
        return createQRCode(content, heightPix, logo, ratio, hints, codeColor);
    }

    public static Bitmap createQRCode(String content, int heightPix, Bitmap logo,
        @FloatRange(from = 0.0f, to = 1.0f) float ratio, Map<EncodeHintType, ?> hints) {
        return createQRCode(content, heightPix, logo, ratio, hints, Color.BLACK);
    }

    /**
     * 生成二维码
     *
     * @param content   二维码的内容
     * @param heightPix 二维码的高
     * @param logo      二维码中间的logo
     * @param ratio     logo所占比例 因为二维码的最大容错率为30%，所以建议ratio的范围小于0.3
     * @param hints     提示
     * @param codeColor 二维码的颜色
     * @return Bitmap
     */
    public static Bitmap createQRCode(String content, int heightPix, Bitmap logo,
        @FloatRange(from = 0.0f, to = 1.0f) float ratio, Map<EncodeHintType, ?> hints, int codeColor) {
        try {
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(content,
                    BarcodeFormat.QR_CODE, heightPix, heightPix, hints);
            int[] pixels = new int[heightPix * heightPix];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < heightPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * heightPix + x] = codeColor;
                    } else {
                        pixels[y * heightPix + x] = Color.WHITE;
                    }
                }
            }
            // 生成二维码图片的格式
            Bitmap bitmap = Bitmap.createBitmap(heightPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, heightPix, 0, 0, heightPix, heightPix);
            if (logo != null) {
                bitmap = addLogo(bitmap, logo, ratio);
            }
            return bitmap;
        } catch (Exception e) {
            LogUtils.logW(e.getMessage());
        }
        return null;
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
            return null;
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
            return null;
        }
        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }
        // logo大小为二维码整体大小
        BigDecimal scaleFactor = new BigDecimal(srcWidth * ratio / logoWidth);
        Bitmap bitmap;
        try {
            bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor,
                    srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo,
                    (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            LogUtils.logW(e.getMessage());
        }
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
        return null;
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
        return null;
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
     * 解析一维码/二维码图片
     *
     * @param bitmap 解析的图片
     * @param hints  解析编码类型
     * @return
     */
    public static String parseCode(Bitmap bitmap, Map<DecodeHintType, Object> hints) {
        Result result = parseCodeResult(bitmap, hints);
        if (result != null) {
            return result.getText();
        }
        return null;
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
        } catch (Exception e) {
            LogUtils.logW(e.getMessage());
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
        } catch (NotFoundException e) {
            Log.e("decodeInternal", "decodeInternal: ", e);
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
     * 压缩图片
     *
     * @param path 解析的图片路径
     * @return Bitmap
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
     * 生成条形码
     *
     * @param content       content
     * @param desiredWidth  desiredWidth
     * @param desiredHeight desiredHeight
     * @return Bitmap
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
    public static Bitmap createBarCode(String content, int desiredWidth,
        int desiredHeight, boolean isShowText) {
        return createBarCode(content, BarcodeFormat.CODE_128, desiredWidth,
                desiredHeight, null, isShowText, 40, Color.BLACK);
    }

    /**
     * 生成条形码
     *
     * @param content       content
     * @param desiredWidth  desiredWidth
     * @param desiredHeight desiredHeight
     * @param isShowText    isShowText
     * @param codeColor     codeColor
     * @return Bitmap
     */
    public static Bitmap createBarCode(String content, int desiredWidth,
        int desiredHeight, boolean isShowText, @ColorInt int codeColor) {
        return createBarCode(content, BarcodeFormat.CODE_128, desiredWidth,
                desiredHeight, null, isShowText, 40, codeColor);
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
        int desiredWidth, int desiredHeight, Map<EncodeHintType, ?> hints) {
        return createBarCode(content, format, desiredWidth,
                desiredHeight, hints, false, 40, Color.BLACK);
    }

    /**
     * 生成条形码
     *
     * @param content       content
     * @param format        format
     * @param desiredWidth  desiredWidth
     * @param desiredHeight desiredHeight
     * @param hints         hints
     * @param isShowText    isShowText
     * @return Bitmap
     */
    public static Bitmap createBarCode(String content, BarcodeFormat format,
        int desiredWidth, int desiredHeight, Map<EncodeHintType, ?> hints, boolean isShowText) {
        return createBarCode(content, format, desiredWidth, desiredHeight,
                hints, isShowText, 40, Color.BLACK);
    }

    /**
     * 生成条形码
     *
     * @param content       content
     * @param format        format
     * @param desiredWidth  desiredWidth
     * @param desiredHeight desiredHeight
     * @param isShowText    isShowText
     * @param codeColor     codeColor
     * @return Bitmap
     */
    public static Bitmap createBarCode(String content, BarcodeFormat format,
        int desiredWidth, int desiredHeight, boolean isShowText, @ColorInt int codeColor) {
        return createBarCode(content, format, desiredWidth, desiredHeight,
                null, isShowText, 40, codeColor);
    }

    /**
     * 生成条形码
     *
     * @param content       content
     * @param format        format
     * @param desiredWidth  desiredWidth
     * @param desiredHeight desiredHeight
     * @param hints         hints
     * @param isShowText    isShowText
     * @return Bitmap
     */
    public static Bitmap createBarCode(String content, BarcodeFormat format, int desiredWidth,
        int desiredHeight, Map<EncodeHintType, ?> hints, boolean isShowText, @ColorInt int codeColor) {
        return createBarCode(content, format, desiredWidth, desiredHeight,
                hints, isShowText, 40, codeColor);
    }

    /**
     * 生成条形码
     *
     * @param content       content
     * @param format        format
     * @param desiredWidth  desiredWidth
     * @param desiredHeight desiredHeight
     * @param hints         hints
     * @param isShowText    isShowText
     * @param textSize      textSize
     * @param codeColor     codeColor
     * @return Bitmap
     */
    public static Bitmap createBarCode(String content, BarcodeFormat format, int desiredWidth,
        int desiredHeight, Map<EncodeHintType, ?> hints, boolean isShowText, int textSize, @ColorInt int codeColor) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        final int white = Color.WHITE;
        final int black = codeColor;
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix result = writer.encode(content, format, desiredWidth,
                    desiredHeight, hints);
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
                return addCode(bitmap, content, textSize, codeColor, textSize / 2);
            }
            return bitmap;
        } catch (WriterException e) {
            LogUtils.logW(e.getMessage());
        }
        return null;
    }

    /**
     * 条形码下面添加文本信息
     *
     * @param src       src
     * @param code      code
     * @param textSize  textSize
     * @param textColor textColor
     * @return Bitmap
     */
    private static Bitmap addCode(Bitmap src, String code, int textSize,
        @ColorInt int textColor, int offset) {
        if (src == null) {
            return null;
        }
        if (TextUtils.isEmpty(code)) {
            return src;
        }
        // 获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        if (srcWidth <= 0 || srcHeight <= 0) {
            return null;
        }
        Bitmap bitmap;
        try {
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
        } catch (Exception e) {
            bitmap = null;
            LogUtils.logW(e.getMessage());
        }
        return bitmap;
    }
}