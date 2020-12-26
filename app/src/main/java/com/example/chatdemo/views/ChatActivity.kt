package com.example.chatdemo.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.chatdemo.R
import com.example.chatdemo.adapters.MessageAdapter
import com.example.chatdemo.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat.*
import java.sql.Date
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatActivity : AppCompatActivity() {

    private  val TAG = "ChatActivity"
    private lateinit var  list:ArrayList<Message>

    var senderRoom:String=""; var  receiverRoom:String=""
    private val firestore by lazy {
        Firebase.firestore
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar?.title=intent.getStringExtra("userName")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val receiverId=intent.getStringExtra("userId")
        val senderUid=FirebaseAuth.getInstance().uid

        senderRoom=senderUid + receiverId
        receiverRoom=receiverId + senderUid

        list= ArrayList()

        firestore.collection("Chats").document(senderRoom)
            .collection("messages")
            .orderBy("timeStamp")
            .addSnapshotListener(EventListener { value, error ->
                Log.d(TAG, "inside: ")
                if(error!=null){
                    Log.d(TAG, "onCreateerrrraaa: ")
                    return@EventListener
                }
                if (value != null) {

                    list.clear()
                    //Log.d(TAG, "onCreate: "+list.size)
                    for (snapshot:DocumentSnapshot in value.documents){
                        //Log.d(TAG, "onCreate: "+snapshot.data)
                            list.add(snapshot.toObject(Message::class.java)!!)
                    }

                    //////LEFT HERE
                    if(list.size>0){
                      rvChats.adapter=MessageAdapter(this,list)
                    }
                    //Log.d(TAG, "onCreate: "+list[0].message)

                }else{
                    Log.d(TAG, "onCreatenull: ")

                }            })

        ivSend.setOnClickListener {
            if(etMessageBox.text.toString().isNotEmpty()){
                val sendMessage=etMessageBox.text.toString()

                val message=Message(sendMessage,senderUid!!,Date().time)

                val lastMsg:HashMap<String,Any> = HashMap()
                lastMsg["lastMsg"] = message.message
                lastMsg["lastMsgTime"] = Date().time
                firestore.collection("Chats").document(senderRoom).set(lastMsg, SetOptions.merge())
                firestore.collection("Chats").document(receiverRoom).set(lastMsg,SetOptions.merge())

                firestore.collection("Chats")
                    .document(senderRoom)
                    .collection("messages")
                    .add(message).addOnSuccessListener{
                        etMessageBox.setText("")
                        firestore.collection("Chats")
                            .document(receiverRoom)
                            .collection("messages")
                            .add(message).addOnSuccessListener{

                            }
                    }



            }
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}