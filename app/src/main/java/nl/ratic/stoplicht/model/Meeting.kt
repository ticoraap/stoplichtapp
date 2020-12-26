package nl.ratic.stoplicht.model

data class Meeting(
    val name: String = "",
    val description: String = "",
    var meetingid: String = "",
    val userid: String = "",
    val votes: HashMap<String, Vote> = hashMapOf(),
    var simpleDate: SimpleDate = SimpleDate()
)