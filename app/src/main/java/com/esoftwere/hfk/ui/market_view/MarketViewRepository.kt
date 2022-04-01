package com.esoftwere.hfk.ui.market_view

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.esoftwere.hfk.model.add_product.AddProductResponseModel
import com.esoftwere.hfk.model.add_product.CategoryListResponseModel
import com.esoftwere.hfk.model.category_unit.CategoryUnitMapRequestModel
import com.esoftwere.hfk.model.category_unit.CategoryUnitMapResponseModel
import com.esoftwere.hfk.model.file_upload.FileUploadResponseModel
import com.esoftwere.hfk.model.main_category.MainCategoryListResponseModel
import com.esoftwere.hfk.model.market_view.MarketViewRequestModel
import com.esoftwere.hfk.model.market_view.MarketViewResponseModel
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

class MarketViewRepository(val mContext: Context) {
    private var hfkServiceAPI: HFKServiceAPI? = null
    private var mMarketViewMutableLiveData: MutableLiveData<ResultWrapper<MarketViewResponseModel>>? =
        null

    private val TAG: String = "MarketViewRepo"

    init {
        hfkServiceAPI = HFKAPIClient.hfkServiceAPI

        mMarketViewMutableLiveData = MutableLiveData()
    }

    fun callMarketViewAPI(marketViewRequestModel: MarketViewRequestModel) {
        mMarketViewMutableLiveData?.value = ResultWrapper.loading(true)
        AndroidUtility.printModelDataWithGSON(TAG, marketViewRequestModel)

        hfkServiceAPI?.getMarketViewAPI(marketViewRequestModel)
            ?.enqueue(object : Callback<MarketViewResponseModel> {
                override fun onResponse(
                    call: Call<MarketViewResponseModel>,
                    response: Response<MarketViewResponseModel>
                ) {
                    mMarketViewMutableLiveData?.value = ResultWrapper.loading(false)

                    if (response.isSuccessful) {
                        val marketViewResponseModel = response.body()
                        marketViewResponseModel?.let { marketViewResponse ->
                            if (marketViewResponse.success) {
                                AndroidUtility.printModelDataWithGSON(TAG, marketViewResponse)

                                mMarketViewMutableLiveData?.postValue(
                                    ResultWrapper.success(
                                        marketViewResponse
                                    )
                                )
                            } else {
                                mMarketViewMutableLiveData?.postValue(
                                    ResultWrapper.failure(
                                        marketViewResponse.message
                                    )
                                )
                            }
                        }
                    } else {
                        response.errorBody()?.string()?.let {
                            Log.d(TAG, it)
                            mMarketViewMutableLiveData?.postValue(ResultWrapper.failure(it))
                        }
                    }
                }

                override fun onFailure(call: Call<MarketViewResponseModel>, t: Throwable) {
                    mMarketViewMutableLiveData?.value = ResultWrapper.loading(false)
                    mMarketViewMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                    t.message?.let { Log.d(TAG, it) }
                }
            })
    }

    fun getMarketViewResponseData(): MutableLiveData<ResultWrapper<MarketViewResponseModel>> {
        return mMarketViewMutableLiveData
            ?: MutableLiveData<ResultWrapper<MarketViewResponseModel>>()
    }
}