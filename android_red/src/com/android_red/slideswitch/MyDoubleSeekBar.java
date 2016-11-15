package com.android_red.slideswitch;

import java.math.BigDecimal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageView;

import com.android_red.intenter.Const;
import com.example.android_red.R;
/**
 * 双游标自定义seekbar
 * 优点�?
 * 1）不会与其他滑动事件冲突，可用于类似侧滑菜单的布�?��
 * 2）自定义游标，带textView跟随滑动
 * 
 * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
 * @author Peter Sinnott (psinnott@gmail.com)
 * @author Thomas Barrasso (tbarrasso@sevenplusandroid.org)
 * @author Yao (chuan_28049@126.com)
 * @author Victor Shi (2015/8/3)
 * 
 * @param <T>
 *            The Number type of the range values. One of Long, Double, Integer,
 *            Float, Short, Byte or BigDecimal.
 */
public class MyDoubleSeekBar<T extends Number> extends ImageView {
	private static final String TAG = MyDoubleSeekBar.class.getSimpleName();
	private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint thumbValuePaint = getThumbValuePaint();
	private final Bitmap thumbImage = BitmapFactory.decodeResource(getResources(), R.drawable.thumb_normal);
	private final Bitmap thumbPressedImage = BitmapFactory.decodeResource(getResources(), R.drawable.thumb_hover);
	//private final Bitmap tvImage = BitmapFactory.decodeResource(getResources(), R.drawable.tv_seekbar_thumb);//thumb上方背景
	
	private final float thumbWidth = thumbImage.getWidth();
	private final float thumbHalfWidth = 0.5f * thumbWidth;
	private final float thumbHalfHeight = 0.5f * thumbImage.getHeight();
	private final float padding = thumbHalfWidth;
	private final T absoluteMinValue, absoluteMaxValue;
	private final NumberType numberType;
	private final double absoluteMinValuePrim, absoluteMaxValuePrim;
	private double normalizedMinValue = 0d;
	private double normalizedMaxValue = 1d;
	private Thumb pressedThumb = null;
	private boolean notifyWhileDragging = false;
	private OnRangeSeekBarChangeListener<T> listener;

	/**
	 * Default color of a {@link MyDoubleSeekBar}, #FF33B5E5. This is also known as
	 * "Ice Cream Sandwich" blue.
	 */
	public static final int DEFAULT_COLOR = Color.argb(0xFF, 0x33, 0xB5, 0xE5);

	/**
	 * An invalid pointer id.
	 */
	public static final int INVALID_POINTER_ID = 255;

	// Localized constants from MotionEvent for compatibility
	// with API < 8 "Froyo".
	public static final int ACTION_POINTER_UP = 0x6, ACTION_POINTER_INDEX_MASK = 0x0000ff00,
			ACTION_POINTER_INDEX_SHIFT = 8;

	private float mDownMotionX;// ��¼touchEvent����ʱ��X����
	private int mActivePointerId = INVALID_POINTER_ID;

	/**
	 * On touch, this offset plus the scaled value from the position of the
	 * touch will form the progress value. Usually 0.
	 */
	float mTouchProgressOffset;

	private int mScaledTouchSlop;
	private boolean mIsDragging;

	/**
	 * Creates a new RangeSeekBar.
	 * 
	 * @param absoluteMinValue
	 *            The minimum value of the selectable range.
	 * @param absoluteMaxValue
	 *            The maximum value of the selectable range.
	 * @param context
	 * @throws IllegalArgumentException
	 *             Will be thrown if min/max value type is not one of Long,
	 *             Double, Integer, Float, Short, Byte or BigDecimal.
	 */
	public MyDoubleSeekBar(T absoluteMinValue, T absoluteMaxValue, Context context) throws IllegalArgumentException {
		super(context);
		this.absoluteMinValue = absoluteMinValue;
		this.absoluteMaxValue = absoluteMaxValue;
		absoluteMinValuePrim = absoluteMinValue.doubleValue();//ֵ
		absoluteMaxValuePrim = absoluteMaxValue.doubleValue();
		numberType = NumberType.fromNumber(absoluteMinValue);// �õ��������ֵ�ö������

		// make RangeSeekBar focusable. This solves focus handling issues in
		// case EditText widgets are being used along with the RangeSeekBar
		// within ScollViews.
		setFocusable(true);
		setFocusableInTouchMode(true);
		init();
	}

	private final void init() {
		// ����Ϊ�Ǵ�����������̾���?
		mScaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	/**
	 * ���ⲿactivity���ã������Ƕ����϶���ʱ���ӡlog��Ϣ��Ĭ����false����ӡ
	 */
	public boolean isNotifyWhileDragging() {
		return notifyWhileDragging;
	}

	/**
	 * Should the widget notify the listener callback while the user is still
	 * dragging a thumb? Default is false.
	 * 
	 * @param flag
	 */
	public void setNotifyWhileDragging(boolean flag) {
		this.notifyWhileDragging = flag;
	}

	/**
	 * Returns the absolute minimum value of the range that has been set at
	 * construction time.
	 * 
	 * @return The absolute minimum value of the range.
	 */
	public T getAbsoluteMinValue() {
		return absoluteMinValue;
	}

	/**
	 * Returns the absolute maximum value of the range that has been set at
	 * construction time.
	 * 
	 * @return The absolute maximum value of the range.
	 */
	public T getAbsoluteMaxValue() {
		return absoluteMaxValue;
	}

	/**
	 * Returns the currently selected min value.
	 * 
	 * @return The currently selected min value.
	 */
	public T getSelectedMinValue() {
		return normalizedToValue(normalizedMinValue);
	}

	/**
	 * Sets the currently selected minimum value. The widget will be invalidated
	 * and redrawn.
	 * 
	 * @param value
	 *            The Number value to set the minimum value to. Will be clamped
	 *            to given absolute minimum/maximum range.
	 */
	public void setSelectedMinValue(T value) {
		// in case absoluteMinValue == absoluteMaxValue, avoid division by zero
		// when normalizing.
		if (0 == (absoluteMaxValuePrim - absoluteMinValuePrim)) {
			// activity���õ����ֵ����Сֵ���
			setNormalizedMinValue(0d);
		} else {
			setNormalizedMinValue(valueToNormalized(value));
		}
	}

	/**
	 * Returns the currently selected max value.
	 * 
	 * @return The currently selected max value.
	 */
	public T getSelectedMaxValue() {
		return normalizedToValue(normalizedMaxValue);
	}

	/**
	 * Sets the currently selected maximum value. The widget will be invalidated
	 * and redrawn.
	 * 
	 * @param value
	 *            The Number value to set the maximum value to. Will be clamped
	 *            to given absolute minimum/maximum range.
	 */
	public void setSelectedMaxValue(T value) {
		// in case absoluteMinValue == absoluteMaxValue, avoid division by zero
		// when normalizing.
		if (0 == (absoluteMaxValuePrim - absoluteMinValuePrim)) {
			setNormalizedMaxValue(1d);
		} else {
			setNormalizedMaxValue(valueToNormalized(value));
		}
	}

	/**
	 * Registers given listener callback to notify about changed selected
	 * values.
	 * 
	 * @param listener
	 *            The listener to notify about changed selected values.
	 */
	public void setOnRangeSeekBarChangeListener(OnRangeSeekBarChangeListener<T> listener) {
		this.listener = listener;
	}

	/**
	 * Handles thumb selection and movement. Notifies listener callback on
	 * certain events.
	 * 
	 * 
	 * ACTION_MASK��Android����Ӧ���ڶ�㴥�������������ϵ���˼����Ƕ����������˼�ɡ�?
	 * ��onTouchEvent(MotionEvent event)�У�ʹ��switch
	 * (event.getAction())���Դ���ACTION_DOWN��ACTION_UP�¼���ʹ��switch
	 * (event.getAction() & MotionEvent.ACTION_MASK)
	 * �Ϳ��Դ������㴥����ACTION_POINTER_DOWN��ACTION_POINTER_UP�¼���
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!isEnabled())
			return false;

		int pointerIndex;// ��¼������index

		final int action = event.getAction();
		switch (action & MotionEvent.ACTION_MASK) {

		case MotionEvent.ACTION_DOWN:
			// Remember where the motion event started
			// event.getPointerCount() -
			// 1�õ����һ�������Ļ�ĵ㣬����ĵ�id��0��event.getPointerCount() - 1
			mActivePointerId = event.getPointerId(event.getPointerCount() - 1);
			pointerIndex = event.findPointerIndex(mActivePointerId);
			mDownMotionX = event.getX(pointerIndex);// �õ�pointerIndex������X����

			pressedThumb = evalPressedThumb(mDownMotionX);// �ж�touch���������ֵthumb������Сֵthumb

			// Only handle thumb presses.
			if (pressedThumb == null)
				return super.onTouchEvent(event);

			setPressed(true);// ���øÿؼ���������
			invalidate();// ִ֪ͨ��onDraw����
			onStartTrackingTouch();// ��mIsDraggingΪtrue����ʼ׷��touch�¼�
			trackTouchEvent(event);
			attemptClaimDrag();

			break;
		case MotionEvent.ACTION_MOVE:
			if (pressedThumb != null) {

				if (mIsDragging) {
					trackTouchEvent(event);
				} else {
					// Scroll to follow the motion event
					pointerIndex = event.findPointerIndex(mActivePointerId);
					final float x = event.getX(pointerIndex);// ��ָ�ڿؼ��ϵ��X����
					// ��ָû�е��������Сֵ�ϣ������ڿؼ����л����¼�?
					if (Math.abs(x - mDownMotionX) > mScaledTouchSlop) {
						setPressed(true);
						invalidate();
						onStartTrackingTouch();
						trackTouchEvent(event);
						attemptClaimDrag();
					}
				}

				if (notifyWhileDragging && listener != null) {
					listener.onRangeSeekBarValuesChanged(this, getSelectedMinValue(), getSelectedMaxValue());
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mIsDragging) {
				trackTouchEvent(event);
				onStopTrackingTouch();
				setPressed(false);
			} else {
				// Touch up when we never crossed the touch slop threshold
				// should be interpreted as a tap-seek to that location.
				onStartTrackingTouch();
				trackTouchEvent(event);
				onStopTrackingTouch();
			}

			pressedThumb = null;// ��ָ̧�����ñ�touch����thumbΪ��
			invalidate();
			if (listener != null) {
				listener.onRangeSeekBarValuesChanged(this, getSelectedMinValue(), getSelectedMaxValue());
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN: {
			final int index = event.getPointerCount() - 1;
			// final int index = ev.getActionIndex();
			mDownMotionX = event.getX(index);
			mActivePointerId = event.getPointerId(index);
			invalidate();
			break;
		}
		case MotionEvent.ACTION_POINTER_UP:
			onSecondaryPointerUp(event);
			invalidate();
			break;
		case MotionEvent.ACTION_CANCEL:
			if (mIsDragging) {
				onStopTrackingTouch();
				setPressed(false);
			}
			invalidate(); // see above explanation
			break;
		}
		return true;
	}

	private final void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = (ev.getAction() & ACTION_POINTER_INDEX_MASK) >> ACTION_POINTER_INDEX_SHIFT;

		final int pointerId = ev.getPointerId(pointerIndex);
		if (pointerId == mActivePointerId) {
			// This was our active pointer going up. Choose
			// a new active pointer and adjust accordingly.
			// TODO: Make this decision more intelligent.
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mDownMotionX = ev.getX(newPointerIndex);
			mActivePointerId = ev.getPointerId(newPointerIndex);
		}
	}

	/**
	 * һֱ׷��touch�¼���ˢ��view
	 * 
	 * @param event
	 */
	private final void trackTouchEvent(MotionEvent event) {
		final int pointerIndex = event.findPointerIndex(mActivePointerId);// �õ����µ��index
		final float x = event.getX(pointerIndex);// �õ���ǰpointerIndex����Ļ�ϵ�x����

		if (Thumb.MIN.equals(pressedThumb)) {
			// screenToNormalized(x)-->�õ���񻯵�?-1��ֵ
			setNormalizedMinValue(screenToNormalized(x));
		} else if (Thumb.MAX.equals(pressedThumb)) {
			setNormalizedMaxValue(screenToNormalized(x));
		}
	}

	/**
	 * Tries to claim the user's drag motion, and requests disallowing any
	 * ancestors from stealing events in the drag.
	 * 
	 * ��ͼ���߸�view��Ҫ�����ӿؼ���drag
	 */
	private void attemptClaimDrag() {
		if (getParent() != null) {
			getParent().requestDisallowInterceptTouchEvent(true);
		}
	}

	/**
	 * This is called when the user has started touching this widget.
	 */
	void onStartTrackingTouch() {
		mIsDragging = true;
	}

	/**
	 * This is called when the user either releases his touch or the touch is
	 * canceled.
	 */
	void onStopTrackingTouch() {
		mIsDragging = false;
	}

	/**
	 * Ensures correct size of the widget.
	 */
	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = 200;
		if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
			width = MeasureSpec.getSize(widthMeasureSpec);
		}
		int height = thumbImage.getHeight();
		if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
			height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec)) + (int) getFontHeight(thumbValuePaint)
					* 3;
		}
		setMeasuredDimension(width, height);
	}

	/**
	 * Draws the widget on the given canvas.
	 */
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Bitmap l_bg = BitmapFactory.decodeResource(getResources(), R.drawable.bjno11);
		Bitmap m_bg = BitmapFactory.decodeResource(getResources(), R.drawable.bjy12);
		Bitmap r_bg = BitmapFactory.decodeResource(getResources(), R.drawable.bjno11);
		Bitmap m_progress = BitmapFactory.decodeResource(getResources(), R.drawable.bjy12);

		canvas.drawBitmap(l_bg, padding - thumbHalfWidth, 0.5f * (getHeight() - l_bg.getHeight()), paint);

		float bg_middle_left = padding - thumbHalfWidth + l_bg.getWidth();// 获取初始状�?下中间部分的左边界坐�?
		float bg_middle_right = getWidth() - padding + thumbHalfWidth - l_bg.getWidth();// 获取初始状�?下中间部分的右边界坐�?
		
		float m_scale = (bg_middle_right - bg_middle_left) / m_progress.getWidth();// 获得中间部分变化比例
		Matrix m_mx = new Matrix();
		m_mx.postScale(m_scale, 1f);
		Bitmap m_bg_new = Bitmap.createBitmap(m_bg, 0, 0, m_progress.getWidth(), m_progress.getHeight(), m_mx, true);
		canvas.drawBitmap(m_bg_new, bg_middle_left, 0.5f * (getHeight() - m_bg.getHeight()), paint);

		canvas.drawBitmap(r_bg, bg_middle_right, 0.5f * (getHeight() - r_bg.getHeight()), paint);

		float rangeL = normalizedToScreen(normalizedMinValue);
		float rangeR = normalizedToScreen(normalizedMaxValue);
		// float length = rangeR - rangeL;
		float left_scale = rangeL / l_bg.getWidth(); //左边缩放比例
		float pro_scale = (rangeR - rangeL) / m_progress.getWidth(); //中间缩放比例
		float right_scale = (getWidth() - rangeR) /  r_bg.getWidth(); //右边缩放比例
		if(left_scale > 0) {
			Matrix left_mx = new Matrix();
			left_mx.postScale(left_scale, 1f);
			
			Bitmap l_bg_new = Bitmap.createBitmap(l_bg, 0, 0, l_bg.getWidth(), l_bg.getHeight(), left_mx, true);
			canvas.drawBitmap(l_bg_new, padding - thumbHalfWidth, 0.5f * (getHeight() - l_bg.getHeight()), paint);
		}
		if (pro_scale > 0) {

			Matrix pro_mx = new Matrix();
			pro_mx.postScale(pro_scale, 1f);
			try {

				Bitmap m_progress_new = Bitmap.createBitmap(m_progress, 0, 0, m_progress.getWidth(),
						m_progress.getHeight(), pro_mx, true);

				canvas.drawBitmap(m_progress_new, rangeL, 0.5f * (getHeight() - m_progress.getHeight()), paint);
			} catch (Exception e) {
				Log.e(TAG,
						"IllegalArgumentException--width=" + m_progress.getWidth() + "Height=" + m_progress.getHeight()
								+ "pro_scale=" + pro_scale, e);

			}

		}
		if(right_scale > 0) {
			Matrix right_mx = new Matrix();
			right_mx.postScale(right_scale, 1f);
			
			Bitmap r_bg_new = Bitmap.createBitmap(r_bg, 0, 0, r_bg.getWidth(), r_bg.getHeight(), right_mx, true);
			canvas.drawBitmap(r_bg_new, rangeR, 0.5f * (getHeight() - r_bg.getHeight()), paint);
		}

		//绘画左右两个游标
		drawThumbMinValue(normalizedToScreen(normalizedMinValue), getSelectedMinValue() + "", canvas);//1111111111111
		drawThumbMaxValue(normalizedToScreen(normalizedMaxValue), getSelectedMaxValue() + "", canvas);//222222222222

		drawThumb(normalizedToScreen(normalizedMinValue), Thumb.MIN.equals(pressedThumb), canvas);

		drawThumb(normalizedToScreen(normalizedMaxValue), Thumb.MAX.equals(pressedThumb), canvas);
		int min = Integer.parseInt(getSelectedMinValue().toString());
		int max = Integer.parseInt(getSelectedMaxValue().toString());
		Const.jdt_min = min;
		Const.jdt_max = max;
        HightMainActivity.textviewP.setText("扫描时间间隔:"+min/10.0+"-"+max/10.0+"秒");	
	}


	@Override
	protected Parcelable onSaveInstanceState() {
		final Bundle bundle = new Bundle();
		bundle.putParcelable("SUPER", super.onSaveInstanceState());
		bundle.putDouble("MIN", normalizedMinValue);
		bundle.putDouble("MAX", normalizedMaxValue);
		return bundle;
	}

	/**
	 * Overridden to restore instance state when device orientation changes.
	 * This method is called automatically if you assign an id to the
	 * RangeSeekBar widget using the {@link #setId(int)} method.
	 */
	@Override
	protected void onRestoreInstanceState(Parcelable parcel) {
		final Bundle bundle = (Bundle) parcel;
		super.onRestoreInstanceState(bundle.getParcelable("SUPER"));
		normalizedMinValue = bundle.getDouble("MIN");
		normalizedMaxValue = bundle.getDouble("MAX");
	}


	private void drawThumb(float screenCoord, boolean pressed, Canvas canvas) {
		canvas.drawBitmap(pressed ? thumbPressedImage : thumbImage, screenCoord - thumbHalfWidth,
				(float) ((0.5f * getHeight()) - thumbHalfHeight), paint);
	}

	/**
	 * 绘制左游�?
	 * 
	 * @param screenCoord
	 * @param text
	 * @param canvas
	 */
	private void drawThumbMinValue(float screenCoord, String text, Canvas canvas) {

		// 右游标的起始�?
		float maxThumbleft = normalizedToScreen(normalizedMaxValue) - thumbHalfWidth;

		// 游标文字区域的右边界位置
		float textRight = screenCoord - thumbHalfWidth + getFontlength(thumbValuePaint, text);

//		if (textRight >= maxThumbleft) {
//			// 左游标与右游标重�?
//			if (pressedThumb == Thumb.MIN) {
//				// touch��Ϊmin
//				canvas.drawBitmap(tvImage, maxThumbleft - getFontlength(thumbValuePaint, text) - 8,
//						(float) ((0.15f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);
//				canvas.drawText(text, maxThumbleft - getFontlength(thumbValuePaint, text) - 2,
//						(float) ((0.4f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);
//				
//			} else {
//				canvas.drawBitmap(tvImage, textRight - getFontlength(thumbValuePaint, text) - 8,
//						(float) ((0.15f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);
//				canvas.drawText(text, textRight - getFontlength(thumbValuePaint, text) - 2,
//						(float) ((0.4f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);
//			}
//
//		} else {
//			// 正常情况
//			canvas.drawBitmap(tvImage, screenCoord - thumbHalfWidth - 8,
//					(float) ((0.15f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);
//			canvas.drawText(text, screenCoord - thumbHalfWidth - 2, (float) ((0.4f * getHeight()) - thumbHalfHeight) - 3,
//					thumbValuePaint);
//
//		}

	}


	private void drawThumbMaxValue(float screenCoord, String text, Canvas canvas) {

		// 左游标的右边�?
		float minThumbValueRight = normalizedToScreen(normalizedMinValue) - thumbHalfWidth
				+ getFontlength(thumbValuePaint, "��" + getSelectedMinValue());

		// 游标文字区域的右边界位置
		float textRight = screenCoord - thumbHalfWidth + getFontlength(thumbValuePaint, text);

//		if (textRight >= getWidth()) {
//			// 右边界超出or等于seekbar宽度
//			canvas.drawBitmap(tvImage, getWidth() - getFontlength(thumbValuePaint, text) - 8,
//					(float) ((0.15f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);
//			canvas.drawText(text, getWidth() - getFontlength(thumbValuePaint, text) - 1,
//					(float) ((0.4f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);
//
//		} else if ((screenCoord - thumbHalfWidth) <= minThumbValueRight) {
//			// 左右游标重叠
//			if (pressedThumb == Thumb.MAX) {
//
//				canvas.drawBitmap(tvImage, minThumbValueRight - 8, (float) ((0.15f * getHeight()) - thumbHalfHeight) - 3,
//						thumbValuePaint);
//				canvas.drawText(text, minThumbValueRight - 1, (float) ((0.4f * getHeight()) - thumbHalfHeight) - 3,
//						thumbValuePaint);
//
//			} else  {
//				canvas.drawBitmap(tvImage, screenCoord - thumbHalfWidth - 8,
//						(float) ((0.15f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);
//				canvas.drawText(text, screenCoord - thumbHalfWidth - 1,
//						(float) ((0.4f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);
//
//			}
//
//		} else {
//			//正常情况
//			canvas.drawBitmap(tvImage, screenCoord - thumbHalfWidth - 8,
//					(float) ((0.15f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);
//			canvas.drawText(text, screenCoord - thumbHalfWidth - 1, (float) ((0.4f * getHeight()) - thumbHalfHeight) - 3,
//					thumbValuePaint);
//		}

	}


	private Paint getThumbValuePaint() {
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		p.setAntiAlias(true);
		p.setFilterBitmap(true);
		p.setTextSize(25);

		return p;
	}


	private float getFontlength(Paint paint, String str) {
		return paint.measureText(str);
	}


	private float getFontHeight(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		return fm.descent - fm.ascent;
	}


	private Thumb evalPressedThumb(float touchX) {
		Thumb result = null;
		boolean minThumbPressed = isInThumbRange(touchX, normalizedMinValue);
		boolean maxThumbPressed = isInThumbRange(touchX, normalizedMaxValue);
		if (minThumbPressed && maxThumbPressed) {

			result = (touchX / getWidth() > 0.5f) ? Thumb.MIN : Thumb.MAX;
		} else if (minThumbPressed) {
			result = Thumb.MIN;
		} else if (maxThumbPressed) {
			result = Thumb.MAX;
		}
		return result;
	}


	private boolean isInThumbRange(float touchX, double normalizedThumbValue) {

		return Math.abs(touchX - normalizedToScreen(normalizedThumbValue)) <= thumbHalfWidth;
	}

	/**
	 * Sets normalized min value to value so that 0 <= value <= normalized max
	 * value <= 1. The View will get invalidated when calling this method.
	 * 
	 * @param value
	 *            The new normalized min value to set.
	 */
	public void setNormalizedMinValue(double value) {
		normalizedMinValue = Math.max(0d, Math.min(1d, Math.min(value, normalizedMaxValue)));
		invalidate();
	}

	/**
	 * Sets normalized max value to value so that 0 <= normalized min value <=
	 * value <= 1. The View will get invalidated when calling this method.
	 * 
	 * @param value
	 *            The new normalized max value to set.
	 */
	public void setNormalizedMaxValue(double value) {
		normalizedMaxValue = Math.max(0d, Math.min(1d, Math.max(value, normalizedMinValue)));
		invalidate();
	}

	
	@SuppressWarnings("unchecked")
	private T normalizedToValue(double normalized) {
		return (T) numberType.toNumber(absoluteMinValuePrim + normalized
				* (absoluteMaxValuePrim - absoluteMinValuePrim));
	}

	
	private double valueToNormalized(T value) {
		if (0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
			// prevent division by zero, simply return 0.
			return 0d;
		}
		return (value.doubleValue() - absoluteMinValuePrim) / (absoluteMaxValuePrim - absoluteMinValuePrim);
	}

	
	private float normalizedToScreen(double normalizedCoord) {
		return (float) (padding + normalizedCoord * (getWidth() - 2 * padding));
		
	}

	/**
	 * Converts screen space x-coordinates into normalized values.
	 * 
	 * @param screenCoord
	 *            The x-coordinate in screen space to convert.
	 * @return The normalized value.
	 */
	private double screenToNormalized(float screenCoord) {
		int width = getWidth();
		if (width <= 2 * padding) {
			// prevent division by zero, simply return 0.
			return 0d;
		} else {
			double result = (screenCoord - padding) / (width - 2 * padding);
			return Math.min(1d, Math.max(0d, result));
		}
	}

	/**
	 * Callback listener interface to notify about changed range values.
	 * 
	 * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
	 * 
	 * @param <T>
	 *            The Number type the RangeSeekBar has been declared with.
	 */
	public interface OnRangeSeekBarChangeListener<T> {
		public void onRangeSeekBarValuesChanged(MyDoubleSeekBar<?> bar, T minValue, T maxValue);
	}


	private static enum Thumb {
		MIN, MAX
	};


	private static enum NumberType {
		LONG, DOUBLE, INTEGER, FLOAT, SHORT, BYTE, BIG_DECIMAL;

		public static <E extends Number> NumberType fromNumber(E value) throws IllegalArgumentException {
			if (value instanceof Long) {
				return LONG;
			}
			if (value instanceof Double) {
				return DOUBLE;
			}
			if (value instanceof Integer) {
				return INTEGER;
			}
			if (value instanceof Float) {
				return FLOAT;
			}
			if (value instanceof Short) {
				return SHORT;
			}
			if (value instanceof Byte) {
				return BYTE;
			}
			if (value instanceof BigDecimal) {
				return BIG_DECIMAL;
			}
			throw new IllegalArgumentException("Number class '" + value.getClass().getName() + "' is not supported");
		}

		public Number toNumber(double value) {
			switch (this) {
			case LONG:
				return new Long((long) value);
			case DOUBLE:
				return value;
			case INTEGER:
				return new Integer((int) value);
			case FLOAT:
				return new Float(value);
			case SHORT:
				return new Short((short) value);
			case BYTE:
				return new Byte((byte) value);
			case BIG_DECIMAL:
				return new BigDecimal(value);
			}
			throw new InstantiationError("can't convert " + this + " to a Number object");
		}
	}
}