package nl.ratic.stoplicht

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import nl.ratic.stoplicht.api.Api


class MainActivity : AppCompatActivity() {

    private lateinit var mainLogin : Button
    private lateinit var mainLogout : Button
    private lateinit var mainGoToMeetinglist : Button
    private lateinit var mainAddMeetings : Button
    private lateinit var mainWelcomeText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        updateUIAuthenticated()
    }

    fun gotoLogin(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun gotoMeetingsList(view: View) {
        val intent = Intent(this, MeetingListActivity::class.java)
        startActivity(intent)
    }

    fun gotoAddMeetings(view: View) {
        val intent = Intent(this, AddMeetingActivity::class.java)
        startActivity(intent)
    }

    fun logOutUser(view: View) {
        Api.authentication.logout()
        updateUIAuthenticated()
    }

    fun updateUIAuthenticated(){
        mainLogin = findViewById(R.id.mainLogin)
        mainLogout = findViewById(R.id.mainLogout)

        mainGoToMeetinglist = findViewById(R.id.mainGoToMeetinglist)
        mainAddMeetings = findViewById(R.id.mainAddMeetings)
        mainWelcomeText = findViewById(R.id.mainWelcomeText)
        if (Api.authentication.isAuthenticated()){
            mainLogin.isEnabled = false
            mainLogout.isEnabled = true
            mainGoToMeetinglist.isEnabled = true
            mainAddMeetings.isEnabled = true
            mainWelcomeText.text = Api.authentication.currentUser()!!.email
        } else {
            mainLogin.isEnabled = true
            mainLogout.isEnabled = false
            mainGoToMeetinglist.isEnabled = false
            mainAddMeetings.isEnabled = false
            mainWelcomeText.text = resources.getString(R.string.not_authenticated)
        }
    }
}