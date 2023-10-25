package com.bingyan.bbhust.utils

import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import com.bingyan.bbhust.App
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.EncryptUtils
import java.io.File
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

object AppUtils {
    /**
     * 获取当前apk的版本号
     */
    val versionCode: Long
        get() {
            var versionCode = 0L
            try {
                //获取软件版本号，对应AndroidManifest.xml下android:versionCode
                val info = App.CONTEXT.packageManager.getPackageInfo(
                    App.CONTEXT.packageName, 0
                )
                versionCode =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) info.longVersionCode
                    else info.versionCode.toLong()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return versionCode
        }

    /**
     * 获取当前apk的版本名
     */
    val versionName: String
        get() {
            var versionName = ""
            try {
                //获取软件版本号，对应AndroidManifest.xml下android:versionName
                versionName = App.CONTEXT.packageManager.getPackageInfo(
                    App.CONTEXT.packageName, 0
                ).versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return versionName
        }

    //首次安装时间
    val firstInstallTime: Long
        get() {
            var value = System.currentTimeMillis()
            try {
                value = App.CONTEXT.packageManager.getPackageInfo(
                    App.CONTEXT.packageName, 0
                ).firstInstallTime
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return value
        }

    const val rawSignature = "87367F4C5A19CBDACA688594F665AB73"
    val signature: String
        @SuppressLint("PackageManagerGetSignatures")
        get() {
            var nowSignMD5 = ""
            val signs: Array<Signature>?
            try {
                // 得到签名 MD5
                signs = if (Build.VERSION.SDK_INT >= 28) {
                    val packageInfo: PackageInfo = App.CONTEXT.packageManager.getPackageInfo(
                        App.CONTEXT.packageName, PackageManager.GET_SIGNING_CERTIFICATES
                    )
                    packageInfo.signingInfo.apkContentsSigners
                } else {
                    val packageInfo: PackageInfo = App.CONTEXT.packageManager.getPackageInfo(
                        App.CONTEXT.packageName, PackageManager.GET_SIGNATURES
                    )
                    packageInfo.signatures
                }
                val signBase64: ByteArray? = EncodeUtils.base64Encode(signs[0].toByteArray())
                nowSignMD5 = EncryptUtils.encryptMD5ToString(signBase64)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return nowSignMD5
        }

    val crc: String
        get() {
            val zf: ZipFile
            try {
                zf = ZipFile(App.CONTEXT.packageCodePath)
                val ze: ZipEntry = zf.getEntry("classes.dex")
                return ze.crc.toString(10)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return "Null"
        }

    val apk = File(App.CONTEXT.packageResourcePath)
}