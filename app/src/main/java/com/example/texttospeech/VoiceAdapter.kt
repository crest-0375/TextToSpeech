package com.example.texttospeech

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.speech.tts.Voice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.texttospeech.databinding.ListItemVoiceBinding


class VoiceAdapter(private val mContext: Context, private val mDataSource: List<Voice>) :
    BaseAdapter() {

    override fun getCount(): Int {
        return mDataSource.size
    }

    override fun getItem(position: Int): Any {
        return mDataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val binding = ListItemVoiceBinding.inflate(LayoutInflater.from(mContext), parent, false)
        if (position % 2 == 0) {
            binding.root.setBackgroundColor(Color.rgb(245, 245, 245))
        }
        val voiceUnderScrutiny = mDataSource[position]
        binding.voiceTitle.text = "VOICE NAME: " + voiceUnderScrutiny.name
        binding.voiceLang.text = "VOICE LANG: " + voiceUnderScrutiny.locale.language

        binding.voiceQuality.text = "QLTY: " + voiceUnderScrutiny.quality.toString()
        if (voiceUnderScrutiny.quality == 500) {
            binding.voiceQuality.setTextColor(Color.GREEN) // set v. high quality to green
        }
        if (!voiceUnderScrutiny.isNetworkConnectionRequired) {
            binding.voiceNetwork.text = "NET_REQ?: NO"
        } else {
            binding.voiceNetwork.text = "NET_REQ?: YES"
        }
        if (!voiceUnderScrutiny.features.contains("notInstalled")) {
            binding.voiceInstalled.setTextColor(Color.GREEN)
            binding.voiceInstalled.text = "INSTLLD?: YES"
        } else {
            binding.voiceInstalled.setTextColor(Color.RED)
            binding.voiceInstalled.text = "INSTLLD?: NO"
        }
        binding.voiceFeatures.text = "FEATURES: " + voiceUnderScrutiny.features.toString()
        return binding.root
    }


}