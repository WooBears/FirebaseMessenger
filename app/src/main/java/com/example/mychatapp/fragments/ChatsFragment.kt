package com.example.mychatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mychatapp.R
import com.example.mychatapp.adapter.UserAdapter
import com.example.mychatapp.databinding.FragmentChatsBinding
import com.example.mychatapp.model.Chatlist
import com.example.mychatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatsFragment : Fragment() {


    lateinit var binding: FragmentChatsBinding
    private var userAdapter: UserAdapter?= null
    private var mUsers: List<User>? = null
    private var usersChatList: List<Chatlist>? = null
    lateinit var recyclerView: RecyclerView
    private var firebaseUser: FirebaseUser? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,

        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerViewChatsList
        recyclerView.setHasFixedSize(true)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersChatList = ArrayList()

        val reference = FirebaseDatabase.getInstance().reference.child("ChatList")
            .child(firebaseUser!!.uid)
        reference!!.addValueEventListener( object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                (usersChatList as ArrayList).clear()

                for (dataSnapshot in p0.children){

                    val chatList = dataSnapshot.getValue(Chatlist::class.java)

                    (usersChatList as ArrayList).add(chatList!!)
                }

                retrieveChatList()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun retrieveChatList(){

        mUsers = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref!!.addValueEventListener( object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList).clear()

                for (snapshots in p0.children){

                    val user = snapshots.getValue(User::class.java)

                    for (eachChatList in usersChatList!!){

                        if(user!!.getUID().equals(eachChatList.getId()))
                        {
                            (mUsers as ArrayList).add(user!!)
                        }
                    }
                }

                userAdapter = UserAdapter(context!!, (mUsers as ArrayList<User>), true)
                recyclerView.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}