package com.esoftwere.hfk.ui.add_product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.esoftwere.hfk.R
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.databinding.FragmentAddProductSecondStepBinding
import com.esoftwere.hfk.model.add_product.AddProductRequestModel
import com.esoftwere.hfk.utils.AndroidUtility
import com.esoftwere.hfk.utils.ValidationHelper

class AddProductSecondStepFrag : Fragment() {
    private lateinit var binding: FragmentAddProductSecondStepBinding

    private val addProductActivity: AddProductActivity by lazy { activity as AddProductActivity }

    private var mSelectedProductQualityType: String = ""
    private var mProductVideoLink: String = ""
    private var mProductPrice: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_product_second_step, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initToolbarTitle()
        initListeners()
    }

    private fun getAddProductRequestModel(): AddProductRequestModel? = arguments?.getParcelable<AddProductRequestModel>(AppConstants.KEY_PREFS_ADD_PRODUCT_ITEM)

    private fun initToolbarTitle() {
        addProductActivity.setToolbarTitle("Product Info")
    }

    private fun initListeners() {
        binding.rgProductQualityType.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_good -> {
                    mSelectedProductQualityType = "Good"
                }

                R.id.rb_better -> {
                    mSelectedProductQualityType = "Better"
                }

                R.id.rb_best -> {
                    mSelectedProductQualityType = "Best"
                }
            }
        }

        binding.btnContinue.setOnClickListener {
            btnContinueClickHandler()
        }
    }

    private fun isSecondStepFormValidated(): Boolean {
        mProductVideoLink = ValidationHelper.optionalBlankText(binding.etInputVideoLink.text.toString())
        mProductPrice = ValidationHelper.optionalBlankText(binding.etInputSellerPrice.text.toString())

        when {
            mProductPrice.isEmpty() -> {
                AndroidUtility.showErrorCustomSnackbar(
                    binding.clSecondStepRoot,
                    getString(R.string.empty_field_validation)
                )
                return false
            }
        }

        return true
    }

    private fun callSecondStepProcedure() {
        if (isSecondStepFormValidated()) {
            /*getAddProductRequestModel()?.let { addProductRequestModel ->
                addProductRequestModel.productQuality = mSelectedProductQualityType
                addProductRequestModel.productVideoLink = mProductVideoLink
                addProductRequestModel.productPrice = mProductPrice

                addProductActivity.startFragment(
                    R.id.fl_addProductContainer,
                    AddProductThirdStepFragment.newInstance(addProductRequestModel)
                )
            }*/
        }
    }

    /**
     * Click Handler
     */
    private fun btnContinueClickHandler() {
        callSecondStepProcedure()
    }

    companion object {
        fun newInstance(addProductRequestModel: AddProductRequestModel): AddProductSecondStepFrag {
            val mAddProductSecondStepFrag = AddProductSecondStepFrag()
            val bundle = Bundle()
            bundle.putParcelable(AppConstants.KEY_PREFS_ADD_PRODUCT_ITEM, addProductRequestModel)
            mAddProductSecondStepFrag.arguments = bundle
            return mAddProductSecondStepFrag
        }
    }
}