package com.example.smartphoneshootingdemo

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MotionEventCompat
import com.example.smartphoneshootingdemo.data.shooting_data.getter_score
import com.example.smartphoneshootingdemo.data.shooting_data.setter_score
import com.example.smartphoneshootingdemo.databinding.ActivityFullscreenBinding
import com.example.viewtest2.ViewDisplay.CircleCanvas
import java.lang.Math.abs
import java.util.*
import kotlin.math.sqrt


//TODO  得分判定 宽度适配
/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity() {

    private var mCircleCanvas //  定义一个画布类
            : CircleCanvas? = null

    private lateinit var binding: ActivityFullscreenBinding
    private lateinit var fullscreenContent: TextView
    private lateinit var fullscreenContentControls: LinearLayout
    private val hideHandler = Handler()

    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            fullscreenContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
        fullscreenContentControls.visibility = View.VISIBLE
    }
    private var isFullscreen: Boolean = false

    private val hideRunnable = Runnable { hide() }

    var currentCirclePosX :Float = 0.toFloat()
    var currentCirclePosY :Float = 0.toFloat()
    var currentCircleLen : Float = 0.toFloat()

    var screenWX = 0
    var screenHY = 0


//    /**
//     * Touch listener to use for in-layout UI controls to delay hiding the
//     * system UI. This is to prevent the jarring behavior of controls going away
//     * while interacting with activity UI.
//     */
//    private val delayHideTouchListener = View.OnTouchListener { view, motionEvent ->
//        when (motionEvent.action) {
//            MotionEvent.ACTION_DOWN -> if (AUTO_HIDE) {
//                delayedHide(AUTO_HIDE_DELAY_MILLIS)
//            }
//            MotionEvent.ACTION_UP -> view.performClick()
//            else -> {
//            }
//        }
//        false
//    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置屏幕为横屏, 设置后会锁定方向

        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isFullscreen = true

        // Set up the user interaction to manually show or hide the system UI.
        fullscreenContent = binding.fullscreenContent
        fullscreenContent.setOnClickListener { toggle() }

        fullscreenContentControls = binding.fullscreenContentControls

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
//        binding.dummyButton.setOnTouchListener(delayHideTouchListener)

        val viewGroup = layoutInflater.inflate(R.layout.activity_fullscreen, null) as ViewGroup
        mCircleCanvas = CircleCanvas(this) //  创建CircleCanvas（画布类）对象
        //  将CircleCanvas对象添加到当前界面的视图中（两个按钮的下方）
        viewGroup.addView(
            mCircleCanvas,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT)
        )
        getScreenWH(this)
        setContentView(viewGroup)


    }
    fun getScreenWH(context: Context) {//TODO 设好宽高默认值或者调好从这个函数
        var displayMetrics = DisplayMetrics()
        //获取windowManager的方式，如果是activity，则可以通过activity.windowManager直接取得，即上边获取dpi中的那样
        var windowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenWX = displayMetrics.widthPixels
        screenHY = displayMetrics.heightPixels
//        Log.e("---", "-----w:$screenW==h:$screenH");
    }


    //  开始随机绘制圆形
    fun DrawRandomCircle() {
        var edge = 20
        val random = Random()
        val randomX = (edge + random.nextInt(screenWX - 2* edge)).toFloat() //  随机生成圆心横坐标（100至200）
        val randomY = (edge + random.nextInt(screenHY - 10* edge).toFloat() )//  随机生成圆心纵坐标（100至200）
        val randomRadius = (40 + random.nextInt(100)).toFloat() //  随机生成圆的半径（20至60）
        var randomColor = 0

        

//        Toast.makeText(this@FullscreenActivity, "CLICKED $randomX, $randomY", Toast.LENGTH_SHORT).show()
        //  产生0至100的随机数，若产生的随机数大于50，则画笔颜色为蓝色
        randomColor = if (random.nextInt(100) > 50) {
            Color.BLUE
        } else {
//  产生0至100的随机数，若产生的随机数大于50，则画笔颜色为红色
            if (random.nextInt(100) > 50) Color.RED else Color.GREEN
        }

        val circleInfo = CircleCanvas.CircleInfo()
        circleInfo.x = randomX
        circleInfo.y = randomY

        circleInfo.radius = randomRadius
        circleInfo.color = randomColor

        //获取状态栏高度
        val res = resources.getIdentifier("status_bar_height","dimen","android")
        var statusBarHeight = 0
        if (res > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(res)
        }
        //获取actionBar
        val tv =  TypedValue()
        var actionBarHeight = 0
        if (this.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, this.resources.displayMetrics)   //   高度问题感谢CSDN博主  版权声明：本文为CSDN博主「poegeeon」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。 原文链接：https://blog.csdn.net/poegeeon/article/details/118947225
        }

        currentCirclePosX = circleInfo.x
        currentCirclePosY = circleInfo.y + statusBarHeight
        currentCircleLen = circleInfo.radius

        mCircleCanvas?.mCircleInfos?.add(circleInfo) //  将当前绘制的实心圆信息加到List对象中
        mCircleCanvas!!.invalidate() //  使画布重绘

    }

    //  清空画布（屏幕的单击事件）（清一次画一个）
    @SuppressLint("SetTextI18n", "CutPasteId")
    fun ClearCircle() {//日常清零
        mCircleCanvas?.mCircleInfos?.clear() //  清除绘制历史
        mCircleCanvas!!.invalidate() //  使画布重绘
//        val background_score: TextView = findViewById<TextView>(R.id.fullscreen_content)
//        background_score.text = "上次得分：${getter_score()}"
//        setter_score( target_score = 0)
//        val current_score: TextView = findViewById<TextView>(R.id.currentScoreDisplay)
//        current_score.text = "@strings/score_display_init"
//        Toast.makeText(this@FullscreenActivity, "计数已清空", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SetTextI18n", "CutPasteId")
    fun ClearCircle(view: View) {//按钮清零
        ClearCircle()
        RestartTXT()
    }

    @SuppressLint("SetTextI18n")
    fun RestartTXT(){
        val background_score: TextView = findViewById<TextView>(R.id.fullscreen_content)
        background_score.text = "上次得分：${getter_score()}"
        Toast.makeText(this@FullscreenActivity, "计数已清空 上次得分：${getter_score()}", Toast.LENGTH_SHORT).show()
        setter_score( target_score = 0 )
        val current_score: TextView = findViewById<TextView>(R.id.currentScoreDisplay)
        current_score.text = "目前得分：${getter_score()}"//按任意处开始
    }

    override fun onResume() {
        super.onResume()
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置屏幕为横屏, 设置后会锁定方向
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    private fun toggle() {
        if (isFullscreen) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        fullscreenContentControls.visibility = View.GONE
        isFullscreen = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            fullscreenContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
        isFullscreen = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }



    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }

    fun checkInput(touchPosX:Float, touchPosY: Float, circlePosX: Float, circlePosY: Float, circleLen:Float): Boolean {//后续更新可以把返回值改为Int以支持多重分数计算，或者分数处理就在这里进行也可以
        val distX = abs(touchPosX - circlePosX)
        val distY = abs(touchPosY - circlePosY)
        val dist = sqrt(distX.toDouble() * distX.toDouble() + distY.toDouble() * distY.toDouble())

//        Toast.makeText(this@FullscreenActivity, "计数已清空 ${getter_score()}", Toast.LENGTH_SHORT).show()

        if(dist <= circleLen)
        {
//            setter_score(change_score = 1);//计分放到外面，结构先留着
            return true
        }
        else
        {
            return false
        }
    }



    private var mVelocityTracker: VelocityTracker? = null

    @SuppressLint("SetTextI18n")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // Reset the velocity tracker back to its initial state.
                mVelocityTracker?.clear()
                // If necessary retrieve a new VelocityTracker object to watch the
                // velocity of a motion.
                mVelocityTracker = mVelocityTracker ?: VelocityTracker.obtain()
                // Add a user's movement to the tracker.
                mVelocityTracker?.addMovement(event)


                val x = event.x.toFloat()
                val y = event.y.toFloat()

                if(checkInput(x,y,currentCirclePosX,currentCirclePosY,currentCircleLen)){
                    ClearCircle()
                    DrawRandomCircle()
                    setter_score(change_score = 1)
                }
                else
                {
                    ClearCircle()
                    DrawRandomCircle()
//                    RestartTXT()//TODO 失败惩罚的事回头再改，先交再说 bug来源为初始化
                }
            }

//            MotionEvent.ACTION_MOVE -> {
//                mVelocityTracker?.apply {
//                    val pointerId: Int = event.getPointerId(event.actionIndex)
//                    addMovement(event)
//                    // When you want to determine the velocity, call
//                    // computeCurrentVelociandroid:launchMode="singleTask" android:screenOrientation="portrait">ty(). Then call getXVelocity()
//                    // and getYVelocity() to retrieve the velocity for each pointer ID.
//                    computeCurrentVelocity(1000)
                    // Log velocity of pixels per second
                    // Best practice to use VelocityTrackerCompat where possible.
//                    Toast.makeText(this@FullscreenActivity, "CLICKED ;点击位置：（${MotionEventCompat.getX(event,pointerId)} ,${MotionEventCompat.getY(event, pointerId)}）; 移动位置：（${getXVelocity(pointerId)} ;${getYVelocity(pointerId)}）", Toast.LENGTH_SHORT).show()
//                    Log.d("", "X velocity: ${getXVelocity(pointerId)}")//X从左到右，Y从上到下
//                    Log.d("", "Y velocity: ${getYVelocity(pointerId)}")


//                }
//            }
//            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                // Return a VelocityTracker object back to be re-used by others.
//                mVelocityTracker?.recycle()
//                mVelocityTracker = null
//            }
        }

//        setter_score(change_score = 1)

        val current_score: TextView = findViewById<TextView>(R.id.currentScoreDisplay)
        current_score.text = "目前得分：${getter_score()}"
        val background_score: TextView = findViewById<TextView>(R.id.fullscreen_content)
        background_score.text = "目前得分：${getter_score()}"


        return true
    }
}

interface ActivityFullscreenBinding {

}
