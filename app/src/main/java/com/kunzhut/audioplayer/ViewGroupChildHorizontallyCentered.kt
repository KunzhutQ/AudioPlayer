package com.kunzhut.audioplayer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Looper
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import kotlin.math.abs


class ViewGroupChildHorizontallyCentered(private val context: Context, attrs: AttributeSet) : ViewGroup(context,attrs) {

    private var textToSet : String
    private val paint = Paint()

    init {
        val typedArray = context.obtainStyledAttributes(attrs,R.styleable.ViewGroupChildHorizontallyCentered)
        textToSet = typedArray.getString(R.styleable.ViewGroupChildHorizontallyCentered_draw_text)?:""
        typedArray.recycle()
    }

    fun setText(text : String){
        textToSet=text
        if(Looper.getMainLooper().isCurrentThread) invalidate() else postInvalidate()

    }
    fun setImage(id : Int, index : Int){
        this.getChildAt(index).setBackgroundResource(id)
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        this.setBackgroundResource(R.color.transparent)
           setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))

    }

    private fun getPxFromDp(a : Float) : Int{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,a, resources.displayMetrics).toInt()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

            this.children.forEach { view ->
                val params = view.layoutParams
                val leftBorder = (width/2)-(params.width/2)
                view.layout(leftBorder, 0,leftBorder+params.width, view.layoutParams.height); }

    }

    override fun onDraw(canvas: Canvas) {
        paint.color=getColor(context,R.color.white_chiffon)
        paint.setTypeface(ResourcesCompat.getFont(context,R.font.bloggersans))
        paint.textSize=22*resources.displayMetrics.scaledDensity

        canvas.drawText(textToSet,(width - paint.textSize * abs(textToSet.length/2))/2,height.toFloat()-getPxFromDp(10f),paint)


    }

}