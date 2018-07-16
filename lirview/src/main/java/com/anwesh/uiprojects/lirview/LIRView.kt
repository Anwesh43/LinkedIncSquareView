package com.anwesh.uiprojects.lirview

/**
 * Created by anweshmishra on 16/07/18.
 */

import android.view.View
import android.content.Context
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
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

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
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

    data class IRNode(var i : Int, val state : State = State()) {
        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }

        private var next : IRNode? = null

        private var prev : IRNode? = null

        fun addNeighbor() {
            if (i < nodes- 1) {
                next = IRNode(i + 1)
                next?.prev = this
            }
        }

        init {
            addNeighbor()
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawSquareNode(i, paint)
            next?.draw(canvas, paint)
        }

        fun getNext(dir : Int, cb : () -> Unit) : IRNode {
            var curr : IRNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class LinkedIR(var i : Int) {

        var curr : IRNode = IRNode(0)

        var dir : Int = 1

        fun update(stopcb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                stopcb(it)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            curr.startUpdating(startcb)
        }

        fun draw(canvas : Canvas, paint : Paint) {
            paint.strokeWidth = Math.min(canvas.width, canvas.height).toFloat() / 60
            paint.strokeCap = Paint.Cap.ROUND
            curr.draw(canvas, paint)
        }
    }

    data class Renderer(var view : LIRView) {

        val lir : LinkedIR = LinkedIR(0)

        val animator : Animator = Animator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            paint.color = Color.parseColor("#2ecc71")
            lir.draw(canvas, paint)
            animator.animate {
                lir.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            lir.startUpdating {
                animator.start()
            }
        }
    }
}