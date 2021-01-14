package nl.ratic.stoplicht.api

import nl.ratic.stoplicht.model.Meeting
import nl.ratic.stoplicht.model.Vote
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Meetings {
    val firebase = FirebaseDatabase.getInstance()
    val meetingsRef = firebase.getReference("meetings")


    fun add(meeting: Meeting, callBack: (Boolean) -> Unit) {
        val meetingid = meetingsRef.push().key.toString()
        meeting.meetingid = meetingid
        meetingsRef.child(meetingid).setValue(meeting).addOnCompleteListener {
            callBack(it.isSuccessful)
        }
    }

    fun get(UUID: String, callBackSuccess: ((Meeting) -> Unit), callBackFailed: (() -> Unit)) {
        meetingsRef.child(UUID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val meeting = dataSnapshot.getValue(Meeting::class.java)
                callBackSuccess(meeting!!)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callBackFailed()
            }
        })
    }

    fun getAll(callBackSuccess: ((MutableList<Meeting>) -> Unit), callBackFailed: (() -> Unit)) {
        meetingsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val meetings = mutableListOf<Meeting>()
                dataSnapshot.children.mapNotNullTo(meetings) { it.getValue(Meeting::class.java) }
                callBackSuccess(meetings)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                callBackFailed()
            }
        })
    }

    fun vote(
        UUID: String,
        vote: Vote,
        callBack: ((Boolean) -> Unit)
    ) {
        val userId = Api.authentication.currentUser()!!.uid
        meetingsRef.child(UUID).child("votes").child(userId).setValue(vote).addOnCompleteListener {
            callBack(it.isSuccessful)
        }
    }


}