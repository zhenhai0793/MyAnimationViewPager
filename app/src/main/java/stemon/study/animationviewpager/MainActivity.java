package stemon.study.animationviewpager;

import java.util.ArrayList;
import java.util.List;

import stemon.study.animationviewpager.CardView.OnPagerListener;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * Created by stemon.zhang
 */
public class MainActivity extends Activity implements OnPageChangeListener, OnPagerListener {

    //View
    private List<Integer> mDataList;//卡片数据
    private ViewPager mViewPager;//卡片Pager
    private List<View> mViewList;//卡片View
    private View mBottomText;

    //Data
    private int mCurrentPage = 1;
    private MyPagerAdapter mPagerAdapter;

    private static final int page_count = 5;//设置卡片的个数
    private static final int translate_duration = 300;//卡片平移的时间
    private static final int disappear_duration = 500;//卡片消失的时间
    private static final int remove_page_delay = 100;//卡片更新时间延迟
    private static final int what_remove_page = 1;//更新ViewPager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initViewPager();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewList = new ArrayList<>();
        mPagerAdapter = new MyPagerAdapter(mViewList);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setPageMargin(dip2px(this, 15f));
        mViewPager.setOnPageChangeListener(this);
        mBottomText = findViewById(R.id.bottom_text);
    }

    private void initData() {
        mDataList = new ArrayList<>();
        for (int i = 0; i < page_count; i++) {
            mDataList.add(i);
        }
    }

    /**
     * dip转px
     *
     * @param context
     * @param dpValue
     * @return
     */
    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void initViewPager() {
        mViewList.clear();
        for (int i = 0, size = mDataList.size(); i < size; i++) {
            CardView view = new CardView(this);
            view.setCardInfo(mDataList.get(i));
            view.setOnPagerListener(this);
            mViewList.add(view);
        }
        mPagerAdapter.setViews(mViewList);
        mPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(mCurrentPage);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        mCurrentPage = arg0;
    }

    @Override
    public void onPagerRemoved() {
        startCardDisappearAnimation();
    }

    /**
     * 设置卡片动画
     * 根据BottomText的位置来设置动画结束的
     */
    private void startCardDisappearAnimation() {

        AnimationSet animationSet = new AnimationSet(true);

        // 透明度
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(disappear_duration);
        animationSet.addAnimation(alphaAnimation);

        // 缩放
//        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0.01f, 1, 0.01f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        scaleAnimation.setDuration(disappear_duration);
//        animationSet.addAnimation(scaleAnimation);

        // 平移
        final View view = mViewList.get(mCurrentPage);

        int[] loc1 = new int[2];
        view.getLocationOnScreen(loc1);
        int startY = loc1[1];

        int[] loc2 = new int[2];
        mBottomText.getLocationOnScreen(loc2);
        int endY = loc2[1];

        int fromX = 0;
        int toX = 0;// mBottomText.getWidth() / 2 - view.getWidth();
        int fromY = 0;
        int toY = endY - startY + mBottomText.getHeight() / 2;

        TranslateAnimation translateAnimation = new TranslateAnimation(fromX, toX, fromY, toY);
        translateAnimation.setDuration(disappear_duration);
        animationSet.addAnimation(translateAnimation);

        animationSet.setFillEnabled(true);
        animationSet.setFillAfter(true);
        animationSet.setInterpolator(new DecelerateInterpolator());

        view.startAnimation(animationSet);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startCardPushAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * 将下一个卡片推进来
     */
    private void startCardPushAnimation() {

        int cardSize = mViewList.size();

        // 当前view
        View curView = mViewList.get(mCurrentPage);
        int[] curViewLoc = new int[2];
        curView.getLocationOnScreen(curViewLoc);
        int curViewX = curViewLoc[0];

        if (mCurrentPage < (cardSize - 2)) {
            // 右边还有至少2页

            View nextView = mViewList.get(mCurrentPage + 1);
            int[] nextViewLoc = new int[2];
            nextView.getLocationOnScreen(nextViewLoc);
            int nextViewX = nextViewLoc[0];
            Animation nextViewAnim = getTranslateAnimation(0, curViewX - nextViewX, 0, 0, true);

            final View nextView2 = mViewList.get(mCurrentPage + 2);
            int[] nextViewLoc2 = new int[2];
            nextView2.getLocationOnScreen(nextViewLoc2);
            final Animation nextViewAnim2 = getTranslateAnimation(0, nextViewX - nextViewLoc2[0], 0, 0, true);

            nextViewAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    nextView2.startAnimation(nextViewAnim2);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            nextViewAnim2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mHandler.sendEmptyMessageDelayed(what_remove_page, remove_page_delay);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            nextView.startAnimation(nextViewAnim);
        } else if (mCurrentPage == (cardSize - 2)) {
            // 右边还有1页

            View newView = mViewList.get(mCurrentPage + 1);
            int[] nextViewLoc = new int[2];
            newView.getLocationOnScreen(nextViewLoc);

            Animation nextViewAnim = getTranslateAnimation(0, curViewX - nextViewLoc[0], 0, 0, true);

            newView.startAnimation(nextViewAnim);

            nextViewAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mHandler.sendEmptyMessageDelayed(what_remove_page, remove_page_delay);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            return;
        } else if (mCurrentPage == (cardSize - 1) && mCurrentPage > 1) {
            // 当前是最后页,左侧还有至少2页

            // 左侧第1页
            View leftView = mViewList.get(mCurrentPage - 1);
            int[] leftViewLoc = new int[2];
            leftView.getLocationOnScreen(leftViewLoc);
            int leftViewX = leftViewLoc[0];
            Animation leftViewAnim = getTranslateAnimation(0, curViewX - leftViewX, 0, 0, true);

            // 左侧第2页
            final View leftView2 = mViewList.get(mCurrentPage - 2);
            int[] leftViewLoc2 = new int[2];
            leftView2.getLocationOnScreen(leftViewLoc2);
            final Animation leftViewAnim2 = getTranslateAnimation(0, leftViewX - leftViewLoc2[0], 0, 0, true);

            leftViewAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    leftView2.startAnimation(leftViewAnim2);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            leftViewAnim2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mCurrentPage--;
                    mHandler.sendEmptyMessageDelayed(what_remove_page, remove_page_delay);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            leftView.startAnimation(leftViewAnim);
            return;
        } else if (mCurrentPage == (cardSize - 1) && mCurrentPage == 1) {
            // 当前是最后页,左侧还有1页

            View leftView = mViewList.get(mCurrentPage - 1);
            int[] leftViewLoc = new int[2];
            leftView.getLocationOnScreen(leftViewLoc);

            Animation leftViewAnim = getTranslateAnimation(0, curViewX - leftViewLoc[0], 0, 0, true);
            leftViewAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mCurrentPage--;
                    mHandler.sendEmptyMessageDelayed(what_remove_page, remove_page_delay);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            leftView.startAnimation(leftViewAnim);
        }
    }

    private Animation getTranslateAnimation(float fromX, float toX, float fromY, float toY, boolean fillAfter) {
        Animation anim = new TranslateAnimation(fromX, toX, fromY, toY);
        anim.setDuration(translate_duration);
        anim.setFillEnabled(true);
        anim.setFillAfter(fillAfter);
        anim.setInterpolator(new LinearInterpolator());
        return anim;
    }

    private void removePage() {
        mDataList.remove(mCurrentPage);
        mViewList.remove(mCurrentPage);
        mPagerAdapter.notifyDataSetChanged();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case what_remove_page: {
                    removePage();
                    break;
                }
            }
        }
    };
}
