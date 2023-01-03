package com.example.mapcapturetest

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.content.res.AppCompatResources
import com.example.mapcapturetest.databinding.ActivityCountDownBinding

class CountDownActivity : BindingActivity<ActivityCountDownBinding>(R.layout.activity_count_down) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val anim = AnimationUtils.loadAnimation(this, R.anim.count_anim)
//        val numList = arrayListOf(
//            AppCompatResources.getDrawable(this, R.drawable.num1),
//            AppCompatResources.getDrawable(this, R.drawable.num2),
//            AppCompatResources.getDrawable(this, R.drawable.num3)
//        )
//        for (i in numList) {
//            binding.ivCount.setImageDrawable(i)
//            binding.ivCount.startAnimation(anim)
//        }
    }
}