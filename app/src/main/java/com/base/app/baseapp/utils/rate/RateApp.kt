package com.base.app.baseapp.utils.rate

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import com.base.app.baseapp.R
import com.base.app.baseapp.databinding.DialogRateAppBinding
import com.base.app.baseapp.utils.Utils.rateApp
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory

@Suppress("DEPRECATION")
class RateApp(private val activity: Activity) {

    private lateinit var reviewManagerInstance: ReviewManager
    private var reviewInfoInstance: ReviewInfo? = null
    private lateinit var dialog: Dialog
    private var onFinishRate: () -> Unit = {}
    private var onDismissCallback: () -> Unit = {}

    fun showDialog() {
        reviewManagerInstance = ReviewManagerFactory.create(activity)
        reviewManagerInstance.requestReviewFlow().addOnCompleteListener {
            if (it.isSuccessful)
                reviewInfoInstance = it.result
        }
        dialog = Dialog(activity)
        val binding = DialogRateAppBinding.inflate(activity.layoutInflater)
        dialog.setContentView(binding.root)
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        binding.ratingBar.setOnRatingChangeListener { _, rating, _ ->
            when (rating) {
                0f -> {
                    binding.imgIcon.setImageResource(R.drawable.rate_0)
                    binding.tvTitle.text = activity.getText(R.string.do_you_like_the_app)
                    binding.tvContent.text = activity.getText(R.string.content_rate_0)
                    binding.imgBg.imageTintList = activity.getColorStateList(R.color.black13192C)
                }

                1f -> {
                    binding.imgIcon.setImageResource(R.drawable.rate_1)
                    binding.tvTitle.text = activity.getText(R.string.oh_no)
                    binding.tvContent.text = activity.getText(R.string.content_rate_1)
                    binding.imgBg.imageTintList = null
                }

                2f -> {
                    binding.imgIcon.setImageResource(R.drawable.rate_2)
                    binding.tvTitle.text = activity.getText(R.string.oh_no)
                    binding.tvContent.text = activity.getText(R.string.content_rate_2)
                    binding.imgBg.imageTintList = null
                }

                3f -> {
                    binding.imgIcon.setImageResource(R.drawable.rate_3)
                    binding.tvTitle.text = activity.getText(R.string.oh_no)
                    binding.tvContent.text = activity.getText(R.string.content_rate_3)
                    binding.imgBg.imageTintList = null
                }

                4f -> {
                    binding.imgIcon.setImageResource(R.drawable.rate_4)
                    binding.tvTitle.text = activity.getText(R.string.we_love_you_too)
                    binding.tvContent.text = activity.getText(R.string.content_rate_4)
                    binding.imgBg.imageTintList = null
                }

                5f -> {
                    binding.imgIcon.setImageResource(R.drawable.rate_5)
                    binding.tvTitle.text = activity.getText(R.string.we_love_you_too)
                    binding.tvContent.text = activity.getText(R.string.content_rate_5)
                    binding.imgBg.imageTintList = null
                }
            }
        }

        dialog.setOnDismissListener {
            onDismissCallback.invoke()
        }

        binding.btnRate.setOnClickListener {
            if (binding.ratingBar.rating != 0f) {
                requestReview()
            }
        }

        binding.btnExit.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun requestReview() {
        reviewInfoInstance?.let {
            val flow = reviewManagerInstance.launchReviewFlow(activity, it)
            flow.addOnCompleteListener { _ ->
                onFinishRate()
                dialog.dismiss()
            }
        } ?: run {
            activity.rateApp()
            onFinishRate()
            dialog.dismiss()
        }
    }

    fun setFinishRate(onFinish: () -> Unit) = apply {
        this.onFinishRate = onFinish
    }

    fun setOnDismissCallback(onDismiss: () -> Unit) = apply {
        this.onDismissCallback = onDismiss
    }
}
