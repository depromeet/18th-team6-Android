package com.obrit.feature.register.screen.receipt

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import java.io.ByteArrayOutputStream

/**
 * 영수증 이미지를 업로드용 JPEG ByteArray로 변환한다.
 *
 * 원본(수 MB)을 그대로 올리면 서버가 413(Request Entity Too Large)으로 거부하므로,
 * 긴 변 기준 다운스케일 + JPEG 재압축으로 [MAX_UPLOAD_BYTES] 이하로 줄인다.
 * 재인코딩 시 사라지는 EXIF 회전은 픽셀에 직접 반영해 OCR 정확도를 유지한다.
 */
internal fun Context.readReceiptImage(uri: Uri): ByteArray? =
    runCatching {
        val bitmap = decodeScaledBitmap(uri) ?: return@runCatching null
        bitmap
            .applyExifOrientation(this, uri)
            .compressUnder(MAX_UPLOAD_BYTES)
    }.onFailure { Log.e(IMAGE_LOG_TAG, "receipt image load failed", it) }
        .getOrNull()

private fun Context.decodeScaledBitmap(uri: Uri): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
    val options =
        BitmapFactory.Options().apply {
            inSampleSize = sampleSizeFor(bounds.outWidth, bounds.outHeight)
        }
    return contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
}

private fun sampleSizeFor(
    width: Int,
    height: Int,
): Int {
    var sample = 1
    var longEdge = maxOf(width, height)
    while (longEdge / 2 >= MAX_IMAGE_DIMENSION) {
        longEdge /= 2
        sample *= 2
    }
    return sample
}

private fun Bitmap.applyExifOrientation(
    context: Context,
    uri: Uri,
): Bitmap {
    val orientation =
        runCatching {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                ExifInterface(stream).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL,
                )
            }
        }.getOrNull() ?: ExifInterface.ORIENTATION_NORMAL
    val degrees =
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> return this
        }
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

/**
 * [maxBytes] 이하 JPEG ByteArray를 보장한다.
 * 품질을 [MIN_QUALITY]까지 낮춰도 예산을 못 맞추면 해상도를 단계적으로 줄여 재시도한다.
 * (서버 body 제한이 우리 예산보다 작으면 413이 나므로, 예산은 반드시 강제되어야 한다.)
 */
private fun Bitmap.compressUnder(maxBytes: Int): ByteArray {
    var current = this
    while (true) {
        val bytes = current.compressWithFloor(maxBytes)
        val longEdge = maxOf(current.width, current.height)
        if (bytes.size <= maxBytes || longEdge <= MIN_SCALE_DIMENSION) return bytes
        val scaled =
            Bitmap.createScaledBitmap(
                current,
                (current.width * DOWNSCALE_FACTOR).toInt(),
                (current.height * DOWNSCALE_FACTOR).toInt(),
                true,
            )
        if (current !== this) current.recycle()
        current = scaled
    }
}

private fun Bitmap.compressWithFloor(maxBytes: Int): ByteArray {
    var quality = INITIAL_QUALITY
    var bytes = compressToJpeg(quality)
    while (bytes.size > maxBytes && quality > MIN_QUALITY) {
        quality -= QUALITY_STEP
        bytes = compressToJpeg(quality)
    }
    return bytes
}

private fun Bitmap.compressToJpeg(quality: Int): ByteArray =
    ByteArrayOutputStream().use { output ->
        compress(Bitmap.CompressFormat.JPEG, quality, output)
        output.toByteArray()
    }

// Gemini는 768px 타일로 분할 처리하므로 ~2048px까지 해상도가 OCR 정확도에 실질적으로 기여한다.
// 서버가 webp 업로드를 거부해 JPEG로 인코딩한다(서버 개발자 확인).
// MAX_UPLOAD_BYTES는 nginx 기본 client_max_body_size(1m=1,048,576) 미만으로 두어 413을 피한다.
// 서버 body 제한이 풀리면 더 올릴 수 있다.
private const val MAX_IMAGE_DIMENSION = 2048
private const val MAX_UPLOAD_BYTES = 1_000_000
private const val INITIAL_QUALITY = 90
private const val MIN_QUALITY = 60
private const val QUALITY_STEP = 5
private const val DOWNSCALE_FACTOR = 0.85f
private const val MIN_SCALE_DIMENSION = 1080
private const val IMAGE_LOG_TAG = "ReceiptImageLoader"
