package com.tekskills.sampleapp.utils.reactions

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.annotation.Px

data class ReactionsConfig(
    val reactions: Collection<Reaction>,
    @Px val reactionSize: Int,
    @Px val horizontalMargin: Int,
    @Px val verticalMargin: Int,

    /** Horizontal gravity compare to parent view or screen */
    val popupGravity: PopupGravity,
    /** Margin between dialog and screen border used by [PopupGravity] screen related values. */
    val popupMargin: Int,
    val popupCornerRadius: Int,
    @ColorInt val popupColor: Int,
    @IntRange(from = 0, to = 255) val popupAlphaValue: Int,

    val reactionTextProvider: ReactionTextProvider,
    val textBackground: Drawable,
    @ColorInt val textColor: Int,
    val textHorizontalPadding: Int,
    val textVerticalPadding: Int,
    val textSize: Float,
    val typeface: Typeface?
)

