package com.example.test001

import android.graphics.Bitmap

class LabelInfo(nameId: Int, iconId: Int) {
    val nameId: Int = nameId

    var iconId: Int = iconId
        set(value) {
            field = value
            iconBitmap = null
        }

    var iconBitmap: Bitmap? = null
}
