package com.adv.ilook.model.data.firebasemodel

import com.google.gson.annotations.SerializedName

data class Response(

	@field:SerializedName("app")
	val app: App? = null
)

data class JsonMember9988776655(

	@field:SerializedName("configure")
	val configure: Configure? = null
)

data class Users(

	@field:SerializedName("9988776655")
	val jsonMember9988776655: JsonMember9988776655? = null
)

data class Configure(

	@field:SerializedName("isConfigured")
	val isConfigured: Boolean? = null,

	@field:SerializedName("hardwareId")
	val hardwareId: Boolean? = null,

	@field:SerializedName("isDeviceNameEdit")
	val isDeviceNameEdit: Boolean? = null,

	@field:SerializedName("deviceName")
	val deviceName: String? = null,

	@field:SerializedName("wayOfSwitch")
	val wayOfSwitch: String? = null
)

data class App(

	@field:SerializedName("users")
	val users: Users? = null
)
