package nl.ratic.stoplicht


import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import nl.ratic.stoplicht.api.Api
import nl.ratic.stoplicht.connectivity.Connectivity
import nl.ratic.stoplicht.model.Meeting
import nl.ratic.stoplicht.model.SimpleDate


class AddMeetingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meeting)
        if(!Connectivity.isInternetAvailable(this)){
            Toast.makeText(this,"No network detected, you cannot create a new meeting offline",Toast.LENGTH_SHORT).show()
        }
    }

    fun addMeetingToFirebase(view: View) {
        if(Connectivity.isInternetAvailable(this)){
            val name = findViewById<EditText>(R.id.addName).text.toString()
            val description = findViewById<EditText>(R.id.addMeetingDescription).text.toString()
            val datePicker: DatePicker = findViewById(R.id.meetingDatePicker)
            val userid = Api.authentication.currentUser()!!.uid
            val meeting =
                Meeting(name, description, simpleDate = getSimpleDateFromDatePicker(datePicker), userid = userid)
            Api.meetings.add(meeting) {
                if (it) {
                    Toast.makeText(this, "Created ${meeting.description}", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed creating meeting", Toast.LENGTH_LONG).show()
                }

            }
        } else {
            Toast.makeText(this,"No network detected, you cannot create a new meeting offline",Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSimpleDateFromDatePicker(datePicker: DatePicker) : SimpleDate {
        val day = datePicker.dayOfMonth
        val month = datePicker.month +1
        val year = datePicker.year
        return SimpleDate(year,month,day)
    }
}