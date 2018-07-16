package com.anwesh.uiprojects.lirview

/**
 * Created by anweshmishra on 16/07/18.
 */

import android.view.View
import android.content.Context
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.RectF

val nodes : Int = 5

fun Canvas.drawSquareNode(i : Int, paint : Paint) {
    val gap = (Math.min(width, height).toFloat()) / nodes
    val size : Float = gap * (i + 1)
    drawAtMid {
        drawRoundedSquare(0f, 0f, gap, paint)
    }
}

fun Canvas.drawAtMid(cb : () -> Unit) {
    save()
    translate(width.toFloat()/2, height.toFloat()/2)
    cb()
    restore()
}

fun Canvas.drawRoundedSquare(x : Float, y : Float, a : Float, paint : Paint) {
    save()
    translate(x, y)
    drawRoundRect(RectF(-a/2, -a/2, a/2, a/2), a/5, a/5, paint)
    restore()
}

class LIRView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var prevScale : Float = 0f, var dir : Float = 0f) {

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                cb()
            }
        }

        fun update(cb : (Float) -> Unit) {
            scale += 0.1f * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {
        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }
}