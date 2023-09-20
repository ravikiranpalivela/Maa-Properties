package com.tekskills.sampleapp.utils.like

import androidx.annotation.DrawableRes


class Icon(
    @param:DrawableRes var onIconResourceId: Int,
    @param:DrawableRes var offIconResourceId: Int,
    var iconType: IconType
)