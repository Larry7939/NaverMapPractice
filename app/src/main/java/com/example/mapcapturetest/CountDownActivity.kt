package com.example.mapcapturetest

import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.appcompat.content.res.AppCompatResources
import com.example.mapcapturetest.databinding.ActivityCountDownBinding


class CountDownActivity : BindingActivity<ActivityCountDownBinding>(R.layout.activity_count_down) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val numList = arrayListOf(
            AppCompatResources.getDrawable(this, R.drawable.num1),
            AppCompatResources.getDrawable(this, R.drawable.num2),
            AppCompatResources.getDrawable(this, R.drawable.num3)
        )
        val anim = AnimationUtils.loadAnimation(this, R.anim.count_anim)
        var counter = -1
        anim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                if (counter < 2) {
                    counter += 1
                    Log.d("counter",counter.toString())
                    binding.isCount.setImageDrawable(numList[counter])
                    binding.isCount.startAnimation(animation)
                }
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })
        binding.isCount.startAnimation(anim)


    }

}
