package com.example.finalapp.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;

import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class GetDeviceUtil {
    @SuppressLint("HardwareIds")
    public static String getEncodedDeviceFingerprint(Context context) {
        Map<String, String> fingerprint = new HashMap<>();
        fingerprint.put("deviceModel", Build.MODEL);              // 设备型号
        fingerprint.put("manufacturer", Build.MANUFACTURER);      // 厂商
        fingerprint.put("deviceBrand", Build.BRAND);              // 品牌
        fingerprint.put("androidVersion", Build.VERSION.RELEASE); // 系统版本
        fingerprint.put("androidID", Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID)); // Android ID

        // 将Map转为JSON字符串
        String json = new Gson().toJson(fingerprint);

        // Base64编码
        return Base64.encodeToString(json.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
    }

    public static String sha256Hex(String input) {
        try {
            // 获取 MessageDigest 实例
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // 对输入字符串进行哈希计算
            byte[] hashBytes = digest.digest(input.getBytes());
            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // 如果算法不可用，抛出异常
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
