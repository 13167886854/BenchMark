package com.example.benchmark.utils;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 获取设备信息工具类
 */
public class DeviceInfoUtils {

    /**
     * 获取设备宽度（px）
     */
    public static int getDeviceWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取设备高度（px）
     */
    public static int getDeviceHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取设备的唯一标识， 需要 “android.permission.READ_Phone_STATE”权限
     */
    //public static String getIMEI(Context context) {
    //    TelephonyManager tm = (TelephonyManager) context
    //            .getSystemService(Context.TELEPHONY_SERVICE);
    //    String deviceId = tm.getDeviceId();
    //    if (deviceId == null) {
    //        return "UnKnown";
    //    } else {
    //        return deviceId;
    //    }
    //}

    /**
     * 获取厂商名
     **/
    public static String getDeviceManufacturer() {
        return android.os.Build.MANUFACTURER;
    }

    /**
     * 获取产品名
     **/
    public static String getDeviceProduct() {
        return android.os.Build.PRODUCT;
    }

    /**
     * 获取手机品牌
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机型号
     */
    public static String getDeviceModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机主板名
     */
    public static String getDeviceBoard() {
        return android.os.Build.BOARD;
    }

    /**
     * 设备名
     **/
    public static String getDeviceDevice() {
        return android.os.Build.DEVICE;
    }

    /**
     * fingerprit 信息
     **/
    public static String getDeviceFubgerprint() {
        return android.os.Build.FINGERPRINT;
    }

    /**
     * 硬件名
     **/
    public static String getDeviceHardware() {
        return android.os.Build.HARDWARE;
    }

    /**
     * 主机
     **/
    public static String getDeviceHost() {
        return android.os.Build.HOST;
    }

    /**
     * 显示ID
     **/
    public static String getDeviceDisplay() {
        return android.os.Build.DISPLAY;
    }

    /**
     * ID
     **/
    public static String getDeviceId() {
        return android.os.Build.ID;
    }

    /**
     * 获取手机用户名
     **/
    public static String getDeviceUser() {
        return android.os.Build.USER;
    }

    /**
     * 获取手机 硬件序列号
     **/
    public static String getDeviceSerial() {
        return android.os.Build.SERIAL;
    }

    /**
     * 获取手机Android 系统SDK
     *
     * @return
     */
    public static int getDeviceSDK() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 获取手机Android 版本
     *
     * @return
     */
    public static String getDeviceAndroidVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取当前手机系统语言。
     */
    public static String getDeviceDefaultLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取CPU信息
     */
    // 获取CPU名字
    public static String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            for (int i = 0; i < array.length; i++) {
            }
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 获取cpu总数
    public static int getCpuNumCores() {
        class CpuFilter implements FileFilter {

            @Override
            public boolean accept(File file) {
                if (Pattern.matches("cpu[0-9]", file.getName())) {
                    return true;
                }
                return false;
            }
        }
        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            //Print exception
            // Log.d(TAG, "CPU Count: Failed.");
            e.printStackTrace();
            //Default to return 1 core
            return 1;
        }
    }


    // 获取可用的CPU数
    public static int getNumAvailableCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static String getMaxCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "N/A";
        }
        return result.trim() + "Hz";
    }

    // 获取CPU最小频率（单位KHZ）

    public static String getMinCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "N/A";
        }
        return result.trim() + "Hz";
    }

    // 实时获取CPU当前频率（单位KHZ）

    public static String getCurCpuFreq() {
        String result = "N/A";
        try {
            FileReader fr = new FileReader(
                    "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            result = text.trim() + "Hz";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 获取当前系统上的语言列表(Locale列表)
     */
    public static String getDeviceSupportLanguage() {
        Log.e("wangjie", "Local:" + Locale.GERMAN);
        Log.e("wangjie", "Local:" + Locale.ENGLISH);
        Log.e("wangjie", "Local:" + Locale.US);
        Log.e("wangjie", "Local:" + Locale.CHINESE);
        Log.e("wangjie", "Local:" + Locale.TAIWAN);
        Log.e("wangjie", "Local:" + Locale.FRANCE);
        Log.e("wangjie", "Local:" + Locale.FRENCH);
        Log.e("wangjie", "Local:" + Locale.GERMANY);
        Log.e("wangjie", "Local:" + Locale.ITALIAN);
        Log.e("wangjie", "Local:" + Locale.JAPAN);
        Log.e("wangjie", "Local:" + Locale.JAPANESE);
        return Locale.getAvailableLocales().toString();
    }

    public static String getDeviceAllInfo(Context context) {

        JSONObject resultObject = new JSONObject();
        resultObject.put("RAM", SDCardUtils.getRAMInfo(context));
        resultObject.put("ROM", SDCardUtils.getStorageInfo(context, 0));

        return
                //"1. IMEI:\t\t" + "\n"
                //
                //        + "\n2. 设备宽度:\t" + getDeviceWidth(context) + "\n"
                //
                //        + "\n3. 设备高度:\t" + getDeviceHeight(context) + "\n"
                //
                //        + "\n4. 是否有内置SD卡:\t" + SDCardUtils.isSDCardMount() + "\n"

                "\nRAM 信息:\t" + SDCardUtils.getRAMInfo(context) + "\n"
                        + "\nROM 信息:\t" + SDCardUtils.getStorageInfo(context, 0) + "\n"

                        //+ "\n7. SD卡 信息:\t" + SDCardUtils.getStorageInfo(context, 1) + "\n"
                        //
                        //+ "\n8. 是否联网:\t" + "Utils.isNetworkConnected(context)" + "\n"
                        //
                        //+ "\n9. 网络类型:\t" + "Utils.GetNetworkType(context)" + "\n"
                        //
                        //+ "\n10. 系统默认语言:\t" + getDeviceDefaultLanguage() + "\n"
                        //
                        //+ "\n11. 硬件序列号(设备名):\t" + android.os.Build.SERIAL + "\n"
                        //
                        //+ "\n12. 手机型号:\t" + android.os.Build.MODEL + "\n"
                        //
                        //+ "\n13. 生产厂商:\t" + android.os.Build.MANUFACTURER + "\n"
                        //
                        //+ "\n14. 手机Fingerprint标识:\t" + android.os.Build.FINGERPRINT + "\n"
                        //
                        //+ "\n15. Android 版本:\t" + android.os.Build.VERSION.RELEASE + "\n"
                        //
                        //+ "\n16. Android SDK版本:\t" + android.os.Build.VERSION.SDK_INT + "\n"
                        //
                        //+ "\n17. 安全patch 时间:\t" + android.os.Build.VERSION.SECURITY_PATCH + "\n"
                        //
                        //+ "\n18. 发布时间:\t" + "Utils.Utc2Local(android.os.Build.TIME)" + "\n"
                        //
                        //+ "\n19. 版本类型:\t" + android.os.Build.TYPE + "\n"
                        //
                        //+ "\n20. 用户名:\t" + android.os.Build.USER + "\n"
                        //
                        //+ "\n21. 产品名:\t" + android.os.Build.PRODUCT + "\n"
                        //
                        //+ "\n22. ID:\t" + android.os.Build.ID + "\n"
                        //
                        //+ "\n23. 显示ID:\t" + android.os.Build.DISPLAY + "\n"

                        //+ "\n硬件名:\t" + android.os.Build.HARDWARE + "\n"

                        //+ "\n产品名:\t" + android.os.Build.DEVICE + "\n"

                        //+ "\n26. Bootloader:\t" + android.os.Build.BOOTLOADER + "\n"

                        //+ "\n主板名:\t" + android.os.Build.BOARD + "\n"

                        //+ "\n28. CodeName:\t" + android.os.Build.VERSION.CODENAME + "\n"

                        //+ "\n29. 语言支持:\t" + getDeviceSupportLanguage() + "\n"

                        //+ "\nCPU:\t" + getCpuName() + "\n"
                        //+ "\nCPU Name:\t" + new ProcCpuInfo().getCpuInfo() + "\n"
                        //+ "\nCPU Name2:\t" + new ProcCpuInfo().getCpuInfo() + "\n"

                        + "\nCPU总数:\t" + getCpuNumCores() + "\n";
        //+ "\nGPU渲染器:\t" + new GPUUtils().gl_renderer + "\n"
        //+ "\nGPU供应商:\t" + new GPUUtils().gl_vendor + "\n"
        //+ "\nGPU版本:\t" + new GPUUtils().gl_version + "\n"
        //+ "\nGPU扩展名:\t" + new GPUUtils().gl_extensions + "\n";

        //+ "\nCPU可用个数:\t" + getNumAvailableCores() + "\n"

        //+ "\nCPU最大频率:\t" + getMaxCpuFreq() + "\n"

        //+ "\nCPU最小频率:\t" + getMinCpuFreq() + "\n"

        //+ "\nCPU当前频率:\t" + getCurCpuFreq();

    }
}
