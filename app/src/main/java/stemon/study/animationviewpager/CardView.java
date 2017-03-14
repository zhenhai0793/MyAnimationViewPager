package stemon.study.animationviewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by stemon.zhang
 */
public class CardView extends LinearLayout  implements View.OnClickListener{

    private Context mContext;
    private TextView mText;//卡片类型
    private TextView mCheckBtn;//查看按钮
    private View mCardView;//卡片样式

    private OnPagerListener pagerListener;//点击事件监听

    public CardView(Context context){
        super(context);
        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        mCardView = inflater.inflate(R.layout.card_layout, this, true);
        mCheckBtn = (TextView) mCardView.findViewById(R.id.check_btn);
        mCheckBtn.setOnClickListener(this);
        mText = (TextView) mCardView.findViewById(R.id.card_text);
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置卡片状态：消失或者显示
     * @param visible
     */
    public void setCardInvisible(int visible){
        mCardView.setVisibility(visible);
    }

    /**
     * 设置卡片相关信息
     * @param itemData
     */
    public void setCardInfo(int itemData){
    	mText.setText(itemData+"");

    }

    @Override
    public void onClick(View view) {
        if(R.id.check_btn == view.getId()){
            if(pagerListener != null){
                pagerListener.onPagerRemoved();
            }
        }
    }

    public void setOnPagerListener(OnPagerListener pagerListener){
        this.pagerListener = pagerListener;
    }

    public interface OnPagerListener {
        void onPagerRemoved();
    }

}
