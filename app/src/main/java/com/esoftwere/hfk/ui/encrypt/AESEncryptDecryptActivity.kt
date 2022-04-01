package com.esoftwere.hfk.ui.encrypt

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.esoftwere.hfk.R

class AESEncryptDecryptActivity : AppCompatActivity() {
    private lateinit var mContext: Context
    private lateinit var mAESEncryptDecrypt: AESEncryptDecrypt

    private val TAG: String = "AESEncryptDecrypt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aesencrypt_decrypt)

        initVariable()
        initAESEncryptDecrypt()
    }

    private fun initVariable() {
        mContext = this@AESEncryptDecryptActivity
        mAESEncryptDecrypt = AESEncryptDecrypt()
    }

    private fun initAESEncryptDecrypt() {
        val stringToEncrypt: String = "Welcome To AES Encryption"
        val encryptedString = mAESEncryptDecrypt.encryptCipherString(mContext, stringToEncrypt)
        val encryptedByteArray = mAESEncryptDecrypt.encrypt(mContext, stringToEncrypt)
        val decryptedString = mAESEncryptDecrypt.decryptCipherString(mContext, encryptedByteArray)

        Log.e(TAG, "EncryptedString: $encryptedString")
        Log.e(TAG, "DecryptedString: $decryptedString")
    }
}