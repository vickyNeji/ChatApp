package com.example.chatdemo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatdemo.R
import com.example.chatdemo.models.Message
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.item_reciever.view.*
import kotlinx.android.synthetic.main.item_send.view.*

class MessageAdapter(val context: Context,val list:ArrayList<Message>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    final val ITEM_SENT=1
    final val ITEM_RECEIVE=2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
          if(viewType==ITEM_SENT){
             val view=LayoutInflater.from(context).inflate(R.layout.item_send,parent,false)
              return SentViewHolder(view)
          }else{
              val view=LayoutInflater.from(context).inflate(R.layout.item_reciever,parent,false)
              return ReceiveViewHolder(view)
          }

    }

    override fun getItemCount(): Int {
    return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message=list[position]
        if(holder.javaClass==SentViewHolder::class.java){
            val sentViewHolder:SentViewHolder= holder as SentViewHolder
            sentViewHolder.itemView.message_text.text = message.message
        }else{
            val receiveViewHolder:ReceiveViewHolder= holder as ReceiveViewHolder
            receiveViewHolder.itemView.received_message.text = message.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message=list[position]
        return if(FirebaseAuth.getInstance().uid==message.senderId){
            ITEM_SENT
        }else ITEM_RECEIVE

    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}