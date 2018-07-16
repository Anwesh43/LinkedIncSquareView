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
}