package com.tekskills.sampleapp.utils.like

class ClickListeners {
}

interface OnAnimationEndListener {
    fun onAnimationEnd(likeButton: LikeButton?)
}

interface OnLikeListener {
    fun liked(likeButton: LikeButton?)
    fun unLiked(likeButton: LikeButton?)
}