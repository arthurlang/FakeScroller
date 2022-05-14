package com.lj.fakescroller

import android.content.Context

private const val DEFAULT_DURATION: Long = 500
open class TouchUtils {
    companion object {
        fun simulateScroll(context: Context?, startX: Float, startY: Float, endX: Float, endY: Float) {
            ScrollTask(
                context,
                startX,
                startY,
                endX,
                endY,
                DEFAULT_DURATION
            ).run()
        }
    }
}
