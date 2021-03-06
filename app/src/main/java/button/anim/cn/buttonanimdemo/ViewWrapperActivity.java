package button.anim.cn.buttonanimdemo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

public class ViewWrapperActivity extends Activity implements View.OnClickListener {

    private Button mAnimateButton;
    private Button mSequenceButton;
    private ViewWrapper mWrapper;
    private ViewWrapper mSequenceWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_wrapper);

        mAnimateButton = (Button) findViewById(R.id.animate_button);
        mAnimateButton.setOnClickListener(this);

        mSequenceButton = (Button) findViewById(R.id.bottom_anim_button);
        mSequenceButton.setOnClickListener(this);

        mWrapper = new ViewWrapper(mAnimateButton);
        mSequenceWrapper = new ViewWrapper(mSequenceButton);

//        ObjectAnimator.ofInt(mAnimateButton, "width", mAnimateButton.getWidth(), 1000).setDuration(1000).start();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mAnimateButton)) {
            performNormalAnimation(v);
        } else {
            performSequenceAnimation(v);
        }
    }

    private void performNormalAnimation(View v) {
        Log.d("##ViewWrapperActivity", "width is " + v.getWidth());

//        ViewWrapper viewWrapper = new ViewWrapper(v);
//        ViewWrapper viewWrapper = new ViewWrapper(mAnimateButton);
        int width = v.getLayoutParams().width;
        int height = v.getHeight(); // current height
        PropertyValuesHolder widthHolder = PropertyValuesHolder.ofInt("width", width * 2);
        PropertyValuesHolder heightHolder = PropertyValuesHolder.ofInt("height", height * 6);

//        ObjectAnimator animator = ObjectAnimator.ofInt(mWrapper, "width", /*viewWrapper.getWidth(),*/ 1500);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(mWrapper, widthHolder, heightHolder);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d("##ANIM", "started");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d("##ANIM", "stopped");
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(1000).start();
    }

    class ViewWrapper {
        View mTargetView;

        public ViewWrapper(View v) {
            mTargetView = v;
        }

        public void setWidth(int width) {
            mTargetView.getLayoutParams().width = width;
            mTargetView.requestLayout();
        }

        // for view's width
        public int getWidth() {
            int width = mTargetView.getLayoutParams().width;
            return width;
        }

        // for view's height
        public void setHeight(int height) {
            mTargetView.getLayoutParams().height = height;
            mTargetView.requestLayout();
        }

        public int getHeight() {
            int height = mTargetView.getLayoutParams().height;
            return height;
        }
    }

    private void performSequenceAnimation(View v) {
        int width = v.getWidth();
        int height = v.getHeight();

        AnimatorSet animSet = new AnimatorSet();

        ObjectAnimator widthAnim = ObjectAnimator.ofInt(mSequenceWrapper, "width", width * 2);
        ObjectAnimator heightAnim = ObjectAnimator.ofInt(mSequenceWrapper, "height", height * 6);

        animSet.play(widthAnim).before(heightAnim);
        animSet.setDuration(1000);
        animSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animSet.start();
    }
}
