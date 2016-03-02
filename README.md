属性动画是API 11加进来的一个新特性，其实在现在来说也没什么新的了。属性动画可以对任意**view**的属性做动画，实现动画的原理就是在给定的时间内把属性从一个值变为另一个值。因此可以说属性动画什么都可以干，只要**view**有这个属性。

所以我们这里对`Button`来做一个简单的属性动画：改变这个`Button`的宽度。也可以用`Tween Animation`，但是明显有一点不能满足要求的地方是`Tween Animation`只能做**Scale**动画，也就是缩放。你可以对这个button做缩放来达到增加宽度的效果，但是这个时候按钮的文字也会跟着出现缩放和变形。同时很重要的一点，`Tween Animation`不改变view的本来位置和大小。看起来这个按钮变大了，但是点击动画执行前的按钮没有覆盖的位置是没有效果的。

所以无论如何都要使用属性动画了。这里使用最简答的方法: `ObjectAnimator`来做这个动画：
```java
ObjectAnimator.ofInt(mAnimateButton, "width", mAnimateButton.getWidth(), 1000)
   .setDuration(1000)
   .start();
```
看起来很简单就实现了按钮的动画。但是运行的时候就会出现问题。因为，属性动画在执行的时候需要改变指定的属性，这里是`width`，的值。使用的就是属性对应的`getWidth`和`setWidth`方法。`getWidth`在没有给定动画的初值时，使用这个方法获得初始值。`setWidth`则在给定的时间内不断地被用来修改属性值来达到动画的效果。**注意，这个方法不是只是用一次**。

但是来看看`Button`的`getWidth`和`setWidth`两个方法的代码：
```java
    /**
     * Return the width of the your view.
     *
     * @return The width of your view, in pixels.
     */
    @ViewDebug.ExportedProperty(category = "layout")
    public final int getWidth() {
        return mRight - mLeft;
    }
```

```java
    /**
     * Makes the TextView exactly this many pixels wide.
     * You could do the same thing by specifying this number in the
     * LayoutParams.
     *
     * @see #setMaxWidth(int)
     * @see #setMinWidth(int)
     * @see #getMinWidth()
     * @see #getMaxWidth()
     *
     * @attr ref android.R.styleable#TextView_width
     */
    @android.view.RemotableViewMethod
    public void setWidth(int pixels) {
        mMaxWidth = mMinWidth = pixels;
        mMaxWidthMode = mMinWidthMode = PIXELS;

        requestLayout();
        invalidate();
    }
```

显然在`setWidth`的时候，并没有用给定的值去修改按钮layout param的宽度。

在这种情况下Google给了三种解决方法：
  1.  给你的view加上get和set方法。但是这需要你有这个权限。
  2. 用一个类来包装目标view，间接的给这个view来添加get和set方法。
  3. 用`ValueAnimator`和`AnimatorUpdateListener`监听动画，自己修改每个时间片的属性修改。

给`Button`添加get和set方法不是很现实，所以只能选择后两者。
下面一一介绍后面两个方法。

###间接给出get、set方法

这个方法看起来很简单，定义一个类间接给出get、set方法就是这样的：
```java
  class ViewWrapper {
        View mTargetView;

        public ViewWrapper(View v) {
            mTargetView = v;
        }

        public void setWidth(int width) {
            mTargetView.getLayoutParams().width = width; // 1
            mTargetView.requestLayout();
        }

        public int getWidth() {
            int width = mTargetView.getLayoutParams().width; // 2
            return width;
        }
    }
```
1. 既然动画是需要修改layout params的宽度，那么我们在这个set方法里就修改layout params的宽度。
2. 返回layout params的宽度。这个值是view在动画之前的宽度。

然后在按钮点击之后开始这个修改宽度的动画：
```java
 @Override
    public void onClick(View v) {
        Log.d("##ViewWrapperActivity", "width is " + v.getWidth());

        // 1
        ViewWrapper viewWrapper = new ViewWrapper(v);  
        // 2
        ObjectAnimator animator = ObjectAnimator.ofInt(viewWrapper, "width", /*viewWrapper.getWidth(),*/ 1500);  
        // 3
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
        // 4
        animator.setDuration(3000).start();
    }
```
1. 用包装类包装**view**，这里是按钮。
2. 开始动画，动画的对象现在为包装类对象。**这里可以修改属性动画的定义了，属性动画可以对任何对象修改属性。这里的包装类对象明显不是一个view**。
3.  这里增加了一个监听器，监听动画是刚开始还是已经结束。
4.  开始动画。在三秒钟的时间内修改按钮的宽度，从初始值修改为1500像素宽。

看起来已经很完美了，运行这个段代码。点击按钮后。好吧，这个动画很奇怪，并没有运行“完全”。点一下动一点，但是没有达到宽度为1500像素。虽然动画监听器`AnimatorListener`的方法`onAnimationEnd`已经执行，而且也打出了执行完成的log，但是宽度始终达不到。所以说动画执行并不“完全”。

那么这是为什么呢？先给出正确的代码各位可以参考着考虑一下：
```java
public class ViewWrapperActivity extends Activity implements View.OnClickListener {

    private Button mAnimateButton;
    // 1
    private ViewWrapper mWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_wrapper);

        mAnimateButton = (Button) findViewById(R.id.animate_button);
        mAnimateButton.setOnClickListener(this);
        // 2
        mWrapper = new ViewWrapper(mAnimateButton);
    }

    @Override
    public void onClick(View v) {
        Log.d("##ViewWrapperActivity", "width is " + v.getWidth());

        ObjectAnimator animator = ObjectAnimator.ofInt(mWrapper, "width", /*viewWrapper.getWidth(),*/ 1500);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            // ...
        });
        animator.setDuration(3000).start();
    }
}
```
1. 声明包装类对象类成员。
2. 在`onCreate`方法里初始化包装类对象。

这样就可以一次动画达到指定宽度了。具体是为什么呢？欢迎再后面的评论中一起讨论。;)

###用`ValueAnimator`和`AnimatorUpdateListener`的组合来实现动画
这个就比较简单了，直接看代码：
```java
    private void performAnimation(final View targetView, final int start, final int end) {
        // 1
        ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);
        // 2
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private final static String ANIM_TAG = "##Value animator";
            private IntEvaluator mIntEvaluator = new IntEvaluator();

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int currentValue = (Integer) animator.getAnimatedValue();
                Log.d(ANIM_TAG, "current value: " + currentValue);
                // 3
                float fraction = animator.getAnimatedFraction();
                targetView.getLayoutParams().width = mIntEvaluator.evaluate(fraction, start, end);
                targetView.requestLayout();
            }
        });
        // 4
        valueAnimator.setDuration(1000).start();
    }
```
1. 用`ValueAnimator`来做动画。`ValueAnimator`并不会实质的做什么。所以需要后面的`AnimatorUpdateListener`来做一些粗活儿。这里指定的从1到100也没有什么实质的作用。并不是把按钮的宽度从1变到100。后面的代码很清晰的表达了这一点。
2. 添加`AnimatorUpdateListener`。最主要的就是在方法`public void onAnimationUpdate(ValueAnimator animator)`中做动画。每一个时间片都会调用一次这个方法。每调用这个方法一次就给这个按钮的宽度设定一个新的值。
3. 第三步的算法是获取当前动画进行的时间片占整个动画时间的百分比，这里是`fraction`。然后根据这个百分比来计算当前时间片对应的按钮宽度是多少。
 当前宽度 = 初始宽度 + fraction * （结束宽度 - 初始宽度）。
 这也就解释了代码`mIntEvaluator.evaluate(fraction, start, end)`的作用。

完整代码看[这里](https://github.com/future-challenger/property_animation_button/tree/master)。
到这里全部解释完。欢迎拍砖，欢迎讨论！
