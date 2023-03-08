/*
 * 版权所有 (c) 华为技术有限公司 2022-2023
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 *
 */

package com.example.benchmark.utils;

import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * DecodeFormatManager
 *
 * @version 1.0
 * @since 2023/3/7 17:24
 */
public final class DecodeFormatManager {
    /**
     * 所有的
     */
    public static final Map<DecodeHintType, Object> ALL_HINTS = new EnumMap<>(DecodeHintType.class);

    /**
     * CODE_128 (最常用的一维码)
     */
    public static final Map<DecodeHintType, Object> CODE_128_HINTS = createDecodeHint(BarcodeFormat.CODE_128);

    /**
     * QR_CODE (最常用的二维码)
     */
    public static final Map<DecodeHintType, Object> QR_CODE_HINTS = createDecodeHint(BarcodeFormat.QR_CODE);

    /**
     * 一维码
     */
    public static final Map<DecodeHintType, Object> ONE_DIMENSIONAL_HINTS = new EnumMap<>(DecodeHintType.class);

    /**
     * 二维码
     */
    public static final Map<DecodeHintType, Object> TWO_DIMENSIONAL_HINTS = new EnumMap<>(DecodeHintType.class);

    /**
     * 默认
     */
    public static final Map<DecodeHintType, Object> DEFAULT_HINTS = new EnumMap<>(DecodeHintType.class);

    static {

        // all hints
        addDecodeHintTypes(ALL_HINTS, getAllFormats());

        // one dimension
        addDecodeHintTypes(ONE_DIMENSIONAL_HINTS, getOneDimensionalFormats());

        // Two dimension
        addDecodeHintTypes(TWO_DIMENSIONAL_HINTS, getTwoDimensionalFormats());

        // default hints
        addDecodeHintTypes(DEFAULT_HINTS, getDefaultFormats());
    }

    private static List<BarcodeFormat> getAllFormats() {
        List<BarcodeFormat> list = new ArrayList<>();
        list.add(BarcodeFormat.AZTEC);
        list.add(BarcodeFormat.CODABAR);
        list.add(BarcodeFormat.CODE_39);
        list.add(BarcodeFormat.CODE_93);
        list.add(BarcodeFormat.CODE_128);
        list.add(BarcodeFormat.DATA_MATRIX);
        list.add(BarcodeFormat.EAN_8);
        list.add(BarcodeFormat.EAN_13);
        list.add(BarcodeFormat.ITF);
        list.add(BarcodeFormat.MAXICODE);
        list.add(BarcodeFormat.PDF_417);
        list.add(BarcodeFormat.QR_CODE);
        list.add(BarcodeFormat.RSS_14);
        list.add(BarcodeFormat.RSS_EXPANDED);
        list.add(BarcodeFormat.UPC_A);
        list.add(BarcodeFormat.UPC_E);
        list.add(BarcodeFormat.UPC_EAN_EXTENSION);
        return list;
    }

    /**
     * getOneDimensionalFormats
     *
     * @return java.util.List<com.google.zxing.BarcodeFormat>
     * @date 2023/3/8 14:28
    */
    private static List<BarcodeFormat> getOneDimensionalFormats() {
        List<BarcodeFormat> list = new ArrayList<>();
        list.add(BarcodeFormat.CODABAR);
        list.add(BarcodeFormat.CODE_39);
        list.add(BarcodeFormat.CODE_93);
        list.add(BarcodeFormat.CODE_128);
        list.add(BarcodeFormat.EAN_8);
        list.add(BarcodeFormat.EAN_13);
        list.add(BarcodeFormat.ITF);
        list.add(BarcodeFormat.RSS_14);
        list.add(BarcodeFormat.RSS_EXPANDED);
        list.add(BarcodeFormat.UPC_A);
        list.add(BarcodeFormat.UPC_E);
        list.add(BarcodeFormat.UPC_EAN_EXTENSION);
        return list;
    }

    /**
     * getTwoDimensionalFormats
     *
     * @return java.util.List<com.google.zxing.BarcodeFormat>
     * @date 2023/3/8 14:28
    */
    private static List<BarcodeFormat> getTwoDimensionalFormats() {
        List<BarcodeFormat> list = new ArrayList<>();
        list.add(BarcodeFormat.AZTEC);
        list.add(BarcodeFormat.DATA_MATRIX);
        list.add(BarcodeFormat.MAXICODE);
        list.add(BarcodeFormat.PDF_417);
        list.add(BarcodeFormat.QR_CODE);
        return list;
    }


    /**
     * getDefaultFormats
     *
     * @return java.util.List<com.google.zxing.BarcodeFormat>
     * @date 2023/3/8 14:28
    */
    private static List<BarcodeFormat> getDefaultFormats() {
        List<BarcodeFormat> list = new ArrayList<>();
        list.add(BarcodeFormat.QR_CODE);
        list.add(BarcodeFormat.UPC_A);
        list.add(BarcodeFormat.EAN_13);
        list.add(BarcodeFormat.CODE_128);
        return list;
    }

    private static <T> List<T> singletonList(T o) {
        return Collections.singletonList(o);
    }

    /**
     * createDecodeHints
     *
     * @param barcodeFormats description
     * @return java.util.Map<com.google.zxing.DecodeHintType, java.lang.Object>
     * @date 2023/3/8 09:24
     */
    public static Map<DecodeHintType, Object> createDecodeHints(@NonNull BarcodeFormat... barcodeFormats) {
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        addDecodeHintTypes(hints, Arrays.asList(barcodeFormats));
        return hints;
    }

    /**
     * createDecodeHint
     *
     * @param barcodeFormat description
     * @return java.util.Map<com.google.zxing.DecodeHintType, java.lang.Object>
     * @date 2023/3/8 09:24
     */
    public static Map<DecodeHintType, Object> createDecodeHint(@NonNull BarcodeFormat barcodeFormat) {
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        addDecodeHintTypes(hints, singletonList(barcodeFormat));
        return hints;
    }

    private static void addDecodeHintTypes(Map<DecodeHintType, Object> hints, List<BarcodeFormat> formats) {

        // Image is known to be of one of a few possible formats.
        hints.put(DecodeHintType.POSSIBLE_FORMATS, formats);

        // Spend more time to try to find a barcode; optimize for accuracy, not speed.
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);

        // Specifies what character encoding to use when decoding, where applicable (type String)
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
    }
}
