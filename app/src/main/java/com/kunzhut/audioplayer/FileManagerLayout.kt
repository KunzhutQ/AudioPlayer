package com.kunzhut.audioplayer

import android.content.Context
import android.os.Looper
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView


class FileManagerLayout(private val context: Context, attributeSet: AttributeSet) : RelativeLayout(context,attributeSet) {

    private var titleText : String

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.FileManagerLayout)
        titleText = typedArray.getString(R.styleable.FileManagerLayout_title_text)?:""
        typedArray.recycle()
    }

    fun setFolderPath(path : String){
        titleText=path
        layout(0,0,0,0)
        if(Looper.getMainLooper().isCurrentThread) invalidate() else postInvalidate()

    }
    private fun getPxFromDp(a : Float) : Int{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,a, resources.displayMetrics).toInt()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        ((this.getChildAt(0) as ViewGroup).getChildAt(0) as TextView).text=titleText

     // val uSc = (20*resources.displayMetrics.scaledDensity*2).toInt()

     // val textView = this.getChildAt(0) as TextView
     // textView.text=titleText
     // textView.movementMethod= ScrollingMovementMethod()
     // textView.layout(0,0,this.width,uSc)

     // this.getChildAt(1).layout(0, uSc, this.width, this.height)

     // val buttonPx = getPxFromDp(15f)

     // val button = this.getChildAt(2)
     // button.measure(0,0)
     // button.layout(this.width-getPxFromDp(85f)-buttonPx,this.height-buttonPx*4,this.width-buttonPx,this.height-buttonPx)

    }

}