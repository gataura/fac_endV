package com.hfad.fac_endv.api.model

import com.google.gson.annotations.SerializedName

class AccessToken {

    @SerializedName("access_token")
    private var accessToken:String = ""

    @SerializedName("token_type")
    private var tokenType:String = ""

    fun getAccessToken(): String {
        return accessToken
    }

    fun getTokenType(): String {
        return tokenType
    }
}