package com.example.chatdemo.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.chatdemo.R
import com.example.chatdemo.adapters.UsersAdapter
import com.example.chatdemo.models.User
import com.example.chatdemo.utils.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),UsersAdapter.OnUserClicked {

    private val firesTore by lazy {
        Firebase.firestore
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firesTore.collection("Users").get()
            .addOnSuccessListener {
                val list:ArrayList<User> = ArrayList()
                for (user in it.toObjects(User::class.java)){
                    if(user.uid!=FirebaseAuth.getInstance().uid){
                        list.add(user)
                    }
                }
                rvContacts.adapter=UsersAdapter(list,this,this)
            }
            .addOnFailureListener {
                showToast("Failed")
            }


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_option_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.search->{
                showToast("Search")
            }

            R.id.settings->{
                showToast("Settings")
            }

        }
        return super.onOptionsItemSelected(item)

    }

    override fun onClicked(userName: String, userId: String) {
        Intent(this,ChatActivity::class.java).apply {
            putExtra("userName",userName)
            putExtra("userId",userId)
            startActivity(this)
        }
    }

}