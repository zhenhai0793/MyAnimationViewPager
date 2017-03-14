package stemon.study.animationviewpager;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import java.util.List;

/**
 * Created by stemon.zhang
 */
public class MyPagerAdapter extends PagerAdapter {

  // 界面列表
  private List<View> mViews;

  public MyPagerAdapter(List<View> mViews) {
    this.mViews = mViews;
  }

  // 销毁index位置的界面
  @Override
  public void destroyItem(View v, int index, Object o) {
    if (this.mViews!=null && this.mViews.size()!=0) {
      ((ViewPager) v).removeView((View)o);
      //(TODO) 删除是会指针越界
//      ((ViewPager) v).removeView(this.mViews.get(index));
    }
  }

  // 获得当前界面数
  @Override
  public int getCount() {
    if (this.mViews != null) {
      return this.mViews.size();
    }
    return 0;
  }

  @Override
  public int getItemPosition(Object object) {
    // TODO Auto-generated method stub
    return POSITION_NONE;
  }

  // 初始化index位置的界面
  @Override
  public Object instantiateItem(View v, int index) {
    ((ViewPager) v).addView(this.mViews.get(index), 0);
    return this.mViews.get(index);
  }

  // 判断是否由对象生成界面
  @Override
  public boolean isViewFromObject(View view, Object object) {
    return (view == object);
  }

  public void setViews(List<View> views) {
    this.mViews = views;
  }

}
