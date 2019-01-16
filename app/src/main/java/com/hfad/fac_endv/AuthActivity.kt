@file:Suppress("DEPRECATION")

package com.hfad.fac_endv

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.annotation.RequiresApi
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.hfad.fac_endv.api.model.AccessToken
import com.hfad.fac_endv.api.service.GitHubClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("OverridingDeprecatedMember")
class AuthActivity : AppCompatActivity(), GithubView {

    lateinit var webView: WebView
    private var presenter = GithubPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        presenter.bind(this)

        webView = find(R.id.webView)

        configureWebview()

        webView.loadUrl(Constants.authLink)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unbind()
    }

    override fun onLoad(response: AccessToken?) {
        response?.let {
            presenter.saveToken(this@AuthActivity, response.getAccessToken())
            MainActivity.start(this@AuthActivity)
        }
    }

    override fun onError(t: Throwable?) {
        Toast.makeText(this@AuthActivity, t?.message, Toast.LENGTH_SHORT).show()
    }

    override fun load(code: String?) {
        presenter.load(code)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebview() {
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                when {
                    url.contains("code=") -> {
                        load(url)
                    }
                    url == Constants.authLink -> view?.loadUrl(url)
                    else -> {
                        view?.loadUrl(url)
                    }
                }
                return true
            }
            @RequiresApi(Build.VERSION_CODES.N)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean {
                when {
                    request.url.toString().contains("code=") -> {
                        load(request.url.toString())
                    }
                    request.url.toString() == Constants.authLink -> view?.loadUrl(request.url.toString())
                    else -> {
                        view?.loadUrl(request.url.toString())
                    }
                }
                return true
            }
        }
    }
}

object Constants {
    var clientId = "34f18331adf3a2ecc7b3"
    var clientSecret = "ffca5fd9a9913aef40b2fed4ac0e319156e7485d"
    var redirectUri = "fac://callback"
    var githubClientUrl = "https://github.com/"
    var githubApiUrl = "https://api.github.com/"
    var authLink = "https://github.com/login/oauth/authorize?client_id=${Constants.clientId}&scope=repo&redirect_uri=${Constants.redirectUri}"
}

interface BaseView {}

abstract class BasePresenter<T: BaseView> {
    var view: T? = null

    fun bind(view: T) {
        this.view = view
    }

    fun unbind() {
        this.view = null
    }
}

interface IGithubPresenter {
    fun load(code: String?)
    fun saveToken(context: Context, token: String)
    fun getToken(context: Context): String?
}

interface GithubView: BaseView {
    fun load(code: String?)
    fun onLoad(response: AccessToken?)
    fun onError(t: Throwable?)
}

class GithubPresenter: BasePresenter<GithubView>(), IGithubPresenter {

    private var prefs: myPrefs? = null

    override fun saveToken(context: Context, token: String) {
        if (prefs == null) {
            prefs = myPrefs(context)
        }
        prefs?.setToken(token)
    }

    override fun getToken(context: Context): String? {
        if (prefs == null) {
            prefs = myPrefs(context)
        }
        return prefs?.getToken()
    }

    override fun load(code: String?) {
        code?.let {
            NetManager.auth(it.split("code=")[1]).enqueue(object : Callback<AccessToken> {
                override fun onResponse(call: Call<AccessToken>?, response: Response<AccessToken>?) {
                    if(response?.body() != null) {
                        view?.onLoad(response.body())
                    } else {
                        view?.onError(Throwable("не то чет"))
                    }
                }
                override fun onFailure(call: Call<AccessToken>?, t: Throwable?) {
                    view?.onError(t)
                }
            })
        }
    }
}

class mGithubClient {

    private val builder = Retrofit
            .Builder()
            .baseUrl(Constants.githubClientUrl)
            .addConverterFactory(GsonConverterFactory.create())

    private val retrofit: Retrofit by lazy {
        builder.build()
    }

    private val client: GitHubClient by lazy {
        retrofit.create(GitHubClient::class.java)
    }

    fun build() = client
}

class myPrefs(val context: Context) {

    private var prefs = PreferenceManager.getDefaultSharedPreferences(context)

    private var editor: SharedPreferences.Editor? = prefs.edit()
        @SuppressLint("CommitPrefEdits")
        get() = prefs.edit()

    enum class KEYS {
        TOKEN
    }

    fun setToken(token: String) {
        editor?.putString(KEYS.TOKEN.name, token)?.apply()
    }
    fun getToken() = prefs.getString(KEYS.TOKEN.name, "")
}

object NetManager {

    private val client = mGithubClient().build()

    fun auth(code: String) = client.getAccessToken(
            Constants.clientId,
            Constants.clientSecret,
            code)

}

