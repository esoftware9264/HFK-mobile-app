package com.esoftwere.hfk.ui.product_list_by_user

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.esoftwere.hfk.base.BaseRemoteAPIResponse
import com.esoftwere.hfk.model.forgot_password.ForgotPasswordRequestModel
import com.esoftwere.hfk.model.forgot_password.ForgotPasswordResponseModel
import com.esoftwere.hfk.model.product_list_by_user.*
import com.esoftwere.hfk.network.NetworkResult
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.network.api_client.HFKAPIClient
import com.esoftwere.hfk.network.api_client.HFKServiceAPI
import com.esoftwere.hfk.utils.AndroidUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductListByUserRepository(val mContext: Context): BaseRemoteAPIResponse() {
    private var hfkServiceAPI: HFKServiceAPI = HFKAPIClient.hfkServiceAPI
    private var mProductListByUserMutableLiveData: MutableLiveData<ResultWrapper<ProductListByUserResponseModel>>? =
        null

    private val TAG: String = "ProductListByUserRepo"

    init {
        mProductListByUserMutableLiveData = MutableLiveData()
    }

    fun callProductListByUserAPI(productListByUserRequestModel: ProductListByUserRequestModel) {
        AndroidUtility.printModelDataWithGSON(TAG, productListByUserRequestModel)
        mProductListByUserMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.productListByUserAPI(productListByUserRequestModel)
            ?.enqueue(object : Callback<ProductListByUserResponseModel> {
                override fun onResponse(
                    call: Call<ProductListByUserResponseModel>,
                    response: Response<ProductListByUserResponseModel>
                ) {
                    mProductListByUserMutableLiveData?.value = ResultWrapper.loading(false)
                    if (response.isSuccessful) {
                        val productListByUserResponseModel = response.body()

                        AndroidUtility.printModelDataWithGSON(TAG, productListByUserResponseModel)
                        productListByUserResponseModel?.let { productListByUserResponse ->
                            mProductListByUserMutableLiveData?.postValue(
                                ResultWrapper.success(
                                    productListByUserResponse
                                )
                            )
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mProductListByUserMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<ProductListByUserResponseModel>, t: Throwable) {
                    mProductListByUserMutableLiveData?.value = ResultWrapper.loading(false)
                    mProductListByUserMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    suspend fun callProductDisableByIdAPI(productDisableRequestModel: ProductDisableRequestModel): Flow<NetworkResult<ProductDisableResponseModel>> {
        AndroidUtility.printModelDataWithGSON(TAG, productDisableRequestModel)
        return flow<NetworkResult<ProductDisableResponseModel>> {
            emit(safeApiCall { hfkServiceAPI.productDisableByIdAPI(productDisableRequestModel) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun callProductRemoveByIdAPI(productDeleteRequestModel: ProductDeleteRequestModel): Flow<NetworkResult<ProductDeleteResponseModel>> {
        AndroidUtility.printModelDataWithGSON(TAG, productDeleteRequestModel)
        return flow<NetworkResult<ProductDeleteResponseModel>> {
            emit(safeApiCall { hfkServiceAPI.productRemoveByIdAPI(productDeleteRequestModel) })
        }.flowOn(Dispatchers.IO)
    }

    fun getProductListByUserResponseData(): MutableLiveData<ResultWrapper<ProductListByUserResponseModel>> {
        return mProductListByUserMutableLiveData
            ?: MutableLiveData<ResultWrapper<ProductListByUserResponseModel>>()
    }
}