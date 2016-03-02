package button.anim.cn.buttonanimdemo;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ValueAnimatorActivity extends Activity implements View.OnClickListener {

    private Button mAnimButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value_animator);

        mAnimButton = (Button) findViewById(R.id.value_animator_button);
        mAnimButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(ValueAnimatorActivity.this, "Anim button clicked", Toast.LENGTH_SHORT).show();

        performAnimation(v, v.getWidth(), 1100);
    }

    private void performAnimation(final View targetView, final int start, final int end) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private final static String ANIM_TAG = "##Value animator";
            private IntEvaluator mIntEvaluator = new IntEvaluator();

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int currentValue = (Integer) animator.getAnimatedValue();
                Log.d(ANIM_TAG, "current value: " + currentValue);

                float fraction = animator.getAnimatedFraction();
                targetView.getLayoutParams().width = mIntEvaluator.evaluate(fraction, start, end);
                targetView.requestLayout();
            }
        });

        valueAnimator.setDuration(1000).start();
    }
}
