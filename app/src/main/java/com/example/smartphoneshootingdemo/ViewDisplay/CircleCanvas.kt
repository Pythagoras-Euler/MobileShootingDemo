package com.example.viewtest2.ViewDisplay

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import com.example.viewtest2.ViewDisplay.CircleCanvas.CircleInfo
import com.example.viewtest2.ViewDisplay.CircleCanvas
import java.util.AbstractCollection
import java.util.ArrayList

class CircleCanvas(context: Context?) : View(context) {
    @JvmField

    //  保存绘制历史
    public var mCircleInfos: MutableList<CircleInfo?> = ArrayList()

    // 保存实心圆相关信息的类
    class CircleInfo {
        var x //  圆心横坐标
                = 0f
        var y //  圆心纵坐标
                = 0f
        var radius //  半径
                = 0f
        var color //  画笔的颜色
                = 0
    }

    //  当画布重绘时调用该方法，Canvas表示画布对象，可以在该对象上绘制基本的图形
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //  根据保存的绘制历史重绘所有的实心圆
        for (circleInfo in mCircleInfos) {
            val paint = Paint()
            //  设置画笔颜色
            paint.color = circleInfo!!.color
            //  绘制实心圆
            canvas.drawCircle(circleInfo.x, circleInfo.y, circleInfo.radius, paint)
        }
    }
}