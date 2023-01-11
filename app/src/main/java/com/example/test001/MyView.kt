package com.example.test001

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.get

/**
 * TODO: document your custom view class.
 */
class MyView : View {
    var data: DataClassesInfo? = null
        set(value) {
            field = value
        }

    var newClassBtn: AppCompatButton? = null

    var iconSize: Int = 0
        private set
    var labelSize: Int = 0
        private set


//    private var _exampleString: String? = null // TODO: use a default from R.string...
//    private var _exampleColor: Int = Color.RED // TODO: use a default from R.color...
//    private var _exampleDimension: Float = 0f // TODO: use a default from R.dimen...
//
//    private lateinit var textPaint: TextPaint
//    private var textWidth: Float = 0f
//    private var textHeight: Float = 0f
//
//    /**
//     * The text to draw
//     */
//    var exampleString: String?
//        get() = _exampleString
//        set(value) {
//            _exampleString = value
//            invalidateTextPaintAndMeasurements()
//        }
//
//    /**
//     * The font color
//     */
//    var exampleColor: Int
//        get() = _exampleColor
//        set(value) {
//            _exampleColor = value
//            invalidateTextPaintAndMeasurements()
//        }
//
//    /**
//     * In the example view, this dimension is the font size.
//     */
//    var exampleDimension: Float
//        get() = _exampleDimension
//        set(value) {
//            _exampleDimension = value
//            invalidateTextPaintAndMeasurements()
//        }
//
//    /**
//     * In the example view, this drawable is drawn above the text.
//     */
//    var exampleDrawable: Drawable? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
//        // Load attributes
//        val a = context.obtainStyledAttributes(
//            attrs, R.styleable.MyView, defStyle, 0
//        )
//
//        _exampleString = a.getString(
//            R.styleable.MyView_exampleString
//        )
//        _exampleColor = a.getColor(
//            R.styleable.MyView_exampleColor,
//            exampleColor
//        )
//        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
//        // values that should fall on pixel boundaries.
//        _exampleDimension = a.getDimension(
//            R.styleable.MyView_exampleDimension,
//            exampleDimension
//        )
//
//        if (a.hasValue(R.styleable.MyView_exampleDrawable)) {
//            exampleDrawable = a.getDrawable(
//                R.styleable.MyView_exampleDrawable
//            )
//            exampleDrawable?.callback = this
//        }
//
//        a.recycle()
//
//        // Set up a default TextPaint object
//        textPaint = TextPaint().apply {
//            flags = Paint.ANTI_ALIAS_FLAG
//            textAlign = Paint.Align.LEFT
//        }
//
//        // Update TextPaint and text measurements from attributes
//        invalidateTextPaintAndMeasurements()
    }

//    private fun invalidateTextPaintAndMeasurements() {
//        textPaint.let {
//            it.textSize = exampleDimension
//            it.color = exampleColor
//            textWidth = it.measureText(exampleString)
//            textHeight = it.fontMetrics.bottom
//        }
//    }

    val paint = Paint().apply { style = Paint.Style.FILL }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (data == null)
            return
        val classes = data!!.classes
        val activeClass = data!!.activeClass

        for (ci in 0 until classes.size) {
            if (ci == activeClass)
                continue
            drawClass(canvas, ci)
        }

        if (activeClass >= 0 && activeClass < classes.size) {
            drawClass(canvas, activeClass)
        }

//        // TODO: consider storing these as member variables to reduce
//        // allocations per draw cycle.
//        val paddingLeft = paddingLeft
//        val paddingTop = paddingTop
//        val paddingRight = paddingRight
//        val paddingBottom = paddingBottom
//
//        val contentWidth = width - paddingLeft - paddingRight
//        val contentHeight = height - paddingTop - paddingBottom

        canvas.drawLine(0f, 0f, 100f, 50f, paint)

//        exampleString?.let {
//            // Draw the text.
//            canvas.drawText(
//                it,
//                paddingLeft + (contentWidth - textWidth) / 2,
//                paddingTop + (contentHeight + textHeight) / 2,
//                textPaint
//            )
//        }
//
//        // Draw the example drawable on top of the text.
//        exampleDrawable?.let {
//            it.setBounds(
//                paddingLeft, paddingTop,
//                paddingLeft + contentWidth, paddingTop + contentHeight
//            )
//            it.draw(canvas)
//        }
    }

    fun drawClass(canvas: Canvas, classIndex: Int) {
        val dataClass = data!!.classes[classIndex]

        if (dataClass.label.iconBitmap == null) {
            if (labelSize <= 0) {
                iconSize = newClassBtn!!.width
                labelSize = (iconSize + 1) / 2
            }

            val iconResNullable = ResourcesCompat.getDrawable(
                resources,
                dataClass.label.iconId,
                null
            )
            val iconRes = iconResNullable!!

            iconRes.colorFilter = MainActivity.makeColorFilter(
                ResourcesCompat.getColor(resources, R.color.black, null),
                dataClass.color
            )

            val bmp = iconRes.toBitmap(iconSize, iconSize, Bitmap.Config.ARGB_8888)

            val w = bmp.width
            val h = bmp.height
            val pixels = IntArray(w * h)
            bmp.getPixels(pixels, 0, w, 0, 0, w, h)

            val colorBlackRgb = Color.BLACK and 0xFFFFFF
            for (i in 0 until pixels.size) {
                if ((pixels[i] and 0xFFFFFF) == colorBlackRgb)
                    pixels[i] = Color.TRANSPARENT
            }

            bmp.setPixels(pixels, 0, w, 0, 0, w, h)

            dataClass.label.iconBitmap = Bitmap.createScaledBitmap(bmp, labelSize, labelSize, true)
        }
        val iconBitmap = dataClass.label.iconBitmap!!
        val labelSize2 = labelSize / 2.0f

        val points = dataClass.points
        val dim = DataClass.DIMENSION
        for (i in 0 until dataClass.numPoints) {
            val offset = i * dim
            canvas.drawBitmap(
                iconBitmap,
                points[offset] - labelSize2,
                points[offset + 1] - labelSize2,
                null
            )
        }
    }
}
