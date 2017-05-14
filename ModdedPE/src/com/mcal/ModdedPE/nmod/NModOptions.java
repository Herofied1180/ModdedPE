package com.mcal.ModdedPE.nmod;
import android.content.*;
import java.util.*;

public class NModOptions
{
	private Context contextThis;
	public static final String TAG_SHARED_PREFERENCE = "nmod_list";
	public static final String TAG_ACTIVE_LIST = "nmod_active_list_tag";
	public static final String TAG_DISABLE_LIST = "nmod_disabled_list_tag";

	public NModOptions(Context thisContext)
	{
		contextThis = thisContext;
	}

	public Vector<String> getAllList()
	{
		Vector<String> ret = new Vector<String>();
		ret.addAll(getDisabledList());
		ret.addAll(getActiveList());
		return ret;
	}

	public void removeByName(String name)
	{
		SharedPreferences preferences=getSharedPreferences();
		Vector<String> activeList=getActiveList();
		Vector<String> disableList=getDisabledList();
		if (activeList.indexOf(name) != -1)
			activeList.remove(name);
		if (disableList.indexOf(name) != -1)
			disableList.remove(name);
		SharedPreferences.Editor editor=preferences.edit();
		editor.putString(TAG_ACTIVE_LIST, fromVector(activeList));
		editor.putString(TAG_ACTIVE_LIST, fromVector(disableList));
		editor.commit();
	}

	public void setIsActive(NMod nmod, boolean isActive)
	{
		if (isActive)
			addNewActive(nmod);
		else
			removeActive(nmod);
	}

	public boolean isActive(NMod nmod)
	{
		Vector<String> activeList=getActiveList();
		return activeList.indexOf(nmod.getPackageName()) != -1;
	}

	private SharedPreferences getSharedPreferences()
	{
		return contextThis.getSharedPreferences(TAG_SHARED_PREFERENCE, Context.MODE_MULTI_PROCESS);
	}

	private void addNewActive(NMod nmod)
	{
		SharedPreferences preferences=getSharedPreferences();
		Vector<String> activeList=getActiveList();
		Vector<String> disableList=getDisabledList();
		if (activeList.indexOf(nmod.getPackageName()) == -1)
			activeList.add(nmod.getPackageName());
		if (disableList.indexOf(nmod.getPackageName()) != -1)
			disableList.remove(nmod.getPackageName());
		SharedPreferences.Editor editor=preferences.edit();
		editor.putString(TAG_ACTIVE_LIST, fromVector(activeList));
		editor.putString(TAG_DISABLE_LIST, fromVector(disableList));
		editor.commit();
	}

	public void removeActive(NMod nmod)
	{
		SharedPreferences preferences=getSharedPreferences();
		Vector<String> activeList=getActiveList();
		Vector<String> disableList=getDisabledList();
		if (activeList.indexOf(nmod.getPackageName()) != -1)
			activeList.remove(nmod.getPackageName());
		if (disableList.indexOf(nmod.getPackageName()) == -1)
			disableList.add(nmod.getPackageName());
		SharedPreferences.Editor editor=preferences.edit();
		editor.putString(TAG_ACTIVE_LIST, fromVector(activeList));
		editor.putString(TAG_DISABLE_LIST, fromVector(disableList));
		editor.commit();
	}

	public void upNMod(NMod nmod)
	{
		SharedPreferences preferences=getSharedPreferences();
		Vector<String> activeList=getActiveList();
		int index=activeList.indexOf(nmod.getPackageName());
		if (index == -1 || index == 0)
			return;
		int indexFront=index - 1;
		String nameFront=activeList.get(indexFront);
		if (nameFront == null || nameFront.isEmpty())
			return;
		String nameSelf=nmod.getPackageName();
		activeList.set(indexFront, nameSelf);
		activeList.set(index, nameFront);
		SharedPreferences.Editor editor=preferences.edit();
		editor.putString(TAG_ACTIVE_LIST, fromVector(activeList));
		editor.commit();
	}

	public void downNMod(NMod nmod)
	{
		SharedPreferences preferences=getSharedPreferences();
		Vector<String> activeList=getActiveList();
		int index=activeList.indexOf(nmod.getPackageName());
		if (index == -1 || index == (activeList.size() - 1))
			return;
		int indexBack=index + 1;
		String nameBack=activeList.get(indexBack);
		if (nameBack == null || nameBack.isEmpty())
			return;
		String nameSelf=nmod.getPackageName();
		activeList.set(indexBack, nameSelf);
		activeList.set(index, nameBack);
		SharedPreferences.Editor editor=preferences.edit();
		editor.putString(TAG_ACTIVE_LIST, fromVector(activeList));
		editor.commit();
	}

	public Vector<String> getActiveList()
	{
		SharedPreferences preferences=getSharedPreferences();
		return toVector(preferences.getString(TAG_ACTIVE_LIST, ""));
	}
	
	public Vector<String> getDisabledList()
	{
		SharedPreferences preferences=getSharedPreferences();
		return toVector(preferences.getString(TAG_DISABLE_LIST, ""));
	}
	private static Vector<String> toVector(String str)
	{
		String[] mstr=str.split("|");
		Vector<String> list = new Vector<String>();
		if (mstr != null)
		{
			for (String strElement:mstr)
			{
				if (strElement != null && !strElement.isEmpty())
					list.add(strElement);
			}
		}
		return list;
	}

	private static String fromVector(Vector<String> vector)
	{
		String str="";
		if (vector != null)
		{
			for (String mstr:vector)
			{
				str += mstr;
				str += "|";
			}
		}
		return str;
	}
}
