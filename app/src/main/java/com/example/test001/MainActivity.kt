package com.example.test001

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import kotlin.math.min

class MainActivity : AppCompatActivity() {
    var data: DataClassesInfo =  DataClassesInfo()

    lateinit var viewClassification: MyView
    lateinit var viewClasses: LinearLayout
    lateinit var newClassBtn: AppCompatButton

    val classTag = "Test001"

    companion object {
        fun makeColorFilter(backColor: Int, frontColor: Int): ColorMatrixColorFilter {
            val backRed = Color.red(backColor).toFloat()
            val backGreen = Color.green(backColor).toFloat()
            val backBlue = Color.blue(backColor).toFloat()

            val frontRed = Color.red(frontColor).toFloat()
            val frontGreen = Color.green(frontColor).toFloat()
            val frontBlue = Color.blue(frontColor).toFloat()

            return ColorMatrixColorFilter(
                floatArrayOf(
                    (frontRed - backRed) / 255, 0f, 0f, 0f, backRed,
                    0f, (frontGreen - backGreen) / 255, 0f, 0f, backGreen,
                    0f, 0f, (frontBlue - backBlue) / 255, 0f, backBlue,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        }

        val labelsWheel = Wheel(
            listOf(
                LabelInfo(R.string.cross, R.drawable.ic_class_cross),
                LabelInfo(R.string.plus, R.drawable.ic_class_plus),
                LabelInfo(R.string.circle, R.drawable.ic_class_circle),
                LabelInfo(R.string.square, R.drawable.ic_class_square),
                LabelInfo(R.string.rhombus, R.drawable.ic_class_rhombus),
            )
        )

        val colorsWheel = Wheel(
            listOf(
                0x0000FF,
                0x008000,
                0xFF0000,
                0x00FFFF,
                0xFF00FF,
                0xFFFF00
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewClassification = findViewById(R.id.classificationView)
        viewClassification.data = data;
        viewClassification.setOnTouchListener(this::onTouchClassificationView)

        viewClasses = findViewById(R.id.classesContainer)
        newClassBtn = findViewById(R.id.buttonPlus)
        viewClassification.newClassBtn = newClassBtn
    }

    fun onPlusClassButton(view: View) {
        if (data.activeClass >= 0) {
            viewClasses[data.activeClass].background.colorFilter = makeColorFilter(
                ResourcesCompat.getColor(resources, R.color.unselected_class_background, null),
                data.classes[data.activeClass].color
            )
        }

        data.activeClass = data.classes.size

        val newClass = DataClass(
            "Class " + data.classes.size,
            labelsWheel.next,
            colorsWheel.next
        )
        data.classes += newClass

        val newButton = AppCompatButton(this)
        newButton.layoutParams = newClassBtn.layoutParams
        val drawable = ResourcesCompat.getDrawable(resources, newClass.label.iconId, null) ?: return

        drawable.colorFilter = makeColorFilter(
            ResourcesCompat.getColor(resources, R.color.selected_class_background, null),
            newClass.color
        )
        newButton.background = drawable

        newButton.contentDescription = newClass.name
        newButton.setOnClickListener(this::onClassSelect)

        newButton.setOnLongClickListener(this::onClassProperties)

        viewClasses.addView(newButton, data.classes.size - 1)
    }

    private fun onClassSelect(it: View) {
        var clickIndex = -1
        for (i in 0 until data.classes.size) {
            if (viewClasses[i] == it) {
                clickIndex = i
                break
            }
        }
        if (clickIndex == data.activeClass || clickIndex < 0)
            return

        if (data.activeClass >= 0)
            viewClasses[data.activeClass].background.colorFilter = makeColorFilter(
                ResourcesCompat.getColor(resources, R.color.unselected_class_background, null),
                data.classes[data.activeClass].color
            )

        data.activeClass = clickIndex

        viewClasses[data.activeClass].background.colorFilter = makeColorFilter(
            ResourcesCompat.getColor(resources, R.color.selected_class_background, null),
            data.classes[data.activeClass].color
        )

        viewClassification.invalidate()
    }

    private fun onClassProperties(it: View): Boolean {
        var clickIndex = -1
        for (i in 0 until data.classes.size) {
            if (viewClasses[i] == it) {
                clickIndex = i
                break
            }
        }
        if (clickIndex < 0)
            return true

        val classInfo = ClassInfoDialog(data.classes[clickIndex],
            { onClassPropertiesOk(clickIndex) },
            { onClassPropertiesDelete(clickIndex) })

        classInfo.show(supportFragmentManager, null)

        return true
    }

    private fun onClassPropertiesOk(clickIndex: Int) {
        Log.i(classTag, "Modify Class[$clickIndex]")

        val drawable = ResourcesCompat.getDrawable(
            resources,
            data.classes[clickIndex].label.iconId,
            null
        ) ?: return

        drawable.colorFilter = makeColorFilter(
            ResourcesCompat.getColor(
                resources,
                if (clickIndex == data.activeClass)
                    R.color.selected_class_background
                else
                    R.color.unselected_class_background, null
            ),
            data.classes[clickIndex].color
        )
        viewClasses[clickIndex].background = drawable

        viewClasses[clickIndex].contentDescription = data.classes[clickIndex].name

        data.classes[clickIndex].label.iconBitmap = null

        viewClassification.invalidate()
    }

    private fun onClassPropertiesDelete(clickIndex: Int) {         // OnDelete class
        Log.i(classTag, "Delete Class[$clickIndex]")
        viewClasses.removeViewAt(clickIndex)

        data.classes.removeAt(clickIndex)
        if (clickIndex < data.activeClass)
            data.activeClass--

        if (clickIndex == data.activeClass) {
            data.activeClass = min(data.activeClass, data.classes.size - 1)

            if (data.activeClass >= 0)
                viewClasses[data.activeClass].background.colorFilter = makeColorFilter(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.selected_class_background,
                        null
                    ),
                    data.classes[data.activeClass].color
                )
        }

        viewClassification.invalidate()
    }

    private fun onTouchClassificationView(v: View, event: MotionEvent): Boolean {
        if (0 <= data.activeClass && data.activeClass < data.classes.size) {
            data.classes[data.activeClass].add(event.getX(), event.getY())
            viewClassification.invalidate()
        }
        return super.onTouchEvent(event)
    }
}
