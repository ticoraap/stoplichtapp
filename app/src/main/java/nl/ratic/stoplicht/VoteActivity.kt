package nl.ratic.stoplicht

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import nl.ratic.stoplicht.api.Api
import nl.ratic.stoplicht.connectivity.Connectivity
import nl.ratic.stoplicht.database.DatabaseHelper
import nl.ratic.stoplicht.model.Meeting
import nl.ratic.stoplicht.model.Vote

class VoteActivity : AppCompatActivity() {


    private lateinit var meeting: Meeting
    private var greenCount = 0
    private var orangeCount = 0
    private var redCount = 0
    private var meetingid: String = ""
    private lateinit var name: TextView
    private lateinit var description: TextView
    private lateinit var voteGreenButton: Button
    private lateinit var voteOrangeButton: Button
    private lateinit var voteRedButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vote)

        name = findViewById(R.id.meetingName)
        description = findViewById(R.id.meetingDescription)
        voteGreenButton = findViewById(R.id.voteGreen)
        voteOrangeButton = findViewById(R.id.voteOrange)
        voteRedButton = findViewById(R.id.voteRed)
        meetingid = intent.extras?.getString("meetingid").toString()
        prepareMeetingData()
        Log.d(
            "VoteActivity onCreate",
            DatabaseHelper.getHelper(this).getMeeting(meetingid).toString()
        )
    }


    private fun prepareMeetingData() {
        setSpinnerVisibility(true)
        if (Connectivity.isInternetAvailable(this)) {
            getMeetingFromApi()
        } else {
            getMeetingFromDatabase()
        }
        setSpinnerVisibility(false)
    }

    private fun getMeetingFromApi() {
        Api.meetings.get(meetingid,
            callBackSuccess = {
                setMeetingData(it)
            },
            callBackFailed = {
                Toast.makeText(this, "Could not retreive meetings from the API", Toast.LENGTH_LONG)
            })
    }

    private fun getMeetingFromDatabase() {
        val dbRef = DatabaseHelper.getHelper(this)
        val meeting = dbRef.getMeeting(meetingid)
        setMeetingData(meeting)
    }

    private fun setSpinnerVisibility(shouldSpin: Boolean) {
        val voteSpinner: ProgressBar = findViewById(R.id.voteSpinner)
        if (shouldSpin) {
            voteSpinner.visibility = View.VISIBLE
        } else {
            voteSpinner.visibility = View.GONE
        }
    }


    private fun setMeetingData(meeting: Meeting) {
        this.meeting = meeting

        name.text = meeting.name
        description.text = meeting.description

        resetVoteCount()
        countVotes()
        setVoteCountOnButtons()
        checkUsersVote()
    }

    private fun resetVoteCount() {
        greenCount = 0
        orangeCount = 0
        redCount = 0
    }

    private fun countVotes() {
        meeting.votes.values.forEach {
            when (it) {
                Vote.GREEN -> greenCount++
                Vote.ORANGE -> orangeCount++
                Vote.RED -> redCount++
            }
        }
    }

    private fun setVoteCountOnButtons() {
        voteGreenButton.text = greenCount.toString()
        voteOrangeButton.text = orangeCount.toString()
        voteRedButton.text = redCount.toString()
    }

    private fun checkUsersVote() {
        var userid = Api.authentication.currentUser()!!.uid
        if (meeting.votes.containsKey(userid)) {
            setActiveColor(meeting.votes.getValue(userid))
        }
    }

    private fun setActiveColor(vote: Vote) {
        when (vote) {
            Vote.GREEN -> {
                voteGreenButton.setBackgroundColor(applicationContext.getColor(R.color.green))
                voteOrangeButton.setBackgroundColor(applicationContext.getColor(R.color.dark_orange))
                voteRedButton.setBackgroundColor(applicationContext.getColor(R.color.dark_red))

                voteGreenButton.setTextColor(applicationContext.getColor(R.color.white))
                voteOrangeButton.setTextColor(applicationContext.getColor(R.color.grey))
                voteRedButton.setTextColor(applicationContext.getColor(R.color.grey))
            }

            Vote.ORANGE -> {
                voteGreenButton.setBackgroundColor(applicationContext.getColor(R.color.dark_green))
                voteOrangeButton.setBackgroundColor(applicationContext.getColor(R.color.orange))
                voteRedButton.setBackgroundColor(applicationContext.getColor(R.color.dark_red))

                voteGreenButton.setTextColor(applicationContext.getColor(R.color.grey))
                voteOrangeButton.setTextColor(applicationContext.getColor(R.color.white))
                voteRedButton.setTextColor(applicationContext.getColor(R.color.grey))
            }
            Vote.RED -> {
                voteGreenButton.setBackgroundColor(applicationContext.getColor(R.color.dark_green))
                voteOrangeButton.setBackgroundColor(applicationContext.getColor(R.color.dark_orange))
                voteRedButton.setBackgroundColor(applicationContext.getColor(R.color.red))

                voteGreenButton.setTextColor(applicationContext.getColor(R.color.grey))
                voteOrangeButton.setTextColor(applicationContext.getColor(R.color.grey))
                voteRedButton.setTextColor(applicationContext.getColor(R.color.white))
            }
        }
    }

    fun voteOnGreen(view: View) {
        voteOn(Vote.GREEN)
    }

    fun voteOnOrange(view: View) {
        voteOn(Vote.ORANGE)
    }

    fun voteOnRed(view: View) {
        voteOn(Vote.RED)
    }

    private fun voteOn(vote: Vote) {

        if (Connectivity.isInternetAvailable(this)) {
            Api.meetings.vote(
                meeting.meetingid,
                vote
            ) {
                if (it) {
                    prepareMeetingData()

                } else {
                    Toast.makeText(
                        this,
                        "Something went wrong please contact the administrator",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                this,
                "No internet available, please try again with a active connection",
                Toast.LENGTH_LONG
            ).show()
        }

    }


}