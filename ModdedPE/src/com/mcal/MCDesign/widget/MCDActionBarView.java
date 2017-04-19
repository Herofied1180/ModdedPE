package com.mcal.MCDesign.widget;
import android.widget.*;
import android.support.v7.widget.*;
import com.mcal.ModdedPE.resources.*;
import com.mcal.ModdedPE.*;
import android.graphics.*;

public class MCDActionBarView extends RelativeLayout
{
	public MCDActionBarView(android.content.Context context)
	{
		super(context);
	}

    public MCDActionBarView(android.content.Context context, android.util.AttributeSet attrs)
	{
		super(context,attrs);
	}

    public MCDActionBarView(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr)
	{
		super(context,attrs,defStyleAttr);
	}

    public MCDActionBarView(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context,attrs,defStyleAttr,defStyleRes);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		
		AppCompatImageView imageView=(AppCompatImageView)findViewById(R.id.mcd_actionbar_bg);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mcd_header_bg);  
		imageView.setImageBitmap(BitmapRepeater.createRepeater(w,h,bitmap));
		
	}
}
