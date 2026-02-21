package com.gabriel.danael_fernandes_react_manager.api.dto

import com.gabriel.danael_fernandes_react_manager.core.authentication.UserAuth
import com.gabriel.danael_fernandes_react_manager.database.entity.ContentCreator

data class CreatorContentResponse(
    val id: Long,
    val name: String,
    val username: String,
    val email: String,
    val bio: String = "bio",
    val avatar: String = "https://i.ytimg.com/vi/KS0rGkcR8A8/hq720.jpg?sqp=-oaymwEnCNAFEJQDSFryq4qpAxkIARUAAIhCGAHYAQHiAQoIGBACGAY4AUAB&rs=AOn4CLCxmMB0U9w1l-RDCJWG4yGe3GoegA",
    val playlistLink: String
) {
    constructor(userInfo: UserAuth, contentCreator: ContentCreator) : this(
        id = contentCreator.id,
        name = contentCreator.fullname,
        username = userInfo.username,
        email = userInfo.email,
        playlistLink = "https://youtube.com/playlist?list=" + contentCreator.playlistId
    )
}
