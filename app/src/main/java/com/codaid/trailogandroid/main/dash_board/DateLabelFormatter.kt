package com.codaid.trailogandroid.main.dash_board

import com.github.mikephil.charting.formatter.ValueFormatter

class DateLabelFormatter(xList: MutableList<String>) : ValueFormatter() {
    private val mXList = xList

    override fun getFormattedValue(value: Float): String {
        return if (mXList.size <= value.toInt() || value < 0){
            ""
        } else {
            mXList[value.toInt()]
        }
    }
}