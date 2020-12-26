package com.example.chatdemo.views

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatdemo.R
import com.example.chatdemo.models.User
import com.example.chatdemo.utils.Constants
import com.example.chatdemo.utils.Constants.Certificate_IMAGE_REQUEST
import com.example.chatdemo.utils.Constants.REQUEST_CODE_STORAGE_PERMISSION
import com.example.chatdemo.utils.showToast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_setup_profile.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.jar.Manifest

class SetupProfileActivity : AppCompatActivity(),EasyPermissions.PermissionCallbacks {

    private  var selectedImageUri:Uri?=null

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    private val firestore by lazy {
        Firebase.firestore
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_profile)

        supportActionBar?.hide()

        imageViewProfile.setOnClickListener {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                 if(EasyPermissions.hasPermissions(this,android.Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                showImageChooser()
                }else{
                requestPermissionsFromUser()
                }
            }else{
                showImageChooser()
            }
        }

        btnSetupProfile.setOnClickListener {
            if(etName.text.toString().isEmpty()){
                showToast("Name is mandatory")
                return@setOnClickListener
            }

            if(selectedImageUri!=null) {
                val reference:StorageReference=firebaseStorage.reference
                    .child("Profiles").child(firebaseAuth.uid.toString())

                reference.putFile(selectedImageUri!!).addOnCompleteListener {task->
                    if(task.isSuccessful){
                        reference.downloadUrl.addOnSuccessListener {uri->
                             val userProfileImage=uri.toString()
                            val user=User(firebaseAuth.uid.toString(),etName.text.toString(),
                            firebaseAuth.currentUser?.phoneNumber.toString(),userProfileImage)

                            firestore.collection("Users")
                                .add(user)
                                .addOnSuccessListener {
                                    showToast("Profile Updated")
                                    startActivity(Intent(this,MainActivity::class.java))
                                    finishAffinity()
                                }.addOnFailureListener {
                                    showToast("Something went wrong")
                                }

                        }
                    }else{
                        showToast("Can't upload image")
                    }
                }
            }else{
                val user=User(firebaseAuth.uid.toString(),etName.text.toString(),
                    firebaseAuth.currentUser?.phoneNumber.toString(),"No Image")

                firestore.collection("Users")
                    .add(user)
                    .addOnSuccessListener {
                        showToast("Profile Updated")
                        startActivity(Intent(this,MainActivity::class.java))
                        finishAffinity()
                    }.addOnFailureListener {
                        showToast("Something went wrong")
                    }
            }

        }

    }

    private fun requestPermissionsFromUser() {
        EasyPermissions.requestPermissions(this,"You need to allow this permission to upload image",
           REQUEST_CODE_STORAGE_PERMISSION,
        android.Manifest.permission.READ_EXTERNAL_STORAGE)

    }

    private fun showImageChooser() {
        val intent= Intent().apply {
            type = "image/*"
            action=Intent.ACTION_PICK
            startActivityForResult(Intent.createChooser(this,"Select Image"),Certificate_IMAGE_REQUEST)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        }else{
            requestPermissionsFromUser()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        showImageChooser()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.data?.let {
            imageViewProfile.setImageURI(it)
            selectedImageUri=it
        }
    }
}