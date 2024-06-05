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
//added limit line import for max and min line
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.firestore.FirebaseFirestore
//imports for calendar - month stuff
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Color


class MainActivity : AppCompatActivity() {
    private lateinit var barChart: BarChart
    private val firestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //variable
        barChart = findViewById(R.id.bar_chart)

        //calendar
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(calendar.time)
        //getting each dat of the month and adding it to a set
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDate = dateFormat.format(calendar.time)

        //calling the function to fetch study data from Firestore
        fetchStudyData(firstDate, currentDate)

    }

    private fun fetchStudyData(startDate: String, endDate: String) {
        firestore.collection("daily_entries")
            .whereGreaterThanOrEqualTo("entryDate", startDate)
            .whereLessThanOrEqualTo("entryDate", endDate)
            .get()
            .addOnSuccessListener { documents ->
                val studyData = mutableListOf<StudyEntry>()
                var minGoal = 0
                var maxGoal = 0
//i named min and max incorrectly in firebase so its just all going to be plurals >_< (crying emoji)
                for (document in documents) {
                    val date = document.getString("entryDate") ?: continue
                    //checking if there is an entry for that date ---> if not then sets tHrs to 0
                    val totalHours = document.getDouble("TotalHrs") ?: 0.0
                    minGoal = document.getDouble("MinGoals")?.toInt() ?: minGoal
                    maxGoal = document.getDouble("MaxGoals")?.toInt() ?: maxGoal
                    studyData.add(StudyEntry(date, totalHours.toFloat()))
                }

                displayBarChart(studyData, minGoal, maxGoal)
            }
            .addOnFailureListener { exception ->
                //maybe i'll add in some error handling, just want to get this working first
            }
    }

    private fun displayBarChart(studyData: List<StudyEntry>, minGoal: Int, maxGoal: Int) {
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        for (i in studyData.indices) {
            val date = studyData[i].date
            val hours = studyData[i].hours
            entries.add(BarEntry(i.toFloat(), hours))
            labels.add(date)
        }

        val dataSet = BarDataSet(entries, "Study Hours")
        val data = BarData(dataSet)

        barChart.data = data
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.granularity = 1f
        barChart.axisLeft.axisMinimum = 0f

        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.setFitBars(true)
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false
        barChart.xAxis.isEnabled = true
        barChart.setDrawGridBackground(false)
        barChart.animateY(1000)

        barChart.invalidate()
    }
    data class StudyEntry(val date: String, val hours: Float)
}

//hi this is old display graph method, asked chat to change it so the one above is the updated version from chat
/*private fun displayBarChart(studyData: List<StudyEntry>, minGoal: Int, maxGoal: Int) {
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()
        val calendar = Calendar.getInstance()

        for (i in studyData.indices) {
            val date = studyData[i].date
            val hours = studyData[i].hours
            entries.add(BarEntry(i.toFloat(), hours))
            labels.add(date)
        }

        val dataSet = BarDataSet(entries, "Study Hours")
        val data = BarData(dataSet)

        barChart.data = data
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.granularity = 1f
        barChart.axisLeft.axisMinimum = 0f

        val leftAxis = barChart.axisLeft
        leftAxis.addLimitLine(LimitLine(minGoal.toFloat(), "Min Goal").apply {
            lineColor = Color.RED
            lineWidth = 2f
            textColor = Color.BLACK
            textSize = 12f
        })
        leftAxis.addLimitLine(LimitLine(maxGoal.toFloat(), "Max Goal").apply {
            lineColor = Color.GREEN
            lineWidth = 2f
            textColor = Color.BLACK
            textSize = 12f
        })

        barChart.invalidate()
    }

    data class StudyEntry(val date: String, val hours: Float)
}*/

//hi ody i commented out this code to use mine, not deleting it though dw
//val barChart = findViewById<BarChart>(R.id.bar_chart)

/* entries = ArrayList<BarEntry>()
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
}*/