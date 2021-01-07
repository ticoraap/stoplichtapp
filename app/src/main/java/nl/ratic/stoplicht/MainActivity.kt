package nl.ratic.stoplicht

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import nl.ratic.stoplicht.api.Api


class MainActivity : AppCompatActivity() {

    private lateinit var loginButton : Button
    private lateinit var logoutButton : Button
    private lateinit var meetingsButton : Button
    private lateinit var addMeetingButton : Button
    private lateinit var welcomeTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        updateUIAutChanged()
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

    fun logout(view: View) {
        Api.authentication.logout()
        updateUIAutChanged()
    }

    private fun updateUIAutChanged(){
        loginButton = findViewById(R.id.mainLogin)
        logoutButton = findViewById(R.id.mainLogout)

        meetingsButton = findViewById(R.id.mainGoToMeetinglist)
        addMeetingButton = findViewById(R.id.mainAddMeetings)
        welcomeTextView = findViewById(R.id.mainWelcomeText)
        if (Api.authentication.isAuthenticated()){
            loginButton.isEnabled = false
            logoutButton.isEnabled = true
            meetingsButton.isEnabled = true
            addMeetingButton.isEnabled = true
            welcomeTextView.text = Api.authentication.currentUser()!!.email
        } else {
            loginButton.isEnabled = true
            logoutButton.isEnabled = false
            meetingsButton.isEnabled = false
            addMeetingButton.isEnabled = false
            welcomeTextView.text = resources.getString(R.string.main_textview_welcome_messgae)
        }
    }
}