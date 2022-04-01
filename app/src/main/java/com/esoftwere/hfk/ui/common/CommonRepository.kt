package com.esoftwere.hfk.ui.common

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.esoftwere.hfk.model.add_product.CategoryListResponseModel
import com.esoftwere.hfk.model.block.BlockListRequestModel
import com.esoftwere.hfk.model.block.BlockListResponseModel
import com.esoftwere.hfk.model.country.CountryListResponseModel
import com.esoftwere.hfk.model.district.DistrictListRequestModel
import com.esoftwere.hfk.model.district.DistrictListResponseModel
import com.esoftwere.hfk.model.file_upload.FileUploadResponseModel
import com.esoftwere.hfk.model.file_upload.VideoUploadResponseModel
import com.esoftwere.hfk.model.main_category.MainCategoryListResponseModel
import com.esoftwere.hfk.model.state.StateListResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.network.api_client.HFKAPIClient
import com.esoftwere.hfk.network.api_client.HFKServiceAPI
import com.esoftwere.hfk.utils.AndroidUtility
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommonRepository(val mContext: Context) {
    private var hfkServiceAPI: HFKServiceAPI? = null
    private var mImageUploadMutableLiveData: MutableLiveData<ResultWrapper<FileUploadResponseModel>>? =
        null
    private var mVideoUploadMutableLiveData: MutableLiveData<ResultWrapper<VideoUploadResponseModel>>? =
        null
    private var mCountryListMutableLiveData: MutableLiveData<ResultWrapper<CountryListResponseModel>>? =
        null
    private var mStateListMutableLiveData: MutableLiveData<ResultWrapper<StateListResponseModel>>? =
        null
    private var mDistrictListMutableLiveData: MutableLiveData<ResultWrapper<DistrictListResponseModel>>? =
        null
    private var mBlockListMutableLiveData: MutableLiveData<ResultWrapper<BlockListResponseModel>>? =
        null
    private var mMainCategoryListMutableLiveData: MutableLiveData<ResultWrapper<MainCategoryListResponseModel>>? =
        null
    private var mCategoryListMutableLiveData: MutableLiveData<ResultWrapper<CategoryListResponseModel>>? =
        null

    private val TAG: String = "CommonRepository"

    init {
        hfkServiceAPI = HFKAPIClient.hfkServiceAPI

        mImageUploadMutableLiveData = MutableLiveData()
        mVideoUploadMutableLiveData = MutableLiveData()
        mCountryListMutableLiveData = MutableLiveData()
        mStateListMutableLiveData = MutableLiveData()
        mDistrictListMutableLiveData = MutableLiveData()
        mBlockListMutableLiveData = MutableLiveData()
        mMainCategoryListMutableLiveData = MutableLiveData()
        mCategoryListMutableLiveData = MutableLiveData()
    }

    fun callImageUploadAPI(
        file: MultipartBody.Part,
        fileTypeRequestBody: RequestBody,
        userIdRequestBody: RequestBody
    ) {
        mImageUploadMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.fileUploadAPI(
            file, fileTypeRequestBody, userIdRequestBody
        )?.enqueue(object : Callback<FileUploadResponseModel> {
            override fun onResponse(
                call: Call<FileUploadResponseModel>,
                response: Response<FileUploadResponseModel>
            ) {
                mImageUploadMutableLiveData?.value = ResultWrapper.loading(false)

                if (response.isSuccessful) {
                    val fileUploadResponseModel = response.body()
                    AndroidUtility.printModelDataWithGSON(TAG, fileUploadResponseModel)

                    fileUploadResponseModel?.let { fileUploadResponse ->
                        if (fileUploadResponse.success) {
                            mImageUploadMutableLiveData?.postValue(
                                ResultWrapper.success(
                                    fileUploadResponse
                                )
                            )
                        } else {
                            mImageUploadMutableLiveData?.postValue(
                                ResultWrapper.failure(
                                    fileUploadResponse.message
                                )
                            )
                        }
                    }
                } else {
                    response.errorBody()?.string()?.let {
                        Log.d(TAG, it)
                        mImageUploadMutableLiveData?.postValue(ResultWrapper.failure(it))
                    }
                }
            }

            override fun onFailure(call: Call<FileUploadResponseModel>, t: Throwable) {
                mImageUploadMutableLiveData?.value = ResultWrapper.loading(false)
                mImageUploadMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                t.message?.let { Log.d(TAG, it) }
            }
        })
    }

    fun callVideoUploadAPI(
        file: MultipartBody.Part,
        fileTypeRequestBody: RequestBody,
        userIdRequestBody: RequestBody
    ) {
        mVideoUploadMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.fileUploadVideoAPI(
            file, fileTypeRequestBody, userIdRequestBody
        )?.enqueue(object : Callback<VideoUploadResponseModel> {
            override fun onResponse(
                call: Call<VideoUploadResponseModel>,
                response: Response<VideoUploadResponseModel>
            ) {
                mVideoUploadMutableLiveData?.value = ResultWrapper.loading(false)

                if (response.isSuccessful) {
                    val fileUploadResponseModel = response.body()
                    AndroidUtility.printModelDataWithGSON(TAG, fileUploadResponseModel)

                    fileUploadResponseModel?.let { fileUploadResponse ->
                        if (fileUploadResponse.success) {
                            mVideoUploadMutableLiveData?.postValue(
                                ResultWrapper.success(
                                    fileUploadResponse
                                )
                            )
                        } else {
                            mVideoUploadMutableLiveData?.postValue(
                                ResultWrapper.failure(
                                    fileUploadResponse.message
                                )
                            )
                        }
                    }
                } else {
                    response.errorBody()?.string()?.let {
                        Log.d(TAG, it)
                        mVideoUploadMutableLiveData?.postValue(ResultWrapper.failure(it))
                    }
                }
            }

            override fun onFailure(call: Call<VideoUploadResponseModel>, t: Throwable) {
                mVideoUploadMutableLiveData?.value = ResultWrapper.loading(false)
                mVideoUploadMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                t.message?.let { Log.d(TAG, it) }
            }
        })
    }

    fun callCountryListAPI() {
        mCountryListMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.countryListAPI()
            ?.enqueue(object : Callback<CountryListResponseModel> {
                override fun onResponse(
                    call: Call<CountryListResponseModel>,
                    response: Response<CountryListResponseModel>
                ) {
                    mCountryListMutableLiveData?.value = ResultWrapper.loading(false)

                    if (response.isSuccessful) {
                        val stateListResponseModel = response.body()
                        stateListResponseModel?.let { stateListResponse ->
                            if (stateListResponse.success) {
                                mCountryListMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        stateListResponse
                                    )
                                )
                            } else {
                                mCountryListMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        stateListResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mStateListMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<CountryListResponseModel>, t: Throwable) {
                    mCountryListMutableLiveData?.value = ResultWrapper.loading(false)
                    mCountryListMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun callStateListAPI(countryId: String) {
        mStateListMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.stateListAPI(countryId)
            ?.enqueue(object : Callback<StateListResponseModel> {
                override fun onResponse(
                    call: Call<StateListResponseModel>,
                    response: Response<StateListResponseModel>
                ) {
                    mStateListMutableLiveData?.value = ResultWrapper.loading(false)

                    if (response.isSuccessful) {
                        val stateListResponseModel = response.body()
                        stateListResponseModel?.let { stateListResponse ->
                            if (stateListResponse.success) {
                                mStateListMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        stateListResponse
                                    )
                                )
                            } else {
                                mStateListMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        stateListResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mStateListMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<StateListResponseModel>, t: Throwable) {
                    mStateListMutableLiveData?.value = ResultWrapper.loading(false)
                    mStateListMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun callDistrictListAPI(districtListRequestModel: DistrictListRequestModel) {
        mDistrictListMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.districtListAPI(districtListRequestModel)
            ?.enqueue(object : Callback<DistrictListResponseModel> {
                override fun onResponse(
                    call: Call<DistrictListResponseModel>,
                    response: Response<DistrictListResponseModel>
                ) {
                    mDistrictListMutableLiveData?.value = ResultWrapper.loading(false)
                    AndroidUtility.printModelDataWithGSON(TAG, districtListRequestModel)

                    if (response.isSuccessful) {
                        val districtListResponseModel = response.body()
                        districtListResponseModel?.let { districtListResponse ->
                            if (districtListResponse.success) {
                                mDistrictListMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        districtListResponse
                                    )
                                )
                            } else {
                                mDistrictListMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        districtListResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mDistrictListMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<DistrictListResponseModel>, t: Throwable) {
                    mDistrictListMutableLiveData?.value = ResultWrapper.loading(false)
                    mDistrictListMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun callBlockListAPI(blockListRequestModel: BlockListRequestModel) {
        mBlockListMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.blockListAPI(blockListRequestModel)
            ?.enqueue(object : Callback<BlockListResponseModel> {
                override fun onResponse(
                    call: Call<BlockListResponseModel>,
                    response: Response<BlockListResponseModel>
                ) {
                    mBlockListMutableLiveData?.value = ResultWrapper.loading(false)
                    AndroidUtility.printModelDataWithGSON(TAG, blockListRequestModel)

                    if (response.isSuccessful) {
                        val blockListResponseModel = response.body()
                        blockListResponseModel?.let { blockListResponse ->
                            if (blockListResponse.success) {
                                mBlockListMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        blockListResponse
                                    )
                                )
                            } else {
                                mBlockListMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        blockListResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mBlockListMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<BlockListResponseModel>, t: Throwable) {
                    mBlockListMutableLiveData?.value = ResultWrapper.loading(false)
                    mBlockListMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun callMainCategoryListAPI() {
        mMainCategoryListMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.mainCategoryListAPI()
            ?.enqueue(object : Callback<MainCategoryListResponseModel> {
                override fun onResponse(
                    call: Call<MainCategoryListResponseModel>,
                    response: Response<MainCategoryListResponseModel>
                ) {
                    mMainCategoryListMutableLiveData?.value = ResultWrapper.loading(false)

                    if (response.isSuccessful) {
                        val mainCatListResponseModel = response.body()
                        mainCatListResponseModel?.let { mainCatListResponse ->
                            if (mainCatListResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, mainCatListResponse)

                                mMainCategoryListMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        mainCatListResponse
                                    )
                                )
                            } else {
                                mMainCategoryListMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        mainCatListResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mMainCategoryListMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<MainCategoryListResponseModel>, t: Throwable) {
                    mMainCategoryListMutableLiveData?.value = ResultWrapper.loading(false)
                    mMainCategoryListMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun callCategoryListAPI(mainCategoryId: String) {
        mCategoryListMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.categoryListAPI(
            mainCatId = mainCategoryId
        )
            ?.enqueue(object : Callback<CategoryListResponseModel> {
                override fun onResponse(
                    call: Call<CategoryListResponseModel>,
                    response: Response<CategoryListResponseModel>
                ) {
                    mCategoryListMutableLiveData?.value = ResultWrapper.loading(false)

                    if (response.isSuccessful) {
                        val categoryListResponseModel = response.body()
                        categoryListResponseModel?.let { categoryListResponse ->
                            if (categoryListResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, categoryListResponse)

                                mCategoryListMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        categoryListResponse
                                    )
                                )
                            } else {
                                mCategoryListMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        categoryListResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mCategoryListMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<CategoryListResponseModel>, t: Throwable) {
                    mCategoryListMutableLiveData?.value = ResultWrapper.loading(false)
                    mCategoryListMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun getImageUploadResponseData(): MutableLiveData<ResultWrapper<FileUploadResponseModel>> {
        return mImageUploadMutableLiveData
            ?: MutableLiveData<ResultWrapper<FileUploadResponseModel>>()
    }

    fun getVideoUploadResponseData(): MutableLiveData<ResultWrapper<VideoUploadResponseModel>> {
        return mVideoUploadMutableLiveData
            ?: MutableLiveData<ResultWrapper<VideoUploadResponseModel>>()
    }

    fun getCountryListResponseData(): MutableLiveData<ResultWrapper<CountryListResponseModel>> {
        return mCountryListMutableLiveData
            ?: MutableLiveData<ResultWrapper<CountryListResponseModel>>()
    }

    fun getStateListResponseData(): MutableLiveData<ResultWrapper<StateListResponseModel>> {
        return mStateListMutableLiveData
            ?: MutableLiveData<ResultWrapper<StateListResponseModel>>()
    }

    fun getDistrictListResponseData(): MutableLiveData<ResultWrapper<DistrictListResponseModel>> {
        return mDistrictListMutableLiveData
            ?: MutableLiveData<ResultWrapper<DistrictListResponseModel>>()
    }

    fun getBlockListResponseData(): MutableLiveData<ResultWrapper<BlockListResponseModel>> {
        return mBlockListMutableLiveData
            ?: MutableLiveData<ResultWrapper<BlockListResponseModel>>()
    }

    fun getMainCategoryListResponseData(): MutableLiveData<ResultWrapper<MainCategoryListResponseModel>> {
        return mMainCategoryListMutableLiveData
            ?: MutableLiveData<ResultWrapper<MainCategoryListResponseModel>>()
    }

    fun getCategoryListResponseData(): MutableLiveData<ResultWrapper<CategoryListResponseModel>> {
        return mCategoryListMutableLiveData
            ?: MutableLiveData<ResultWrapper<CategoryListResponseModel>>()
    }
}