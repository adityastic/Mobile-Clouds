package com.adityagupta.mobileclouds.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File


object ActionHelper {
    private fun getMimeType(url: String?): String? {
        val ext = MimeTypeMap.getFileExtensionFromUrl(url)
        var mime: String? = null
        if (ext != null) {
            mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
        }
        return mime
    }

    fun getFileIntent(context: Context, file: File): Intent {
        val path: Uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val chooser = Intent.createChooser(intent, "Open File With")
        intent.setDataAndType(path, getMimeType(file.absolutePath))
        return chooser
    }
}