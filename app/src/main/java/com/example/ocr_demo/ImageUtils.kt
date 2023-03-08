package com.example.ocr_demo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.camera.core.impl.utils.Exif
import androidx.core.net.toFile

@SuppressLint("RestrictedApi")
fun createBitMap(context: Context, uri: Uri): Bitmap? {
    var sampleSize = 1
    context.contentResolver.openInputStream(uri)?.use { inputStream ->
        BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, this)
            sampleSize = calculateInSampleSize(this)
        }
    }
    context.contentResolver.openInputStream(uri)?.use { inputStream ->
        return BitmapFactory.Options().run {
            inSampleSize = sampleSize
            inJustDecodeBounds = false
            val rotation =  kotlin.runCatching { Exif.createFromFile(uri.toFile()).rotation }.getOrDefault(0)
            BitmapFactory.decodeStream(inputStream, null, this)?.rotate(rotation)
        }
    }
    return null
}

fun Bitmap.rotate(degrees: Int): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

private fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int = 640,
    reqHeight: Int = 480
): Int {
    val (height: Int, width: Int) = options.run { outHeight to outWidth }
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {

        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}