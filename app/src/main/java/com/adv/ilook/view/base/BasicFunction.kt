package com.adv.ilook.view.base

import android.content.Context
import android.util.Log
import com.adv.ilook.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject

class BasicFunction @Inject constructor(context: Context?) {
    var bufferReader: BufferedReader? = null
    var builderContents: StringBuilder = StringBuilder()
    private val TAG = "BasicFunction"
    fun getFileFromAsset(fileName: String, context: Context?): String {

        try {
            if (context != null) {
                bufferReader = BufferedReader(InputStreamReader(context.assets.open(fileName)))
                // do reading, usually loop until end of file reading
                var mLine: String?
                while (bufferReader!!.readLine().also { mLine = it } != null) {
                    //process line
                    builderContents.append(mLine)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, "getFileFromAsset:1 " + e.message)
            //log the exception
        } finally {
            try {
                bufferReader?.close()
            } catch (e: IOException) {
                //log the exception
                Log.e(TAG, "getFileFromAsset:2 " + e.message)
            }
        }

        return builderContents.toString()
    }


    fun removeWhiteSpace(strings: String, function: (String) -> Unit): String {
        val pattern = "\\s+".toRegex()
        val res = strings.replace(pattern, "")
        function(res)
        return res
    }

    object Fun {
        fun spiltContainsComma(
            delimeter: Char,
            strings: String,
            function: (List<String>) -> Unit
        ): List<String> {

            val res = strings.split(delimeter).map { it.trim() }
            function(res)
            return res
        }
    }

    companion object Screens {
        private var scr = hashMapOf<String, Any>()
        fun getScreens(): HashMap<String, Any> {
            //forward
            scr["splash_to_splash"] = R.id.action_splashFragment_self
            scr["splash_to_login_screen"] = R.id.action_splashFragment_to_loginFragment
            scr["login_to_home_screen"] = R.id.action_splashFragment_to_loginFragment
            scr["splash_to_select_screen_type"] = R.id.action_splashFragment_to_selectScreenFragment

           //backward
            scr["null"] = 0
            scr["finish"] = -1


            return scr
        }
    }
}