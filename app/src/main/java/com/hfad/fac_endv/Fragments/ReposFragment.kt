package com.hfad.fac_endv.Fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hfad.fac_endv.Constants
import com.hfad.fac_endv.GitAdapter.GitAdapter

import com.hfad.fac_endv.R
import com.hfad.fac_endv.api.model.GitHubRepo
import com.hfad.fac_endv.api.service.GitHubClient
import com.hfad.fac_endv.mGithubClient
import com.hfad.fac_endv.myPrefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ReposFragment : Fragment() {

    private lateinit var myToken:String
    private var sp: myPrefs? = null
    private val client = mGithubClient().build()
    var repos = mutableListOf<GitHubRepo>()
    private lateinit var gitResyclerView: RecyclerView
    private lateinit var gitAdapter: GitAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var LOG = "LOG"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_repos, container, false)


        gitResyclerView = view.findViewById(R.id.git_recycler_view)
        layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false)
        gitResyclerView.layoutManager = layoutManager
        gitAdapter = GitAdapter(repos)
        gitResyclerView.adapter = gitAdapter

        sp = myPrefs(this.requireContext())

        myToken = sp?.getToken().toString()

        getRepos(myToken).enqueue(object: Callback<List<GitHubRepo>> {
            override fun onResponse(call: Call<List<GitHubRepo>>, response: Response<List<GitHubRepo>>) {
                var jsonString = response.body().toString()
                repos.clear()
                repos.addAll(response.body())
                gitAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<GitHubRepo>>?, t: Throwable?) {

            }

        })

        return view
    }

    private fun getRepos(token: String) = client.reposForToken(token)

    class mGithubClient {

        private val builder = Retrofit
                .Builder()
                .baseUrl(Constants.githubApiUrl)
                .addConverterFactory(GsonConverterFactory.create())

        private val retrofit: Retrofit by lazy {
            builder.build()
        }

        private val client: GitHubClient by lazy {
            retrofit.create(GitHubClient::class.java)
        }

        fun build() = client
    }


}
