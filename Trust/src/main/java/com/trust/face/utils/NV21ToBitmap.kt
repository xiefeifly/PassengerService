package com.trust.face.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.renderscript.*
import com.trust.face.utils.NV21ToBitmap

/**
 * @author zhangx by 2018-12-27
 * @description
 */
class NV21ToBitmap(context: Context?) {
    private val rs: RenderScript
    private val yuvToRgbIntrinsic: ScriptIntrinsicYuvToRGB
    private var yuvType: Type.Builder? = null
    private var rgbaType: Type.Builder? = null
    private var `in`: Allocation? = null
    private var out: Allocation? = null
    private val matrix: Matrix

    init {
        rs = RenderScript.create(context)
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs))
        matrix = Matrix()
        matrix.postRotate(270f)
    }

    fun nv21ToBitmap(nv21: ByteArray, width: Int, height: Int): Bitmap {
        if (yuvType == null) {
            yuvType = Type.Builder(rs, Element.U8(rs)).setX(nv21.size)
            `in` = Allocation.createTyped(rs, yuvType?.create(), Allocation.USAGE_SCRIPT)
            rgbaType = Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height)
            out = Allocation.createTyped(rs, rgbaType?.create(), Allocation.USAGE_SCRIPT)
        }
        `in`!!.copyFrom(nv21)
        yuvToRgbIntrinsic.setInput(`in`)
        yuvToRgbIntrinsic.forEach(out)
        val bmpout = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        out!!.copyTo(bmpout)
        return bmpout
    }

    fun nv21ToRGBA(nv21: ByteArray, width: Int, height: Int): ByteArray {
        if (yuvType == null) {
            yuvType = Type.Builder(rs, Element.U8(rs)).setX(nv21.size)
            `in` = Allocation.createTyped(rs, yuvType?.create(), Allocation.USAGE_SCRIPT)
            rgbaType = Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height)
            out = Allocation.createTyped(rs, rgbaType?.create(), Allocation.USAGE_SCRIPT)
        }
        `in`!!.copyFrom(nv21)
        yuvToRgbIntrinsic.setInput(`in`)
        yuvToRgbIntrinsic.forEach(out)
        val outBuffer = ByteArray(width * height * 4)
        out!!.copyTo(outBuffer)
        return outBuffer
    }

    companion object {
        @Volatile
        private var nv21ToBitmap: NV21ToBitmap? = null
        fun getInstance(context: Context?): NV21ToBitmap? {
            if (null == nv21ToBitmap) {
                if (nv21ToBitmap == null) {
                    synchronized(NV21ToBitmap::class.java) {
                        nv21ToBitmap = NV21ToBitmap(context)
                    }
                }
            }
            return nv21ToBitmap
        }
    }
}