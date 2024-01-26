package com.example.mychatapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.mychatapp.MainActivity
import com.example.mychatapp.MessageChatActivity
import com.example.mychatapp.databinding.UserSearchItemBinding
import com.example.mychatapp.model.User
import com.google.firebase.database.core.view.View
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

const val USER_ID: String = "visit.id"

class UserAdapter(
    mContext: Context,
    mUser: List<User>,
    isChatCheck: Boolean
) : RecyclerView.Adapter<UserAdapter.UserViewHolder?>()
{
    private val mContext: Context
    private val mUser: List<User>
    private var isChatCheck: Boolean


    init {
        this.mUser = mUser
        this.mContext = mContext
        this.isChatCheck = isChatCheck
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            UserSearchItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user: User = mUser[position]

        holder.binding.itemUsername.text = user!!.getUsername()
        Picasso.get().load(user.getProfile()).into(holder.binding.itemProfileImage)

        holder.itemView.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )
            val builder = AlertDialog.Builder(mContext)
            builder.setTitle("What do you want?")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, position ->
                if (position == 0){
                    val intent = Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra(USER_ID, user.getUID())
                    mContext.startActivity(intent)
                }
                if (position == 1){

                }
            })
            builder.show()
        }
    }

    class UserViewHolder(val binding: UserSearchItemBinding) : RecyclerView.ViewHolder(binding.root)
    {
        var userName: TextView
        var profileImage: CircleImageView
        var onlineImageView: CircleImageView
        var ofllineImagview: CircleImageView
        var lastMessagesTxt: TextView

        init {
            userName = binding.itemUsername
            profileImage = binding.itemProfileImage
            onlineImageView = binding. itemImageOnline
            ofllineImagview = binding.itemImageOffline
            lastMessagesTxt = binding.itemLastMessage

        }
    }

}