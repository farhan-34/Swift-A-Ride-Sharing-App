package com.example.swift.frontEnd.rider.chat

import com.example.swift.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.offer_message_from.view.*
import kotlinx.android.synthetic.main.offer_message_to.view.*


//class ChatFromItem(val text: String, val user: Rider): Item<GroupieViewHolder>() {
class ChatFromItem(val text: String): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = text

        //val uri = user.profileImageUrl
        //val targetImageView = viewHolder.itemView.imageview_chat_from_row
        //Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

//class ChatToItem(val text: String, val user: User): Item<GroupieViewHolder>() {
class ChatToItem(val text: String): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text

        // load our user image into the star
        //val uri = user.profileImageUrl
        //val targetImageView = viewHolder.itemView.imageview_chat_to_row
        //Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}


class OfferMsgFromItem(val text: String): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.from_offer_msg_textView.text = text

    }

    override fun getLayout(): Int {
        return R.layout.offer_message_from
    }
}

class OfferMsgToItem(val text: String): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.to_offer_msg_textView.text = text

    }

    override fun getLayout(): Int {
        return R.layout.offer_message_to
    }
}