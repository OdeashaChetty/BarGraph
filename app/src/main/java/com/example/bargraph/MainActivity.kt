package com.example.bargraph

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log


class MainActivity : AppCompatActivity() {
    private lateinit var barChart: BarChart
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        barChart = findViewById(R.id.bar_chart)
        firestore = FirebaseFirestore.getInstance()

        // Test data
        val testData = listOf(
            StudyEntry("2024-06-01", 4f),
            StudyEntry("2024-06-02", 5f),
            StudyEntry("2024-06-03", 6f)
        )
        displayBarChart(testData, minGoal = 2, maxGoal = 5)

        // Fetch data from Firestore
        fetchStudyData("2024-06-01", "2024-06-30")
    }

    private fun displayBarChart(studyData: List<StudyEntry>, minGoal: Int, maxGoal: Int) {
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (i in studyData.indices) {
            val date = studyData[i].date
            val hours = studyData[i].hours
            val dayOfMonth = dateFormat.parse(date)?.let {
                calendar.time = it
                calendar.get(Calendar.DAY_OF_MONTH).toFloat()
            } ?: 0f
            entries.add(BarEntry(dayOfMonth, hours))
            labels.add(dayOfMonth.toString())
        }

        val dataSet = BarDataSet(entries, "TotalHrs")
        dataSet.color = Color.BLUE
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 10f
        val data = BarData(dataSet)
        data.barWidth = 0.9f

        barChart.data = data
        barChart.setFitBars(true)
        //barChart.invalidate()

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.granularity = 1f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setLabelCount(31, true)
        xAxis.labelRotationAngle = -45f

        val leftAxis = barChart.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 24f

        val rightAxis = barChart.axisRight
        rightAxis.isEnabled = false

        // Add limit lines for min and max goals
        val maxGoalLine = LimitLine(maxGoal.toFloat())
        maxGoalLine.lineColor = Color.RED
        maxGoalLine.lineWidth = 2f
        leftAxis.addLimitLine(maxGoalLine)

        val minGoalLine = LimitLine(minGoal.toFloat())
        minGoalLine.lineColor = Color.GREEN
        minGoalLine.lineWidth = 2f
        leftAxis.addLimitLine(minGoalLine)

        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.animateY(1000)
       // barChart.invalidate()
    }

    private fun fetchStudyData(startDate: String, endDate: String) {
        firestore.collection("daily_entries")
           // .whereGreaterThanOrEqualTo("entryDate", startDate)
            //.whereLessThanOrEqualTo("entryDate", endDate)
            .get()
            .addOnSuccessListener { documents ->
                val studyData = mutableListOf<StudyEntry>()
                val completeData = mutableMapOf<String, Float>()
                var minGoal = 0
                var maxGoal = 0

                for (document in documents) {
                    val date = document.getString("entryDate") ?: continue
                    val totalHours = document.getDouble("TotalHrs") ?: 0.0
                    minGoal = document.getDouble("MinGoals")?.toInt() ?: minGoal
                    maxGoal = document.getDouble("MaxGoals")?.toInt() ?: maxGoal
                    completeData[date] = totalHours.toFloat()

                    // Log retrieved data
                    Log.d("FirestoreData", "Date: $date, TotalHours: $totalHours, MinGoal: $minGoal, MaxGoal: $maxGoal")
                }

                val calendar = Calendar.getInstance()
                calendar.time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(startDate)!!
                while (calendar.time.before(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(endDate)!!) || calendar.time.equals(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(endDate)!!)) {
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                    if (!completeData.containsKey(date)) {
                        completeData[date] = 0f
                    }
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }

                for ((date, hours) in completeData.toSortedMap()) {
                    studyData.add(StudyEntry(date, hours))
                }

                displayBarChart(studyData, minGoal, maxGoal)
            }
            .addOnFailureListener { exception ->
                // Log error
                Log.e("FirestoreError", "Error fetching data", exception)
            }
    }

    data class StudyEntry(val date: String, val hours: Float)
}



        //this is user input, it works, but its not pulling from firestore
       /* barChart = findViewById(R.id.bar_chart)

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(calendar.time)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDate = dateFormat.format(calendar.time)

        fetchStudyData(firstDate, currentDate)
    }

    private fun fetchStudyData(startDate: String, endDate: String) {


        firestore.collection("daily_entries")
            .whereGreaterThanOrEqualTo("entryDate", startDate)
            .whereLessThanOrEqualTo("entryDate", endDate)
            .get()
            .addOnSuccessListener { documents ->
                val studyData = mutableListOf<StudyEntry>()
                val completeData = mutableMapOf<String, Float>()
                var minGoal =0
                var maxGoal = 0

                for (document in documents) {
                    val date = document.getString("entryDate") ?: continue
                    val totalHours = document.getDouble("TotalHrs") ?: 0.0
                    minGoal = document.getDouble("MinGoals")?.toInt() ?: minGoal
                    maxGoal = document.getDouble("MaxGoals")?.toInt() ?: maxGoal
                    completeData[date] = totalHours.toFloat()
                }

                val calendar = Calendar.getInstance()
                calendar.time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(startDate)!!
                while (calendar.time.before(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(endDate)!!) || calendar.time.equals(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(endDate)!!)) {
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                    if (!completeData.containsKey(date)) {
                        completeData[date] = 0f
                    }
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }

                for ((date, hours) in completeData.toSortedMap()) {
                    studyData.add(StudyEntry(date, hours))
                }

                displayBarChart(studyData, minGoal, maxGoal)
            }
            .addOnFailureListener { exception ->
                Log.e("MainActivity", "Error getting documents: ", exception)
            }
    }

    private fun displayBarChart(studyData: List<StudyEntry>, minGoal: Int, maxGoal: Int) {
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        for (i in studyData.indices) {
            val date = studyData[i].date
            val day = date.substring(date.length - 2).toInt()
            val hours = studyData[i].hours
            entries.add(BarEntry(day.toFloat(), hours))
            labels.add(day.toString())
        }

        val dataSet = BarDataSet(entries, "Study Hours")
        dataSet.color = Color.BLUE
        val data = BarData(dataSet)
        data.barWidth = 0.9f

        barChart.data = data

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.granularity = 1f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setLabelCount(31, true)
        xAxis.axisMinimum = 1f
        xAxis.axisMaximum = 31f
        xAxis.labelRotationAngle = -45f // Rotate labels for better readability

        val leftAxis = barChart.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 24f // Hours range from 0 to 24

        val rightAxis = barChart.axisRight
        rightAxis.isEnabled = false

        // Add limit lines for min and max goals without labels
        val maxGoalLine = LimitLine(maxGoal.toFloat())
        maxGoalLine.lineColor = Color.RED
        maxGoalLine.lineWidth = 2f
        leftAxis.addLimitLine(maxGoalLine)

        val minGoalLine = LimitLine(minGoal.toFloat())
        minGoalLine.lineColor = Color.GREEN
        minGoalLine.lineWidth = 2f
        leftAxis.addLimitLine(minGoalLine)

        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.setFitBars(true)
        barChart.setDrawGridBackground(false)
        barChart.animateY(1000)
        barChart.invalidate()
    }

    data class StudyEntry(val date: String, val hours: Float)
}*/

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
