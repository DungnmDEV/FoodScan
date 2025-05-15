package com.base.app.baseapp.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding
import com.base.app.baseapp.utils.AndroidBug5497Workaround
import com.base.app.baseapp.utils.PreferenceHelper
import java.util.Locale

@Suppress("DEPRECATION")
abstract class BaseActivity<VB : ViewBinding>(private val bindingInflater: (LayoutInflater) -> VB) :
    AppCompatActivity() {
    private lateinit var _binding: VB
    protected val binding get() = _binding
    protected lateinit var preferenceHelper: PreferenceHelper
    protected val TAG = "TAG123"

    private lateinit var dialogLoading: Dialog

    protected abstract fun initView()

    private var isKeyboardShowing = false


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingInflater(layoutInflater)
        preferenceHelper = PreferenceHelper.getInstance(applicationContext)
        setContentView(_binding.root)
        updateLanguage(this)
        setupStatusBar()
        initView()

        binding.root.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val currentFocusView = currentFocus
                if (currentFocusView is EditText) {
                    currentFocusView.clearFocus()
                    hideKeyboard(currentFocusView)
                }
            }
            false
        }

        setupEditTextFocusListener(binding.root as ViewGroup)

        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            binding.root.getWindowVisibleDisplayFrame(r)
            val screenHeight = binding.root.getRootView().height
            var keypadHeight = screenHeight - r.bottom

            if (keypadHeight > screenHeight * 0.15) {
                if (!isKeyboardShowing) {
                    isKeyboardShowing = true
                    setupUIonKeyBoardShow(true)
                }
            } else {
                if (isKeyboardShowing) {
                    isKeyboardShowing = false
                    setupUIonKeyBoardShow(false)
                    window.decorView.clearFocus()
                }
            }
        }
    }

    private fun setupEditTextFocusListener(rootView: ViewGroup) {
        for (i in 0 until rootView.childCount) {
            val child = rootView.getChildAt(i)
            if (child is EditText) {
                child.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        val textLength = child.length()
                        child.post {
                            child.setSelection(textLength)
                        }
                    }
                    setupUIonKeyBoardShow(hasFocus)
                }

                child.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        hideKeyboard(child)
                        true
                    } else {
                        false
                    }
                }
            } else if (child is ViewGroup) {
                setupEditTextFocusListener(child)  // Đệ quy cho ViewGroup con
            } else {
                child.setOnFocusChangeListener { _, _ ->
                    setupUIonKeyBoardShow(false)
                }
            }
        }
    }

    private fun setupUIonKeyBoardShow(isShow: Boolean) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            WindowCompat.setDecorFitsSystemWindows(window, isShow)
            if (isShow) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                }
                window.decorView.apply {
                    systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }

                val params = binding.root.layoutParams as ViewGroup.MarginLayoutParams
                params.topMargin = -getStatusBarHeight()
                binding.root.layoutParams = params

                window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    )
                }
                val params = binding.root.layoutParams as ViewGroup.MarginLayoutParams
                params.topMargin = 0
                binding.root.layoutParams = params
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                setupStatusBar()
            }
        } else {
            AndroidBug5497Workaround.assistActivity(this)
        }
    }

    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    fun getStatusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    override fun attachBaseContext(newBase: Context) {
        preferenceHelper = PreferenceHelper.getInstance(newBase)
        val context = updateLanguage(newBase)
        super.attachBaseContext(context)
    }

    private fun updateLanguage(context: Context): Context {
        val config = Configuration()
        val locale = Locale(preferenceHelper.languageCode)
        locale.let { Locale.setDefault(it) }
        config.locale = locale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        return context
    }

    open fun setupStatusBar() {
        val decorView = window.decorView
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        val layoutParams = window.attributes
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            layoutParams.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        window.attributes = layoutParams
        val windowInsetsController = WindowCompat.getInsetsController(window, decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        window.statusBarColor = Color.TRANSPARENT
    }


    override fun onResume() {
        super.onResume()
        setupStatusBar()
    }


}
