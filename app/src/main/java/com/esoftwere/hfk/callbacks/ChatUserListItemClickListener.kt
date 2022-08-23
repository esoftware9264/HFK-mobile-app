package com.esoftwere.hfk.callbacks

import com.esoftwere.hfk.model.chat_user_list.ChatUserListDataModel

interface ChatUserListItemClickListener {
    fun onChatUserListItemClick(chatUserListDataModel: ChatUserListDataModel)
}