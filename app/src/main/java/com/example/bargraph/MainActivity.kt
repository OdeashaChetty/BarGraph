package com.example.bargraph

import android.os.Bundle
//import androidx.activity.en
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.blue
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
        val barChart = findViewById<BarChart>(R.id.bar_chart)

        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1f,10f))
        entries.add(BarEntry(2f, 20f))
        entries.add(BarEntry(3f, 30f))
        entries.add(BarEntry(4f, 40f))
        entries.add(BarEntry(5f, 50f))

        val dataSet = BarDataSet(entries, "Bar Data Set")
        dataSet.color.blue
        val data = BarData(dataSet)
        barChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setFitBars(true)
            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false
            xAxis.isEnabled = false
            setDrawGridBackground(false)
            animateY(1000)
            invalidate()

            barChart.data = data
            barChart.invalidate()
        }
    }
}