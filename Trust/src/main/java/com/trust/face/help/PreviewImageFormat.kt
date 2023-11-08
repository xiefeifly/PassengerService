package com.trust.face.help

/**
 *
 */
enum class PreviewImageFormat(val format: Int) {
    PIX_FMT_GRAY8(0),
    PIX_FMT_NV12(1),
    PIX_FMT_NV21(2),
    PIX_FMT_BGRA8888(3),
    PIX_FMT_BGR888(4),
    PIX_FMT_RGB888(
        5
    ),
    PIX_FMT_RGBA888(6);

}