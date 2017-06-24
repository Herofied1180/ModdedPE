package org.mcal.moddedpe.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.mcal.moddedpe.R;
import org.mcal.pesdk.nmod.NMod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class NModLoadFailActivity extends BaseActivity
{
	private static final String KEY_TYPE_STRING = "type_string";
	private static final String KEY_MESSAGE = "message";
	private static final String KEY_TYPE = "type";
	private static final String KEY_PACKAGE_NAME = "package_name";
	private static final String KEY_ICON_PATH = "icon_path";
	private static final String KEY_MC_DATA = "mc_data";

	private ArrayList<Integer> mTypes = new ArrayList<Integer>();
	private ArrayList<String> mPackageNames = new ArrayList<String>();
	private ArrayList<String> mMessages = new ArrayList<String>();
	private ArrayList<String> mTypeStrings = new ArrayList<String>();
	private ArrayList<String> mIconPaths = new ArrayList<String>();
	private Bundle mMCData;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moddedpe_nmod_load_failed);

		mTypes = getIntent().getExtras().getIntegerArrayList(KEY_TYPE);
		mMessages = getIntent().getExtras().getStringArrayList(KEY_MESSAGE);
		mIconPaths = getIntent().getExtras().getStringArrayList(KEY_ICON_PATH);
		mTypeStrings = getIntent().getExtras().getStringArrayList(KEY_TYPE_STRING);
		mPackageNames = getIntent().getExtras().getStringArrayList(KEY_PACKAGE_NAME);
		mMCData = getIntent().getExtras().getBundle(KEY_MC_DATA);

		ListView errorListView = (ListView)findViewById(R.id.nmod_load_failed_list_view);
		errorListView.setAdapter(new ViewAdapter());
		
		findViewById(R.id.load_failed_next_button).setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View p1)
				{
					onNextClicked();
				}
				
			
		});
	}

	private class BuggedNModInfo
	{
		public String mTypeString;
		public String mMessage;
		public int mType;
		public String mPackageName;
		public String mIconPath;
	}

	private class ViewAdapter extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			return mPackageNames.size();
		}

		@Override
		public Object getItem(int p1)
		{
			return p1;
		}

		@Override
		public long getItemId(int p1)
		{
			return p1;
		}

		@Override
		public View getView(int p1, View p2, ViewGroup p3)
		{
			CardView view = (CardView)getLayoutInflater().inflate(R.layout.moddedpe_nmod_load_failed_item_card, null);
			AppCompatTextView packageNameTextView = (AppCompatTextView)view.findViewById(R.id.moddedpe_nmod_load_failed_item_card_package_name);
			packageNameTextView.setText(mPackageNames.get(p1));
			AppCompatTextView errorMessageTextView = (AppCompatTextView)view.findViewById(R.id.moddedpe_nmod_load_failed_item_card_message);
			errorMessageTextView.setText(getString(R.string.load_fail_msg, new Object[]{mTypeStrings.get(p1),mMessages.get(p1)}));
			AppCompatImageView imageViewIcon = (AppCompatImageView)view.findViewById(R.id.moddedpe_nmod_load_failed_item_card_icon);
			try
			{
				if (mIconPaths.get(p1) != null)
					imageViewIcon.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(mIconPaths.get(p1))));
				else
					imageViewIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mcd_null_pack));
			}
			catch (FileNotFoundException e)
			{
				imageViewIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mcd_null_pack));
			}
			final int index = p1;
			view.setOnClickListener(new View.OnClickListener()
				{

					@Override
					public void onClick(View p1)
					{
						new AlertDialog.Builder(NModLoadFailActivity.this).setTitle(R.string.load_fail_title).setMessage(getString(R.string.load_fail_msg, new Object[]{mTypeStrings.get(index),mMessages.get(index)})).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
							{

								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									p1.dismiss();
								}


							}).show();
					}
				});
			return view;
		}
	}

	private void onNextClicked()
	{
		Intent intent = new Intent(this, MinecraftActivity.class);
		intent.putExtras(mMCData);
		startActivity(intent);
		finish();
	}

	@Override
	public void onBackPressed()
	{

	}

	public static void startThisActivity(Context context, ArrayList<NMod> nmods, Bundle data)
	{
		Intent intent=new Intent(context, NModLoadFailActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bundle bundle = new Bundle();
		ArrayList<Integer> mTypes = new ArrayList<Integer>();
		ArrayList<String> mPackageNames = new ArrayList<String>();
		ArrayList<String> mMessages = new ArrayList<String>();
		ArrayList<String> mTypeStrings = new ArrayList<String>();
		ArrayList<String> mIconPaths = new ArrayList<String>();
		for (NMod nmod:nmods)
		{
			mTypes.add(nmod.getLoadException().getType());
			mPackageNames.add(nmod.getPackageName());
			mMessages.add(nmod.getLoadException().getCause().toString());
			mTypeStrings.add(nmod.getLoadException().toTypeString());
			File iconPath = nmod.copyIconToData();
			if (iconPath != null)
				mIconPaths.add(iconPath.getAbsolutePath());
			else
				mIconPaths.add(null);
		}
		bundle.putIntegerArrayList(KEY_TYPE, mTypes);
		bundle.putStringArrayList(KEY_MESSAGE, mMessages);
		bundle.putStringArrayList(KEY_ICON_PATH, mIconPaths);
		bundle.putStringArrayList(KEY_TYPE_STRING, mTypeStrings);
		bundle.putStringArrayList(KEY_PACKAGE_NAME, mPackageNames);
		bundle.putBundle(KEY_MC_DATA, data);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}
}
