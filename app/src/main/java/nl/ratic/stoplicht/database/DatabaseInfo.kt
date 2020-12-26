package nl.ratic.stoplicht.database

class DatabaseInfo {

    object StoplichtTables {
        const val MEETING = "meeting"
        const val VOTE = "vote"
        const val MESSAGE = "message"
    }

    object MeetingColumns {
        const val MEETINGID = "meetingid"
        const val USERID = "userid"
        const val NAME = "name"
        const val DESCRIPTION = "description"
        const val DATEFORMATTED = "dateformatted"

    }

    object VoteColumns {
        const val USERID = "userid"
        const val MEETINGID = "meetingid"
        const val VOTE = "vote"
    }


}

