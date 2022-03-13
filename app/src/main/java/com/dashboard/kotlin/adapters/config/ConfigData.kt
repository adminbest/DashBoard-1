package com.dashboard.kotlin.adapters.config

import android.util.Log
import androidx.annotation.Keep
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ConfigYaml(
    val proxies: @Contextual Any?,
    val `proxy-providers`: MutableMap<String, Proxy> = mutableMapOf(),
    val `proxy-groups`: MutableList<Group> = mutableListOf(),
    val `rule-providers`: @Contextual Any? = null,
    val rules: @Contextual Any? = null
){
    init {
        Log.d("TEST", "$this")
    }

    @Serializable
    data class Proxy(
        val type: String,
        var url: String= "",
        val path: String,
        val interval: String,
        val `health-check`: HealthCheck?
    ){
        @Serializable
        data class HealthCheck(
            val enable: Boolean,
            val url: String,
            val interval: String
        )
    }

    @Serializable
    data class Group(
        val name: String,
        val type: String,
        val proxies: MutableList<String> = mutableListOf(),
        val use: MutableList<String> = mutableListOf(),
        val filter: String? = null
    )

    fun addSubscripts(name: String, url: String){
        if (name.replace(" ", "").isEmpty()) return
        `proxy-providers`[name] = Proxy(
            if (url.isNotEmpty()) "http" else "file",
            url,
            "./proxy_providers/$name.yaml",
            "3600",
            null
        )

        var flag = false
        `proxy-groups`.forEach {
            when (it.name) {
                "Proxy" -> {
                    it.proxies.add(name)
                }
                name -> {
                    flag = true
                    if (name !in it.use){
                        it.use.add(name)
                    }
                }
            }
        }
        if (!flag){
            `proxy-groups`.add(
                Group(
                    name,
                    "select",
                    mutableListOf(),
                    mutableListOf(name)
                )
            )
        }
    }

    fun deleteSubscript(name: String){
        if (name.replace(" ", "") == "") return
        `proxy-providers`.remove(name)

        var group: Group? =null
        `proxy-groups`.forEach {
            when (it.name) {
                "Proxy" -> {
                    it.proxies.remove(name)
                }
                name ->
                    group = it
            }
        }
        `proxy-groups`.remove(group)

    }
}