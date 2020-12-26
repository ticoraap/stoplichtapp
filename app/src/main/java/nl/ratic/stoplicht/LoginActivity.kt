package nl.ratic.stoplicht

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.text.trimmedLength
import androidx.core.widget.addTextChangedListener
import nl.ratic.stoplicht.api.Api



class LoginActivity : AppCompatActivity() {

    private final val LOGTAG = "LoginActivity"
    private var isValidEmail : Boolean = false
    private var isValidPassword : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.username)
        username.addTextChangedListener(){

            fun Editable.isValidEmail() : Boolean{
                return !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
            }
            isValidEmail = it?.isValidEmail() ?: false
            checkBothInput()
        }

        val password = findViewById<EditText>(R.id.password)
        password.addTextChangedListener(){
            fun Editable.isValidPassword() : Boolean{
                return !isNullOrEmpty() && trimmedLength() >= 6
            }

            isValidPassword = it?.isValidPassword() ?: false
            checkBothInput()
        }
    }

    private fun checkBothInput(){
        if (isValidEmail && isValidPassword){
            enableLogin()
        } else {
            disableLogin()
        }
    }

    private fun enableLogin() {
        findViewById<Button>(R.id.login).isEnabled = true
    }
    private fun disableLogin() {
        findViewById<Button>(R.id.login).isEnabled = false
    }

    private fun showSpinner(){
        val loadAnimation = findViewById<ProgressBar>(R.id.loading)
        loadAnimation.visibility = View.VISIBLE;
    }
    private fun hideSpinner(){
        val loadAnimation = findViewById<ProgressBar>(R.id.loading)
        loadAnimation.visibility = View.INVISIBLE;
    }

    fun tryLogin(view : View){
        showSpinner()
        val username = findViewById<EditText>(R.id.username).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString()
        Api.authentication.login(username, password) {
            if(it){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
//                Snackbar.make(this.findViewById(android.R.id.content), "Authenticatie niet gelukt", Snackbar.LENGTH_LONG).setAction("", null).show();
                Toast.makeText(this,"authenticatie niet gelukt",Toast.LENGTH_LONG).show()
                hideSpinner()
            }
        }
    }




}