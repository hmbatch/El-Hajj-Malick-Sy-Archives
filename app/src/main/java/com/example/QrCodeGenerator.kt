package com.example

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.EncodeHintType
import java.util.Hashtable

object QrCodeGenerator {
    /**
     * Generates a QR Code Bitmap for the given content.
     * Uses Natural Tones dark forest green for the QR pattern.
     */
    fun generate(content: String, size: Int = 512): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            val hints = Hashtable<EncodeHintType, Any>().apply {
                put(EncodeHintType.MARGIN, 1) // Tight margin for high density
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
            }
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    // Use theme color: NaturalDarkGreen (0xFF131F11) for active pixels
                    pixels[offset + x] = if (bitMatrix.get(x, y)) {
                        0xFF131F11.toInt()
                    } else {
                        0xFFFFFFFF.toInt()
                    }
                }
            }
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
