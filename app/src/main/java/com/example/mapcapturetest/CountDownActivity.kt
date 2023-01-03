package com.example.mapcapturetest

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import com.example.mapcapturetest.databinding.ActivityCountDownBinding


class CountDownActivity : BindingActivity<ActivityCountDownBinding>(R.layout.activity_count_down) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val anim = AnimationUtils.loadAnimation(this, R.anim.count_anim)
        val imageSwitcher = binding.isCount
        imageSwitcher.animation = anim
        val numList = arrayListOf(
            AppCompatResources.getDrawable(this, R.drawable.num1),
            AppCompatResources.getDrawable(this, R.drawable.num2),
            AppCompatResources.getDrawable(this, R.drawable.num3)
        )
//        var animCounter = 0
//        val imageSwitcherHandler = Handler(Looper.getMainLooper())
//        val runnable = Runnable {
//            runCatching {
//                Log.d("counter", "${animCounter}")
//                animCounter += 1
//                when (animCounter) {
//                    1 -> {
//                        imageSwitcher.setImageDrawable(numList[0])
//                        imageSwitcher.startAnimation(anim)
//                        Log.d("1", "1")
//                    }
//                    2 -> {
//                        imageSwitcher.setImageDrawable(numList[1])
//                        imageSwitcher.startAnimation(anim)
//                        Log.d("2", "2")
//                    }
//                    4 -> {
//                        imageSwitcher.setImageDrawable(numList[2])
//                        imageSwitcher.startAnimation(anim)
//                        Log.d("3", "3")
//                    }
//                    else -> {
//                        imageSwitcher.setImageDrawable(numList[2])
//                        imageSwitcher.startAnimation(anim)
//                        Log.d("4", "4")
//                    }
//                }
//            }
//        }
//        for (i in 0..3) {
//            imageSwitcherHandler.postDelayed(runnable, 1000)
//        }
        for (i in numList){
            imageViewAnimatedChange(this,binding.isCount,i)
        }
    }

    fun imageViewAnimatedChange(c: Context?, v: ImageView, new_image: Drawable?) {
        val anim = AnimationUtils.loadAnimation(c, R.anim.count_anim)
        anim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                v.setImageDrawable(new_image)
            }
        })
        v.startAnimation(anim)
    }
}
