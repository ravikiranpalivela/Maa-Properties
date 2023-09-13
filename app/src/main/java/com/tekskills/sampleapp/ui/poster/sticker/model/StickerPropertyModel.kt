package com.tekskills.sampleapp.ui.poster.sticker.model

import java.io.Serializable


class StickerPropertyModel : Serializable {
    //id
    var stickerId: Long = 0

    //
    var text: String? = null

    //x
    private var xLocation = 0f

    //y
    private var yLocation = 0f

    //
    var degree = 0f

    //
    var scaling = 0f

    //
    var order = 0

    //
    var horizonMirror = 0

    //PNG URL
    var stickerURL: String? = null
    fun getxLocation(): Float {
        return xLocation
    }

    fun setxLocation(xLocation: Float) {
        this.xLocation = xLocation
    }

    fun getyLocation(): Float {
        return yLocation
    }

    fun setyLocation(yLocation: Float) {
        this.yLocation = yLocation
    }

    companion object {
        private const val serialVersionUID = 3800737478616389410L
    }
}