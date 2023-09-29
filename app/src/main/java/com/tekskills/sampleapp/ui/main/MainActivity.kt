package com.tekskills.sampleapp.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.MotionEventCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.data.prefrences.SharedPrefManager
import com.tekskills.sampleapp.databinding.ActivityMainBinding
import com.tekskills.sampleapp.model.ExpandedMenuModel
import com.tekskills.sampleapp.ui.adapter.ExpandableListAdapter
import com.tekskills.sampleapp.ui.adapter.ViewPagerAdapter
import com.tekskills.sampleapp.ui.base.BaseActivity
import com.tekskills.sampleapp.ui.splash.SplashActivity
import com.tekskills.sampleapp.utils.DoubleClickListener
import com.tekskills.sampleapp.utils.NetworkObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel, MainActivity>() {

    private var mMenuAdapter: ExpandableListAdapter? = null
    private var listDataHeader: ArrayList<ExpandedMenuModel>? = null
    private var listDataChild: HashMap<ExpandedMenuModel, List<String>>? = null


    override fun onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (binding.dlRoot.isDrawerOpen(binding.navView)) {
            binding.dlRoot.closeDrawer(binding.navView)
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBar);

        defineLayout()
        observeUserNetworkConnection()

        observeRetrofitErrors()
        observeBannerResponse()

        binding.includeNavHeader?.ivNavigationCancel?.setOnClickListener {
            binding.dlRoot.closeDrawers()
        }

//        binding.ivDrawer.setOnClickListener {
//            if (binding.dlRoot.isDrawerOpen(GravityCompat.START)) {
//                binding.dlRoot.closeDrawer(GravityCompat.START)
//            } else {
//                binding.dlRoot.openDrawer(GravityCompat.START)
//            }
//        }

        setupDrawerContent(binding.navView)

        prepareListData()
        mMenuAdapter = ExpandableListAdapter(
            this, listDataHeader!!,
            listDataChild!!, binding.elNavigationMenu
        )
        /**
         * setting expandable list in navigation drawer
         */
        binding.elNavigationMenu.setAdapter(mMenuAdapter)

        binding.elNavigationMenu.setOnChildClickListener { listView, view, grpPos, childPos, l ->
            var fragment: Fragment? = null
            var args: Bundle? = null

            if (grpPos == 2) {
//                when (childPos) {
//                    0 -> {
//                        fragment = UserDetailsFragment()
//                        args = Bundle().apply {
//                            putInt("KEY_PRODUCT_ID", childPos)
//                        }
//                    }
//
//                    1 -> {
//                        fragment = MyVideosFragment()
//                        args = Bundle().apply {
//                            putInt("KEY_PRODUCT_ID", grpPos)
//                        }
//                    }
//
//                    2 -> {
//                        fragment = HelpFAQFragment()
//                        this.supportFragmentManager.beginTransaction()
//                            .replace(
//                                R.id.fragment_home_details,
//                                fragment,
//                                fragment?.javaClass?.simpleName
//                            )
//                            .addToBackStack("first")
//                            .commit()
//                    }
//
//                    3 -> {
//                        fragment = PrivacyPolicyFragment()
//                        args = Bundle().apply {
//                            putString("services", "Privacy Policy")
//                            putString("weburl", "about.html")
//                            putInt("KEY_PRODUCT_ID", grpPos)
//                        }
//                    }
//
//                    4 -> {
//                        fragment = TermsAndConditionsFragment()
//                        args = Bundle().apply {
//                            putString("services", "Terms & Conditions")
//                            putString("weburl", "about.html")
//                            putInt("KEY_PRODUCT_ID", grpPos)
//                        }
//                    }
//
//                    5 -> {
//                        fragment = ContactUsFragment()
//                        args = Bundle().apply {
//                            putString("services", "Contact Us")
//                            putString("weburl", "contact.html")
//                            putInt("KEY_PRODUCT_ID", grpPos)
//                        }
//                    }
//
//                    6 -> {
//                        fragment = AboutUsFragment()
//                        args = Bundle().apply {
//                            putString("services", "About Us")
//                            putString("weburl", "about.html")
//                            putInt("KEY_PRODUCT_ID", grpPos)
//                        }
//                    }
//
//                    7 -> {
//                        viewModel?.clearData()
//                        signOutAzure()
//                    }
//                }
            }

            if (fragment != null && args != null) {
//                startFragment(fragment, args)
            }

            binding.dlRoot.closeDrawers()
            false
        }

        binding.elNavigationMenu.setOnGroupClickListener { listView, view, grpPos, l ->

            when (grpPos) {
                0, 1, 2, 3 -> {
                    val fragment = when (grpPos) {
                        0 -> intent =
                            Intent(applicationContext, MainActivity::class.java)

                        1 -> intent =
                            Intent(applicationContext, MainActivity::class.java)

                        2 -> intent =
                            Intent(applicationContext, SplashActivity::class.java)

                        3 -> intent =
                            Intent(applicationContext, MainActivity::class.java)

                        else -> null
                    }
                    val args = Bundle().apply {
                        putInt("KEY_PRODUCT_ID", grpPos)
                    }

                    fragment?.let {
                        this.startActivity(intent)
                        binding.dlRoot.closeDrawers()
                    }
                }

                else -> binding.elNavigationMenu.getExpandableListPosition(grpPos)
            }
            false
        }
    }

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            viewModel.category.value = getTitle(position)
            viewModel.refreshResponse()
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
        }
    }

    fun appBarLayoutHandle(value: Boolean) {
        binding.motionBase.visibility =
            if (!value)
                GONE
            else VISIBLE
        binding.tabLayout.visibility =
            if (!value)
                GONE else VISIBLE
    }

    private fun defineLayout() {
        setUpToolBar()

        binding.pager.adapter = createPagerAdapter()

        binding.pager.registerOnPageChangeCallback(onPageChangeCallback)
        binding.pager.isUserInputEnabled = false

        setUpTabLayout()

        binding.ivDrawer.setOnClickListener {
            if (binding.dlRoot.isDrawerOpen(binding.navView))
                binding.dlRoot.closeDrawer(binding.navView)
            else
                binding.dlRoot.openDrawer(binding.navView)
        }

        binding.ivLogo.setOnClickListener {
            val goToMainActivity = Intent(applicationContext, MainActivity::class.java)
            val activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                this,
                binding.ivLogo,
                "app_logo"
            )
            startActivity(goToMainActivity, activityOptions.toBundle())
        }
    }

    private fun setUpToolBar() {
        toolbar = binding.toolBar
        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setUpTabLayout() {
        val tabTitles = resources.getStringArray(R.array.intro_title_list)

        TabLayoutMediator(binding.tabLayout, binding.pager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.text = tabTitles[position]
            }).attach()

        binding.tabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.view?.children?.forEach {
                    if (it is LinearLayout) {
                        val view1 = it.getChildAt(0)

                        if (view1 is CardView)
                            view1.setCardBackgroundColor(resources.getColor(R.color.colorSecondary))

                        val view2 = view1.findViewById<TextView>(R.id.title)

                        if (view2 is TextView) {
                            view2.post {
                                view2.textSize = 14f
                                view2.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                            }
                        }
                    }
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.view?.children?.forEach {
                    if (it is LinearLayout) {
                        val view1 = it.getChildAt(0)

                        if (view1 is CardView)
                            view1.setCardBackgroundColor(resources.getColor(R.color.colorRed))

                        val view2 = view1.findViewById<TextView>(R.id.title)

                        if (view2 is TextView) {
                            view2.post {
                                view2.textSize = 16f
                                view2.setTextColor(Color.WHITE)
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, intent)
        }
    }

    @SuppressLint("ResourceAsColor")
    fun addCustomView(
        title: String,
        size: Float = 14f,
        color: Int = R.color.colorPrimaryDark
    ): View {
        val view = layoutInflater.inflate(R.layout.item_tab, binding.tabLayout, false)
        val titleTextView = view.findViewById<TextView>(R.id.title)
        titleTextView.apply {
            text = title


            if (color == Color.WHITE) {
                (view.findViewById<CardView>(R.id.cardView)).setCardBackgroundColor(
                    resources.getColor(
                        R.color.colorRed
                    )
                )
                setTextColor(color)
            } else {
                setTextColor(resources.getColor(color))
            }

            textSize = size
        }

        return view
    }

    private fun getTitle(position: Int): String {
        return when (position) {
            0 -> "All"
            1 -> "News"
            2 -> "Wishes"
            3 -> "Posters"
            4 -> "Shorts"
            5 -> "Science"
            6 -> "Entertainment"
            7 -> "Sports"
            else -> "null"
        }
    }

    private fun createPagerAdapter(): ViewPagerAdapter =
        ViewPagerAdapter(this, getContext())

    private fun showDialogWithRadioButtons(
        titleText: String,
        firstRadioButtonText: String,
        secondRadioButtonText: String,
        negativeButtonText: String = "Cancel",
        positiveButtonText: String = "Apply",
        positiveButtonAction: (Dialog) -> Unit,
        negativeButtonAction: (Dialog) -> Unit = { dialog ->
            dialog.dismiss()
        }
    ) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_main_with_radiobtns)

        val titleTextView = dialog.findViewById<TextView>(R.id.title_main_dialog)
        val radioButton1 =
            dialog.findViewById<RadioButton>(R.id.radio_button1)
        val radioButton2 =
            dialog.findViewById<RadioButton>(R.id.radio_button2)

        titleTextView.text = titleText
        radioButton1.text = firstRadioButtonText
        radioButton2.text = secondRadioButtonText

        dialog.findViewById<Button>(R.id.apply_button_dialog).setOnClickListener {
            positiveButtonAction(dialog)
        }
        dialog.findViewById<Button>(R.id.leave_button_dialog).setOnClickListener {
            negativeButtonAction(dialog)
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    override fun getViewModel(): Class<MainViewModel> = MainViewModel::class.java
    override fun getViewModelStoreOwner(): MainActivity = this
    override fun getContext(): Context = this

    private fun observeUserNetworkConnection() {
        NetworkObserver.getNetworkLiveData(applicationContext)
            .observe(this, Observer { isConnected ->
                if (!isConnected) {
                    binding.textViewNetworkStatus.text = "No internet connection"
                    binding.networkStatusLayout.apply {
                        binding.networkStatusLayout.visibility = VISIBLE
                        setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                android.R.color.holo_red_light
                            )
                        )
                    }
                } else {
                    binding.textViewNetworkStatus.text = "Back Online"

                    viewModel.refreshResponse()

                    binding.networkStatusLayout.apply {
                        animate()
                            .alpha(1f)
                            .setStartDelay(1000)
                            .setDuration(1000)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    binding.networkStatusLayout.visibility = GONE
                                }
                            }).start()
                        setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                android.R.color.holo_green_light
                            )
                        )
                    }
                }
            })
    }

    private fun observeRetrofitErrors() {
        viewModel.errorMessage.observe(this, Observer {
            if (it != null) {
                it.getContentIfNotHandled().let {
                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun observeBannerResponse() {
        viewModel.responseBannerLiveData.observe(this, Observer {
            if (it != null)
                it.body()?.let { articles ->
                    val sharedPrefManager: SharedPrefManager = SharedPrefManager.getInstance(this)
                    sharedPrefManager.setBannerSelectValue()
                    viewModel.saveBannerData(articles)
                }
        })
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            binding.dlRoot.closeDrawers()
            true
        }
    }

    private fun prepareListData() {
        listDataHeader = ArrayList()
        listDataChild = HashMap()

        val items =
            listOf("Home", "Help & Faq's", "Privacy Policy", "Terms & Conditions", "Settings")

        for (itemText in items) {
            val item = ExpandedMenuModel().apply {
                iconName = itemText
                iconImg = R.drawable.ic_home
            }
            listDataHeader?.add(item)
            listDataChild?.put(item, arrayListOf()) // Empty list for child items
        }
    }


    companion object {
        lateinit var toolbar: Toolbar
    }
}

