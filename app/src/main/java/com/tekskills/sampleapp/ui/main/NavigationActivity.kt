package com.tekskills.sampleapp.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.GestureDetector
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.databinding.ActivityMainBinding
import com.tekskills.sampleapp.databinding.ActivityNavigationBinding
import com.tekskills.sampleapp.model.ExpandedMenuModel
import com.tekskills.sampleapp.ui.adapter.ExpandableListAdapter
import com.tekskills.sampleapp.ui.adapter.ViewPagerAdapter
import com.tekskills.sampleapp.ui.articledetails.ArticlesFragment
import com.tekskills.sampleapp.ui.base.BaseActivity
import com.tekskills.sampleapp.ui.splash.SplashActivity
import com.tekskills.sampleapp.utils.NetworkObserver


class NavigationActivity : BaseActivity<ActivityNavigationBinding, MainViewModel, NavigationActivity>() {

    private var mMenuAdapter: ExpandableListAdapter? = null
    private var listDataHeader: ArrayList<ExpandedMenuModel>? = null
    private var listDataChild: HashMap<ExpandedMenuModel, List<String>>? = null

    var gestureDetector: GestureDetector? = null

    var handler: Handler? = null
    var runnable: Runnable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** Binding view into activity */
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java].apply {}

        val view = binding.root
        setContentView(view)

        // remove actionbar
        supportActionBar?.hide()
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        setSupportActionBar(binding.includeHome.toolBar)

        binding.includeNavHeader?.ivNavigationCancel?.setOnClickListener {
            binding.drawerLayout.closeDrawers()
        }

        handler = Handler()
        runnable = Runnable {
            Log.d("Event", "inactive mins ${binding.includeHome.toolBar.isVisible}")
//            appBarLayoutHandle(true)
//            binding.motionBase.visibility = if (binding.motionBase.isVisible) GONE else VISIBLE
        }
        startHandler()

        defineLayout()
        observeUserNetworkConnection()

        observeRetrofitErrors()

        /**
         * handling drawer layout
         */
        binding.includeHome.ivDrawer.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                binding.drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.END)
            }
        }

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

            binding.drawerLayout.closeDrawers()
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
                            binding.drawerLayout.closeDrawers()
                        }
                    }

                    else -> binding.elNavigationMenu.getExpandableListPosition(grpPos)
            }
            false
        }
    }

    private fun observeUserNetworkConnection() {
        NetworkObserver.getNetworkLiveData(applicationContext)
            .observe(this, Observer { isConnected ->
                if (!isConnected) {
                    binding.includeHome.textViewNetworkStatus.text = "No internet connection"
                    binding.includeHome.networkStatusLayout.apply {
                        binding.includeHome.networkStatusLayout.visibility = VISIBLE
                        setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                android.R.color.holo_red_light
                            )
                        )
                    }
                } else {
                    binding.includeHome.textViewNetworkStatus.text = "Back Online"

                    viewModel.refreshResponse()

                    binding.includeHome.networkStatusLayout.apply {
                        animate()
                            .alpha(1f)
                            .setStartDelay(1000)
                            .setDuration(1000)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    binding.includeHome.networkStatusLayout.visibility = GONE
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

    override fun onUserInteraction() {
        super.onUserInteraction()
        stopHandler()
        startHandler()
    }

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            viewModel.category.value = getTitle(position)
            viewModel.refreshResponse()
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            binding.includeHome.toolBar.visibility = if (binding.includeHome.toolBar.isVisible) GONE else VISIBLE
        }
    }

    private fun defineLayout() {

        binding.includeHome.pager.adapter = createPagerAdapter()

        binding.includeHome.pager.registerOnPageChangeCallback(onPageChangeCallback)
        binding.includeHome.pager.isUserInputEnabled = false

        setUpTabLayout()


        binding.includeHome.ivLogo.setOnClickListener {
            val goToMainActivity = Intent(applicationContext, MainActivity::class.java)
            val activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                this,
                binding.includeHome.ivLogo,
                "app_logo"
            )
            startActivity(goToMainActivity, activityOptions.toBundle())
        }
    }

    private fun setUpTabLayout() {
        val tabTitles = resources.getStringArray(R.array.intro_title_list)

        TabLayoutMediator(binding.includeHome.tabLayout, binding.includeHome.pager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.text = tabTitles[position]
            }).attach()

        binding.includeHome.tabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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



    private fun startHandler() {
        handler?.postDelayed(runnable!!, 500)
    }

    private fun stopHandler() {
        handler?.removeCallbacks(runnable!!)
    }


    override fun getViewBinding(): ActivityNavigationBinding = ActivityNavigationBinding.inflate(layoutInflater)
    override fun getViewModel(): Class<MainViewModel> = MainViewModel::class.java
    override fun getViewModelStoreOwner(): NavigationActivity = this
    override fun getContext(): Context = this


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

    private fun startSplashActivity() {
        val intent = Intent(this@NavigationActivity, SplashActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            binding.drawerLayout.closeDrawers()
            true
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this@NavigationActivity, NavigationActivity::class.java)
        startActivity(intent)
        finish()
    }
}
