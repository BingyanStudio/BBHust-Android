package com.bingyan.bbhust.ui.provider

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper
import com.bingyan.bbhust.utils.save


data class ChooseFiles(val input: String = "image/*", val callback: (List<String>) -> Unit)

/**
 * Get files
 *
 * @constructor Create empty Get images
 */
open class Picker :
    ActivityResultContract<ChooseFiles, List<@JvmSuppressWildcards Uri>>() {
    var input = ChooseFiles {}

    @CallSuper
    override fun createIntent(context: Context, input: ChooseFiles): Intent {
        this.input = input
        return Intent(Intent.ACTION_GET_CONTENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType(input.input)
            .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    }

    final override fun getSynchronousResult(
        context: Context,
        input: ChooseFiles
    ): SynchronousResult<List<Uri>>? = null

    final override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> {
        val result = intent.takeIf {
            resultCode == Activity.RESULT_OK
        }?.getClipDataUris() ?: emptyList()
        if (result.isNotEmpty()) {
            val res = result.mapNotNull {
                it.save("tmp")
            }
            if (res.isNotEmpty()) {
                input.callback(res)
            }
        }
        return result
    }

    internal companion object {
        internal fun Intent.getClipDataUris(): List<Uri> {
            // Use a LinkedHashSet to maintain any ordering that may be
            // present in the ClipData
            val resultSet = LinkedHashSet<Uri>()
            data?.let { data ->
                resultSet.add(data)
            }
            val clipData = clipData
            if (clipData == null && resultSet.isEmpty()) {
                return emptyList()
            } else if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    if (uri != null) {
                        resultSet.add(uri)
                    }
                }
            }
            return ArrayList(resultSet)
        }
    }
}