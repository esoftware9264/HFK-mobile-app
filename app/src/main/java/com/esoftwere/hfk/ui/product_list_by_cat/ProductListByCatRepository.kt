package com.esoftwere.hfk.ui.product_list_by_cat

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.esoftwere.hfk.model.product_list_by_cat.ProductListByCatRequestModel
import com.esoftwere.hfk.model.product_list_by_cat.ProductListByCatResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.network.api_client.HFKAPIClient
import com.esoftwere.hfk.network.api_client.HFKServiceAPI
import com.esoftwere.hfk.utils.AndroidUtility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductListByCatRepository(val mContext: Context) {
    private var hfkServiceAPI: HFKServiceAPI? = null
    private var mProductListByCatMutableLiveData: MutableLiveData<ResultWrapper<ProductListByCatResponseModel>>? =
        null

    private val TAG: String = "ProductListByCatRepo"

    init {
        hfkServiceAPI = HFKAPIClient.hfkServiceAPI

        mProductListByCatMutableLiveData = MutableLiveData()
    }

    fun callProductListByCatAPI(productListByCatRequestModel: ProductListByCatRequestModel) {
        mProductListByCatMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.productListByCatAPI(productListByCatRequestModel)
            ?.enqueue(object : Callback<ProductListByCatResponseModel> {
                override fun onResponse(
                    call: Call<ProductListByCatResponseModel>,
                    response: Response<ProductListByCatResponseModel>
                ) {
                    mProductListByCatMutableLiveData?.value = ResultWrapper.loading(false)
                    AndroidUtility.printModelDataWithGSON(TAG, productListByCatRequestModel)

                    if (response.isSuccessful) {
                        val categoryListResponseModel = response.body()
                        categoryListResponseModel?.let { categoryListResponse ->
                            if (categoryListResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, categoryListResponse)

                                mProductListByCatMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        categoryListResponse
                                    )
                                )
                            } else {
                                mProductListByCatMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        categoryListResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mProductListByCatMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<ProductListByCatResponseModel>, t: Throwable) {
                    mProductListByCatMutableLiveData?.value = ResultWrapper.loading(false)
                    mProductListByCatMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun getProductListByCatResponseData(): MutableLiveData<ResultWrapper<ProductListByCatResponseModel>> {
        return mProductListByCatMutableLiveData
            ?: MutableLiveData<ResultWrapper<ProductListByCatResponseModel>>()
    }
}