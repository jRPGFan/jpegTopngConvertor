package com.example.jpegtopngconvertor.tools

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import io.reactivex.rxjava3.core.Single
import java.io.File

class ImageConvertor(private val context: Context) : IImageConvertor {
    override fun convertJpegToPng(uriTarget: Uri): Single<Uri> =
        Single.create { emitter ->
            try {
                val pngFilePath = getPathFromUri(uriTarget)
                val jpeg = MediaStore.Images.Media.getBitmap(context.contentResolver, uriTarget)
                val resultFile = File(pngFilePath)
                val png = jpeg.compress(Bitmap.CompressFormat.PNG, 100, resultFile.outputStream())
                if (png)
                    emitter.onSuccess(Uri.fromFile(resultFile))
                else
                    emitter.onError(Exception("Conversion Error!"))
            } catch (e: Throwable) {
                emitter.onError(e)
            }
        }

    private fun getPathFromUri(contentUri: Uri): String {
        var res: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(contentUri, projection, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(projection[0])
            columnIndex.let {
                res = cursor.getString(columnIndex)
                res = res?.substring(0, res!!.lastIndexOf('.'))
            }
            cursor.close()
        }
        return "$res.png"
    }
}