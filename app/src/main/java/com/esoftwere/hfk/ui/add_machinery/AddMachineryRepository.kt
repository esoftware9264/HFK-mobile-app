package com.esoftwere.hfk.ui.add_machinery

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.esoftwere.hfk.model.add_machinery.AddMachineryRequestModel
import com.esoftwere.hfk.model.add_product.AddProductResponseModel
import com.esoftwere.hfk.model.add_product.CategoryListResponseModel
import com.esoftwere.hfk.model.category_unit.CategoryUnitMapRequestModel
import com.esoftwere.hfk.model.category_unit.CategoryUnitMapResponseModel
import com.esoftwere.hfk.model.file_upload.FileUploadResponseModel
import com.esoftwere.hfk.model.main_category.MainCategoryListResponseModel
import com.esoftwere.hfk.model.register.RegisterRequestModel
import com.esoftwere.hfk.model.register.RegisterResponseModel
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

class AddMachineryRepository(val mContext: Context) {
    private var hfkServiceAPI: HFKServiceAPI? = null
    private var mMainCategoryListMutableLiveData: MutableLiveData<ResultWrapper<MainCategoryListResponseModel>>? =
        null
    private var mCategoryListMutableLiveData: MutableLiveData<ResultWrapper<CategoryListResponseModel>>? =
        null
    private var mCategoryUnitMapMutableLiveData: MutableLiveData<ResultWrapper<CategoryUnitMapResponseModel>>? =
        null
    private var mImageUploadMutableLiveData: MutableLiveData<ResultWrapper<FileUploadResponseModel>>? =
        null
    private var mAddMachineryMutableLiveData: MutableLiveData<ResultWrapper<AddProductResponseModel>>? =
        null

    private val TAG: String = "AddMachineryRepo"

    init {
        hfkServiceAPI = HFKAPIClient.hfkServiceAPI

        mMainCategoryListMutableLiveData = MutableLiveData()
        mCategoryListMutableLiveData = MutableLiveData()
        mCategoryUnitMapMutableLiveData = MutableLiveData()
        mImageUploadMutableLiveData = MutableLiveData()
        mAddMachineryMutableLiveData = MutableLiveData()
    }

    fun callMainCategoryListAPI() {
        mMainCategoryListMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.mainCategoryListAPI(isMachinery = 1)
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

        hfkServiceAPI?.categoryListAPI(mainCatId = mainCategoryId, isMachinery = 1)
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

    fun callCategoryUnitMapAPI(categoryUnitMapRequestModel: CategoryUnitMapRequestModel) {
        mCategoryUnitMapMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.categoryUnitMapAPI(categoryUnitMapRequestModel)
            ?.enqueue(object : Callback<CategoryUnitMapResponseModel> {
                override fun onResponse(
                    call: Call<CategoryUnitMapResponseModel>,
                    response: Response<CategoryUnitMapResponseModel>
                ) {
                    mCategoryUnitMapMutableLiveData?.value = ResultWrapper.loading(false)
                    AndroidUtility.printModelDataWithGSON(TAG, categoryUnitMapRequestModel)

                    if (response.isSuccessful) {
                        val categoryListResponseModel = response.body()
                        categoryListResponseModel?.let { categoryListResponse ->
                            if (categoryListResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, categoryListResponse)

                                mCategoryUnitMapMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        categoryListResponse
                                    )
                                )
                            } else {
                                mCategoryUnitMapMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        categoryListResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mCategoryUnitMapMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<CategoryUnitMapResponseModel>, t: Throwable) {
                    mCategoryUnitMapMutableLiveData?.value = ResultWrapper.loading(false)
                    mCategoryUnitMapMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun callAddMachineryAPI(addMachineryRequestModel: AddMachineryRequestModel) {
        mAddMachineryMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.addMachineryAPI(
            addMachineryRequestModel
        )?.enqueue(object : Callback<AddProductResponseModel> {
            override fun onResponse(
                call: Call<AddProductResponseModel>,
                response: Response<AddProductResponseModel>
            ) {
                mAddMachineryMutableLiveData?.value = ResultWrapper.loading(false)
                AndroidUtility.printModelDataWithGSON(TAG, addMachineryRequestModel)

                if (response.isSuccessful) {
                    val addProductResponseModel = response.body()
                    AndroidUtility.printModelDataWithGSON(TAG, addProductResponseModel)

                    addProductResponseModel?.let { addProductResponse ->
                        if (addProductResponse.success) {
                            mAddMachineryMutableLiveData?.postValue(
                                ResultWrapper.success(
                                    addProductResponse
                                )
                            )
                        } else {
                            mAddMachineryMutableLiveData?.postValue(
                                ResultWrapper.failure(
                                    addProductResponse.message
                                )
                            )
                        }
                    }
                } else {
                    response.errorBody()?.string()?.let {
                        Log.d(TAG, it)
                        mAddMachineryMutableLiveData?.postValue(ResultWrapper.failure(it))
                    }
                }
            }

            override fun onFailure(call: Call<AddProductResponseModel>, t: Throwable) {
                mAddMachineryMutableLiveData?.value = ResultWrapper.loading(false)
                mAddMachineryMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                t.message?.let { Log.d(TAG, it) }
            }
        })
    }

    fun callImageUploadAPI(
        file: MultipartBody.Part,
        fileTypeRequestBody: RequestBody,
        userIdRequestBody: RequestBody
    ) {
        mImageUploadMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.fileUploadAPI(
            file,
            fileTypeRequestBody,
            userIdRequestBody
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

    fun getMainCategoryListResponseData(): MutableLiveData<ResultWrapper<MainCategoryListResponseModel>> {
        return mMainCategoryListMutableLiveData
            ?: MutableLiveData<ResultWrapper<MainCategoryListResponseModel>>()
    }

    fun getCategoryListResponseData(): MutableLiveData<ResultWrapper<CategoryListResponseModel>> {
        return mCategoryListMutableLiveData
            ?: MutableLiveData<ResultWrapper<CategoryListResponseModel>>()
    }

    fun getCategoryUnitMapResponseData(): MutableLiveData<ResultWrapper<CategoryUnitMapResponseModel>> {
        return mCategoryUnitMapMutableLiveData
            ?: MutableLiveData<ResultWrapper<CategoryUnitMapResponseModel>>()
    }

    fun getImageUploadResponseData(): MutableLiveData<ResultWrapper<FileUploadResponseModel>> {
        return mImageUploadMutableLiveData
            ?: MutableLiveData<ResultWrapper<FileUploadResponseModel>>()
    }

    fun getAddMachineryResponseData(): MutableLiveData<ResultWrapper<AddProductResponseModel>> {
        return mAddMachineryMutableLiveData
            ?: MutableLiveData<ResultWrapper<AddProductResponseModel>>()
    }
}