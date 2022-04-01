package com.esoftwere.hfk.ui.category_list

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.esoftwere.hfk.model.add_product.AddProductResponseModel
import com.esoftwere.hfk.model.add_product.CategoryListResponseModel
import com.esoftwere.hfk.model.category.CategoryListByMainCatIdRequestModel
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

class CategoryListRepository(val mContext: Context) {
    private var hfkServiceAPI: HFKServiceAPI? = null
    private var mCategoryListMutableLiveData: MutableLiveData<ResultWrapper<CategoryListResponseModel>>? =
        null
    private var mCategoryListByMainCatMutableLiveData: MutableLiveData<ResultWrapper<CategoryListResponseModel>>? =
        null

    private val TAG: String = "CategoryListRepo"

    init {
        hfkServiceAPI = HFKAPIClient.hfkServiceAPI

        mCategoryListMutableLiveData = MutableLiveData()
        mCategoryListByMainCatMutableLiveData = MutableLiveData()
    }

    fun callCategoryListAPI() {
        mCategoryListMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.categoryListAPI(mainCatId = "1")
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

    fun callCategoryListByMainCatAPI(categoryListByMainCatIdRequestModel: CategoryListByMainCatIdRequestModel) {
        mCategoryListByMainCatMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.categoryListByMainCatAPI(categoryListByMainCatIdRequestModel)
            ?.enqueue(object : Callback<CategoryListResponseModel> {
                override fun onResponse(
                    call: Call<CategoryListResponseModel>,
                    response: Response<CategoryListResponseModel>
                ) {
                    mCategoryListByMainCatMutableLiveData?.value = ResultWrapper.loading(false)

                    if (response.isSuccessful) {
                        val categoryListResponseModel = response.body()
                        categoryListResponseModel?.let { categoryListResponse ->
                            if (categoryListResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, categoryListResponse)

                                mCategoryListByMainCatMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        categoryListResponse
                                    )
                                )
                            } else {
                                mCategoryListByMainCatMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        categoryListResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mCategoryListByMainCatMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<CategoryListResponseModel>, t: Throwable) {
                    mCategoryListByMainCatMutableLiveData?.value = ResultWrapper.loading(false)
                    mCategoryListByMainCatMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun getCategoryListResponseData(): MutableLiveData<ResultWrapper<CategoryListResponseModel>> {
        return mCategoryListMutableLiveData
            ?: MutableLiveData<ResultWrapper<CategoryListResponseModel>>()
    }

    fun getCategoryListByMainCatResponseData(): MutableLiveData<ResultWrapper<CategoryListResponseModel>> {
        return mCategoryListByMainCatMutableLiveData
            ?: MutableLiveData<ResultWrapper<CategoryListResponseModel>>()
    }
}