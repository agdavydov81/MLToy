package com.example.test001

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.test001.databinding.ClassInfoBinding
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import java.lang.Math.cbrt
import java.lang.Math.pow

class ClassInfoDialog(
    val data: DataClass,
    val onOkCallback: Runnable,
    val onDeleteCallback: Runnable
) : DialogFragment(), ColorPickerDialogListener {
    companion object {
        fun rgbtoLin(colorPart: Int): Double {
            val c = colorPart / 255.0
            return if (c <= 0.04045) (c / 12.92) else pow((c + 0.055) / 1.055, 2.4)
        }

        fun rgb2Luminance(rgb: Int): Double {
            return 0.2126 * rgbtoLin(Color.red(rgb)) +
                    0.7152 * rgbtoLin(Color.green(rgb)) +
                    0.0722 * rgbtoLin(Color.blue(rgb))
        }

        fun rgb2Luma(rgb: Int): Double {
            val Y = rgb2Luminance(rgb)
            return if (Y <= 0.008856) (Y * 903.3) else (cbrt(Y) * 116 - 16)
        }
    }

    var dataColor = 0xFF000000.or(data.color.toLong()).toInt()
    var dataLabel = data.label

    lateinit var binding: ClassInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ClassInfoBinding.inflate(layoutInflater)

        var info = getString(R.string.classInfoPoints, data.numPoints)
        if (data.numPoints > 0) {
            for (d in 0 until DataClass.DIMENSION) {
                val statInfo = StatInfo()
                for (i in d until data.numPoints * DataClass.DIMENSION + d step DataClass.DIMENSION)
                    statInfo.add(data.points[i])
                info += "\n" + getString(
                    R.string.classInfoDimension,
                    d,
                    statInfo.min,
                    statInfo.max,
                    statInfo.mean,
                    statInfo.std
                )
            }
        }

        binding.classDescription.text = info

        binding.className.setText(data.name)

        setClassColor(dataColor)

        binding.classColor.setOnClickListener {
            val colorPicker = ColorPickerDialog.newBuilder()
                .setColor(0xFF0000FF.toInt())
                .create()

            colorPicker.setColorPickerDialogListener(this)
            colorPicker.show(requireActivity().supportFragmentManager, "color-picker-dialog")
        }

        binding.classLabel.text =
            getString(R.string.classLabel, resources.getString(dataLabel.nameId))
        binding.classLabel.setOnClickListener(this::onClassNameEdit)


        binding.classOk.setOnClickListener {
            data.name = binding.className.text.toString()
            data.color = dataColor
            data.label = dataLabel
            dismiss()
            onOkCallback.run()
        }

        binding.classDelete.setOnClickListener(this::onClassDeleteConfirm)

        return binding.root
    }

    private fun onClassNameEdit(it: View) {
        val labelsList = ArrayList<String>()
        var labelsListSelection = -1
        for (li in 0 until MainActivity.labelsWheel.values.size) {
            val listItem = MainActivity.labelsWheel.values[li]
            labelsList.add(resources.getString(listItem.nameId))
            if (listItem == dataLabel)
                labelsListSelection = li
        }

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.selectClassLabel)
            .setSingleChoiceItems(labelsList.toTypedArray(), labelsListSelection) { _, id ->
                labelsListSelection = id
            }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                if (labelsListSelection >= 0) {
                    dataLabel = MainActivity.labelsWheel.values[labelsListSelection]
                    binding.classLabel.text =
                        getString(R.string.classLabel, resources.getString(dataLabel.nameId))
                }
            }
            .show()
    }

    private fun onClassDeleteConfirm(it: View) {
        val context = context
        if (context != null) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(R.string.deleteClassConfirm)

            builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                dismiss()
                onDeleteCallback.run()
            }

            builder.setNegativeButton(android.R.string.cancel) { dialog, which ->
            }

            builder.create().show()
        }
    }

    private fun setClassColor(color: Int) {
        this.dataColor = color
        binding.classColor.backgroundTintList = ColorStateList.valueOf(color)
        binding.classColor.text = getString(R.string.classColor, color.and(0xFFFFFF))
        binding.classColor.setTextColor(if (rgb2Luma(color) >= 50) 0xFF000000L.toInt() else 0xFFFFFFFF.toInt())
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        setClassColor(color)
    }

    override fun onDialogDismissed(dialogId: Int) {
        //Log.i("ColorPicker", "onDialogDismissed")
    }
}