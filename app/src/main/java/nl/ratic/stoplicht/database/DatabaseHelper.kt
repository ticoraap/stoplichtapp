package nl.ratic.stoplicht.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import nl.ratic.stoplicht.model.Meeting
import nl.ratic.stoplicht.model.SimpleDate
import nl.ratic.stoplicht.model.Vote

class DatabaseHelper : SQLiteOpenHelper {
    private constructor(ctx: Context) : super(ctx, dbName, null, dbVersion)

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE " + DatabaseInfo.StoplichtTables.MEETING + " (" +
                    "${DatabaseInfo.MeetingColumns.MEETINGID}  TEXT PRIMARY KEY, " +
                    "${DatabaseInfo.MeetingColumns.USERID} TEXT," +
                    "${DatabaseInfo.MeetingColumns.NAME} TEXT, " +
                    "${DatabaseInfo.MeetingColumns.DATEFORMATTED} TEXT, " +
                    "${DatabaseInfo.MeetingColumns.DESCRIPTION} TEXT);"
        )
        db.execSQL(
            "CREATE TABLE " + DatabaseInfo.StoplichtTables.VOTE + " (" +
                    "${DatabaseInfo.VoteColumns.USERID} TEXT," +
                    "${DatabaseInfo.VoteColumns.MEETINGID} TEXT," +
                    "${DatabaseInfo.VoteColumns.VOTE} TEXT," +
                    "PRIMARY KEY (${DatabaseInfo.VoteColumns.USERID}, ${DatabaseInfo.VoteColumns.MEETINGID})" +
                    ");"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseInfo.StoplichtTables.MEETING)
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseInfo.StoplichtTables.VOTE)
        onCreate(db)
    }

    fun insertMeetings(meetings: List<Meeting>) {
        mSQLDB!!.beginTransaction()
        meetings.forEach { meeting ->
            val meetingValues = ContentValues()
            meetingValues.put(DatabaseInfo.MeetingColumns.NAME, meeting.name)
            meetingValues.put(DatabaseInfo.MeetingColumns.DESCRIPTION, meeting.description)
            meetingValues.put(DatabaseInfo.MeetingColumns.MEETINGID, meeting.meetingid)
            meetingValues.put(DatabaseInfo.MeetingColumns.USERID, meeting.userid)
            meetingValues.put(
                DatabaseInfo.MeetingColumns.DATEFORMATTED,
                meeting.simpleDate.getDateFormatted()
            )
            mSQLDB!!.insertWithOnConflict(
                DatabaseInfo.StoplichtTables.MEETING,
                null,
                meetingValues,
                SQLiteDatabase.CONFLICT_REPLACE
            )

            meeting.votes.forEach { vote ->
                val voteValues = ContentValues()
                voteValues.put(DatabaseInfo.VoteColumns.USERID, vote.key)
                voteValues.put(DatabaseInfo.VoteColumns.VOTE, vote.value.toString())
                voteValues.put(DatabaseInfo.MeetingColumns.MEETINGID, meeting.meetingid)
                mSQLDB!!.insertWithOnConflict(
                    DatabaseInfo.StoplichtTables.VOTE,
                    null,
                    voteValues,
                    SQLiteDatabase.CONFLICT_REPLACE
                )
            }
        }
        mSQLDB!!.setTransactionSuccessful()
        mSQLDB!!.endTransaction()
    }

    fun getMeeting(meetingid: String): Meeting {
        val meetingCursor = mSQLDB!!.query(
            DatabaseInfo.StoplichtTables.MEETING,
            meetingColumns,
            "${DatabaseInfo.MeetingColumns.MEETINGID} = \"${meetingid}\"",
            null,
            null,
            null,
            null
        )

        val voteCursor = mSQLDB!!.query(
            DatabaseInfo.StoplichtTables.VOTE,
            voteColumns,
            null,
            null,
            null,
            null,
            null
        )

        val voteHashMap = hashMapOf<String, HashMap<String, Vote>>()
        while (voteCursor.moveToNext()) {
            val meetingid =
                voteCursor.getString(voteCursor.getColumnIndex(DatabaseInfo.VoteColumns.MEETINGID))
            val userid =
                voteCursor.getString(voteCursor.getColumnIndex(DatabaseInfo.VoteColumns.USERID))
            val vote =
                Vote.valueOf(voteCursor.getString(voteCursor.getColumnIndex(DatabaseInfo.VoteColumns.VOTE)))
            if (voteHashMap.containsKey(meetingid)) {
                voteHashMap.getValue(meetingid)[userid] = vote
            } else {
                voteHashMap[meetingid] = hashMapOf(Pair(userid, vote))
            }
        }
        meetingCursor.moveToFirst()



        val meetingName =
            meetingCursor.getString(meetingCursor.getColumnIndex(DatabaseInfo.MeetingColumns.NAME))
        val meetingDescription =
            meetingCursor.getString(meetingCursor.getColumnIndex(DatabaseInfo.MeetingColumns.DESCRIPTION))
        val meetingid =
            meetingCursor.getString(meetingCursor.getColumnIndex(DatabaseInfo.MeetingColumns.MEETINGID))
        val meetingUserid =
            meetingCursor.getString(meetingCursor.getColumnIndex(DatabaseInfo.MeetingColumns.USERID))
        var meetingVotes = hashMapOf<String, Vote>()
        if (voteHashMap.containsKey(meetingid)) {
            meetingVotes = voteHashMap.getValue(meetingid)!!
        }
        val simpleDate = SimpleDate()
        simpleDate.setDateFormatted(
            meetingCursor.getString(
                meetingCursor.getColumnIndex(
                    DatabaseInfo.MeetingColumns.DATEFORMATTED
                )
            )
        )
        val meeting = Meeting(
            meetingName,
            meetingDescription,
            meetingid,
            meetingUserid,
            meetingVotes,
            simpleDate
        )


        voteCursor.close()
        meetingCursor.close()
        return meeting
    }

    fun getMeetings(): List<Meeting> {

        val meetingCursor = mSQLDB!!.query(
            DatabaseInfo.StoplichtTables.MEETING,
            meetingColumns,
            null,
            null,
            null,
            null,
            null
        )


        val voteCursor = mSQLDB!!.query(
            DatabaseInfo.StoplichtTables.VOTE,
            voteColumns,
            null,
            null,
            null,
            null,
            null
        )

        val voteHashMap = hashMapOf<String, HashMap<String, Vote>>()
        while (voteCursor.moveToNext()) {
            val meetingid =
                voteCursor.getString(voteCursor.getColumnIndex(DatabaseInfo.VoteColumns.MEETINGID))
            val userid =
                voteCursor.getString(voteCursor.getColumnIndex(DatabaseInfo.VoteColumns.USERID))
            val vote =
                Vote.valueOf(voteCursor.getString(voteCursor.getColumnIndex(DatabaseInfo.VoteColumns.VOTE)))
            if (voteHashMap.containsKey(meetingid)) {
                voteHashMap.getValue(meetingid)[userid] = vote
            } else {
                voteHashMap[meetingid] = hashMapOf(Pair(userid, vote))
            }
        }

        val meetings = mutableListOf<Meeting>()
        while (meetingCursor.moveToNext()) {

            val meetingName =
                meetingCursor.getString(meetingCursor.getColumnIndex(DatabaseInfo.MeetingColumns.NAME))
            val meetingDescription =
                meetingCursor.getString(meetingCursor.getColumnIndex(DatabaseInfo.MeetingColumns.DESCRIPTION))
            val meetingid =
                meetingCursor.getString(meetingCursor.getColumnIndex(DatabaseInfo.MeetingColumns.MEETINGID))
            val meetingUserid =
                meetingCursor.getString(meetingCursor.getColumnIndex(DatabaseInfo.MeetingColumns.USERID))
            var meetingVotes = hashMapOf<String, Vote>()
            if (voteHashMap.containsKey(meetingid)) {
                meetingVotes = voteHashMap.getValue(meetingid)!!
            }
            val simpleDate = SimpleDate()
            simpleDate.setDateFormatted(
                meetingCursor.getString(
                    meetingCursor.getColumnIndex(
                        DatabaseInfo.MeetingColumns.DATEFORMATTED
                    )
                )
            )
            val meeting = Meeting(
                meetingName,
                meetingDescription,
                meetingid,
                meetingUserid,
                meetingVotes,
                simpleDate
            )
            meetings.add(meeting)
        }
        voteCursor.close()
        meetingCursor.close()
        return meetings
    }

    fun insert(table: String?, nullColumnHack: String?, values: ContentValues) {
        mSQLDB!!.insert(table, nullColumnHack, values)
    }

    fun insertWithOnConflict(table: String?, nullColumnHack: String?, values: ContentValues?) {
        mSQLDB!!.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun query(
        table: String?,
        columns: Array<String?>?,
        selection: String?,
        selectArgs: Array<String?>?,
        groupBy: String?,
        having: String?,
        orderBy: String?
    ): Cursor {
        return mSQLDB!!.query(table, columns, selection, selectArgs, groupBy, having, orderBy)
    }

    fun clearOldMeetings() {
        mSQLDB!!.execSQL("delete from ${DatabaseInfo.StoplichtTables.MEETING}")
        mSQLDB!!.execSQL("delete from ${DatabaseInfo.StoplichtTables.VOTE}")
    }


    companion object {
        private var mSQLDB: SQLiteDatabase? = null
        private var mInstance: DatabaseHelper? = null
        private const val dbName = "meetings.db"
        private const val dbVersion = 6
        private val meetingColumns = arrayOf(
            DatabaseInfo.MeetingColumns.NAME,
            DatabaseInfo.MeetingColumns.DESCRIPTION,
            DatabaseInfo.MeetingColumns.DATEFORMATTED,
            DatabaseInfo.MeetingColumns.MEETINGID,
            DatabaseInfo.MeetingColumns.USERID
        )
        private val voteColumns = arrayOf(
            DatabaseInfo.VoteColumns.MEETINGID,
            DatabaseInfo.VoteColumns.USERID,
            DatabaseInfo.VoteColumns.VOTE
        )

        @Synchronized
        fun getHelper(ctx: Context): DatabaseHelper {
            if (mInstance == null) {
                mInstance = DatabaseHelper(ctx)
                mSQLDB = mInstance!!.writableDatabase
            }
            return mInstance!!
        }
    }
}