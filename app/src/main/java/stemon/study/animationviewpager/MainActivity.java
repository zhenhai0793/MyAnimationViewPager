package stemon.study.animationviewpager;

import java.util.ArrayList;
import java.util.List;

import stemon.study.animationviewpager.CardView.OnCheckListener;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * Created by stemon.zhang
 */
public class MainActivity extends Activity implements OnPageChangeListener, OnCheckListener{

	//View 
	private List<Integer> mCardData;//卡片数据 
	private ViewPager mPager;//卡片Pager
    private List<View> mCardList;//卡片View
    private View mBottomText;
    
    //Data
    private int mCurrentPage;
    private int mNewPosition;
    private MyPagerAdapter mPagerAdapter;
    
    private static final int PAGE_SIZE = 5;//设置卡片的个数
    private static final int CARD_TRANSLATE_TIME = 300;//卡片平移的时间
    private static final int CARD_DISAPPEAR_TIME = 500;//卡片消失的时间
    private static final int CARD_DELAYED_UPDATE_TIME = 100;//卡片更新时间延迟
    private static final int HANDLER_MESSAGE_UPDATE_CARD = 1;//更新ViewPager


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//初始化UI
		initView();
		//初始化数据
		initData();
		//设置View
		setViewPager();
	}
	
	/**
	 * 初始化UI
	 */
	private void initView(){
	 mPager = (ViewPager) findViewById(R.id.viewpager);
     mCardList = new ArrayList<View>();
     mPagerAdapter = new MyPagerAdapter(mCardList);
     mPager.setAdapter(mPagerAdapter);
     mPager.setOffscreenPageLimit(2);
     mPager.setPageMargin(dip2px(this, 15f));
     mPager.setOnPageChangeListener(this);
     mBottomText = findViewById(R.id.bottom_text);
     
	}
	
	/**
	 * 初始化数据，设置卡片的数据
	 */
	private void initData(){
		mCardData = new ArrayList<Integer>();
		for(int counter =0; counter < PAGE_SIZE; counter++){
			mCardData.add(counter);
		}
	}
	
//	private int dip2px(Context paramContext, float paramFloat) {
//        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
//        ((Activity) paramContext).getWindowManager().getDefaultDisplay()
//                .getMetrics(localDisplayMetrics);
//        return (int) FloatMath.ceil(paramFloat * localDisplayMetrics.density);
//    }

	/**
	 * dip转px
	 * @param context
	 * @param dpValue
	 * @return
	 */
    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
	
	/**
	 * 设置卡片View
	 */
	private void setViewPager(){
		mCardList.clear();
		for(int counter =0, size=mCardData.size(); counter < size; counter++){
			CardView view = new CardView(this);
			view.setCardInfo(mCardData.get(counter));
			view.setOnCheckListener(this);
			mCardList.add(view);
		}
		mPagerAdapter.setViews(mCardList);
		mPagerAdapter.notifyDataSetChanged();
		mPager.setCurrentItem(mCurrentPage);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		mCurrentPage = arg0;
	}

	@Override
	public void checkDetail() {
		// TODO Auto-generated method stub
		startCardDisappearAnimation();
	}
	
	 /**
     * 设置卡片动画
     * 根据BottomText的位置来设置动画结束的
     */
    private void startCardDisappearAnimation() {
        int dispearAnimYPos = mBottomText.getHeight() / 2;
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(CARD_DISAPPEAR_TIME);
        animationSet.addAnimation(alphaAnimation);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0.01f, 1, 0.01f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(CARD_DISAPPEAR_TIME);
        animationSet.addAnimation(scaleAnimation);
        final View view = mCardList.get(mCurrentPage);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int startY = location[1];
        mBottomText.getLocationOnScreen(location);
        int endY = location[1];
        TranslateAnimation translateAnimation = new TranslateAnimation(0, -view.getWidth() + mBottomText.getWidth() / 2, 0, endY - startY + dispearAnimYPos);
        translateAnimation.setDuration(CARD_DISAPPEAR_TIME);
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
                //卡片推出
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
        View view = mCardList.get(mCurrentPage);
        mNewPosition = mCurrentPage;
        int cardSize = mCardList.size();
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int endLeft = location[0];
        if (mNewPosition < (cardSize - 2)) {//右边移动两张
            View viewRight1 = mCardList.get(mNewPosition + 1);
            viewRight1.getLocationOnScreen(location);
            int rightEnd = location[0];
            Animation anim1 = getTranslateAnimation(0, endLeft - rightEnd, 0, 0, true);
            final View viewRight2 = mCardList.get(mNewPosition + 2);
            viewRight2.getLocationOnScreen(location);
            final Animation anim2 = getTranslateAnimation(0, rightEnd - location[0], 0, 0, true);
            viewRight1.startAnimation(anim1);
            anim1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    viewRight2.startAnimation(anim2);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            anim2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_UPDATE_CARD, CARD_DELAYED_UPDATE_TIME);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            return;
        } else if (mNewPosition == (cardSize - 2)) {//右侧移动一张
            View viewRight1 = mCardList.get(mNewPosition + 1);
            viewRight1.getLocationOnScreen(location);
            Animation anim1 = getTranslateAnimation(0, endLeft - location[0], 0, 0, true);
            viewRight1.startAnimation(anim1);
            anim1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_UPDATE_CARD, CARD_DELAYED_UPDATE_TIME);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            return;
        } else if (mNewPosition == (cardSize - 1) && mNewPosition > 1) {//右侧没有，左侧移动两张
            View viewLeft1 = mCardList.get(mNewPosition - 1);
            viewLeft1.getLocationOnScreen(location);
            int leftEnd = location[0];
            Animation anim1 = getTranslateAnimation(0, endLeft - leftEnd, 0, 0, true);
            final View viewLeft2 = mCardList.get(mNewPosition - 2);
            viewLeft2.getLocationOnScreen(location);
            final Animation anim2 = getTranslateAnimation(0, leftEnd - location[0], 0, 0, true);
            viewLeft1.startAnimation(anim1);
            anim1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    viewLeft2.startAnimation(anim2);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            anim2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mNewPosition--;
                    mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_UPDATE_CARD, CARD_DELAYED_UPDATE_TIME);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            return;
        } else if (mNewPosition == (cardSize - 1) && mNewPosition == 1) {//右侧没有，左侧移动一张
            View viewLeft1 = mCardList.get(mNewPosition - 1);
            viewLeft1.getLocationOnScreen(location);
            Animation animation = getTranslateAnimation(0, endLeft - location[0], 0, 0, true);
            viewLeft1.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mNewPosition--;
                    //延迟更新ViewPager
                    mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_UPDATE_CARD, CARD_DELAYED_UPDATE_TIME);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            return;
        }
    }

    /**
     * 获得平移动画
     * @param fromX
     * @param toX
     * @param fromY
     * @param toY
     * @param fillAfter
     * @return
     */
    private Animation getTranslateAnimation(float fromX, float toX, float fromY, float toY, boolean fillAfter) {
        Animation anim = new TranslateAnimation(fromX, toX, fromY, toY);
        anim.setDuration(CARD_TRANSLATE_TIME);
        anim.setFillEnabled(true);
        anim.setFillAfter(fillAfter);
        anim.setInterpolator(new LinearInterpolator());
        return anim;
    }

    /**
     * 更新页面
     */
    private void updateCard() {
        mCardData.remove(mCurrentPage);
        mCurrentPage = mNewPosition;
        setViewPager();
    }
    
    /**
     * 处理UI更新操作
     */
    private Handler mHandler = new Handler(){
    	@Override
    	public void handleMessage(Message msg) {
    		// TODO Auto-generated method stub
    		switch(msg.what){
    		case HANDLER_MESSAGE_UPDATE_CARD:{
    			updateCard();
    			break;
    		}
    		}
    	}
    };
}
