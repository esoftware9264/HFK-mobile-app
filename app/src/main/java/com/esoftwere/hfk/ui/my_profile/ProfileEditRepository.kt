package com.esoftwere.hfk.ui.my_profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.esoftwere.hfk.model.profile_update.ProfilePicUpdateRequestModel
import com.esoftwere.hfk.model.profile_update.ProfilePicUpdateResponseModel
import com.esoftwere.hfk.network.ResultWrapper
import com.esoftwere.hfk.network.api_client.HFKAPIClient
import com.esoftwere.hfk.network.api_client.HFKServiceAPI
import com.esoftwere.hfk.utils.AndroidUtility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileEditRepository(val mContext: Context) {
    private var hfkServiceAPI: HFKServiceAPI? = null
    private var mProfilePicUpdateMutableLiveData: MutableLiveData<ResultWrapper<ProfilePicUpdateResponseModel>>? =
        null

    private val TAG: String = "ProfileEditRepo"

    init {
        hfkServiceAPI = HFKAPIClient.hfkServiceAPI

        mProfilePicUpdateMutableLiveData = MutableLiveData()
    }

    fun callProfilePicUpdateAPI(
        profilePicUpdateRequestModel: ProfilePicUpdateRequestModel
    ) {
        mProfilePicUpdateMutableLiveData?.value = ResultWrapper.loading(true)

        hfkServiceAPI?.updateProfilePicAPI(
            profilePicUpdateRequestModel
        )?.enqueue(object : Callback<ProfilePicUpdateResponseModel> {
            override fun onResponse(
                call: Call<ProfilePicUpdateResponseModel>,
                response: Response<ProfilePicUpdateResponseModel>
            ) {
                mProfilePicUpdateMutableLiveData?.value = ResultWrapper.loading(false)

                if (response.isSuccessful) {
                    val profilePicUpdateResponseModel = response.body()
                    AndroidUtility.printModelDataWithGSON(TAG, profilePicUpdateResponseModel)

                    profilePicUpdateResponseModel?.let { profilePicUpdateResponse ->
                        if (profilePicUpdateResponse.success) {
                            mProfilePicUpdateMutableLiveData?.postValue(
                                ResultWrapper.success(
                                    profilePicUpdateResponse
                                )
                            )
                        } else {
                            mProfilePicUpdateMutableLiveData?.postValue(
                                ResultWrapper.failure(
                                    profilePicUpdateResponse.message
                                )
                            )
                        }
                    }
                } else {
                    response.errorBody()?.string()?.let {
                        Log.d(TAG, it)
                        mProfilePicUpdateMutableLiveData?.postValue(ResultWrapper.failure(it))
                    }
                }
            }

            override fun onFailure(call: Call<ProfilePicUpdateResponseModel>, t: Throwable) {
                mProfilePicUpdateMutableLiveData?.value = ResultWrapper.loading(false)
                mProfilePicUpdateMutableLiveData?.postValue(ResultWrapper.failure(t.message))
                t.message?.let { Log.d(TAG, it) }
            }
        })
    }

    fun getProfilePicUpdateResponseData(): MutableLiveData<ResultWrapper<ProfilePicUpdateResponseModel>> {
        return mProfilePicUpdateMutableLiveData
            ?: MutableLiveData<ResultWrapper<ProfilePicUpdateResponseModel>>()
    }
}