package com.kunzhut.audioplayer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Looper
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.getDrawableOrThrow
import kotlin.math.abs

class ImageViewTextPosImpl(private val context: Context, attrs : AttributeSet) : androidx.appcompat.widget.AppCompatImageView(context, attrs) {


    private var textToSet : String
    private var imageSrc : Drawable
    private var srcWidth : Int
    private var srcHeight : Int
    private val paint = Paint()

    init {
       val typedArray = context.obtainStyledAttributes(attrs,R.styleable.ImageViewTextPosImpl)
        textToSet = typedArray.getString(R.styleable.ImageViewTextPosImpl_text)?:""
        imageSrc = typedArray.getDrawableOrThrow(R.styleable.ImageViewTextPosImpl_source_reference)
        srcWidth=typedArray.getDimensionPixelSize(R.styleable.ImageViewTextPosImpl_source_width,0)
        srcHeight=typedArray.getDimensionPixelSize(R.styleable.ImageViewTextPosImpl_source_height,0)
        typedArray.recycle()
    }

    fun setColorToSource(color : Int){
         imageSrc.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        if(Looper.getMainLooper().isCurrentThread) invalidate() else postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val leftBorder = width/2-srcWidth/2
        imageSrc.setBounds(leftBorder,height-srcHeight,leftBorder+srcWidth,height)
        imageSrc.draw(canvas)
        paint.color= ContextCompat.getColor(context, R.color.gold)
        paint.setTypeface(ResourcesCompat.getFont(context,R.font.bloggersans))
        paint.textSize=15*resources.displayMetrics.scaledDensity
        canvas.drawText(textToSet,(width - paint.textSize * abs(textToSet.length/2))/2,paint.textSize,paint)

    }
}