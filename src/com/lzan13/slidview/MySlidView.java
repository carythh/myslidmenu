package com.lzan13.slidview;


import com.lzan13.main.ConstantQuantity;
import com.lzan13.main.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * @author Administrator
 * 
 */
public class MySlidView extends RelativeLayout {

	private View mMenuView;
	private View mSlidView;

	private RelativeLayout bgShade;

	private Context mContext;

	private float mTouchSlop;

	private Scroller mScroller; // ��������
	private VelocityTracker mVelocityTracker; // �����ж�˦������
	private static final int SNAP_VELOCITY = 100; // �����ٶȷ�ֵ

	private float mLastMotionX;
	private float mLastMotionY;
	private float mFirstMotionX;
	private int menuWidth; 		//��߲˵�slidmenu�Ŀ��

	public int menuState;		//�˵�״̬
	private boolean mIsBeingDragged = true; // �Ƿ��ڻ���

	public MySlidView(Context context) {
		super(context);
		init(context);
	}

	public MySlidView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MySlidView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	/**
	 * @param context
	 * ��ʼ��
	 */
	private void init(Context context) {
		mContext = context;
		mScroller = new Scroller(context);

		menuState = ConstantQuantity.MENU_STATE_CLOSE;

		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

		bgShade = new RelativeLayout(context);
	}

	/**
	 * @param menu
	 * @param slid
	 */
	public void addViews(View menu, View slid) {
		setMenuView(menu);
		setSlidView(slid);
	}

	/**
	 * @see android.view.View#computeScroll()
	 */
	@Override
	public void computeScroll() {
		if (!mScroller.isFinished()) {
			if (mScroller.computeScrollOffset()) {
				int oldX = mSlidView.getScrollX();
				int x = mScroller.getCurrX();
				if (oldX != x) {
					if (mSlidView != null) {
						mSlidView.scrollTo(mScroller.getCurrX(), 0);
						bgShade.scrollTo(x + 10, 0);//
					}
				}
				invalidate();
			}
		}
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		postInvalidate();
	}

	/**
	 * @see android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
	 * �������ƣ��ڴ���������֮ǰ�������´����¼������ж�֮��Ĳ�����
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		menuWidth = getMenuViewWidth();
		int action = event.getAction();

		float x = event.getX();
		float y = event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mFirstMotionX = x;
			mLastMotionX = x;
			mLastMotionY = y;
			mIsBeingDragged = false;
			
			break;
		case MotionEvent.ACTION_MOVE:
			float deltaX = mLastMotionX - x;
			float deltaY = mLastMotionY - y;
			if (Math.abs(deltaX) > mTouchSlop
					&& Math.abs(deltaX) > Math.abs(deltaY)) {
				float oldScrollX = mSlidView.getScrollX();
				if (oldScrollX <= 0) {
					mIsBeingDragged = true;
					mLastMotionX = x;
				} else {
					if (deltaX < 0) {
						mIsBeingDragged = true;
						mLastMotionX = x;
					}
				}
			}
			if (menuState == ConstantQuantity.MENU_STATE_OPEN) {
				System.out.println(mFirstMotionX + " ; "
						+ mSlidView.getScrollX());
				if (mFirstMotionX >= 0
						&& mFirstMotionX < Math.abs(mSlidView.getScrollX())) {
					mIsBeingDragged = false;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if (menuState == ConstantQuantity.MENU_STATE_OPEN) {
				if(mFirstMotionX >= menuWidth && mLastMotionX >= menuWidth){
					openMenuView();
				}
			}
			break;
		}	
		return mIsBeingDragged;
	}

	/**
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent) 
	 * ���ƴ���
	 * ����ȥ����������
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
			mVelocityTracker.addMovement(event);
		}

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			mLastMotionY = y;
			System.out.println("mSlidView-scrollX-->" + mSlidView.getScrollX());
			System.out.println("mMenuView-Width-->" + getMenuViewWidth());
			if (mSlidView.getScrollX() == -getMenuViewWidth()
					&& mLastMotionX < getMenuViewWidth()) {
				// return false;
			}

			break;
		case MotionEvent.ACTION_MOVE:
			if (mIsBeingDragged) {
				float delateX = mLastMotionX - x;
				mLastMotionX = x;
				if (!(mSlidView.getScrollX() < 0)) {
					if (delateX > 0) {
						break;
					}
				}

				if ((mSlidView.getScrollX() <= 0)
						&& (mSlidView.getScrollX() >= -(ConstantQuantity.screenWidth - ConstantQuantity.screenDensity * 50))) {
					mSlidView.scrollBy((int) delateX, 0);
				}

				System.out.println("delateX--->" + delateX);
				System.out.println("mSlidView.ScrollX--->"
						+ mSlidView.getScrollX());
			}

			if (mSlidView.getScrollX() >= 0) {
				return false;
			}

			if (mSlidView != null) {
				bgShade.scrollTo(mSlidView.getScrollX() + 10, 0);
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			int velocityX = 0;
			if (mVelocityTracker != null) {
				mVelocityTracker.addMovement(event);
				mVelocityTracker.computeCurrentVelocity(1000);
				velocityX = (int) mVelocityTracker.getXVelocity();
			}
			//���뿪��Ļʱ���ݻ������ٶ����жϲ˵��Ƿ�򿪻�ر�
			showVelocityMenuView(velocityX);
			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			break;
		}

		return true;
	}

	/**
	 * @param view
	 * ���ò�߲˵�����
	 */
	public void setMenuView(View view) {
		LayoutParams behindParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT);
		addView(view, behindParams);
		mMenuView = view;

	}

	/**
	 * @param view
	 * �����м们������ �����м们�����ֵ�ͬʱ������һ���������֣�������ʾ�����Ӱ�����Ӳ㼶����Ч��
	 */
	public void setSlidView(View view) {
		LayoutParams bgShadeParams = new LayoutParams(
				ConstantQuantity.screenWidth, ConstantQuantity.screenHeight);
		bgShadeParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		bgShade.setLayoutParams(bgShadeParams);

		LayoutParams bgParams = new LayoutParams(ConstantQuantity.screenWidth,
				ConstantQuantity.screenHeight - 48);
		bgParams.addRule(RelativeLayout.CENTER_IN_PARENT);

		View bgShadeContent = new View(mContext);

		bgShadeContent.setBackgroundResource(R.drawable.view_left_bg);

		bgShade.removeAllViews();
		bgShade.addView(bgShadeContent, bgParams);
		if (getChildCount() > 1) {
			removeViewAt(1);
			removeViewAt(1);
		}

		addView(bgShade, bgParams);

		LayoutParams aboveParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		addView(view, aboveParams);
		mSlidView = view;
		mSlidView.bringToFront();
		System.out.println(getChildCount());
	}

	/**
	 * @return ��ò˵��Ŀ��
	 */
	private int getMenuViewWidth() {
		if (mMenuView == null) {
			return 0;
		}
		return mMenuView.getWidth();
	}

	
	/**
	 * @param velocity
	 * �жϲ˵��Ĵ򿪻�ر�
	 */
	private void showVelocityMenuView(int velocity) {
		if (velocity > 0) {
			if (velocity > SNAP_VELOCITY) {
				smoothScrollTo(-(menuWidth + mSlidView.getScrollX()));
				setMenuState();
			} else {
				if (mSlidView.getScrollX() > -ConstantQuantity.screenWidth / 2) {
					smoothScrollTo(-mSlidView.getScrollX());
				} else {
					smoothScrollTo(-(menuWidth + mSlidView.getScrollX()));
					setMenuState();
				}
			}
		}

		if (velocity < 0) {
			if (velocity < -SNAP_VELOCITY) {
				smoothScrollTo(-mSlidView.getScrollX());
				setMenuState();
			} else {
				if (mSlidView.getScrollX() > -ConstantQuantity.screenWidth / 2) {
					smoothScrollTo(-mSlidView.getScrollX());
				} else {
					smoothScrollTo(-(menuWidth + mSlidView.getScrollX()));
					setMenuState();
				}
			}
		}

		if (velocity == 0) {
			if (mSlidView.getScrollX() > -(ConstantQuantity.screenWidth - ConstantQuantity.screenDensity * 50) / 2) {
				smoothScrollTo(-mSlidView.getScrollX());
			} else {
				smoothScrollTo(-(menuWidth + mSlidView.getScrollX()));
				setMenuState();
			}
		}
	}
	
	/**
	 * ȥ�򿪻��߹رղ˵�����
	 */
	public void openMenuView() {
		menuWidth = getMenuViewWidth();
		if (mSlidView.getScrollX() == 0) {
			// mMenuView.setVisibility(View.VISIBLE);
			smoothScrollTo(-menuWidth);
			setMenuState();
		} else if (mSlidView.getScrollX() == -menuWidth) {
			smoothScrollTo(menuWidth);
			setMenuState();
		}
	}
	
	/**
	 * ������ʵ�ִ���رյĻ���Ч��
	 * @param distanceX
	 */
	private void smoothScrollTo(int distanceX) {
		mScroller.startScroll(mSlidView.getScrollX(), 0, 
				distanceX, 0, ConstantQuantity.DURATION_TIME);
		invalidate();
	}
	
	/**
	 * ���ò˵��򿪻�ر�״̬
	 */
	private void setMenuState() {
		if (menuState == ConstantQuantity.MENU_STATE_CLOSE) {
			menuState = ConstantQuantity.MENU_STATE_OPEN;
			// mSlidView.setEnabled(false);
			System.out.println("�˵�����");
		} else {
			menuState = ConstantQuantity.MENU_STATE_CLOSE;
			// mSlidView.setEnabled(true);
			System.out.println("�˵����ر�");
		}
	}

	

}