package com.example.untils;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.example.cz.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 2013/8/13
 * �Զ���ListView��ʵ��OnScrollListener�ӿ�
 * ��ListView��Ϊʵ��"����ˢ��"��"�������ظ���"�����Ƶģ�����Ч���ɲο�����΢������Ѷ΢��
 * @author jacyayj
 *
 */
public class PullToRefreshListView extends ListView implements OnScrollListener {

    private static final int TAP_TO_REFRESH = 1;			//��δˢ�£�
    private static final int PULL_TO_REFRESH = 2;			// ����ˢ��
    private static final int RELEASE_TO_REFRESH = 3;		// �ͷ�ˢ��
    private static final int REFRESHING = 4;				// ����ˢ��
    private static final int TAP_TO_LOADMORE = 5;			// δ���ظ���
    private static final int LOADING = 6;					// ���ڼ���
    

    private static final String TAG = "PullToRefreshListView";

    private OnRefreshListener mOnRefreshListener;			// ˢ�¼�����

    /**
     * Listener that will receive notifications every time the list scrolls.
     */
    private OnScrollListener mOnScrollListener;				// �б����������
    private LayoutInflater mInflater;		    			// ���ڼ��ز����ļ�

    private LinearLayout mRefreshHeaderView;				// ˢ����ͼ(Ҳ����ͷ���ǲ���)	
    private TextView mRefreshViewText;						// ˢ����ʾ�ı�		
    private ImageView mRefreshViewImage;					// ˢ���������µ��Ǹ�ͼƬ
    private ProgressBar mRefreshViewProgress;				// ������Բ�ν�����
    private TextView mRefreshViewLastUpdated;				// ������µ��ı�
    
    private RelativeLayout mLoadMoreFooterView;				// ���ظ���
    private TextView mLoadMoreText;							// ��ʾ�ı�
    private ProgressBar mLoadMoreProgress;					// ���ظ��������
    

    private int mCurrentScrollState;						// ��ǰ����λ��			
    private int mRefreshState;								// ˢ��״̬	
    private int mLoadState;									// ����״̬

    private RotateAnimation mFlipAnimation;					// ��������
    private RotateAnimation mReverseFlipAnimation;			// �ָ�����

    private int mRefreshViewHeight;							// ˢ����ͼ�߶�					
    private int mRefreshOriginalTopPadding;					// ԭʼ�ϲ���϶
    private int mLastMotionY;								// ��¼���λ��
    
	public PullToRefreshListView(Context context) {
        super(context);
        init(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        // Load all of the animations we need in code rather than through XML
    	/** ������ת����**/
    	// ������1.��ת��ʼ�ĽǶ� 2.��ת�����ĽǶ� 3. X������ģʽ 4.X���������ֵ 5.Y�������ģʽ 6.Y���������ֵ
        mFlipAnimation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(250);				// ���ó���ʱ��
        mFlipAnimation.setFillAfter(true);				// ����ִ�����Ƿ�ͣ����ִ�����״̬
        mReverseFlipAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(250);
        mReverseFlipAnimation.setFillAfter(true);

        // ��ȡLayoutInflaterʵ������
        mInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        // ��������ˢ�µ�ͷ����ͼ
		mRefreshHeaderView = (LinearLayout) mInflater.inflate(
				R.layout.head, this, false);
        mRefreshViewText =
            (TextView) mRefreshHeaderView.findViewById(R.id.head_tipsTextView);
        mRefreshViewImage =
            (ImageView) mRefreshHeaderView.findViewById(R.id.head_arrowImageView);
        mRefreshViewProgress =
            (ProgressBar) mRefreshHeaderView.findViewById(R.id.head_progressBar);
        mRefreshViewLastUpdated =
            (TextView) mRefreshHeaderView.findViewById(R.id.head_lastUpdatedTextView);
		mLoadMoreFooterView = (RelativeLayout) mInflater.inflate(
				R.layout.foot, this, false);
		mLoadMoreText = (TextView) mLoadMoreFooterView.findViewById(R.id.loadmore_text);
		mLoadMoreProgress = (ProgressBar) mLoadMoreFooterView.findViewById(R.id.loadmore_progress);
		

        mRefreshViewImage.setMinimumHeight(50);		// ����ͼƬ��С�߶�
        mRefreshHeaderView.setOnClickListener(new OnClickRefreshListener());
        mRefreshOriginalTopPadding = mRefreshHeaderView.getPaddingTop();
        mLoadMoreFooterView.setOnClickListener(new OnClickLoadMoreListener());

        mRefreshState = TAP_TO_REFRESH;				// ��ʼˢ��״̬
        mLoadState = TAP_TO_LOADMORE;

        addHeaderView(mRefreshHeaderView);			// ����ͷ����ͼ
        addFooterView(mLoadMoreFooterView);			// ����β����ͼ

        super.setOnScrollListener(this);		

        measureView(mRefreshHeaderView);				// ������ͼ
        mRefreshViewHeight = mRefreshHeaderView.getMeasuredHeight();	// �õ���ͼ�ĸ߶�
    }

    @Override
    protected void onAttachedToWindow() {
        setSelection(1);		// ���õ�ǰѡ�е���
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        setSelection(1);
    }

    /**
     * Set the listener that will receive notifications every time the list
     * scrolls.
     * 
     * @param l The scroll listener. 
     */
    @Override
    public void setOnScrollListener(AbsListView.OnScrollListener l) {
        mOnScrollListener = l;
    }

    /**
     * Register a callback to be invoked when this list should be refreshed.
     * ע�������
     * @param onRefreshListener The callback to run.
     */
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    /**
     * Set a text to represent when the list was last updated.
     * ����һ���ı�����ʾ������µ��б���ʾ������������б��ʱ��
     * @param lastUpdated Last updated at.
     */
    public void setLastUpdated(CharSequence lastUpdated) {
        if (lastUpdated != null) {
            mRefreshViewLastUpdated.setVisibility(View.VISIBLE);
            mRefreshViewLastUpdated.setText("������ʱ��: " + lastUpdated);
        } else {
            mRefreshViewLastUpdated.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int y = (int) event.getY();	 // ��ȡ���λ�õ�Y����

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:		// ��ָ̧��
                if (!isVerticalScrollBarEnabled()) {
                    setVerticalScrollBarEnabled(true);
                }
                if (getFirstVisiblePosition() == 0 && mRefreshState != REFRESHING) {
                    if ((mRefreshHeaderView.getBottom() > mRefreshViewHeight
                            || mRefreshHeaderView.getTop() >= 0)
                            && mRefreshState == RELEASE_TO_REFRESH) {
                        // Initiate the refresh
                        mRefreshState = REFRESHING;		// ˢ��״̬
                        prepareForRefresh();
                        onRefresh();
                    } else if (mRefreshHeaderView.getBottom() < mRefreshViewHeight
                            || mRefreshHeaderView.getTop() < 0) {
                        // Abort refresh and scroll down below the refresh view
                        resetHeader();
                        setSelection(1);
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                applyHeaderPadding(event);
                break;
        }
        return super.onTouchEvent(event);
    }

    private void applyHeaderPadding(MotionEvent ev) {
        final int historySize = ev.getHistorySize();

        // Workaround for getPointerCount() which is unavailable in 1.5
        // (it's always 1 in 1.5)
        int pointerCount = 1;
        try {
            Method method = MotionEvent.class.getMethod("getPointerCount");
            pointerCount = (Integer)method.invoke(ev);
        } catch (NoSuchMethodException e) {
            pointerCount = 1;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (IllegalAccessException e) {
            System.err.println("unexpected " + e);
        } catch (InvocationTargetException e) {
            System.err.println("unexpected " + e);
        }

        for (int h = 0; h < historySize; h++) {
            for (int p = 0; p < pointerCount; p++) {
                if (mRefreshState == RELEASE_TO_REFRESH) {
                    if (isVerticalFadingEdgeEnabled()) {
                        setVerticalScrollBarEnabled(false);
                    }

                    int historicalY = 0;
                    try {
                        // For Android > 2.0
                        Method method = MotionEvent.class.getMethod(
                                "getHistoricalY", Integer.TYPE, Integer.TYPE);
                        historicalY = ((Float) method.invoke(ev, p, h)).intValue();
                    } catch (NoSuchMethodException e) {
                        // For Android < 2.0
                        historicalY = (int) (ev.getHistoricalY(h));
                    } catch (IllegalArgumentException e) {
                        throw e;
                    } catch (IllegalAccessException e) {
                        System.err.println("unexpected " + e);
                    } catch (InvocationTargetException e) {
                        System.err.println("unexpected " + e);
                    }

                    // Calculate the padding to apply, we divide by 1.7 to
                    // simulate a more resistant effect during pull.
                    int topPadding = (int) (((historicalY - mLastMotionY)
                            - mRefreshViewHeight) / 1.7);

                    // �����ϡ��¡������ĸ�λ�õļ�϶��϶
                    mRefreshHeaderView.setPadding(
                            mRefreshHeaderView.getPaddingLeft(),
                            topPadding,
                            mRefreshHeaderView.getPaddingRight(),
                            mRefreshHeaderView.getPaddingBottom());
                }
            }
        }
    }

    /**
     * Sets the header padding back to original size.
     * ����ͷ������ԭʼ��С
     */
    private void resetHeaderPadding() {
        mRefreshHeaderView.setPadding(
                mRefreshHeaderView.getPaddingLeft(),
                mRefreshOriginalTopPadding,
                mRefreshHeaderView.getPaddingRight(),
                mRefreshHeaderView.getPaddingBottom());
    }

    /**
     * Resets the header to the original state.
     * ��������ͷ��Ϊԭʼ״̬
     */
    public void resetHeader() {
        if (mRefreshState != TAP_TO_REFRESH) {
            mRefreshState = TAP_TO_REFRESH;

            resetHeaderPadding();

            // Set refresh view text to the pull label
            mRefreshViewText.setText("ˢ�³ɹ�");
            // Replace refresh drawable with arrow drawable
            mRefreshViewImage.setImageResource(R.drawable.arrow_down);
            // Clear the full rotation animation
            mRefreshViewImage.clearAnimation();
            // Hide progress bar and arrow.
            mRefreshViewImage.setVisibility(View.GONE);
            mRefreshViewProgress.setVisibility(View.GONE);
        }
    }
    
    /**
     * ����ListViewβ����ͼΪ��ʼ״̬
     */
    public void resetFooter() {
    	if(mLoadState != TAP_TO_LOADMORE) {
    		mLoadState = TAP_TO_LOADMORE;
    		
    		// ����������Ϊ���ɼ�
    		mLoadMoreProgress.setVisibility(View.GONE);
    		// ��ť���ı��滻Ϊ�����ظ��ࡱ
    		mLoadMoreText.setText("������ظ���");
    	}
    	
    }
    

    /**
     * ������ͼ�Ĵ�С
     * @param child
     */
    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0,
                0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        // When the refresh view is completely visible, change the text to say
        // "Release to refresh..." and flip the arrow drawable.
        if (mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL
                && mRefreshState != REFRESHING) {
            if (firstVisibleItem == 0) {		// �����һ���ɼ���ĿΪ0
                mRefreshViewImage.setVisibility(View.VISIBLE);	// ��ָʾ��ͷ��ÿɼ�
                /**���ͷ����ͼ����븸������λ�ô���������߶�+20����ͷ����ͼ�Ķ���λ��>0,����Ҫ��ˢ��״̬������"�ͷ���ˢ��"**/
                if ((mRefreshHeaderView.getBottom() > mRefreshViewHeight + 20
                        || mRefreshHeaderView.getTop() >= 0)
                        && mRefreshState != RELEASE_TO_REFRESH) {
                    mRefreshViewText.setText("�ͷ���ˢ��");// ����ˢ���ı�Ϊ"Release to refresh..."
                    mRefreshViewImage.clearAnimation();					// �������	
                    mRefreshViewImage.startAnimation(mFlipAnimation);	// ��������
                    mRefreshState = RELEASE_TO_REFRESH;					// ����ˢ��״̬Ϊ���ͷ���ˢ��"
                } else if (mRefreshHeaderView.getBottom() < mRefreshViewHeight + 20
                        && mRefreshState != PULL_TO_REFRESH) {
                    mRefreshViewText.setText("����ˢ��");// ����ˢ���ı�Ϊ"Pull to refresh..."
                    if (mRefreshState != TAP_TO_REFRESH) {
                        mRefreshViewImage.clearAnimation();
                        mRefreshViewImage.startAnimation(mReverseFlipAnimation);
                    }
                    mRefreshState = PULL_TO_REFRESH;
                }
            } else {
                mRefreshViewImage.setVisibility(View.GONE);			// ��ˢ�¼�ͷ���ɼ�
                resetHeader();	// ��������ͷ��Ϊԭʼ״̬
            }
        } else if (mCurrentScrollState == SCROLL_STATE_FLING
                && firstVisibleItem == 0
                && mRefreshState != REFRESHING) {
            setSelection(1);
        }

        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem,
                    visibleItemCount, totalItemCount);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mCurrentScrollState = scrollState;

        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }
    

    /**Ϊˢ����׼��**/
    public void prepareForRefresh() {
        resetHeaderPadding();		

        mRefreshViewImage.setVisibility(View.GONE);			// ȥ��ˢ�µļ�ͷ
        // We need this hack, otherwise it will keep the previous drawable.
        mRefreshViewImage.setImageDrawable(null);
        mRefreshViewProgress.setVisibility(View.VISIBLE);	// Բ�ν�������Ϊ�ɼ�

        // Set refresh view text to the refreshing label
        mRefreshViewText.setText("������");

        mRefreshState = REFRESHING;
    }
    
    /**Ϊ���ظ�����׼��**/
    public void prepareForLoadMore() {
    	mLoadMoreProgress.setVisibility(View.VISIBLE);	 
    	mLoadMoreText.setText("���ڼ���");
    	mLoadState = LOADING;
    }

    public void onRefresh() {
        Log.d(TAG, "onRefresh");

        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh();
        }
    }
    
    public void OnLoadMore() {
    	Log.d(TAG, "onLoadMore");
    	if(mOnRefreshListener != null) {
    		mOnRefreshListener.onLoadMore();
    	}
    }

    /**
     * Resets the list to a normal state after a refresh.
     * @param lastUpdated Last updated at.
     */
    public void onRefreshComplete(CharSequence lastUpdated) {
        setLastUpdated(lastUpdated);	// ��ʾ����ʱ��
        onRefreshComplete();
    }

    /**
     * Resets the list to a normal state after a refresh.
     */
    public void onRefreshComplete() {        
        Log.d(TAG, "onRefreshComplete");

        resetHeader();

        // If refresh view is visible when loading completes, scroll down to
        // the next item.
        if (mRefreshHeaderView.getBottom() > 0) {
            invalidateViews();
            setSelection(1);
        }
    }
    
    public void onLoadMoreComplete() {
    	Log.d(TAG, "onLoadMoreComplete");
    	resetFooter();
    }

    /**
     * Invoked when the refresh view is clicked on. This is mainly used when
     * there's only a few items in the list and it's not possible to drag the
     * list.
     * ���ˢ��
     */
    private class OnClickRefreshListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (mRefreshState != REFRESHING) {
                prepareForRefresh();
                onRefresh();
            }
        }

    }
    
    /**
     * 
     * @author wwj
     * ���ظ���
     */
    private class OnClickLoadMoreListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if(mLoadState != LOADING) {
				prepareForLoadMore();
				OnLoadMore();
			}
		}
    }

    /**
     * Interface definition for a callback to be invoked when list should be
     * refreshed.
     * �ӿڶ���һ���ص��������б�Ӧ����ˢ��
     */
    public interface OnRefreshListener {
        /**
         * Called when the list should be refreshed.
         * ���б�Ӧ����ˢ���ǵ����������
         * <p>
         * A call to {@link PullToRefreshListView #onRefreshComplete()} is
         * expected to indicate that the refresh has completed.
         */
        public void onRefresh();
        
        public void onLoadMore();
    }
}
