package com.rabbit.magazine.view;

import com.rabbit.magazine.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TitleView extends RelativeLayout {

	public static final int TITLE_TEXT_ID = 0x01;

	TextView titleText;
	ImageView titleLogo;

	String titleTextStr;
	int titleTextColor;
	int titleLogoRes;

	public TitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TitleView);
		titleTextStr = ta.getString(R.styleable.TitleView_title_text);
		titleLogoRes = ta.getResourceId(R.styleable.TitleView_logo_res, -1);
		titleTextColor = ta.getColor(R.styleable.TitleView_title_text_color, -1);

		titleText = new TextView(context);
		titleText.setId(TITLE_TEXT_ID);
		titleText.setText(titleTextStr);
		titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		if (titleTextColor != -1) {
			titleText.setTextColor(titleTextColor);
		} else {
			titleText.setTextColor(Color.WHITE);
		}
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		this.addView(titleText, lp);

		if (titleLogoRes != -1) {
			titleLogo = new ImageView(context);
			titleLogo.setBackgroundResource(titleLogoRes);

			RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			lp2.addRule(RelativeLayout.LEFT_OF, TITLE_TEXT_ID);
			lp2.setMargins(0, 0, 8, 0);
			lp2.addRule(RelativeLayout.CENTER_VERTICAL);
			this.addView(titleLogo, lp2);
		}

	}
}
