package com.bingyan.bbhust.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.text.TextUtils
import androidx.annotation.ColorInt
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.CharacterSetECI
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.Hashtable

//
//object ShareUtils {
//	fun shareImage(path: String, componentName: ComponentName?) {
//		val file = File(path)
//		val uri = UriUtils.file2Uri(file)
//		val intent = Intent().apply {
//			action = Intent.ACTION_SEND //设置分享行为
//			type = "image/*" //设置分享内容的类型
//			putExtra(Intent.EXTRA_STREAM, uri)
//			putExtra(Intent.EXTRA_SUBJECT, R.string.share.string)
//			component = componentName
//		}
//
//		val shareIntent = Intent.createChooser(intent, R.string.share.string)
//		Msg.event(GlobalEvent.JumpIntent(shareIntent))
//	}
//
//
//	/**
//	[分享到酷安],name:com.coolapk.market.view.feedv8.ShareFeedV8Activity,package:com.coolapk.market
//	[支付宝],name:com.alipay.mobile.quinox.splash.ShareDispenseActivity,package:com.eg.android.AlipayGphone
//	[抖音],name:com.ss.android.ugc.aweme.share.SystemShareRealActivity,package:com.ss.android.ugc.aweme
//	[发送给朋友],name:com.tencent.mm.ui.tools.ShareImgUI,package:com.tencent.mm
//	[发送到微信状态],name:com.tencent.mm.ui.tools.ShareToStatusUI,package:com.tencent.mm
//	[添加到微信收藏],name:com.tencent.mm.ui.tools.AddFavoriteUI,package:com.tencent.mm
//	[发送到朋友圈],name:com.tencent.mm.ui.tools.ShareToTimeLineUI,package:com.tencent.mm
//	[发送给好友],name:com.tencent.mobileqq.activity.JumpActivity,package:com.tencent.mobileqq
//	[发送到我的电脑],name:com.tencent.mobileqq.activity.qfileJumpActivity,package:com.tencent.mobileqq
//	[保存到QQ收藏],name:cooperation.qqfav.widget.QfavJumpActivity,package:com.tencent.mobileqq
//	[微博轻享版],name:com.weico.international.activity.compose.SeaComposeActivity,package:com.weico.international
//	 */
//
//	/**
//	 * 获取可选的分享对象
//	 */
//	fun getResolver(intent: Intent) {
//		val packageManager: PackageManager = App.CONTEXT.packageManager
//
//		val resolveInfoList: List<ResolveInfo> = packageManager
//			.queryIntentActivities(
//				intent,
//				PackageManager.GET_INTENT_FILTERS
//			)
//
//		var componentName: ComponentName? = null
//
//		resolveInfoList.forEach { r ->
//			Log.i(
//				"Resolve",
//				"[${r.loadLabel(App.CONTEXT.packageManager)}],name:${r.activityInfo.name},package:${r.activityInfo.packageName}"
//			)
//			if (TextUtils.equals(
//					r.activityInfo.packageName,
//					"YouNeedAppPackageName"
//				)
//			) {
//				componentName = ComponentName(
//					"com.tencent.mobileqq",
//					r.activityInfo.name
//				)
//				return@forEach
//			}
//		}
//	}
//
//	fun shareText(text: String) {
//		val intent = Intent().apply {
//			action = Intent.ACTION_SEND //设置分享行为
//			type = "text/plain" //设置分享内容的类型
//			putExtra(Intent.EXTRA_TEXT, text)
//			putExtra(Intent.EXTRA_SUBJECT, R.string.share.string)
//		}
//		val shareIntent = Intent.createChooser(intent, R.string.share.string)
//		Msg.event(GlobalEvent.JumpIntent(shareIntent))
//	}
//
//	fun share(post: PostsQuery.Post) {
//		Msg.post.value = post
//	}
//
//	fun share(src: String) {
//		GlobalScope.launch {
//			val loader = Mojito.imageLoader() ?: return@launch Msg.toast(R.string.load_fail.string)
//			if (loader is CoilImageLoader) {
//				val file = loader.getCoilCacheFile(src)
//				val uri = UriUtils.file2Uri(file)
//				val intent = Intent().apply {
//					action = Intent.ACTION_SEND //设置分享行为
//					type = "image/*" //设置分享内容的类型
//					putExtra(Intent.EXTRA_STREAM, uri)
//					putExtra(Intent.EXTRA_SUBJECT, R.string.share.string)
//				}
//
//				val shareIntent = Intent.createChooser(intent, R.string.share.string)
//				Msg.event(GlobalEvent.JumpIntent(shareIntent))
//			} else {
//				Msg.toast(R.string.internal_error.string)
//			}
//		}
//	}
//
//	fun qrcode(postId: String): Bitmap? {
//		return QRCodeUtil.createQRCodeBitmap(
//			getLink(postId),
//			256,
//			256
//		)
//	}
//
//	fun getLink(id: String) = "${schemaHttps}${domain}/post/detail?id=${id}"
//	fun savePictures(srcList: List<String>) {
//		GlobalScope.launch {
//			val loader = Mojito.imageLoader() ?: return@launch Msg.toast(R.string.load_fail.string)
//			if (loader is CoilImageLoader) {
//				var suc = 0
//				var failed = 0
//				srcList.forEach { src ->
//					val file = loader.getCoilCacheFile(src)
//					val bm = file?.readBytes()
//					if (bm != null) {
//						if (App.CONTEXT.save(bm, md5(bm))) {
//							suc++
//						} else {
//							failed++
//						}
//					} else {
//						//wait_for_loaded
//						failed++
//					}
//				}
//				if (failed == 0) {
//					Msg.toast(string(if (suc == 1) R.string.saved else R.string.saved_all))
//				} else {
//					Msg.toast(string(R.string.saved_suc_count, suc, failed))
//				}
//			} else {
//				Msg.toast(R.string.internal_error.string)
//			}
//		}
//	}
//
//	/**
//	 * 保存图片
//	 */
//	fun Context.save(byteArray: ByteArray, name: String): Boolean {
//		val values = ContentValues()
//		val options = BitmapFactory.Options()
//		options.inJustDecodeBounds = true
//		BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
//		values.put(MediaStore.MediaColumns.DISPLAY_NAME, name.split("/").last())
//		values.put(MediaStore.MediaColumns.MIME_TYPE, options.outMimeType)
//
//		val path = if (name.contains("/")) {
//			"${Environment.DIRECTORY_PICTURES}/BBHUST/${name.split("/").first()}"
//		} else {
//			"${Environment.DIRECTORY_PICTURES}/BBHUST"
//		}
//		values.put(MediaStore.MediaColumns.RELATIVE_PATH, path);
//		var uri: Uri? = null
//		var outputStream: OutputStream? = null
//		try {
//			uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//			outputStream = contentResolver.openOutputStream(uri!!)!!
//			outputStream.write(byteArray)
//			outputStream.flush()
//			outputStream.close()
//		} catch (e: Exception) {
//			if (uri != null) {
//				contentResolver.delete(uri, null, null);
//			}
//			outputStream?.close()
//			return false
//		}
//		outputStream.close()
//		return true
//	}
//
//	/**
//	 * 检查图片是否存在
//	 * [参考链接](https://blog.csdn.net/asd912756674/article/details/114845358)
//	 */
//	fun Context.exist(name: String): Boolean {
//		val projection = arrayOf(
//			MediaStore.Images.Media._ID,
//		)
//		val path = if (name.contains("/")) {
//			"${Environment.DIRECTORY_PICTURES}/BBHUST/${name.split("/").first()}"
//		} else {
//			"${Environment.DIRECTORY_PICTURES}/BBHUST"
//		}
//		val selection =
//			"${MediaStore.Images.Media.RELATIVE_PATH} LIKE ? AND ${MediaStore.Images.Media.DISPLAY_NAME} = ?"
//		val selectionArgs = arrayOf(
//			"%${path}%",
//			name.split("/").last(),
//		)
//		val sortOrder = "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
//		val query = contentResolver.query(
//			MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//			projection,
//			selection,
//			selectionArgs,
//			sortOrder
//		)
//		query?.use { cursor ->
//			while (cursor.moveToNext()) {
//				return true
//			}
//		}
//		return false
//	}
//
//
//}

/**
 * @ClassName: QRCodeUtil
 * @Description: 二维码工具类
 * @Author Wangnan
 * @Date 2017/2/8
 */
object QRCodeUtil {
    /**
     * 创建二维码位图
     *
     * @param content 字符串内容(支持中文)
     * @param width 位图宽度(单位:px)
     * @param height 位图高度(单位:px)
     * @return
     */
    fun createQRCodeBitmap(content: String?, width: Int, height: Int): Bitmap? {
        return createQRCodeBitmap(
            content,
            width,
            height,
            "UTF-8",
            "H",
            "2",
            Color.BLACK,
            Color.WHITE
        )
    }

    /**
     * 创建二维码位图 (支持自定义配置和自定义样式)
     *
     * @param content 字符串内容
     * @param width 位图宽度,要求>=0(单位:px)
     * @param height 位图高度,要求>=0(单位:px)
     * @param character_set 字符集/字符转码格式 (支持格式:[CharacterSetECI])。传null时,zxing源码默认使用 "ISO-8859-1"
     * @param error_correction 容错级别 (支持级别:[ErrorCorrectionLevel])。传null时,zxing源码默认使用 "L"
     * @param margin 空白边距 (可修改,要求:整型且>=0), 传null时,zxing源码默认使用"4"。
     * @param color_black 黑色色块的自定义颜色值
     * @param color_white 白色色块的自定义颜色值
     * @return
     */
    fun createQRCodeBitmap(
        content: String?,
        width: Int,
        height: Int,
        character_set: String?,
        error_correction: String?,
        margin: String?,
        @ColorInt color_black: Int,
        @ColorInt color_white: Int
    ): Bitmap? {
        /** 1.参数合法性判断  */
        if (TextUtils.isEmpty(content)) { // 字符串内容判空
            return null
        }
        if (width < 0 || height < 0) { // 宽和高都需要>=0
            return null
        }
        try {
            /** 2.设置二维码相关配置,生成BitMatrix(位矩阵)对象  */
            val hints: Hashtable<EncodeHintType, String?> = Hashtable()
            if (!TextUtils.isEmpty(character_set)) {
                hints.put(EncodeHintType.CHARACTER_SET, character_set) // 字符转码格式设置
            }
            if (!TextUtils.isEmpty(error_correction)) {
                hints.put(EncodeHintType.ERROR_CORRECTION, error_correction) // 容错级别设置
            }
            if (!TextUtils.isEmpty(margin)) {
                hints.put(EncodeHintType.MARGIN, margin) // 空白边距设置
            }
            val bitMatrix =
                QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)

            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值  */
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (bitMatrix[x, y]) {
                        pixels[y * width + x] = color_black // 黑色色块像素设置
                    } else {
                        pixels[y * width + x] = color_white // 白色色块像素设置
                    }
                }
            }
            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,之后返回Bitmap对象  */
            val bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }
}
