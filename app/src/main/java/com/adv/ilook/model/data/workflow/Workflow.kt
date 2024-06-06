package com.adv.ilook.model.data.workflow

import com.google.gson.annotations.SerializedName

data class Workflow(

	@field:SerializedName("screens")
	val screens: Screens? = null,

	@field:SerializedName("mqtt")
	val mqtt: Mqtt? = null
)

data class UserName(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("helper_text")
	val helperText: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class SplashScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<Any?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null,

	@field:SerializedName("views")
	val views: List<ViewsItem?>? = null
)

data class GuideBtn(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class LoginScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<SelectScreenItem?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null,

	@field:SerializedName("properties")
	val properties: Properties? = null,

	@field:SerializedName("views")
	val views: Views? = null
)

data class Validation(

	@field:SerializedName("enable")
	val enable: Boolean? = null
)

data class Screens(

	@field:SerializedName("commodity_classification_screen")
	val commodityClassificationScreen: CommodityClassificationScreen? = null,

	@field:SerializedName("select_screen_type")
	val selectScreenType: SelectScreenType? = null,

	@field:SerializedName("voice_translator_screen")
	val voiceTranslatorScreen: VoiceTranslatorScreen? = null,

	@field:SerializedName("see_for_me_screen")
	val seeForMeScreen: SeeForMeScreen? = null,

	@field:SerializedName("output_screen")
	val outputScreen: OutputScreen? = null,

	@field:SerializedName("logout_screen")
	val logoutScreen: LogoutScreen? = null,

	@field:SerializedName("login_screen")
	val loginScreen: LoginScreen? = null,

	@field:SerializedName("otp_screen")
	val otpScreen: OtpScreen? = null,

	@field:SerializedName("text_to_speech_screen")
	val textToSpeechScreen: TextToSpeechScreen? = null,

	@field:SerializedName("splash_screen")
	val splashScreen: SplashScreen? = null,

	@field:SerializedName("device_control_screen")
	val deviceControlScreen: DeviceControlScreen? = null,

	@field:SerializedName("object_detection_screen")
	val objectDetectionScreen: ObjectDetectionScreen? = null,

	@field:SerializedName("home_screen")
	val homeScreen: HomeScreen? = null,

	@field:SerializedName("instruction_screen")
	val instructionScreen: InstructionScreen? = null
)

data class SelectScreenType(

	@field:SerializedName("select_screen")
	val selectScreen: List<SelectScreenItem?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null,

	@field:SerializedName("views")
	val views: Views? = null
)

data class SeeForMeScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<Any?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null
)

data class TextView(

	@field:SerializedName("vi_mode_text")
	val viModeText: ViModeText? = null,

	@field:SerializedName("guide_mode_text")
	val guideModeText: GuideModeText? = null,

	@field:SerializedName("header")
	val header: Header? = null,

	@field:SerializedName("otp_code")
	val otpCode: OtpCode? = null,

	@field:SerializedName("user_name")
	val userName: UserName? = null,

	@field:SerializedName("mobile_number")
	val mobileNumber: MobileNumber? = null
)

data class Image(

	@field:SerializedName("background_image")
	val backgroundImage: String? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null
)

data class Mqtt(

	@field:SerializedName("enable")
	val enable: Boolean? = null
)

data class Icon(

	@field:SerializedName("color")
	val color: String? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("width")
	val width: Int? = null,

	@field:SerializedName("url")
	val url: String? = null,

	@field:SerializedName("height")
	val height: Int? = null
)

data class GenerateOtp(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class HomeScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<SelectScreenItem?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null
)

data class MobileNumber(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("helper_text")
	val helperText: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class LeftIcon(

	@field:SerializedName("color")
	val color: String? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("width")
	val width: Int? = null,

	@field:SerializedName("url")
	val url: String? = null,

	@field:SerializedName("height")
	val height: Int? = null
)

data class TextToSpeechScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<Any?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null
)

data class Color(

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("secondary_color")
	val secondaryColor: String? = null,

	@field:SerializedName("primary_color")
	val primaryColor: String? = null
)

data class Login(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class OtpCode(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("helper_text")
	val helperText: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class Header(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class Properties(

	@field:SerializedName("image")
	val image: Image? = null,

	@field:SerializedName("color")
	val color: Color? = null
)

data class DeviceControlScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<Any?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null
)

data class ObjectDetectionScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<Any?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null
)

data class VoiceTranslatorScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<Any?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null
)

data class CommodityClassificationScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<Any?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null
)

data class SelectScreenItem(

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null,

	@field:SerializedName("select_screen")
	val selectScreen: List<Any?>? = null
)

data class ButtonView(

	@field:SerializedName("vi_btn")
	val viBtn: ViBtn? = null,

	@field:SerializedName("guide_btn")
	val guideBtn: GuideBtn? = null,

	@field:SerializedName("generate_otp")
	val generateOtp: GenerateOtp? = null,

	@field:SerializedName("verify_otp")
	val verifyOtp: VerifyOtp? = null,

	@field:SerializedName("login")
	val login: Login? = null
)

data class OtpScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<SelectScreenItem?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null,

	@field:SerializedName("properties")
	val properties: Properties? = null,

	@field:SerializedName("views")
	val views: Views? = null
)

data class InstructionScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<SelectScreenItem?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null,

	@field:SerializedName("views")
	val views: Views? = null
)

data class RightIcon(

	@field:SerializedName("color")
	val color: String? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("width")
	val width: Int? = null,

	@field:SerializedName("url")
	val url: String? = null,

	@field:SerializedName("height")
	val height: Int? = null
)

data class ViewsItem(

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("view_type")
	val viewType: String? = null
)

data class ViBtn(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class Views(

	@field:SerializedName("button_view")
	val buttonView: ButtonView? = null,

	@field:SerializedName("image_view")
	val imageView: ImageView? = null,

	@field:SerializedName("text_view")
	val textView: TextView? = null
)

data class ViModeText(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("helper_text")
	val helperText: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class LogoutScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<Any?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null
)

data class VerifyOtp(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class ImageView(

	@field:SerializedName("icon")
	val icon: Icon? = null
)

data class GuideModeText(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("helper_text")
	val helperText: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class OutputScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<Any?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null
)
