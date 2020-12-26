package nl.ratic.stoplicht

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import nl.ratic.stoplicht.api.Api
import nl.ratic.stoplicht.connectivity.Connectivity
import nl.ratic.stoplicht.database.DatabaseHelper
import nl.ratic.stoplicht.model.Meeting
import nl.ratic.stoplicht.model.SimpleDate

class MeetingListActivity : AppCompatActivity(), MeetingClickedListener {

    private var meetingList: MutableList<Meeting> = mutableListOf()
    private lateinit var meetingsAdapter: MeetingsAdapter
    private lateinit var datePicker: DatePicker
    private var filterOnDay = false
    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting_list)

        recyclerView = findViewById(R.id.recyclerView)

        datePicker = findViewById(R.id.meetingDatePicker)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        datePicker.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
            filterMeetingListWith(getSimpleDateFromDatepicker())
        }

        val filterDaySwitch: Switch = findViewById(R.id.filterDaySwitch)
        filterDaySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            filterOnDay = isChecked
            if (isChecked) {

                val scale: Float = applicationContext.resources.displayMetrics.density
                val pixels = (500 * scale + 0.5f)
                recyclerView.layoutParams.height = pixels.toInt()
                datePicker.visibility = View.VISIBLE
                filterMeetingListWith(getSimpleDateFromDatepicker())
            } else {
                val scale: Float = applicationContext.resources.displayMetrics.density
                val pixels = (600 * scale + 0.5f)
                recyclerView.layoutParams.height = pixels.toInt()
                datePicker.visibility = View.GONE
                meetingsAdapter.filter.filter("")
            }
            datePicker.requestLayout()
        }


        meetingsAdapter = MeetingsAdapter(meetingList, this)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = meetingsAdapter
        prepareMeetingsData()
    }

    private fun getSimpleDateFromDatepicker(): SimpleDate {
        val simpleDate = SimpleDate(datePicker.year, datePicker.month + 1, datePicker.dayOfMonth)
        return simpleDate
    }

    private fun filterMeetingListWith(simpleDate: SimpleDate) {
        if (filterOnDay) {
            meetingsAdapter.filter.filter(simpleDate.getDateFormatted())
        } else {
            clearFilterConstraint()
        }
    }

    private fun clearFilterConstraint() {
        meetingsAdapter.filter.filter("")
    }

    private fun prepareMeetingsData() {
        setSpinnerVisibility(true)
        if (Connectivity.isInternetAvailable(this)){
            getMeetingsFromApi()
        } else {
            getMeetingsFromDatabase()
        }
        setSpinnerVisibility(false)
    }

    private fun getMeetingsFromApi(){
        Api.meetings.getAll(
            callBackSuccess = {
                meetingList.clear()
                meetingList.addAll(it)
                meetingsAdapter.notifyDataSetChanged()
                addMeetingsToDatabase()
            },
            callBackFailed = {
                Toast.makeText(this,"Could not retreive meetings from the API", Toast.LENGTH_LONG)
            })
    }

    private fun getMeetingsFromDatabase(){
        Toast.makeText(this,"No Internet Available. \nRetreived meetings from offline mode", Toast.LENGTH_LONG).show()
        meetingList.clear()
        val dbRef = DatabaseHelper.getHelper(this)
        meetingList.addAll(dbRef.getMeetings())
        Log.d("meetingList",meetingList.toString())
        meetingsAdapter.notifyDataSetChanged()
    }



    private fun addMeetingsToDatabase(){
        val dbHelper: DatabaseHelper = DatabaseHelper.getHelper(this)!!
        dbHelper.clearOldMeetings()
        dbHelper.insertMeetings(meetingList)
    }

    override fun clickedOnMeeting(meeting: Meeting) {
        val intent = Intent(this, VoteActivity::class.java)
        intent.putExtra("meetingid", meeting.meetingid)
        startActivity(intent)
    }

    private fun setSpinnerVisibility(shouldSpin: Boolean){
        val meetingListSpinner : ProgressBar = findViewById(R.id.meetingListSpinner)
        if (shouldSpin){
            meetingListSpinner.visibility = View.VISIBLE
        } else {
            meetingListSpinner.visibility = View.GONE
        }
    }
}

interface MeetingClickedListener {
    fun clickedOnMeeting(meeting: Meeting)
}