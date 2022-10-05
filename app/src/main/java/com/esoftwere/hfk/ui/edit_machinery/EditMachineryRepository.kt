package com.esoftwere.hfk.ui.edit_machinery

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.esoftwere.hfk.base.BaseRemoteAPIResponse
import com.esoftwere.hfk.model.edit_machinery.EditMachineryRequestModel
import com.esoftwere.hfk.model.edit_machinery.EditMachineryResponseModel
import com.esoftwere.hfk.model.edit_product.EditProductRequestModel
import com.esoftwere.hfk.model.edit_product.EditProductResponseModel
import com.esoftwere.hfk.model.forgot_password.ForgotPasswordRequestModel
import com.esoftwere.hfk.model.forgot_password.ForgotPasswordResponseModel
import com.esoftwere.hfk.model.product_list_by_user.ProductDeleteRequestModel
import com.esoftwere.hfk.model.product_list_by_user.ProductDeleteResponseModel
import com.esoftwere.hfk.model.product_list_by_user.ProductListByUserRequestModel
import com.esoftwere.hfk.model.product_list_by_user.ProductListByUserResponseModel
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

class EditMachineryRepository(val mContext: Context): BaseRemoteAPIResponse() {
    private var hfkServiceAPI: HFKServiceAPI = HFKAPIClient.hfkServiceAPI
    private val TAG: String = "EditMachineryRepo"

    suspend fun callEditMachineryByIdAPI(editMachineryRequestModel: EditMachineryRequestModel): Flow<NetworkResult<EditMachineryResponseModel>> {
        return flow<NetworkResult<EditMachineryResponseModel>> {
            emit(safeApiCall { hfkServiceAPI.editMachineryByIdAPI(editMachineryRequestModel) })
        }.flowOn(Dispatchers.IO)
    }
}