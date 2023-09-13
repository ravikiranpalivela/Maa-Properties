package com.tekskills.sampleapp.ui.poster.helper

import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.ui.poster.recyleradapter.EditorCollection

class PipList {
    //random mask
    val pipObjectMaskList: ArrayList<EditorCollection>
        get() = ArrayList()

    //random fram
    val pipObjectFrameList: ArrayList<EditorCollection>
        get() = ArrayList()

    //magazine mask
    val pipMagazineMaskList: ArrayList<EditorCollection>
        get() {
            val list = ArrayList<EditorCollection>()
            list.add(EditorCollection(R.drawable.main_mask1))
            list.add(EditorCollection(R.drawable.main_mask2))
            list.add(EditorCollection(R.drawable.main_mask3))
            list.add(EditorCollection(R.drawable.main_frame_test))
//            list.add(EditorCollection(R.drawable.main_mask5))
//            list.add(EditorCollection(R.drawable.main_mask6))
//            list.add(EditorCollection(R.drawable.main_mask7))
//            list.add(EditorCollection(R.drawable.main_mask8))
//            list.add(EditorCollection(R.drawable.main_mask9))
//            list.add(EditorCollection(R.drawable.main_mask10))
//            list.add(EditorCollection(R.drawable.main_mask11))
//            list.add(EditorCollection(R.drawable.main_mask12))
//            list.add(EditorCollection(R.drawable.main_mask13))
//            list.add(EditorCollection(R.drawable.main_mask14))
//            list.add(EditorCollection(R.drawable.main_mask15))
//            list.add(EditorCollection(R.drawable.main_mask16))
//            list.add(EditorCollection(R.drawable.main_mask17))
//            list.add(EditorCollection(R.drawable.main_mask18))
//            list.add(EditorCollection(R.drawable.main_mask19))
//            list.add(EditorCollection(R.drawable.main_mask20))
            return list
        }

    //magazine frames
    val pipMagazineFrameList: ArrayList<EditorCollection>
        get() {
            val list = ArrayList<EditorCollection>()
            list.add(EditorCollection(R.drawable.main_frame1))
            list.add(EditorCollection(R.drawable.main_frame2))
            list.add(EditorCollection(R.drawable.main_frame3))
            list.add(EditorCollection(R.drawable.main_frame_test))
//            list.add(EditorCollection(R.drawable.main_frame5))
//            list.add(EditorCollection(R.drawable.main_frame6))
//            list.add(EditorCollection(R.drawable.main_frame7))
//            list.add(EditorCollection(R.drawable.main_frame8))
//            list.add(EditorCollection(R.drawable.main_frame9))
//            list.add(EditorCollection(R.drawable.main_frame10))
//            list.add(EditorCollection(R.drawable.main_frame11))
//            list.add(EditorCollection(R.drawable.main_frame12))
//            list.add(EditorCollection(R.drawable.main_frame13))
//            list.add(EditorCollection(R.drawable.main_frame14))
//            list.add(EditorCollection(R.drawable.main_frame15))
//            list.add(EditorCollection(R.drawable.main_frame16))
//            list.add(EditorCollection(R.drawable.main_frame17))
//            list.add(EditorCollection(R.drawable.main_frame18))
//            list.add(EditorCollection(R.drawable.main_frame19))
//            list.add(EditorCollection(R.drawable.main_frame20))
            return list
        }

    //shape mask
    val pipShapesMaskList: ArrayList<EditorCollection>
        get() {
            val list = ArrayList<EditorCollection>()
            list.add(EditorCollection(R.drawable.shape_mask1))
            list.add(EditorCollection(R.drawable.shape_mask2))
            list.add(EditorCollection(R.drawable.shape_mask3))
            list.add(EditorCollection(R.drawable.shape_mask4))
//            list.add(EditorCollection(R.drawable.shape_mask5))
            return list
        }

    //shape frames
    val pipShapesFrameList: ArrayList<EditorCollection>
        get() {
            val list = ArrayList<EditorCollection>()
            list.add(EditorCollection(R.drawable.shape_frame1))
            list.add(EditorCollection(R.drawable.shape_frame2))
            list.add(EditorCollection(R.drawable.shape_frame3))
            list.add(EditorCollection(R.drawable.shape_frame4))
//            list.add(EditorCollection(R.drawable.shape_frame5))
            return list
        }
    val twoImagePipMaskList: ArrayList<EditorCollection>
        get() = ArrayList()
    val twoImagePipFrameList: ArrayList<EditorCollection>
        get() = ArrayList()
    val filtersList: ArrayList<EditorCollection>
        get() {
            val list = ArrayList<EditorCollection>()
            list.add(EditorCollection(R.drawable.filter_normal))
            list.add(EditorCollection(R.drawable.filter_hdr))
            list.add(EditorCollection(R.drawable.filter_lomo))
            list.add(EditorCollection(R.drawable.filter_glow))
            list.add(EditorCollection(R.drawable.filter_sketch))
            list.add(EditorCollection(R.drawable.filter_gray))
            list.add(EditorCollection(R.drawable.filter_black))
            list.add(EditorCollection(R.drawable.filter_relief))
            list.add(EditorCollection(R.drawable.filter_tv))
            list.add(EditorCollection(R.drawable.filter_old))
            return list
        }
    val twoImagePipThumbnails: ArrayList<EditorCollection>
        get() = ArrayList()
}