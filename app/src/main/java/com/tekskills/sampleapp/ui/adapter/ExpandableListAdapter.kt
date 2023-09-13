package com.tekskills.sampleapp.ui.adapter

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.TextView
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.model.ExpandedMenuModel


class ExpandableListAdapter(private val mContext: Context,
                            listDataHeader: List<ExpandedMenuModel>, listChildData: HashMap<ExpandedMenuModel, List<String>>,
                            mView: ExpandableListView
) : BaseExpandableListAdapter() {
    private val mListDataHeader : List<ExpandedMenuModel>

    // child data in format of header title, child title
    private val mListDataChild: HashMap<ExpandedMenuModel, List<String>>
    private var expandList : ExpandableListView

    init {
        mListDataHeader = listDataHeader
        mListDataChild = listChildData
        expandList = mView
    }

    override fun getGroupCount(): Int {
        val i = mListDataHeader.size
        Log.d("GROUP-COUNT", i.toString())
        return mListDataHeader.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        var childCount = 0
        if (mListDataChild[mListDataHeader[groupPosition]]!!.isNotEmpty()) {
            childCount = mListDataChild[mListDataHeader[groupPosition]]!!.size
        }
        return childCount
    }

    override fun getGroup(groupPosition: Int): Any {
        return mListDataHeader[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        Log.d(
            "Submenu", "${mListDataChild[mListDataHeader[groupPosition]]
            !![childPosition]} parent pos: $groupPosition child pos: $childPosition"
        )
        return mListDataChild[mListDataHeader[groupPosition]]!![childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var view = convertView
        val headerTitle: ExpandedMenuModel = getGroup(groupPosition) as ExpandedMenuModel
        if (view == null) {
            val infalInflater = mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = infalInflater.inflate(R.layout.list_header, null)
        }
        val lblListHeader = view!!.findViewById<View>(R.id.submenu) as TextView
        val headerIcon = view.findViewById<View>(R.id.iconimage) as ImageView
        val ivGroupIndicator = view.findViewById<View>(R.id.ivGroupIndicator) as ImageView
        ivGroupIndicator.isSelected = isExpanded

        lblListHeader.setTypeface(null, Typeface.BOLD)
        lblListHeader.text = headerTitle.iconName
        if(headerTitle.iconImg != -1) {
            headerIcon.setImageResource(headerTitle.iconImg)
        }
        if(getChildrenCount(groupPosition)==0)
        {
            view.background = mContext.getDrawable(R.color.white)
            ivGroupIndicator.visibility = View.GONE
        }
        else{
            view.background = mContext.getDrawable(R.color.colorAccent)
            ivGroupIndicator.visibility = View.VISIBLE
        }
        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var view = convertView
        val childText = getChild(groupPosition, childPosition) as String
        if (view == null) {
            val infalInflater = mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = infalInflater.inflate(R.layout.list_submenu, null)
        }

        val txtListChild = view!!.findViewById<View>(R.id.submenu) as TextView
        txtListChild.text = childText
        Log.d("submenu","child text ${childText} ")
        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}