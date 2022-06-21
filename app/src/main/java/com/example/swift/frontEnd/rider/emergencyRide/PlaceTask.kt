package com.example.swift.frontEnd.rider.emergencyRide

import android.os.AsyncTask
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.URL

class PlaceTask: AsyncTask<String, Int, String>() {
    override fun doInBackground(vararg p0: String?): String? {
        var data: String? = null
        try {
            data = downloadUrl(p0[0])
        }catch (e:IOException){
            e.printStackTrace()
        }
        return data
    }

    override fun onPostExecute(result: String?) {
        ParserTask().execute(result)
    }

    private fun downloadUrl(p0: String?): String {
        var data = ""
        try {
            var url = URL(p0)
            var connection = url.openConnection()
            connection.connect()
            var stream = connection.getInputStream()
            var reader = BufferedReader(InputStreamReader(stream))
            var builder = StringBuilder()
            var line: String? = ""

            while (line != null) {
                line = reader.readLine()
                if (line != null) {
                    builder.append(line)
                }
            }
            data = builder.toString()
            reader.close()
        }catch (e:IOException){
            e.stackTrace
        }

        return data
    }

}