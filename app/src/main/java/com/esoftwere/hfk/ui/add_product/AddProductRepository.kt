package com.esoftwere.hfk.ui.add_product

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.esoftwere.hfk.model.add_product.*
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

class AddProductRepository(val mContext: Context) {
    private var hfkServiceAPI: HFKServiceAPI? = null
    private var mMainCategoryListMutableLiveData: MutableLiveData<ResultWrapper<MainCategoryListResponseModel>>? =
        null
    private var mCategoryListMutableLiveData: MutableLiveData<ResultWrapper<CategoryListResponseModel>>? =
        null
    private var mCategoryUnitMapMutableLiveData: MutableLiveData<ResultWrapper<CategoryUnitMapResponseModel>>? =
        null
    private var mCategoryUnitMutableLiveData: MutableLiveData<ResultWrapper<CategoryUnitResponseModel>>? =
        null
    private var mImageUploadMutableLiveData: MutableLiveData<ResultWrapper<FileUploadResponseModel>>? =
        null
    private var mAddProductMutableLiveData: MutableLiveData<ResultWrapper<AddProductResponseModel>>? =
        null

    private val TAG: String = "AddProductRepository"

    init {
        hfkServiceAPI = HFKAPIClient.hfkServiceAPI

        mMainCategoryListMutableLiveData = MutableLiveData()
        mCategoryListMutableLiveData = MutableLiveData()
        mCategoryUnitMapMutableLiveData = MutableLiveData()
        mCategoryUnitMutableLiveData = MutableLiveData()
        mImageUploadMutableLiveData = MutableLiveData()
        mAddProductMutableLiveData = MutableLiveData()
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
        )?.enqueue(object : Callback<CategoryListResponseModel> {
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

    fun callCategoryUnitAPI(categoryUnitRequestModel: CategoryUnitRequestModel) {
        mCategoryUnitMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.categoryUnitAPI(categoryUnitRequestModel)
            ?.enqueue(object : Callback<CategoryUnitResponseModel> {
                override fun onResponse(
                    call: Call<CategoryUnitResponseModel>,
                    response: Response<CategoryUnitResponseModel>
                ) {
                    mCategoryUnitMutableLiveData?.value = ResultWrapper.loading(false)
                    AndroidUtility.printModelDataWithGSON(TAG, categoryUnitRequestModel)

                    if (response.isSuccessful) {
                        val categoryListResponseModel = response.body()
                        categoryListResponseModel?.let { categoryListResponse ->
                            if (categoryListResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, categoryListResponse)

                                mCategoryUnitMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        categoryListResponse
                                    )
                                )
                            } else {
                                mCategoryUnitMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        categoryListResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mCategoryUnitMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<CategoryUnitResponseModel>, t: Throwable) {
                    mCategoryUnitMutableLiveData?.value = ResultWrapper.loading(false)
                    mCategoryUnitMutableLiveData?.postValue(ResultWrapper.failure(t.message))
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

    fun callAddProductAPI(addProductRequestModel: AddProductRequestModel) {
        mAddProductMutableLiveData?.value = ResultWrapper.loading(true)
        AndroidUtility.printModelDataWithGSON(TAG, addProductRequestModel)

        hfkServiceAPI?.addProductAPI(addProductRequestModel)
            ?.enqueue(object : Callback<AddProductResponseModel> {
                override fun onResponse(
                    call: Call<AddProductResponseModel>,
                    response: Response<AddProductResponseModel>
                ) {
                    mAddProductMutableLiveData?.value = ResultWrapper.loading(false)
                    AndroidUtility.printModelDataWithGSON(TAG, addProductRequestModel)

                    if (response.isSuccessful) {
                        val addProductResponseModel = response.body()
                        AndroidUtility.printModelDataWithGSON(TAG, addProductResponseModel)

                        addProductResponseModel?.let { addProductResponse ->
                            if (addProductResponse.success) {
                                mAddProductMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        addProductResponse
                                    )
                                )
                            } else {
                                mAddProductMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        addProductResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mAddProductMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<AddProductResponseModel>, t: Throwable) {
                    mAddProductMutableLiveData?.value = ResultWrapper.loading(false)
                    mAddProductMutableLiveData?.postValue(ResultWrapper.failure(t.message))
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

    fun getCategoryUnitResponseData(): MutableLiveData<ResultWrapper<CategoryUnitResponseModel>> {
        return mCategoryUnitMutableLiveData
            ?: MutableLiveData<ResultWrapper<CategoryUnitResponseModel>>()
    }

    fun getImageUploadResponseData(): MutableLiveData<ResultWrapper<FileUploadResponseModel>> {
        return mImageUploadMutableLiveData
            ?: MutableLiveData<ResultWrapper<FileUploadResponseModel>>()
    }

    fun getAddProductResponseData(): MutableLiveData<ResultWrapper<AddProductResponseModel>> {
        return mAddProductMutableLiveData
            ?: MutableLiveData<ResultWrapper<AddProductResponseModel>>()
    }
}