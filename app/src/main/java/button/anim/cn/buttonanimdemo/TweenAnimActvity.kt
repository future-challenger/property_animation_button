package button.anim.cn.buttonanimdemo

import android.app.Activity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button

class TweenAnimActvity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tween_anim_actvity)

        var button = findViewById(R.id.tween_button) as Button
        button.setOnClickListener { v ->

            var anim = AnimationUtils.loadAnimation(this@TweenAnimActvity, R.anim.scale_anim)
            v.startAnimation(anim)
        }
    }
}
