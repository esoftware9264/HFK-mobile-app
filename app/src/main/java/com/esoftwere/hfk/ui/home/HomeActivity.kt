package com.esoftwere.hfk.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.esoftwere.hfk.R
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivityHomeBinding
import com.esoftwere.hfk.ui.add_product.AddProductActivity
import com.esoftwere.hfk.ui.base.BaseActivity
import com.esoftwere.hfk.ui.login.LoginActivity
import com.esoftwere.hfk.ui.notification.NotificationListActivity
import com.esoftwere.hfk.ui.wish_list.WishListActivity

import android.net.Uri
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.model.location_filter.LocationFilterModel
import com.esoftwere.hfk.model.login.UserDataModel
import com.esoftwere.hfk.socket.SocketConstants
import com.esoftwere.hfk.socket.SocketLibrary
import com.esoftwere.hfk.socket.SocketParser
import com.esoftwere.hfk.socket.SocketUtility
import com.esoftwere.hfk.ui.add_machinery.AddMachineryActivity
import com.esoftwere.hfk.ui.chat_user_list.ChatUserListActivity
import com.esoftwere.hfk.ui.location_filter.LocationFilterActivity
import com.esoftwere.hfk.ui.market_view.MarketViewActivity
import com.esoftwere.hfk.ui.my_profile.MyProfileActivity
import com.esoftwere.hfk.ui.notification_preference.NotificationPreferenceActivity
import com.esoftwere.hfk.ui.product_list_by_user.ProductListByUserActivity
import com.esoftwere.hfk.ui.search.ProductSearchActivity
import com.esoftwere.hfk.ui.web_view.WebViewActivity
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.loadImageFromUrl
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.nav_header.view.*

class HomeActivity : BaseActivity() {
    private lateinit var binding: ActivityHomeBinding

    private val TAG: String = "HomeScreen"

    private var navHeaderView: View? = null
    private var navHeaderUserName: AppCompatTextView? = null
    private var navHeaderUserProfiler: CircleImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        initUIElements()
        initToolbar()
        initListeners()
        initializeSocketVariable()
        setUpDrawerHeaderUserProfile()

        if (savedInstanceState == null) {
            startFragment(R.id.fl_homeContainer, HomeFragment.newInstance(null), true)
        }
    }

    override fun onBackPressed() {
        if (binding.dlHomeRoot.isDrawerOpen(GravityCompat.START)) {
            binding.dlHomeRoot.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.REQUEST_CODE_LOCATION_FILTER) {
            if (resultCode == RESULT_OK) {
                val locationFilterModel: LocationFilterModel? = data?.getParcelableExtra<LocationFilterModel>(AppConstants.INTENT_KEY_LOCATION_FILTER_DATA)
                Log.e(TAG, "=========HomeActivity==========")
                AndroidUtility.printModelDataWithGSON(TAG, locationFilterModel)
                locationFilterModel?.let { locationModel ->
                    val districtName: String = ValidationHelper.optionalBlankText(locationModel.districtName)
                    val stateName: String = ValidationHelper.optionalBlankText(locationModel.stateName)

                    binding.toolbarHome.llHeaderLocation.findViewById<AppCompatTextView>(R.id.tv_location).text =
                        (if(stateName.isNotEmpty()) {
                            if (districtName.isNotEmpty()) {
                                "${locationModel.districtName}, ${locationModel.stateName}"
                            } else {
                                "${locationModel.stateName}"
                            }
                        } else {
                            "Uluberia, WB"
                        }).toString()
                    startFragment(R.id.fl_homeContainer, HomeFragment.newInstance(locationModel), true)
                }
            }
        }
    }

    private fun initUIElements() {
        navHeaderView = binding.navView?.getHeaderView(0)
        navHeaderView?.apply {
            navHeaderUserName = findViewById(R.id.tv_userName)
            navHeaderUserProfiler = findViewById(R.id.iv_userProfile)

            navHeaderUserProfiler?.setOnClickListener {
                moveToMyProfileActivity()
                binding.dlHomeRoot.closeDrawer(GravityCompat.START)
            }
        }
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbarHome.toolbar)
        binding.toolbarHome.toolbar.setNavigationIcon(R.drawable.ic_ham_burger_menu)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
        }
    }

    private fun initListeners() {
        binding.toolbarHome.toolbar.setNavigationOnClickListener {
            binding.dlHomeRoot.openDrawer(GravityCompat.START)
        }

        binding.toolbarHome.tvSearchHome.setOnClickListener {
            moveToProductSearchActivity()
        }

        binding.toolbarHome.llHeaderLocation.setOnClickListener {
            moveToLocationFilterActivity()
        }

        binding.navView.setNavigationItemSelectedListener { drawerItem ->
            when (drawerItem.itemId) {
                R.id.navigation_item_my_product_list -> {
                    moveToProductListByUserActivity()
                }
                R.id.navigation_item_market_view -> {
                    moveToMarketViewActivity()
                }
                R.id.navigation_item_wishlist -> {
                    moveToWishListActivity()
                }
                R.id.navigation_item_notification -> {
                    moveToNotificationListActivity()
                }
                R.id.navigation_item_notification_preference -> {
                    moveToNotificationPreferenceActivity()
                }
                R.id.navigation_item_tc-> {
                    moveToWebViewActivity(getString(R.string.terms_amp_conditions), AppConstants.TERMS_CONDITION_URL)
                }
                R.id.navigation_item_privacy_policy -> {
                    moveToWebViewActivity(getString(R.string.privacy_policy), AppConstants.PRIVACY_POLICY_URL)
                }
                R.id.navigation_item_user_policy -> {
                    moveToWebViewActivity(getString(R.string.user_policy), AppConstants.USER_POLICY_URL)
                }
                R.id.navigation_item_share_apk -> {
                    moveToPlayStoreIntent()
                }
                R.id.navigation_item_logout -> {
                    clearPrefsData()
                    moveToLoginActivity()
                }
            }

            binding.dlHomeRoot.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true
        }


        binding.bnvHome.setOnNavigationItemSelectedListener { bottomMenuItem ->
            when (bottomMenuItem.itemId) {
                R.id.bottom_nav_tractor -> {
                    moveToAddMachineryActivity()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_seller -> {
                    moveToAddProductActivity()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_my_product -> {
                    moveToProductListByUserActivity()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_market_view -> {
                    moveToMarketViewActivity()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_chat -> {
                    moveToChatUserListActivity()
                    return@setOnNavigationItemSelectedListener true
                }
            }

            false
        }
    }

    private fun setUpDrawerHeaderUserProfile() {
        val userDataModel: UserDataModel? =
            HFKApplication.applicationInstance.tinyDB.getCustomDataObjects<UserDataModel>(
                AppConstants.KEY_PREFS_USER_DETAILS
            )
        userDataModel?.let { userDetails ->
            Log.e("HomeAct", "FirstName: ${userDetails.firstName}")
            val userFirstName: String = ValidationHelper.optionalBlankText(userDetails.firstName)
            val userLastName: String = ValidationHelper.optionalBlankText(userDetails.lastName)
            val userProfileImage: String = ValidationHelper.optionalBlankText(AndroidUtility.getUserImage())
            navHeaderUserName?.text = "$userFirstName $userLastName"
            if (userProfileImage.isNotEmpty()) {
                navHeaderUserProfiler?.loadImageFromUrl(userProfileImage, R.drawable.ic_profile)
            }

            val districtName: String = ValidationHelper.optionalBlankText(userDetails.districtName)
            val stateName: String = ValidationHelper.optionalBlankText(userDetails.stateName)
            binding.toolbarHome.llHeaderLocation.findViewById<AppCompatTextView>(R.id.tv_location).text =
                (if(stateName.isNotEmpty()) {
                    if (districtName.isNotEmpty()) {
                        "$districtName, $stateName"
                    } else {
                        "$stateName"
                    }
                } else {
                    "Uluberia, WB"
                }).toString()
        }
    }

    private fun initializeSocketVariable() {
        try {
            /*SocketCommonData.mSocket = null;*/
            SocketConstants.mSocketUtility = SocketUtility()
            SocketConstants.mSocketParser = SocketParser()
            SocketConstants.mSocketLibrary = SocketLibrary()
            SocketConstants.mSocketUtility?.socketUrl = SocketConstants.SOCKET_URL
            SocketConstants.mSocketUtility?.socketPort = SocketConstants.SOCKET_PORT
        } catch (e: Exception) {
            Log.e("CreateSocketException", "ExceptionCause: " + e.message)
        }
    }

    private fun clearPrefsData() {
        HFKApplication.applicationInstance.tinyDB.clear()
    }

    private fun openYoutubeChannel() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(AppConstants.YOUTUBE_CHANNEL)
        //intent.setPackage("com.google.android.youtube")
        val manager = packageManager
        val info = manager.queryIntentActivities(intent, 0)
        if (info.size > 0) {
            startActivity(intent)
        } else {
            //No Application can handle your intent
        }
    }

    /**
     * Redirection Handler
     */
    private fun moveToPlayStoreIntent() {
        val hfkPlayStoreIntent: String = "https://play.google.com/store/apps/details?id=com.esoftwere.hfk"
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(
            Intent.EXTRA_TEXT,
            hfkPlayStoreIntent
        )
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share)))
    }

    private fun moveToProductSearchActivity() {
        val intent = Intent(this, ProductSearchActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun moveToLocationFilterActivity() {
        val intent = Intent(this, LocationFilterActivity::class.java)
        startActivityForResult(intent, AppConstants.REQUEST_CODE_LOCATION_FILTER)
        overridePendingTransition(R.anim.animation_appears_from_bottom, R.anim.animation_disappears_to_bottom)
    }

    private fun moveToProductListByUserActivity() {
        val intent = Intent(this, ProductListByUserActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    private fun moveToMarketViewActivity() {
        val intent = Intent(this, MarketViewActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    private fun moveToWishListActivity() {
        val intent = Intent(this, WishListActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    private fun moveToNotificationListActivity() {
        val intent = Intent(this, NotificationListActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    private fun moveToNotificationPreferenceActivity() {
        val intent = Intent(this, NotificationPreferenceActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    private fun moveToAddMachineryActivity() {
        val intent = Intent(this, AddMachineryActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    private fun moveToAddProductActivity() {
        val intent = Intent(this, AddProductActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    private fun moveToMyProfileActivity() {
        val intent = Intent(this, MyProfileActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    private fun moveToChatUserListActivity() {
        val intent = Intent(this, ChatUserListActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    private fun moveToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finishAffinity()
    }

    private fun moveToWebViewActivity(toolbarTitle: String, webUrl: String) {
        val webIntent = Intent(this, WebViewActivity::class.java)
        webIntent.putExtra(AppConstants.INTENT_KEY_WEB_TOOLBAR_TITLE, toolbarTitle)
        webIntent.putExtra(AppConstants.INTENT_KEY_WEB_URL, webUrl)
        startActivity(webIntent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}