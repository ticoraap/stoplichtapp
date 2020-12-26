package nl.ratic.stoplicht

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import nl.ratic.stoplicht.model.Meeting
import nl.ratic.stoplicht.model.Vote
import nl.ratic.stoplicht.model.VoteCount

class MeetingsAdapter(private var meetingsList: List<Meeting>, meetingClickedListener: MeetingClickedListener) :
    RecyclerView.Adapter<MeetingsAdapter.MyViewHolder>(), Filterable {

    val meetingClickedListener = meetingClickedListener
    var meetingsFilteredList = meetingsList

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)  {

        var name: TextView = view.findViewById(R.id.name)
        var description: TextView = view.findViewById(R.id.description)
        var date: TextView = view.findViewById(R.id.date)
        var box : RelativeLayout = view.findViewById(R.id.box)
        var greenbox : RelativeLayout = view.findViewById(R.id.greenbox)
        var orangebox : RelativeLayout = view.findViewById(R.id.orangebox)
        var redbox : RelativeLayout = view.findViewById(R.id.redbox)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.meeting_list_row, parent, false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val meeting = meetingsFilteredList[position]
        val voteCount = countVotes(meeting)
        holder.name.text = meeting.name
        holder.description.text = meeting.description
        holder.date.text = meeting.simpleDate.getDateFormatted()
        holder.greenbox.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                voteCount.green

        )
        holder.orangebox.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                voteCount.orange

        )
        holder.redbox.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                voteCount.red

        )
        holder.box.setOnClickListener(){
            meetingClickedListener.clickedOnMeeting(meetingsFilteredList.get(position))
        }

    }

    private fun countVotes(meeting : Meeting) : VoteCount{
        val voteCount = VoteCount(0f,0f,0f)
        meeting.votes.values.forEach {
            when (it) {
                Vote.GREEN -> voteCount.green++
                Vote.ORANGE -> voteCount.orange++
                Vote.RED -> voteCount.red++
            }
        }
        return voteCount
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
                    val resultList = ArrayList<Meeting>()
                    for (meeting in meetingsList){
                        if (meeting.simpleDate.getDateFormatted() == charSearch){
                            resultList.add(meeting)
                        }
                    }
                    meetingsFilteredList = resultList
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