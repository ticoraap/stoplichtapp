package nl.ratic.stoplicht

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import nl.ratic.stoplicht.model.Meeting

class MeetingsAdapter(private var meetingsList: List<Meeting>, meetingClickedListener: MeetingClickedListener) :
    RecyclerView.Adapter<MeetingsAdapter.MeetingViewHolder>(), Filterable {

    val meetingClickedListener = meetingClickedListener
    var meetingsFilteredList = meetingsList

    inner class MeetingViewHolder(view: View) : RecyclerView.ViewHolder(view)  {
        var name: TextView = view.findViewById(R.id.name)
        var description: TextView = view.findViewById(R.id.description)
        var date: TextView = view.findViewById(R.id.date)
        var box : RelativeLayout = view.findViewById(R.id.box)
        var greenbox : RelativeLayout = view.findViewById(R.id.greenbox)
        var orangebox : RelativeLayout = view.findViewById(R.id.orangebox)
        var redbox : RelativeLayout = view.findViewById(R.id.redbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.meeting_list_row, parent, false)
        return MeetingViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MeetingViewHolder, position: Int) {
        val meeting = meetingsFilteredList[position]
        holder.name.text = meeting.name
        holder.description.text = meeting.description
        holder.date.text = meeting.simpleDate.getDateFormatted()
        holder.greenbox.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                meeting.getVoteCount().green

        )
        holder.orangebox.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                meeting.getVoteCount().orange

        )
        holder.redbox.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                meeting.getVoteCount().red

        )
        holder.box.setOnClickListener(){
            meetingClickedListener.clickedOnMeeting(meetingsFilteredList[position])
        }
    }

    override fun getItemCount(): Int {
        return meetingsFilteredList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()){
                    meetingsFilteredList = meetingsList
                } else {
                    val filteredResultList = ArrayList<Meeting>()
                    for (meeting in meetingsList){
                        if (meeting.simpleDate.getDateFormatted() == charSearch){
                            filteredResultList.add(meeting)
                        }
                    }
                    meetingsFilteredList = filteredResultList
                }
                val filterResults = FilterResults()
                filterResults.values = meetingsFilteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                meetingsFilteredList = results?.values as List<Meeting>
                notifyDataSetChanged()
            }
        }
    }
}