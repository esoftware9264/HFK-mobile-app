package com.esoftwere.hfk.ui.my_profile

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.model.profile_update.ProfilePicUpdateRequestModel
import com.esoftwere.hfk.model.profile_update.ProfilePicUpdateResponseModel
import com.esoftwere.hfk.network.ResultWrapper

class ProfileEditViewModel(private val mApplication: HFKApplication) :
    AndroidViewModel(mApplication) {
    private var mProfileEditRepository: ProfileEditRepository? = null
    var mProfilePicUpdateLiveData: LiveData<ResultWrapper<ProfilePicUpdateResponseModel>>? = null

    init {
        mProfileEditRepository = ProfileEditRepository(mApplication)
        mProfilePicUpdateLiveData = mProfileEditRepository?.getProfilePicUpdateResponseData()
    }

    fun callProfilePicUpdateAPI(profilePicUpdateRequestModel: ProfilePicUpdateRequestModel) {
        mProfileEditRepository?.callProfilePicUpdateAPI(profilePicUpdateRequestModel)
    }
}

class ProfileEditViewModelFactory(private val application: HFKApplication) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileEditViewModel(application) as T
    }
}