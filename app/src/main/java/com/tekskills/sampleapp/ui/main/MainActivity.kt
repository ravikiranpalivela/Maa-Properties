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
import androidx.core.view.MotionEventCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.iconDrawable
import com.mikepenz.materialdrawer.model.interfaces.nameRes
import com.mikepenz.materialdrawer.model.interfaces.nameText
import com.mikepenz.materialdrawer.util.addStickyDrawerItems
import com.mikepenz.materialdrawer.util.removeAllItems
import com.mikepenz.materialdrawer.widget.MaterialDrawerSliderView
import com.tekskills.sampleapp.R
import com.tekskills.sampleapp.databinding.ActivityMainBinding
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

    override fun onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (binding.dlRoot.isDrawerOpen(binding.slider)) {
            binding.dlRoot.closeDrawer(binding.slider)
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

        binding.slider.apply {
            setDrawerItems()
            setSelection(1, false)
            setSavedInstance(savedInstanceState)
        }

        binding.slider.onDrawerItemClickListener = { v, drawerItem, position ->
            var intent: Intent? = null
            when (drawerItem.identifier) {
                100L -> intent =
                    Intent(applicationContext, MainActivity::class.java)

                200L -> intent =
                    Intent(applicationContext, MainActivity::class.java)

                325L -> intent =
                    Intent(applicationContext, SplashActivity::class.java)

                350L -> intent =
                    Intent(applicationContext, MainActivity::class.java)

                475L -> showDialogWithRadioButtons(
                    "Choose Language", "Telugu", "English", positiveButtonAction = { dialog ->
                        dialog.dismiss()
                        val radioGroup =
                            dialog.findViewById<RadioGroup>(R.id.radiogroup_dialog_main)
                        val id = radioGroup.checkedRadioButtonId
                        viewModel.changeLanguage(id)
                    }
                )

                425L -> showDialogWithRadioButtons(
                    "Choose view type", "List", "Tab", positiveButtonAction = { dialog ->
                        dialog.dismiss()
                        val radioGroup =
                            dialog.findViewById<RadioGroup>(R.id.radiogroup_dialog_main)
                        val id = radioGroup.checkedRadioButtonId
                        viewModel.changeViewType(id)
                    }
                )

                450L -> showDialogWithRadioButtons(
                    "Choose your theme", "Light", "Dark", positiveButtonAction = { dialog ->
                        dialog.dismiss()
                        val radioGroup =
                            dialog.findViewById<RadioGroup>(R.id.radiogroup_dialog_main)

                        val id = radioGroup.checkedRadioButtonId
                        when (id) {
                            R.id.radio_button1 -> {
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                                lifecycleScope.launch {
                                    prefrences.saveUserTheme("Light")
                                }
                            }

                            R.id.radio_button2 -> {
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                                lifecycleScope.launch {
                                    prefrences.saveUserTheme("Dark")
                                }
                            }
                        }
                    }
                )
            }

            if (intent != null) {
                this.startActivity(intent)
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
            if (binding.dlRoot.isDrawerOpen(binding.slider))
                binding.dlRoot.closeDrawer(binding.slider)
            else
                binding.dlRoot.openDrawer(binding.slider)
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

    private fun MaterialDrawerSliderView.setDrawerItems() {
        removeAllItems()
        itemAdapter.add(
            PrimaryDrawerItem().apply {
                iconDrawable = getDrawable(R.drawable.ic_home)
                nameRes = R.string.drawer_item_home
                identifier = 100
            },
            PrimaryDrawerItem().apply {
                iconDrawable = getDrawable(R.drawable.ic_home)
                nameRes = R.string.drawer_item_about_us
            },
            PrimaryDrawerItem().apply {
                iconDrawable = getDrawable(R.drawable.ic_bookmark)
                nameRes = R.string.drawer_item_contact_us
                identifier = 200
            },
//            PrimaryDrawerItem().apply {
//                nameRes = R.string.drawer_item_language; iconicsIcon =
//                FontAwesome.Icon.faw_blog; identifier = 200
//            },
//            ExpandableBadgeDrawerItem().apply {
//                nameText = "Setting"; iconicsIcon =
//                GoogleMaterial.Icon.gmd_format_bold; identifier = 300; isSelectable = false; badge =
//                StringHolder("100")
//                badgeStyle = BadgeStyle().apply {
//                    textColor = ColorHolder.fromColor(Color.WHITE); color =
//                    ColorHolder.fromColorRes(R.color.colorAccent)
//                }
//                subItems = mutableListOf(
//                    SecondaryDrawerItem().apply {
//                        nameText = "CollapsableItem"; level = 2; iconicsIcon =
//                        GoogleMaterial.Icon.gmd_format_bold; identifier = 325
//                    },
//                    SecondaryDrawerItem().apply {
//                        nameText = "CollapsableItem 2"; level = 2; iconicsIcon =
//                        GoogleMaterial.Icon.gmd_format_bold; identifier = 350
//                    }
//                )
//            },
            ExpandableDrawerItem().apply {
                nameText = "Settings"
//                iconicsIcon = FontAwesome.Icon.faw_cog;
                identifier = 400; isSelectable = false
                subItems = mutableListOf(
//                    SecondaryDrawerItem().apply {
//                        nameText = "Change View Type"; level = 2;
////                        iconicsIcon = FontAwesome.Icon.faw_list;
//                        identifier = 425
//                    },
                    SecondaryDrawerItem().apply {
                        nameText = "Change Theme"; level = 2
//                        iconicsIcon = FontAwesome.Icon.faw_blog;
                        identifier = 450
                    },
                    SecondaryDrawerItem().apply {
                        nameText = "Change Language"; level = 2
//                        iconicsIcon = FontAwesome.Icon.faw_language;
                        identifier = 475
                    }
                )
            },
//            SectionDrawerItem().apply { nameRes = R.string.drawer_item_section_header },
//            SecondaryDrawerItem().apply { nameRes = R.string.drawer_item_settings; iconicsIcon = FontAwesome.Icon.faw_cog },
//            SecondaryDrawerItem().apply { nameRes = R.string.drawer_item_help; iconicsIcon = FontAwesome.Icon.faw_question; isEnabled = false },
//            SecondaryDrawerItem().apply { nameRes = R.string.drawer_item_open_source; iconicsIcon = FontAwesomeBrand.Icon.fab_github },
//            SecondaryDrawerItem().apply { nameRes = R.string.drawer_item_contact; iconicsIcon = FontAwesome.Icon.faw_bullhorn }
        )

        addStickyDrawerItems(
            SecondaryDrawerItem().apply {
                nameRes = R.string.drawer_item_language
                identifier = 11
            },
            SecondaryDrawerItem().apply {
                nameRes = R.string.drawer_item_settings
                identifier = 12
            }
        )
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
                    viewModel.addBanners(articles)
                }
        })
    }

    companion object {
        lateinit var toolbar: Toolbar
    }
}

