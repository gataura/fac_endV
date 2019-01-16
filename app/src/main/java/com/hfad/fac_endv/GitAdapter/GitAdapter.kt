package com.hfad.fac_endv.GitAdapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hfad.fac_endv.R
import com.hfad.fac_endv.R.id.parent
import com.hfad.fac_endv.api.model.GitHubRepo
import kotlinx.android.synthetic.main.git_item_view.view.*

@Suppress("DEPRECATION")
class GitAdapter(var values: List<GitHubRepo>): RecyclerView.Adapter<GitAdapter.GitViewHolder>() {


    class GitViewHolder(itemView: View) : ViewHolder(itemView) {

        var gitText: TextView = itemView.findViewById(R.id.item_text)

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): GitViewHolder {

        val view: View = LayoutInflater.from(p0.context).inflate(R.layout.git_item_view, p0, false)
        return GitViewHolder(view)
    }


    override fun onBindViewHolder(p0: GitViewHolder, p1: Int) {

        val repoText: GitHubRepo = values[p1]
        p0.gitText.text = repoText.getName()

    }

    override fun getItemCount(): Int {
        return values.size
    }

}