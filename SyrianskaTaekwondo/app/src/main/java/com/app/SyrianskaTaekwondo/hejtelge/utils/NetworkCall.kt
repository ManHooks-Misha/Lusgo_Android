package com.app.SyrianskaTaekwondo.hejtelge.utils

import android.content.Context
import android.view.View
import okhttp3.RequestBody

import vk.help.network.NetworkRequest
import vk.help.network.ResultsListener

class NetworkCall @JvmOverloads constructor(
    private val progressHelp: Any? = null,
    listener: ResultsListener,
    requestJSON: String = "",
    requestBody: RequestBody? = null,
    requestMap: HashMap<String, String>? = null
) : NetworkRequest(listener, requestJSON, requestBody, requestMap) {

    override fun onPreExecute() {
        if (progressHelp != null) {
            if (progressHelp is Context) {
                HelpingClass.showProgress(progressHelp)
            } else if (progressHelp is View) {
                progressHelp.visibility = View.VISIBLE
            }
        }
        super.onPreExecute()
        setMediaType("application/json")
        val map=HashMap<String,String>()
        map["Accept-Encoding"]="identity"//add here
        setHeader(map)
    }

    override fun onPostExecute(output_: String) {
        if (progressHelp != null) {
            if (progressHelp is Context) {
                HelpingClass.hideProgress()
            } else if (progressHelp is View) {
                progressHelp.visibility = View.GONE
            }
        }
        super.onPostExecute(output_)
    }

}