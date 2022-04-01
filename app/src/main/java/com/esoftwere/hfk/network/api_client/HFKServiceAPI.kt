package com.esoftwere.hfk.network.api_client

import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.model.add_machinery.AddMachineryRequestModel
import com.esoftwere.hfk.model.add_product.*
import com.esoftwere.hfk.model.block.BlockListRequestModel
import com.esoftwere.hfk.model.block.BlockListResponseModel
import com.esoftwere.hfk.model.category.CategoryListByMainCatIdRequestModel
import com.esoftwere.hfk.model.category_unit.CategoryUnitMapRequestModel
import com.esoftwere.hfk.model.category_unit.CategoryUnitMapResponseModel
import com.esoftwere.hfk.model.chat.ChatRequestModel
import com.esoftwere.hfk.model.chat.ChatResponseModel
import com.esoftwere.hfk.model.country.CountryListResponseModel
import com.esoftwere.hfk.model.district.DistrictListRequestModel
import com.esoftwere.hfk.model.district.DistrictListResponseModel
import com.esoftwere.hfk.model.fcm_token.UpdateFCMTokenRequestModel
import com.esoftwere.hfk.model.fcm_token.UpdateFCMTokenResponseModel
import com.esoftwere.hfk.model.file_upload.FileUploadResponseModel
import com.esoftwere.hfk.model.file_upload.VideoUploadResponseModel
import com.esoftwere.hfk.model.home.DashboardResponseModel
import com.esoftwere.hfk.model.login.LoginRequestModel
import com.esoftwere.hfk.model.login.LoginResponseModel
import com.esoftwere.hfk.model.main_category.MainCategoryListResponseModel
import com.esoftwere.hfk.model.market_view.MarketViewRequestModel
import com.esoftwere.hfk.model.market_view.MarketViewResponseModel
import com.esoftwere.hfk.model.notification.NotificationListRequestModel
import com.esoftwere.hfk.model.notification.NotificationListResponseModel
import com.esoftwere.hfk.model.product_details.ProductDetailsRequestModel
import com.esoftwere.hfk.model.product_details.ProductDetailsResponseModel
import com.esoftwere.hfk.model.product_list_by_cat.ProductListByCatRequestModel
import com.esoftwere.hfk.model.product_list_by_cat.ProductListByCatResponseModel
import com.esoftwere.hfk.model.product_list_by_user.ProductListByUserRequestModel
import com.esoftwere.hfk.model.product_list_by_user.ProductListByUserResponseModel
import com.esoftwere.hfk.model.product_rating.ProductRatingRequestModel
import com.esoftwere.hfk.model.product_rating.ProductRatingResponseModel
import com.esoftwere.hfk.model.product_review.ProductReviewRequestModel
import com.esoftwere.hfk.model.product_review.ProductReviewResponseModel
import com.esoftwere.hfk.model.profile_update.ProfilePicUpdateRequestModel
import com.esoftwere.hfk.model.profile_update.ProfilePicUpdateResponseModel
import com.esoftwere.hfk.model.register.RegisterRequestModel
import com.esoftwere.hfk.model.register.RegisterResponseModel
import com.esoftwere.hfk.model.search.SearchRequestModel
import com.esoftwere.hfk.model.search.SearchResponseModel
import com.esoftwere.hfk.model.seller_profile.SellerProfileRequestModel
import com.esoftwere.hfk.model.seller_profile.SellerProfileResponseModel
import com.esoftwere.hfk.model.send_push_notification.SendPushNotificationRequestModel
import com.esoftwere.hfk.model.send_push_notification.SendPushNotificationResponseModel
import com.esoftwere.hfk.model.state.StateListResponseModel
import com.esoftwere.hfk.model.verify_otp.VerifyOtpRequestModel
import com.esoftwere.hfk.model.verify_otp.VerifyOtpResponseModel
import com.esoftwere.hfk.model.wish_list.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
<<<<<<< HEAD
import retrofit2.Response
=======
>>>>>>> 74e78f0 (Initial Commit)
import retrofit2.http.*

interface HFKServiceAPI {

    /**
     * State List API
     */
    @GET(AppConstants.REQUEST_TYPE_COUNTRY_LIST)
    fun countryListAPI(): Call<CountryListResponseModel>

    /**
     * State List API
     */
    @GET(AppConstants.REQUEST_TYPE_STATE_LIST)
    fun stateListAPI(@Query("country_id") countryId: String = "1"): Call<StateListResponseModel>

    /**
     * District List By State Id API
     */
    @Headers(AppConstants.HEADER_CONTENT_TYPE_JSON)
    @POST(AppConstants.REQUEST_TYPE_DISTRICT_LIST_BY_STATE)
    fun districtListAPI(@Body districtListRequestModel: DistrictListRequestModel): Call<DistrictListResponseModel>

    /**
     * Block List By District Id API
     */
    @Headers(AppConstants.HEADER_CONTENT_TYPE_JSON)
    @POST(AppConstants.REQUEST_TYPE_BLOCK_LIST_BY_DISTRICT)
    fun blockListAPI(@Body blockListRequestModel: BlockListRequestModel): Call<BlockListResponseModel>

    /**
     * Register API
     */
    @POST(AppConstants.REQUEST_TYPE_REGISTER)
    fun registerAPI(@Body registerRequestModel: RegisterRequestModel): Call<RegisterResponseModel>

    /**
     * Verify OTP API
     */
    @POST(AppConstants.REQUEST_TYPE_VERIFY_OTP)
    fun verifyOtpAPI(@Body verifyOtpRequestModel: VerifyOtpRequestModel): Call<VerifyOtpResponseModel>

    /**
     * Login API
     */
    @POST(AppConstants.REQUEST_TYPE_LOGIN)
    fun loginAPI(@Body loginRequestModel: LoginRequestModel): Call<LoginResponseModel>

    /**
<<<<<<< HEAD
     * Login API
     */
    @POST(AppConstants.REQUEST_TYPE_LOGIN)
    suspend fun loginAPIFlow(@Body loginRequestModel: LoginRequestModel): Response<LoginResponseModel>

    /**
=======
>>>>>>> 74e78f0 (Initial Commit)
     * Dashboard API
     */
    @GET(AppConstants.REQUEST_TYPE_DASHBOARD)
    fun dashboardAPI(
        @Query("logged_id") loginId: String,
        @Query("state_id") stateId: String,
        @Query("district_id") districtId: String,
        @Query("block_id") blockId: String
    ): Call<DashboardResponseModel>

    /**
     * Category List API
     */
    @GET(AppConstants.REQUEST_TYPE_MAIN_CATEGORY_LIST)
    fun mainCategoryListAPI(@Query("is_machinery") isMachinery: Int = 0): Call<MainCategoryListResponseModel>

    /**
     * Category List API
     */
    @GET(AppConstants.REQUEST_TYPE_CATEGORY_LIST)
    fun categoryListAPI(@Query("main_category_id") mainCatId: String = "", @Query("is_machinery") isMachinery: Int = 0): Call<CategoryListResponseModel>

    /**
     * Category List By Main Category ID API
     */
    @POST(AppConstants.REQUEST_TYPE_CATEGORY_LIST_BY_MAIN_CAT)
    fun categoryListByMainCatAPI(@Body categoryListByMainCatIdRequestModel: CategoryListByMainCatIdRequestModel): Call<CategoryListResponseModel>

    /**
     * Add Product API
     */
    @POST(AppConstants.REQUEST_TYPE_ADD_PRODUCT)
    fun addProductAPI(@Body addProductRequestModel: AddProductRequestModel): Call<AddProductResponseModel>

    /**
     * Add Machinery API
     */
    @POST(AppConstants.REQUEST_TYPE_ADD_MACHINERY)
    fun addMachineryAPI(@Body addMachineryRequestModel: AddMachineryRequestModel): Call<AddProductResponseModel>

    /**
     * Image Upload API
     */
    @Multipart
    @POST(AppConstants.REQUEST_TYPE_UPLOAD_FILE)
    fun fileUploadAPI(
        @Part file: MultipartBody.Part,
        @Part("file_type") fileType: RequestBody,
        @Part("user_id") userId: RequestBody
    ): Call<FileUploadResponseModel>

    /**
     * Video Upload API
     */
    @Multipart
    @POST(AppConstants.REQUEST_TYPE_UPLOAD_VIDEO_FILE)
    fun fileUploadVideoAPI(
        @Part file: MultipartBody.Part,
        @Part("file_type") fileType: RequestBody,
        @Part("user_id") userId: RequestBody
    ): Call<VideoUploadResponseModel>

    /**
     * Category Unit API
     */
    @POST(AppConstants.REQUEST_TYPE_CATEGORY_UNIT)
    fun categoryUnitAPI(@Body categoryUnitRequestModel: CategoryUnitRequestModel): Call<CategoryUnitResponseModel>

    /**
     * Category Unit Map API
     */
    @POST(AppConstants.REQUEST_TYPE_CATEGORY_UNIT_MAP)
    fun categoryUnitMapAPI(@Body categoryUnitMapRequestModel: CategoryUnitMapRequestModel): Call<CategoryUnitMapResponseModel>

    /**
     * Product Details By Id API
     */
    @POST(AppConstants.REQUEST_TYPE_PRODUCT_DETAILS)
    fun productDetailsAPI(@Body productDetailsRequestModel: ProductDetailsRequestModel): Call<ProductDetailsResponseModel>

    /**
     * Product List By Category By Id API
     */
    @POST(AppConstants.REQUEST_TYPE_PRODUCT_LIST_BY_CAT)
    fun productListByCatAPI(@Body productListByCatRequestModel: ProductListByCatRequestModel): Call<ProductListByCatResponseModel>

    /**
     * Product Rating By Product Id API
     */
    @POST(AppConstants.REQUEST_TYPE_PRODUCT_RATING_API)
    fun productRatingAPI(@Body productRatingRequestModel: ProductRatingRequestModel): Call<ProductRatingResponseModel>

    /**
     * Product Review List API
     */
    @POST(AppConstants.REQUEST_TYPE_PRODUCT_REVIEW_LIST)
    fun productReviewListAPI(@Body productReviewRequestModel: ProductReviewRequestModel): Call<ProductReviewResponseModel>

    /**
     * Send Chat Notification API
     */
    @POST(AppConstants.REQUEST_TYPE_SEND_CHAT_NOTIFICATION)
    fun sendChatNotificationAPI(@Body chatRequestModel: ChatRequestModel): Call<ChatResponseModel>

    /**
     * Update FCM Token API
     */
    @POST(AppConstants.REQUEST_TYPE_UPDATE_FCM_TOKEN)
    fun updateFCMTokenAPI(@Body updateFCMTokenRequestModel: UpdateFCMTokenRequestModel): Call<UpdateFCMTokenResponseModel>

    /**
     * Profile Picture Update API
     */
    @POST(AppConstants.REQUEST_TYPE_PROFILE_PIC_UPDATE)
    fun updateProfilePicAPI(@Body profilePicUpdateRequestModel: ProfilePicUpdateRequestModel): Call<ProfilePicUpdateResponseModel>

    /**
     * Seller Profile Details API
     */
    @POST(AppConstants.REQUEST_TYPE_SELLER_PROFILE_DETAILS)
    fun sellerProfileDetailsAPI(@Body sellerProfileRequestModel: SellerProfileRequestModel): Call<SellerProfileResponseModel>

    /**
     * Add To Wish List API
     */
    @POST(AppConstants.REQUEST_TYPE_ADD_TO_WISH_LIST)
    fun addToWishListAPI(@Body addToWishListRequestModel: AddToWishListRequestModel): Call<AddToWishListResponseModel>

    /**
     * Get Wish List Listing API
     */
    @POST(AppConstants.REQUEST_TYPE_WISH_LIST_LISTING)
    fun getWishListListingAPI(@Body wishListListingRequestModel: WishListListingRequestModel): Call<WishListListingResponseModel>

    /**
     * Remove Wish List API
     */
    @POST(AppConstants.REQUEST_TYPE_WISH_LIST_REMOVE)
    fun removeWishListAPI(@Body wishListRemoveRequestModel: WishListRemoveRequestModel): Call<WishListRemoveResponseModel>

    /**
     * Notification List API
     */
    @POST(AppConstants.REQUEST_TYPE_NOTIFICATION_LIST)
    fun notificationListAPI(@Body notificationListRequestModel: NotificationListRequestModel): Call<NotificationListResponseModel>

    /**
     * Search List API
     */
    @POST(AppConstants.REQUEST_TYPE_PRODUCT_SEARCH)
    fun searchListAPI(@Body searchRequestModel: SearchRequestModel): Call<SearchResponseModel>

    /**
     * Get Wish List Listing API
     */
    @POST(AppConstants.REQUEST_TYPE_PRODUCT_LIST_BY_USER)
    fun productListByUserAPI(@Body productListByUserRequestModel: ProductListByUserRequestModel): Call<ProductListByUserResponseModel>

    /**
     * Market View API
     */
    @POST(AppConstants.REQUEST_TYPE_MARKET_VIEW)
    fun getMarketViewAPI(@Body marketViewRequestModel: MarketViewRequestModel): Call<MarketViewResponseModel>

    /**
     * Send Push Notification API
     */
    @POST(AppConstants.REQUEST_TYPE_SEND_PUSH_NOTIFICATION)
    fun sendPushNotificationAPI(@Body sendPushNotificationRequestModel: SendPushNotificationRequestModel): Call<SendPushNotificationResponseModel>
}