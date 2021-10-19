package com.codaid.trailogandroid.main.dash_board

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codaid.trailogandroid.R
import com.codaid.trailogandroid.databinding.FragmentWeightBinding
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.Utils
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Collections.max
import java.util.Collections.min

class WeightFragment : Fragment() {

    private var _binding: FragmentWeightBinding? = null
    private val colorBlue = Color.rgb(248, 12, 84)
    private val colorBlack = Color.rgb(51, 51, 51)
    private val db = Firebase.firestore
    private var xList = mutableListOf<String>()
    private var yList = mutableListOf<Float>()
    private var yValues = LineDataSet(mutableListOf<Entry>(), "")


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWeightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loading.visibility = View.VISIBLE
        val userId = getUserId()

        binding.lineChartExample.setNoDataText("")
        binding.lineChartExample.setNoDataTextColor(colorBlue)
        binding.lineChartExample.getPaint(Chart.PAINT_INFO).textSize = Utils.convertDpToPixel(17f)
        CoroutineScope(Dispatchers.Main).launch {
            getWeights(userId)
            if (xList.size > 0 && yList.size > 0) {
                setupLineChart()
                binding.lineChartExample.data = lineDataWithCount()
                binding.lineChartExample.invalidate()
            } else{
                binding.noDataText.text = getString(R.string.nodata_weight)
            }
        binding.loading.visibility = View.GONE
        }
    }

    private suspend fun getWeights(userId: String?){
        val querySnapshot = userId?.let { db.collection("weights_$it").get() }?.await()
        if (querySnapshot != null) {
            for (document in querySnapshot.documents) {
                val month = document.id.split("-")[1]
                val day = document.id.split("-")[2]
                xList.add(String.format(getString(R.string.date_format_view), month, day))
                yList.add(document.data!!["weight"].toString().toFloat())
            }
        }
    }

    private fun getUserId(): String? {
        val sharedPref = requireContext().getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        return sharedPref.getString(getString(R.string.saved_user_id), getString(R.string.default_user_id))
    }

    private fun setupLineChart(){
        binding.lineChartExample.apply {
            description.isEnabled = false
            setTouchEnabled(false)
            isDragEnabled = false
            isScaleXEnabled = false
            setPinchZoom(false)
            setDrawGridBackground(false)
            legend.isEnabled = false
            axisRight.isEnabled = false

            xAxis.apply {
                setDrawGridLines(true)
            }

            axisLeft.apply {
                textColor = colorBlack
                setDrawGridLines(true)
            }
        }
    }

    private fun lineDataWithCount():LineData {
        val yEntryList = mutableListOf<Entry>()
        for (i in yList.indices){
            yEntryList.add(
                Entry(i.toFloat(), yList[i])
            )
        }

        binding.lineChartExample.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            labelCount = xList.size
            granularity = 1f
            valueFormatter = DateLabelFormatter(xList)
        }

        yValues = LineDataSet(yEntryList, "weight").apply {
            axisDependency =  YAxis.AxisDependency.LEFT
            setDrawCircleHole(false)
            setDrawValues(false)
            setCircleColor(colorBlue)
            circleRadius = 4f
            lineWidth = 2f
            color = colorBlue
        }
        binding.lineChartExample.apply {
            axisLeft.apply {
                axisMaximum = max(yList) + 1
                axisMinimum = min(yList) - 3
            }
        }
        return LineData(yValues)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}