package com.esoftwere.hfk.ui.product_details

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.esoftwere.hfk.R
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.databinding.ActivityProductDetailsBinding
import com.esoftwere.hfk.databinding.BottomProductRatingViewBinding
import com.esoftwere.hfk.model.product_rating.ProductRatingRequestModel
import com.esoftwere.hfk.model.product_rating.ProductRatingResponseModel
import com.esoftwere.hfk.model.send_push_notification.SendPushNotificationRequestModel
import com.esoftwere.hfk.model.send_push_notification.SendPushNotificationResponseModel
import com.esoftwere.hfk.model.wish_list.AddToWishListRequestModel
import com.esoftwere.hfk.model.wish_list.AddToWishListResponseModel
import com.esoftwere.hfk.model.wish_list.WishListDataModel
import com.esoftwere.hfk.model.wish_list.WishListRemoveRequestModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.ui.add_product.AddProductActivity
import com.esoftwere.hfk.ui.chat.ChatActivity
import com.esoftwere.hfk.ui.dialog.CustomLoaderDialog
import com.esoftwere.hfk.ui.dialog.CustomVideoDialog
import com.esoftwere.hfk.ui.product_details.adapter.ProductDetailsItemBannerAdapter
import com.esoftwere.hfk.ui.seller_profile.SellerProfileActivity
import com.esoftwere.hfk.ui.wish_list.WishListViewModel
import com.esoftwere.hfk.ui.wish_list.WishListViewModelFactory
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.ValidationHelper
import com.esoftwere.hfk.utils.addINRSymbolToPrice
import com.esoftwere.hfk.utils.loadImageFromUrl
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esoftwere.hfk.callbacks.RelatedProductItemClickListener
import com.esoftwere.hfk.model.product_details.*
import com.esoftwere.hfk.model.product_review.ProductReviewRequestModel
import com.esoftwere.hfk.model.product_review.ProductReviewResponseModel
import com.esoftwere.hfk.ui.product_details.adapter.ProductItemReviewAdapter
import com.esoftwere.hfk.ui.product_details.adapter.RelatedProductItemAdapter


class ProductDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var mContext: Context
    private lateinit var mCustomLoaderDialog: CustomLoaderDialog
    private lateinit var mProductDetailsViewModel: ProductDetailsViewModel
    private lateinit var mWishListViewModel: WishListViewModel
    private lateinit var mProductDetailsItemBannerAdapter: ProductDetailsItemBannerAdapter
    private lateinit var mRelatedProductItemAdapter: RelatedProductItemAdapter
    private lateinit var mProductReviewAdapter: ProductItemReviewAdapter

    private val TAG: String = "ProductDetailsAct"

    private var mProductDetailsModel: ProductDetailsModel? = null
    private var mSellerId: String = ""
    private var mIsRating: Int = 0
    private var mProductVideoLink: String = ""
    private var mSellerMobileNo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_details)

        initToolbar()
        initVariable()
        initListeners()
        initViewModel()

        initProductDetailsBannerAdapter()
        initRelatedProductItemAdapter()
        initProductItemReviewAdapter()
        callProductDetailsAPI()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun getProductId(): String {
        return ValidationHelper.optionalBlankText(intent.extras?.getString(AppConstants.INTENT_KEY_PRODUCT_ID))
    }

    private fun getProductType(): String {
        return ValidationHelper.optionalBlankText(intent.extras?.getString(AppConstants.INTENT_KEY_PRODUCT_TYPE))
    }

    private fun initToolbar() {
        binding.toolbarCommon.apply {
            setSupportActionBar(toolbar)
            ivToolbarBack?.setOnClickListener {
                toolbarBackClickHandler()
            }
        }
    }

    private fun initVariable() {
        mContext = this@ProductDetailsActivity
        mCustomLoaderDialog = CustomLoaderDialog(mContext)
    }

    private fun initListeners() {
        binding.ivProductFavourite.setOnClickListener {
            addProductFavouriteClickHandler()
        }
        binding.btnBuyNow.setOnClickListener {
            buyNowClickHandler()
        }
        binding.btnChat.setOnClickListener {
            moveToChatActivity()
        }
        binding.ivProductVideoLink.setOnClickListener {
            productVideoClickHandler()
        }
        binding.tvViewSellerProfile.setOnClickListener {
            viewSellerProfileClickHandler()
        }
        binding.ivSellerPhone.setOnClickListener {
            if (mSellerMobileNo.isNotEmpty()) {
                openDialer(mSellerMobileNo)
            }
        }
        binding.rbProductRating.setOnTouchListener(View.OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                productRatingClickHandler()
            }
            return@OnTouchListener true
        })
    }

    private fun initProductDetailsBannerAdapter() {
        mProductDetailsItemBannerAdapter = ProductDetailsItemBannerAdapter(mContext)

        binding.svInstrumentBanner.apply {
            setSliderAdapter(mProductDetailsItemBannerAdapter)
            setIndicatorAnimation(IndicatorAnimationType.WORM)
            setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
            autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
            startAutoCycle()
        }
    }

    private fun initRelatedProductItemAdapter() {
        mRelatedProductItemAdapter = RelatedProductItemAdapter(mContext, object : RelatedProductItemClickListener {
            override fun onRelatedProductItemClicked(relatedProductItemModel: RelatedProductItemModel) {
                val productId = ValidationHelper.optionalBlankText(relatedProductItemModel.itemId)
                if (productId.isNotEmpty()) {
                    moveToProductDetailsActivity(relatedProductItemModel.itemId)
                }
            }
        })
        val linearLayoutManager =
            LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
        binding.rvRelatedProducts.layoutManager = linearLayoutManager
        binding.rvRelatedProducts.itemAnimator = DefaultItemAnimator()
        binding.rvRelatedProducts.adapter = mRelatedProductItemAdapter
    }

    private fun initProductItemReviewAdapter() {
        mProductReviewAdapter = ProductItemReviewAdapter(mContext)
        val layoutManagerInstrumentReview =
            LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.rvReview.layoutManager = layoutManagerInstrumentReview
        binding.rvReview.itemAnimator = DefaultItemAnimator()
        binding.rvReview.adapter = mProductReviewAdapter
    }

    private fun initViewModel() {
        mProductDetailsViewModel = ViewModelProvider(
            this,
            ProductDetailsViewModelFactory(this.applicationContext as HFKApplication)
        ).get<ProductDetailsViewModel>(ProductDetailsViewModel::class.java)

        mWishListViewModel = ViewModelProvider(
            this,
            WishListViewModelFactory(this.applicationContext as HFKApplication)
        ).get<WishListViewModel>(WishListViewModel::class.java)

        mProductDetailsViewModel.mProductDetailsLiveData?.observe(
            this,
            Observer<ResultWrapper<ProductDetailsResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { productDetailsResponse ->
                            setProductDetailsResponseData(productDetailsResponse)
                            callProductReviewListAPI()
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.llProductDetailsRoot,
                            result.error
                        )
                    }
                }
            })

        mProductDetailsViewModel.mProductReviewLiveData?.observe(
            this,
            Observer<ResultWrapper<ProductReviewResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { productReviewResponse ->
                            setReviewListResponseData(productReviewResponse)
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.llProductDetailsRoot,
                            result.error
                        )
                    }
                }
            })

        mWishListViewModel.mAddWishListLiveData?.observe(
            this,
            Observer<ResultWrapper<AddToWishListResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { addWishListResponse ->
                            AndroidUtility.showSuccessCustomToast(
                                mContext,
                                addWishListResponse.message
                            )

                            binding.ivProductFavourite.setImageResource(R.drawable.ic_heart_selected)
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.llProductDetailsRoot,
                            result.error
                        )
                    }
                }
            })

        mProductDetailsViewModel.mProductRatingLiveData?.observe(
            this,
            Observer<ResultWrapper<ProductRatingResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { productRatingResponse ->
                            AndroidUtility.showSuccessCustomToast(
                                mContext,
                                productRatingResponse.message
                            )

                            callProductDetailsAPI()
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.llProductDetailsRoot,
                            result.error
                        )
                    }
                }
            })

        mProductDetailsViewModel.mSendNotificationLiveData?.observe(
            this,
            Observer<ResultWrapper<SendPushNotificationResponseModel>> { result ->
                when (result) {
                    is ResultWrapper.Progress -> {
                        if (result.isLoading.not()) {
                            hideLoader()
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.data?.let { pushNotificationResponse ->
                            AndroidUtility.showSuccessCustomToast(
                                mContext,
                                pushNotificationResponse.message
                            )
                        }
                    }

                    is ResultWrapper.Failure -> {
                        AndroidUtility.showErrorCustomSnackbar(
                            binding.llProductDetailsRoot,
                            result.error
                        )
                    }
                }
            })
    }

    private fun showLoader() {
        if (this::mCustomLoaderDialog.isInitialized) {
            mCustomLoaderDialog.show()
        }
    }

    private fun hideLoader() {
        if (this::mCustomLoaderDialog.isInitialized) {
            if (mCustomLoaderDialog.isShowing) {
                mCustomLoaderDialog.cancel()
            }
        }
    }

    private fun setProductDetailsResponseData(productDetailsResponse: ProductDetailsResponseModel) {
        mProductDetailsModel = productDetailsResponse.productDetailsModel
        val userDetailsModel: UserDetailsModel = productDetailsResponse.userDetailsModel

        Log.e(TAG, "CurrentTimeInMills: ${System.currentTimeMillis()}")

        mProductDetailsModel?.let { productDetails ->
            val productName = ValidationHelper.optionalBlankText(productDetails.productName)
            val productPrice = ValidationHelper.optionalBlankText(productDetails.productPrice)
            val productPriceUnit = ValidationHelper.optionalBlankText(productDetails.priceUnitId)
            val productQuantity = ValidationHelper.optionalBlankText(productDetails.productQuantity)
            val productQuantityUnitId = ValidationHelper.optionalBlankText(productDetails.productQuantityUnitId)
            val productQuality = ValidationHelper.optionalBlankText(productDetails.productQuality)
            val productDescription =
                ValidationHelper.optionalBlankText(productDetails.productDescription)
            val productRating = ValidationHelper.optionalBlankText(productDetails.rating)
            val productVideoLink = ValidationHelper.optionalBlankText(productDetails.videoLink)
            val productCreatedAt = ValidationHelper.optionalBlankText(productDetails.createdAt)
            val productLocation = ValidationHelper.optionalBlankText(productDetails.productLocation)
            mIsRating = productDetails.isRating

            //Set Banner Data
            productDetails.productImageList?.let { instrumentBannerList ->
                if (instrumentBannerList.isNotEmpty()) {
                    if (this::mProductDetailsItemBannerAdapter.isInitialized) {
                        mProductDetailsItemBannerAdapter.setProductDetailsBannerList(instrumentBannerList)
                    }
                }
            }
            if (productPrice.isNotEmpty()) {
                binding.tvProductPrice.text = productPrice.addINRSymbolToPrice()

                if (productPriceUnit.isNotEmpty()) {
                    binding.tvProductPriceUnit.text = productPriceUnit
                }
            }
            if (productQuantity.isNotEmpty()) {
                binding.grpProductQuantity.visibility = View.VISIBLE
                binding.tvAvailableQuantity.text = productQuantity
                binding.tvProductQuantityUnit.text = productQuantityUnitId
            }
            if (productQuality.isNotEmpty()) {
                binding.grpProductQuality.visibility = View.VISIBLE
                binding.tvProductQuality.text = productQuality
            }
            if (productRating.isNotEmpty()) {
                binding.rbProductRating.rating = productRating.toFloat()
            }
            if (productVideoLink.isNotEmpty()) {
                mProductVideoLink = productVideoLink
                binding.ivProductVideoLink.visibility = View.VISIBLE
            }
            if (getProductType().isNotEmpty() && getProductType().equals(AppConstants.ITEM_TYPE_MACHINERY, true)) {
                binding.grpProductAge.visibility = View.VISIBLE
                binding.tvProductAge.text = "${productDetails.machineryYears} Years ${productDetails.machineryMonths} Months"
                binding.tvProductNoOfOwners.text = "${productDetails.numberOfOwners}"
            }
            binding.tvProductName.text = productName
            binding.tvDescription.text = productDescription
            binding.tvProductUploadedDate.text = productCreatedAt
            binding.tvLocation.text  = productLocation
            binding.tvProductViewCount.text = "${productDetails.productViewCount}"
            binding.tvProductDistance.text = ValidationHelper.optionalBlankText(productDetails.productDistance)
        }
        productDetailsResponse.relatedProductList?.let { relatedItemProductList ->
            if (relatedItemProductList.isNotEmpty()) {
                if (this::mRelatedProductItemAdapter.isInitialized) {
                    binding.grpRelatedProducts.visibility = View.VISIBLE
                    mRelatedProductItemAdapter.setRelatedProductList(relatedItemProductList)
                }
            }
        }
        userDetailsModel?.let { userDetails ->
            /*val sellerImage = ValidationHelper.optionalBlankText(userDetails.userImage)*/
            mSellerId = ValidationHelper.optionalBlankText(userDetails.userId)
            val sellerFName = ValidationHelper.optionalBlankText(userDetails.firstName)
            val sellerLName = ValidationHelper.optionalBlankText(userDetails.lastName)
            val sellerMobileNo = ValidationHelper.optionalBlankText(userDetails.mobile)
            mSellerMobileNo = sellerMobileNo

            binding.tvSellerName.text = "$sellerFName $sellerLName"
            binding.tvSellerMobile.text = sellerMobileNo

            if(mSellerId == AndroidUtility.getUserId()) {
                binding.rbProductRating.apply {
                    isEnabled = false
                    isClickable = false
                    isFocusable = false
                }

                binding.btnBuyNow.visibility = View.GONE
            }
            binding.tvSellerMobile.setOnClickListener {
                if (sellerMobileNo.isNotEmpty()) {
                    //openDialer(sellerMobileNo)
                }
            }
        }
    }

    private fun setReviewListResponseData(productReviewResponseModel: ProductReviewResponseModel) {
        productReviewResponseModel.reviewList?.let { reviewList ->
            if (reviewList.isNotEmpty()) {
                if (this::mProductReviewAdapter.isInitialized) {
                    binding.tvReviewLabel.visibility = View.VISIBLE
                    binding.rvReview.visibility = View.VISIBLE

                    mProductReviewAdapter.setItemReviewList(reviewList)
                }
            }
        }
    }

    private fun showBottomSheetRatingDialog() {
        val mBottomSheetDialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        var bottomProductRatingBinding: BottomProductRatingViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.bottom_product_rating_view,
            null,
            false
        )
        mBottomSheetDialog.setContentView(bottomProductRatingBinding.root)
        mBottomSheetDialog.show()

        bottomProductRatingBinding.btnReviewSubmit.setOnClickListener {
            val productRating: Float = bottomProductRatingBinding.rbItemRating.rating
            val productReview: String =
                ValidationHelper.optionalBlankText(bottomProductRatingBinding.etReview.text.toString())

            when {
                productRating.isNaN().not() and (productRating.equals(0.0f)) -> {
                    AndroidUtility.showErrorCustomToast(
                        mContext,
                        getString(R.string.empty_rating_validation)
                    )
                }
                 productReview.isEmpty() -> {
                     AndroidUtility.showErrorCustomToast(mContext, getString(R.string.empty_review_validation))
                 }
                else -> {
                    mBottomSheetDialog.dismiss()
                    callProductRatingAPI(productRating.toString(), productReview)
                }
            }
        }
    }

    /**
     * Redirection Handler
     */
    private fun openYoutubeInBrowser(youtubeLink: String) {
        val intentBrowser = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink))
        startActivity(intentBrowser)
    }

    private fun openDialer(userMobile: String) {
        val dialerUri = Uri.parse("tel:$userMobile")
        val dialerIntent = Intent(Intent.ACTION_DIAL, dialerUri)

        try {
            startActivity(dialerIntent)
        } catch (se: SecurityException) {
            Log.e(TAG, "OpenDialerEx: ${se.message}")
        }
    }

    private fun moveToChatActivity() {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(AppConstants.INTENT_KEY_CHAT_RECEIVER_ID, mSellerId)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    private fun moveToSellerProfileActivity() {
        val intent = Intent(this, SellerProfileActivity::class.java)
        intent.putExtra(AppConstants.INTENT_KEY_SELLER_PROFILE_ID, mSellerId)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    private fun moveToProductDetailsActivity(productId: String) {
        val intent = Intent(this, ProductDetailsActivity::class.java)
        intent.putExtra(AppConstants.INTENT_KEY_PRODUCT_ID, productId)
        startActivity(intent)
    }

    /**
     * Click Handler
     */
    private fun toolbarBackClickHandler() {
        onBackPressed()
    }

    private fun addProductFavouriteClickHandler() {
        mProductDetailsModel?.let { productDetailsModel ->
            callWishListItemAddAPI(productDetailsModel)
        }
    }

    private fun buyNowClickHandler() {
        callSendPushNotificationAPI()
    }

    private fun viewSellerProfileClickHandler() {
        moveToSellerProfileActivity()
    }

    private fun productRatingClickHandler() {
        if (mIsRating == 0) {
            showBottomSheetRatingDialog()
        } else {
            AndroidUtility.showErrorCustomToast(mContext, "Product Rating Already Given.")
        }
    }

    private fun productVideoClickHandler() {
        val customVideoDialog = CustomVideoDialog(this, mProductVideoLink)
        customVideoDialog.show()
    }

    /**
     * Calling API
     */
    private fun callProductDetailsAPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.llProductDetailsRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mProductDetailsViewModel.callProductDetailsAPI(
            ProductDetailsRequestModel(
                productId = getProductId(),
                productType = getProductType(),
                userStateId = AndroidUtility.getUserStateId(),
                userMstId = AndroidUtility.getUserId()
            )
        )
    }

    private fun callProductReviewListAPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.llProductDetailsRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mProductDetailsViewModel.callProductReviewListAPI(
            ProductReviewRequestModel(
                productId = getProductId()
            )
        )
    }

    private fun callWishListItemAddAPI(productDetailsModel: ProductDetailsModel) {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(binding.llProductDetailsRoot, getString(R.string.please_check_internet))
            return
        }

        showLoader()
        mWishListViewModel.callAddWishListAPI(
            AddToWishListRequestModel(
                productMstId = productDetailsModel.productId,
                productQuantity = productDetailsModel.productQuantity,
                productPrice = productDetailsModel.productPrice,
                userMstId = AndroidUtility.getUserId()
            )
        )
    }

    private fun callProductRatingAPI(productRating: String, comments: String) {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.llProductDetailsRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mProductDetailsViewModel.callProductRatingAPI(
            ProductRatingRequestModel(
                productMstId = getProductId(),
                userMstId = AndroidUtility.getUserId(),
                productRating = productRating,
                productComments = comments
            )
        )
    }

    private fun callSendPushNotificationAPI() {
        if (AndroidUtility.isNetworkAvailable(mContext).not()) {
            AndroidUtility.showErrorCustomSnackbar(
                binding.llProductDetailsRoot,
                getString(R.string.please_check_internet)
            )
            return
        }

        showLoader()
        mProductDetailsViewModel.callSendPushNotificationAPI(
            SendPushNotificationRequestModel(
                productId = getProductId(),
                loginId = AndroidUtility.getUserId()
            )
        )
    }
}