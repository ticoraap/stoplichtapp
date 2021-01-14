package nl.ratic.stoplicht

import android.content.Intent
import android.os.Bundle
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
    private var filterOnDay = false

    private lateinit var meetingsAdapter: MeetingsAdapter
    private lateinit var datePicker: DatePicker
    private lateinit var recyclerView: RecyclerView
    private lateinit var filterDaySwitch : Switch


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting_list)

        recyclerView = findViewById(R.id.recyclerView)
        datePicker = findViewById(R.id.meetingDatePicker)
        filterDaySwitch = findViewById(R.id.filterDaySwitch)

        datePicker.setOnDateChangedListener { _, _, _, _ ->
            filterMeetingList(getSimpleDateFromDatepicker())
        }

        filterDaySwitch.setOnCheckedChangeListener { _, isChecked ->
            filterOnDay = isChecked
            if (isChecked) {
                showDatePicker()
            } else {
                hideDatePicker()
            }
            datePicker.requestLayout()
        }


        meetingsAdapter = MeetingsAdapter(meetingList, this)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = meetingsAdapter
    }

    override fun onResume() {
        super.onResume()
        loadLatestMeetingData()
    }

    private fun showDatePicker(){
        val scale: Float = applicationContext.resources.displayMetrics.density
        val pixels = (500 * scale + 0.5f)
        recyclerView.layoutParams.height = pixels.toInt()
        datePicker.visibility = View.VISIBLE
        filterMeetingList(getSimpleDateFromDatepicker())
    }

    private fun hideDatePicker(){
        val scale: Float = applicationContext.resources.displayMetrics.density
        val pixels = (600 * scale + 0.5f)
        recyclerView.layoutParams.height = pixels.toInt()
        datePicker.visibility = View.GONE
        meetingsAdapter.filter.filter("")
    }

    private fun getSimpleDateFromDatepicker(): SimpleDate {
        return SimpleDate(datePicker.year, datePicker.month + 1, datePicker.dayOfMonth)
    }

    private fun filterMeetingList(simpleDate: SimpleDate) {
        if (filterOnDay) {
            meetingsAdapter.filter.filter(simpleDate.getDateFormatted())
        } else {
            meetingsAdapter.filter.filter("")
        }
    }

    private fun loadLatestMeetingData() {
        showSpinner()
        if (Connectivity.isInternetAvailable(this)){
            getMeetingsFromApi()
        } else {
            getMeetingsFromDatabase()
        }
        hideSpinner()
    }

    private fun getMeetingsFromDatabase(){
        Toast.makeText(this,"No Internet Available. \nRetreived meetings from offline mode", Toast.LENGTH_LONG).show()
        meetingList.clear()
        val dbRef = DatabaseHelper.getHelper(this)
        meetingList.addAll(dbRef.getMeetings())
        meetingsAdapter.notifyDataSetChanged()
    }

    private fun getMeetingsFromApi(){
        Api.meetings.getAll(
            callBackSuccess = {
                refreshMeetingsOnDevice(it)
            },
            callBackFailed = {
                Toast.makeText(this,"Could not retreive meetings from the API", Toast.LENGTH_LONG).show()
            })
    }

    private fun refreshMeetingsOnDevice(meetings : List<Meeting>){
        meetingList.clear()
        meetingList.addAll(meetings)
        meetingsAdapter.notifyDataSetChanged()
        flushAndAddMeetingsToDatabase(meetings)
    }

    private fun flushAndAddMeetingsToDatabase(meetings : List<Meeting>){
        val dbHelper: DatabaseHelper = DatabaseHelper.getHelper(this)
        dbHelper.clearOldMeetings()
        dbHelper.insertMeetings(meetings)
    }

    private fun showSpinner(){
        val meetingListSpinner = findViewById<ProgressBar>(R.id.meetingListSpinner)
        meetingListSpinner.visibility = View.VISIBLE
    }

    private fun hideSpinner(){
        val meetingListSpinner = findViewById<ProgressBar>(R.id.meetingListSpinner)
        meetingListSpinner.visibility = View.GONE
    }

    override fun clickedOnMeeting(meeting: Meeting) {
        val intent = Intent(this, VoteActivity::class.java)
        intent.putExtra("meetingid", meeting.meetingid)
        startActivity(intent)
    }
}

interface MeetingClickedListener {
    fun clickedOnMeeting(meeting: Meeting)
}