package nl.ratic.stoplicht.model

import android.util.Log
import java.time.LocalDate
import java.time.format.DateTimeFormatter


data class SimpleDate(var year : Int = 1970, var month : Int = 1, var day : Int = 1) {

    private var dateFormatted = ""

    fun getDateFormatted() : String {
        if (dateFormatted.isEmpty()){
            var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            var date = LocalDate.of(year,month,day)
            return date.format(formatter)
        }
        return dateFormatted
    }

    fun setDateFormatted(dateFormatted: String){
        this.dateFormatted = dateFormatted
    }

}