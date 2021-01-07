package nl.ratic.stoplicht.api

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class Authentication {
    private val authIntance = FirebaseAuth.getInstance()

    fun currentUser(): FirebaseUser? {
        return authIntance.currentUser
    }

    fun isAuthenticated() : Boolean {
        return authIntance.currentUser != null
    }

    fun login(email: String, password: String, callBack: (Boolean, String) -> Unit) {
        authIntance.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            callBack(it.isSuccessful, it.exception.toString())
        }
    }

    fun logout() {
        authIntance.signOut()
    }
}
