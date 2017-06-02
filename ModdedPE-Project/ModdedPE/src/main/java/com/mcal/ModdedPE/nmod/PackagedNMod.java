package com.mcal.ModdedPE.nmod;
import android.content.*;
import android.content.res.*;
import java.util.*;
import java.io.*;
import android.graphics.*;
import com.mcal.ModdedPE.nmod.NMod.*;
import com.mcal.ModdedPE.*;
import com.google.gson.*;
import android.content.pm.*;
import java.util.zip.*;

public class PackagedNMod extends NMod
{
	private Context packageContext;

	@Override
	public String getPackageResourcePath()
	{
		return getPackageContext().getPackageResourcePath();
	}

	@Override
	public NModPerloadBean copyNModFiles()
	{
		NModPerloadBean ret = new NModPerloadBean();
		ret.assets_path = getPackageResourcePath();
		return ret;
	}

	@Override
	public boolean isSupportedABI()
	{
		
		return false;
	}
	
	@Override
	public int getNModType()
	{
		return NMOD_TYPE_PACKAGED;
	}
	
	@Override
	public String getNativeLibsPath()
	{
		return getPackageContext().getApplicationInfo().nativeLibraryDir;
	}

	public PackagedNMod(Context contextThiz, Context packageContext)
	{
		super(contextThiz);
		this.packageContext = packageContext;
		preload();
	}

	public Context getPackageContext()
	{
		return packageContext;
	}

	public AssetManager getAssets()
	{
		return packageContext.getAssets();
	}

	public String getPackageName()
	{
		if (mInfo != null && mInfo.package_name != null)
			return mInfo.package_name;
		if (getPackageContext() == null)
			return toString();
		return getPackageContext().getPackageName();
	}

	public Bitmap createIcon()
	{
		try
		{
			PackageManager packageManager = getPackageContext().getPackageManager();
			PackageInfo packageInfo = null;
			packageInfo = packageManager.getPackageInfo(getPackageContext().getPackageName(), 0);
			int iconRes = packageInfo.applicationInfo.icon;
			return BitmapFactory.decodeResource(getPackageContext().getResources(), iconRes);
		}
		catch (PackageManager.NameNotFoundException e)
		{}
		return null;
	}

	@Override
	protected InputStream createInfoInputStream()
	{
		try
		{
			return getAssets().open(MANIFEST_NAME);
		}
		catch (IOException e)
		{
			return null;
		}
	}
}