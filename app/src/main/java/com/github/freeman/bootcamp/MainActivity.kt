package com.github.freeman.bootcamp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val tag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun fetchData(view: View) {
        Log.d(tag, "fetchData entered")
        val fetchedActivity = findViewById<TextView>(R.id.fetchedActivity)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.boredapi.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val boredApi = retrofit.create(BoredApi::class.java)

        Log.d(tag, "Starting boredApi.getActivity().enqueue part")
        boredApi.getActivity().enqueue(object : Callback<BoredActivity> {
            override fun onResponse(call: Call<BoredActivity>, response: Response<BoredActivity>) {
                Log.d(tag, "Entered the onResponse function")
                if (response.isSuccessful) {
                    // Displays the fetched data in a textView
                    fetchedActivity.text = response.body()?.activity.toString()
                }
            }

            override fun onFailure(call: Call<BoredActivity>, t: Throwable) {
                // Handle the error
                Log.d(tag, "Entered the onFailure function. t.message: " + t.message +
                        "\n t.cause: " + t.cause + "\n stack trace: " + t.stackTraceToString())
                fetchedActivity.text = "onFailure: not implemented yet."
            }
        })
    }
}