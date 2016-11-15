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
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import com.example.android_red.R;
/**
 * 单游标自定义seekbar
 * 优点�?
 * 1）不会与其他滑动事件冲突，可用于类似侧滑菜单的布�?��
 * 2）自定义游标，带textView跟随滑动
 * 
 * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
 * @author Peter Sinnott (psinnott@gmail.com)
 * @author Thomas Barrasso (tbarrasso@sevenplusandroid.org)
 * @author Yao (chuan_28049@126.com)
 * @author Victor Shi (2015/8/11)
 * 
 * @param <T>
 *            The Number type of the range values. One of Long, Double, Integer,
 *            Float, Short, Byte or BigDecimal.
 */
public class MySingleSeekBar<T extends Number> extends ImageView {
	private static final String TAG = MySingleSeekBar.class.getSimpleName();
	private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint thumbValuePaint = getThumbValuePaint();
	private final Bitmap thumbImage = BitmapFactory.decodeResource(getResources(), R.drawable.thumb_normal);
	
	//private final Bitmap tvImage = BitmapFactory.decodeResource(getResources(), R.drawable.tv_seekbar_thumb);//thumb上方背景
	
	private final Bitmap thumbPressedImage = BitmapFactory.decodeResource(getResources(), R.drawable.thumb_hover);
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

	public static final int DEFAULT_COLOR = Color.argb(0xFF, 0x33, 0xB5, 0xE5);

	public static final int INVALID_POINTER_ID = 255;

	public static final int ACTION_POINTER_UP = 0x6, ACTION_POINTER_INDEX_MASK = 0x0000ff00,
			ACTION_POINTER_INDEX_SHIFT = 8;

	private float mDownMotionX;
	private int mActivePointerId = INVALID_POINTER_ID;

	float mTouchProgressOffset;

	private int mScaledTouchSlop;
	private boolean mIsDragging;


	public MySingleSeekBar(T absoluteMinValue, T absoluteMaxValue, Context context) throws IllegalArgumentException {
		super(context);
		this.absoluteMinValue = absoluteMinValue;
		this.absoluteMaxValue = absoluteMaxValue;
		absoluteMinValuePrim = absoluteMinValue.doubleValue();
		absoluteMaxValuePrim = absoluteMaxValue.doubleValue();
		numberType = NumberType.fromNumber(absoluteMinValue);

		setFocusable(true);
		setFocusableInTouchMode(true);
		init();
	}

	private final void init() {
		mScaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}


	public boolean isNotifyWhileDragging() {
		return notifyWhileDragging;
	}

	public void setNotifyWhileDragging(boolean flag) {
		this.notifyWhileDragging = flag;
	}


	public T getAbsoluteMinValue() {
		return absoluteMinValue;
	}


	public T getAbsoluteMaxValue() {
		return absoluteMaxValue;
	}


	public T getSelectedMinValue() {
		return normalizedToValue(normalizedMinValue);
	}

	public void setSelectedMinValue(T value) {
		if (0 == (absoluteMaxValuePrim - absoluteMinValuePrim)) {
			setNormalizedMinValue(0d);
		} else {
			setNormalizedMinValue(valueToNormalized(value));
		}
	}

	public T getSelectedMaxValue() {
		return normalizedToValue(normalizedMaxValue);
	}


	public void setSelectedMaxValue(T value) {

		if (0 == (absoluteMaxValuePrim - absoluteMinValuePrim)) {
			setNormalizedMaxValue(1d);
		} else {
			setNormalizedMaxValue(valueToNormalized(value));
		}
	}


	public void setOnRangeSeekBarChangeListener(OnRangeSeekBarChangeListener<T> listener) {
		this.listener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!isEnabled())
			return false;

		int pointerIndex;

		final int action = event.getAction();
		switch (action & MotionEvent.ACTION_MASK) {

		case MotionEvent.ACTION_DOWN:

			mActivePointerId = event.getPointerId(event.getPointerCount() - 1);
			pointerIndex = event.findPointerIndex(mActivePointerId);
			mDownMotionX = event.getX(pointerIndex);

			pressedThumb = evalPressedThumb(mDownMotionX);

			if (pressedThumb == null)
				return super.onTouchEvent(event);

			setPressed(true);
			invalidate();
			onStartTrackingTouch();
			trackTouchEvent(event);
			attemptClaimDrag();

			break;
		case MotionEvent.ACTION_MOVE:
			if (pressedThumb != null) {

				if (mIsDragging) {
					trackTouchEvent(event);
				} else {
					pointerIndex = event.findPointerIndex(mActivePointerId);
					final float x = event.getX(pointerIndex);
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
				onStartTrackingTouch();
				trackTouchEvent(event);
				onStopTrackingTouch();
			}

			pressedThumb = null;
			invalidate();
			if (listener != null) {
				listener.onRangeSeekBarValuesChanged(this, getSelectedMinValue(), getSelectedMaxValue());
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN: {
			final int index = event.getPointerCount() - 1;
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
			invalidate(); 
			break;
		}
		return true;
	}

	private final void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = (ev.getAction() & ACTION_POINTER_INDEX_MASK) >> ACTION_POINTER_INDEX_SHIFT;

		final int pointerId = ev.getPointerId(pointerIndex);
		if (pointerId == mActivePointerId) {
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mDownMotionX = ev.getX(newPointerIndex);
			mActivePointerId = ev.getPointerId(newPointerIndex);
		}
	}


	private final void trackTouchEvent(MotionEvent event) {
		final int pointerIndex = event.findPointerIndex(mActivePointerId);
		final float x = event.getX(pointerIndex);

		if (Thumb.MIN.equals(pressedThumb)) {
			setNormalizedMinValue(screenToNormalized(x));
		}
		else if (Thumb.MAX.equals(pressedThumb)) {
			setNormalizedMaxValue(screenToNormalized(x));
		}
	}


	private void attemptClaimDrag() {
		if (getParent() != null) {
			getParent().requestDisallowInterceptTouchEvent(true);
		}
	}


	void onStartTrackingTouch() {
		mIsDragging = true;
	}


	void onStopTrackingTouch() {
		mIsDragging = false;
	}


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


//	@Override
//	protected synchronized void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
//
//		Bitmap l_bg = BitmapFactory.decodeResource(getResources(), R.drawable.green_seekbar);
//		Bitmap r_bg = BitmapFactory.decodeResource(getResources(), R.drawable.red_seekbar);
//		Bitmap m_progress = BitmapFactory.decodeResource(getResources(), R.drawable.red_seekbar);
//
//		canvas.drawBitmap(l_bg, padding - thumbHalfWidth, 0.5f * (getHeight() - l_bg.getHeight()), paint);
//
//		float bg_middle_left = padding - thumbHalfWidth + l_bg.getWidth();// ��Ҫƽ�̵��м䱳���Ŀ�ʼ����
//		float bg_middle_right = getWidth() - padding + thumbHalfWidth - l_bg.getWidth();// ��Ҫƽ�̵��м䱳���Ŀ�ʼ����
//		
//		float m_scale = (bg_middle_right - bg_middle_left) / m_progress.getWidth();// �ϲ������Сֵ�������m_progress����
//		Matrix m_mx = new Matrix();
//		m_mx.postScale(m_scale, 1f);
//		Bitmap m_bg_new = Bitmap.createBitmap(r_bg, 0, 0, m_progress.getWidth(), m_progress.getHeight(), m_mx, true);
//		canvas.drawBitmap(m_bg_new, bg_middle_left, 0.5f * (getHeight() - r_bg.getHeight()), paint);
//
//		float rangeL = normalizedToScreen(normalizedMinValue);
//		float rangeR = normalizedToScreen(normalizedMaxValue);
//		// float length = rangeR - rangeL;
//		float left_scale = rangeL / l_bg.getWidth();
//		float pro_scale = (rangeR - rangeL) / m_progress.getWidth();// �ϲ������Сֵ�������m_progress����
//
//		if(left_scale > 0) {
//			Matrix left_mx = new Matrix();
//			left_mx.postScale(left_scale, 1f);
//			
//			Bitmap l_bg_new = Bitmap.createBitmap(l_bg, 0, 0, l_bg.getWidth(), l_bg.getHeight(), left_mx, true);
//			canvas.drawBitmap(l_bg_new, padding - thumbHalfWidth, 0.5f * (getHeight() - l_bg.getHeight()), paint);
//		}
//		if (pro_scale > 0) {
//
//			Matrix pro_mx = new Matrix();
//			pro_mx.postScale(pro_scale, 1f);
//			try {
//
//				Bitmap m_progress_new = Bitmap.createBitmap(m_progress, 0, 0, m_progress.getWidth(),
//						m_progress.getHeight(), pro_mx, true);
//
//				canvas.drawBitmap(m_progress_new, rangeL, 0.5f * (getHeight() - m_progress.getHeight()), paint);
//			} catch (Exception e) {
//				// ��pro_scale�ǳ�С������width=12��Height=48��pro_scale=0.01989065ʱ��
//				// ��߰����������ֵΪ0.238��0.949��ϵͳǿתΪint�ͺ��ͱ��?�ˡ��ͳ��ַǷ������쳣
//				Log.e(TAG,
//						"IllegalArgumentException--width=" + m_progress.getWidth() + "Height=" + m_progress.getHeight()
//								+ "pro_scale=" + pro_scale, e);
//
//			}
//
//		}
//
//		//���minThumb�Ķ�Ӧ��ֵ
//		drawThumbMinValue(normalizedToScreen(normalizedMinValue), getSelectedMinValue() + "", canvas);  //1111111
//
//		// draw minimum thumb
//		drawThumb(normalizedToScreen(normalizedMinValue), Thumb.MIN.equals(pressedThumb), canvas);
//
//	}


	@Override
	protected Parcelable onSaveInstanceState() {
		final Bundle bundle = new Bundle();
		bundle.putParcelable("SUPER", super.onSaveInstanceState());
		bundle.putDouble("MIN", normalizedMinValue);
		bundle.putDouble("MAX", normalizedMaxValue);
		return bundle;
	}

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

	private void drawThumbMinValue(float screenCoord, String text, Canvas canvas) {

		float maxThumbleft = getWidth();
		float textRight = screenCoord - thumbHalfWidth + getFontlength(thumbValuePaint, text);

//		if (textRight >= maxThumbleft) {
//			if (pressedThumb == Thumb.MIN) {
//				canvas.drawBitmap(tvImage, maxThumbleft - getFontlength(thumbValuePaint, text) - 8,
//						(float) ((0.15f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);
//				canvas.drawText(text, maxThumbleft - getFontlength(thumbValuePaint, text) - 3,
//						(float) ((0.4f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);
//				
//			} else {
//				canvas.drawBitmap(tvImage, textRight - getFontlength(thumbValuePaint, text) - 8,
//						(float) ((0.15f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);
//				canvas.drawText(text, textRight - getFontlength(thumbValuePaint, text) - 3,
//						(float) ((0.4f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);
//			}
//
//			Log.e(TAG, "textRight>=maxThumbleft***textRight=" + textRight + "maxThumbleft=" + maxThumbleft);
//		} else {
//			canvas.drawBitmap(tvImage, screenCoord - thumbHalfWidth - 8,
//					(float) ((0.15f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);
//			canvas.drawText(text, screenCoord - thumbHalfWidth - 3, (float) ((0.4f * getHeight()) - thumbHalfHeight) - 3,
//					thumbValuePaint);
//
//			Log.i(TAG, "textRight<maxThumbleft***textRight=" + textRight + "maxThumbleft=" + maxThumbleft);
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


	public void setNormalizedMinValue(double value) {
		normalizedMinValue = Math.max(0d, Math.min(1d, Math.min(value, normalizedMaxValue)));
		invalidate();
	}


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
			return 0d;
		}
		return (value.doubleValue() - absoluteMinValuePrim) / (absoluteMaxValuePrim - absoluteMinValuePrim);
	}


	private float normalizedToScreen(double normalizedCoord) {
		return (float) (padding + normalizedCoord * (getWidth() - 2 * padding));
	}


	private double screenToNormalized(float screenCoord) {
		int width = getWidth();
		if (width <= 2 * padding) {
			return 0d;
		} else {
			double result = (screenCoord - padding) / (width - 2 * padding);
			return Math.min(1d, Math.max(0d, result));
		}
	}

	public interface OnRangeSeekBarChangeListener<T> {
		public void onRangeSeekBarValuesChanged(MySingleSeekBar<?> bar, T minValue, T maxValue);
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