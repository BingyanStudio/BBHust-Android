package com.bingyan.bbhust.utils

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Locale


object FileType {
    private val mFileTypes = HashMap<String, String>()

    init {
        //images
        mFileTypes["FFD8"] = "jpg"
        mFileTypes["8950"] = "png"
        mFileTypes["4749"] = "gif"
        mFileTypes["5249"] = "webp"
        mFileTypes["4949"] = "tif"
        mFileTypes["424D"] = "bmp"
    }

    val File.guessSuffix: String?
        get() {
            val guess = mFileTypes[getFileHeader(this)]
            println("File:${this.absolutePath},Guess:${this.nameWithoutExtension}.$guess")
            return guess
        }

    //获取文件头信息
    private fun getFileHeader(file: File): String? {
        var inputStream: FileInputStream? = null
        var value: String? = null
        try {
            inputStream = file.inputStream()
            val b = ByteArray(2)
            inputStream.read(b, 0, b.size)
            value = b.hexString
        } catch (_: Exception) {
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (_: IOException) {
                }
            }
        }
        return value
    }

    private val ByteArray.hexString: String
        get() {
            val builder = StringBuilder()
            if (isEmpty()) {
                return ""
            }
            var hv: String
            for (i in indices) {
                hv = Integer.toHexString(get(i).toInt() and 0xFF).uppercase(Locale.getDefault())
                if (hv.length < 2) {
                    builder.append(0)
                }
                builder.append(hv)
            }
            return builder.toString()
        }
}