package com.esoftwere.hfk.core

object AppConstants {
    /**
     * Time Duration Constants
     */
    const val MILLISECONDS_1000: Long = 1000
    const val MILLISECONDS_2000: Long = 2000
    const val MILLISECONDS_3000: Long = 3000
    const val MILLISECONDS_6000: Long = 6000
    const val MILLISECONDS_60000: Long = 60000

    /** Remote Server URL Details */
    const val BASE_URL = "https://myhfk.com/app/"
    const val BASE_URL_IMAGE = "https://myhfk.com/assets/images/category_images/"
    const val PRIVACY_POLICY_URL = "https://myhfk.com/privacy_policy.php"
    const val TERMS_CONDITION_URL = "https://myhfk.com/terms_conditions.php"

    /** Content Header Type  */
    const val HEADER_CONTENT_TYPE_JSON = "Content-Type: application/json"

    /**
     * APP Related Variable
     */
    const val SEARCH_TYPE_CATEGORY = "category"
    const val SEARCH_TYPE_PRODUCT = "product"
    const val SEARCH_TYPE_MACHINERY = "machinery"
    const val ITEM_TYPE_PRODUCT = "product"
    const val ITEM_TYPE_MACHINERY = "machinery"
    const val YOUTUBE_CHANNEL = "https://www.youtube.com/channel/UCrzV4BY64ouzxrs19MT1jmw"
    const val REQUEST_CODE_VIDEO_GALLERY: Int = 1001
    const val REQUEST_CODE_VIDEO_CAPTURE: Int = 1002
    const val REQUEST_CODE_LOCATION_FILTER: Int = 1003

    /**
     * Shared Preference Key Name
     */
    const val KEY_PREFS_USER_IS_LOGGED_IN = "PREFS_USER_IS_LOGGED_IN"
    const val KEY_PREFS_USER_DETAILS = "PREFS_USER_DETAILS"
    const val KEY_PREFS_USER_ID = "PREFS_USER_ID"
    const val KEY_PREFS_USER_IMAGE = "PREFS_USER_IMAGE"
    const val KEY_PREFS_ADD_PRODUCT_ITEM = "PREFS_ADD_PRODUCT"
    const val KEY_PREFS_FCM_TOKEN = "PREFS_FCM_TOKEN"

    /**
     * Intent Key
     */
    const val INTENT_KEY_WEB_URL = "KEY_WEB_URL"
    const val INTENT_KEY_WEB_TOOLBAR_TITLE = "KEY_WEB_TOOLBAR_TITLE"
    const val INTENT_KEY_USER_PHONE = "KEY_USER_PHONE"
    const val INTENT_KEY_PRODUCT_ID = "KEY_PRODUCT_ID"
    const val INTENT_KEY_PRODUCT_TYPE = "KEY_PRODUCT_TYPE"
    const val INTENT_KEY_CATEGORY_ID = "KEY_CATEGORY_ID"
    const val INTENT_KEY_CATEGORY_NAME = "KEY_PRODUCT_NAME"
    const val INTENT_KEY_CATEGORY_TYPE = "KEY_CATEGORY_TYPE"
    const val INTENT_KEY_CHAT_RECEIVER_ID = "KEY_CHAT_RECEIVER_ID"
    const val INTENT_KEY_SELLER_PROFILE_ID = "KEY_SELLER_PROFILE_ID"
    const val INTENT_KEY_LOCATION_FILTER_DATA = "KEY_LOCATION_FILTER_DATA"
    const val INTENT_KEY_LOCATION_FILTER_DATA_HOME = "KEY_LOCATION_FILTER_DATA_HOME"

    /**
     * REST Web Service Path
     */
    const val REQUEST_TYPE_LOGIN = "customer/login"
    const val REQUEST_TYPE_REGISTER = "customer/register"
    const val REQUEST_TYPE_VERIFY_OTP = "customer/otpValidation"
    const val REQUEST_TYPE_COUNTRY_LIST = "customer/countryList"
    const val REQUEST_TYPE_STATE_LIST = "customer/stateList"
    const val REQUEST_TYPE_DISTRICT_LIST_BY_STATE = "customer/districtByStateId"
    const val REQUEST_TYPE_BLOCK_LIST_BY_DISTRICT = "customer/blockByDistrictId"
    const val REQUEST_TYPE_DASHBOARD = "customer/dashboard"
    const val REQUEST_TYPE_MAIN_CATEGORY_LIST = "customer/mainCategoryList"
    const val REQUEST_TYPE_CATEGORY_LIST = "customer/categoryList"
    const val REQUEST_TYPE_CATEGORY_LIST_BY_MAIN_CAT = "customer/categoryListByMainCategory"
    const val REQUEST_TYPE_ADD_PRODUCT = "customer/addProduct"
    const val REQUEST_TYPE_ADD_MACHINERY = "customer/addMachinery"
    const val REQUEST_TYPE_CATEGORY_UNIT_MAP = "product/category_unit_map"
    const val REQUEST_TYPE_CATEGORY_UNIT = "customer/unitListByCategory"
    const val REQUEST_TYPE_PRODUCT_LIST_BY_USER = "customer/productListByUserId"
    const val REQUEST_TYPE_PRODUCT_DETAILS = "customer/productDetailsById"
    const val REQUEST_TYPE_PRODUCT_LIST_BY_CAT = "customer/productListByCategoryId"
    const val REQUEST_TYPE_PRODUCT_RATING_API = "customer/productRating"
    const val REQUEST_TYPE_PRODUCT_REVIEW_LIST = "customer/commentListByProductId"
    const val REQUEST_TYPE_SEND_CHAT_NOTIFICATION = "customer/send_message"
    const val REQUEST_TYPE_UPDATE_FCM_TOKEN = "customer/updateTokenByUserId"
    const val REQUEST_TYPE_PROFILE_PIC_UPDATE = "customer/sellerProfileUpdate"
    const val REQUEST_TYPE_SELLER_PROFILE_DETAILS = "customer/sellerProfile"
    const val REQUEST_TYPE_ADD_TO_WISH_LIST = "product/addToWishlist"
    const val REQUEST_TYPE_WISH_LIST_LISTING = "product/wishlistList"
    const val REQUEST_TYPE_WISH_LIST_REMOVE = "product/wishlistRemove"
    const val REQUEST_TYPE_NOTIFICATION_LIST = "customer/listNotification"
    const val REQUEST_TYPE_PRODUCT_SEARCH = "product/search"
    const val REQUEST_TYPE_UPLOAD_FILE = "upload/file"
    const val REQUEST_TYPE_UPLOAD_VIDEO_FILE = "upload/videoFile"
    const val REQUEST_TYPE_MARKET_VIEW = "customer/marketValue"
    const val REQUEST_TYPE_SEND_PUSH_NOTIFICATION = "customer/sendPushNotification"
    const val REQUEST_TYPE_CHAT_USER_LIST_BY_ID = "customer/getChatUsersById"
    const val REQUEST_TYPE_FORGOT_PASSWORD_API = "customer/forgotPassword"
}