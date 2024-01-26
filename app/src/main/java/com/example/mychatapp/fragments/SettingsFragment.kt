package com.example.mychatapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.mychatapp.R
import com.example.mychatapp.databinding.FragmentSettingsBinding
import com.example.mychatapp.model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import retrofit2.http.Url
import java.security.ProtectionDomain

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    var userReference: DatabaseReference? = null
    var firbaseUser: FirebaseUser? = null
    private val RequestCode = 438
    private var imageUri: Uri? = null
    private var storageRef: StorageReference? = null
    private var coverChecker: String? = null
    private var socialChecker: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firbaseUser = FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("Users").child(firbaseUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")

         userReference!!.addValueEventListener(object : ValueEventListener {
             override fun onDataChange(p0: DataSnapshot) {
                 if (p0.exists()){

                     val user: User? = p0.getValue(User::class.java)

                     if (context!=null){
                         binding.usernameSettings.text = user!!.getUsername()
                         Picasso.get().load(user.getProfile()).into(binding.profileImageSettings)
                         Picasso.get().load(user.getCover()).into(binding.coverImage)
                     }
                 }
             }

             override fun onCancelled(error: DatabaseError) {
                 TODO("Not yet implemented")
             }

         })

        binding.profileImageSettings.setOnClickListener {
            pickImage()
        }

        binding.coverImage.setOnClickListener {
            coverChecker = "cover"
            pickImage()
        }

        binding.setFacebook.setOnClickListener {
            socialChecker = "facebook"
            setSocialLinks()
        }

        binding.setInstagram.setOnClickListener {
            socialChecker = "instagram"
            setSocialLinks()
        }

        binding.setWebsite.setOnClickListener {
            socialChecker = "website"
            setSocialLinks()
        }
    }

    private fun setSocialLinks() {

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        if (socialChecker == "website"){
            builder.setTitle("Write URL:")
        }
        else {
            builder.setTitle("Write username:")
        }

        val editText = EditText(context)


        if (socialChecker == "website"){
            editText.hint = "e.g www.google.com"
        }
        else {
            editText.hint = "e.g pahlavidavlatmirov_"
        }

        builder.setView(editText)

        builder.setPositiveButton("Create", DialogInterface.OnClickListener { dialog, which ->

            val str = editText.text.toString()

            if (str.isEmpty()){
                Toast.makeText(context,"Please write something..", Toast.LENGTH_LONG).show()
            }
            else{
                createSaveSocialLink(str)
            }
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })

        builder.show()

    }

    private fun createSaveSocialLink(str: String) {
        val mapSocial = HashMap<String, Any>()

        when(socialChecker)
        {
            "facebook" ->
            {
                mapSocial["facebook"] = "https://m.facebook.com/$str"
            }
            "instagram" ->
            {
                mapSocial["instagram"] = "https://m.instagram.com/$str"
            }
            "website" ->
            {
                mapSocial["instagram"] = "https:/$str"
            }
        }

        userReference!!.updateChildren(mapSocial).addOnCompleteListener {
            task ->

            if (task.isSuccessful)
            {
                Toast.makeText(context, "Updated successfully!",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun pickImage() {
        //change it cause it is depricated
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode && resultCode == Activity.RESULT_OK && data!!.data != null){
            imageUri = data.data
            Toast.makeText(context,"uploading....", Toast.LENGTH_LONG).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("Image is uploading, please wait...")
        progressBar.show()

        if (imageUri != null){
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")

            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask<Uri?>(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful)
                {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val dowlnoadUril = task.result
                    val url = dowlnoadUril.toString()

                    if (coverChecker == "cover")
                    {
                        val mapCoverImg = HashMap<String, Any>()
                        mapCoverImg["cover"] = url
                        userReference!!.updateChildren(mapCoverImg)
                        coverChecker = ""
                    }
                    else
                    {
                        val mapCoverImg = HashMap<String, Any>()
                        mapCoverImg["profile"] = url
                        userReference!!.updateChildren(mapCoverImg)
                        coverChecker = ""
                    }
                    progressBar.dismiss()
                }
            }
        }
    }
}