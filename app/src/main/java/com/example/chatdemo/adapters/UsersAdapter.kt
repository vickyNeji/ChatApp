package com.example.chatdemo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatdemo.R
import com.example.chatdemo.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.row_conversation.view.*
import java.text.SimpleDateFormat
import java.util.*

class UsersAdapter(private val arrayList:List<User>, private val context:Context,val onUserClicked: OnUserClicked):RecyclerView.Adapter<UsersAdapter.DataHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHolder {
    return DataHolder(
        LayoutInflater.from(
            parent.context
        ).inflate(R.layout.row_conversation, parent, false)
    )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: DataHolder, position: Int) {

        val senderId=FirebaseAuth.getInstance().uid
        val senderRoom=senderId+arrayList[position].uid

        Firebase.firestore.collection("Chats")
            .document(senderRoom)
            .addSnapshotListener(EventListener { value, error ->
                if(value?.exists()!!) {
                    val lastMsg = value.getString("lastMsg")
                    val time = value.getLong("lastMsgTime")

                    holder.itemView.tvLastMessage.text = lastMsg
                    holder.itemView.msgTime.text=SimpleDateFormat("hh:mm a").format(Date(time!!))
                }else{
                    holder.itemView.tvLastMessage.text="Tap to chat"
                }
            })

        holder.itemView.tvUserName.text=arrayList[position].name
        Glide.with(context).load(arrayList[position].profileImage)
            .placeholder(R.drawable.images)
            .error(R.drawable.images)
            .into(holder.itemView.ivPerson)
        holder.itemView.setOnClickListener {
            onUserClicked.onClicked(arrayList[position].name,arrayList[position].uid)
        }

    }

    class DataHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
       // val binding:RowConversationBinding= RowConversationBinding.bind(itemView)
    }


    interface OnUserClicked{
        fun onClicked(userName:String, userId:String)
    }
}