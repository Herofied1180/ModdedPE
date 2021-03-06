package org.mcal.pesdk.nmod;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

class NModManager
{
	private ArrayList<NMod> mEnabledNMods=new ArrayList<>();
	private ArrayList<NMod> mAllNMods=new ArrayList<>();
	private ArrayList<NMod> mDisabledNMods=new ArrayList<>();
	private Context mContext;

	NModManager(Context context)
	{
		this.mContext = context;
	}

	ArrayList<NMod> getEnabledNMods()
	{
		return mEnabledNMods;
	}

	ArrayList<NMod> getEnabledNModsIsValidBanner()
	{
		ArrayList<NMod> ret=new ArrayList<>();
		for (NMod nmod:getEnabledNMods())
		{
			if (nmod.isValidBanner())
				ret.add(nmod);
		}
		return ret;
	}

	ArrayList<NMod> getAllNMods()
	{
		return mAllNMods;
	}

	void init()
	{
		mAllNMods = new ArrayList<>();
		mEnabledNMods = new ArrayList<>();
		mDisabledNMods = new ArrayList<>();

		NModDataLoader dataloader = new NModDataLoader(mContext);

		for (String item:dataloader.getAllList())
		{
			if (!PackageNameChecker.isValidPackageName(item))
			{
				dataloader.removeByName(item);
			}
		}

		forEachItemToAddNMod(dataloader.getEnabledList(), true);
		forEachItemToAddNMod(dataloader.getDisabledList(), false);
		refreshDatas();
	}

	void removeImportedNMod(NMod nmod)
	{
		mEnabledNMods.remove(nmod);
		mDisabledNMods.remove(nmod);
		mAllNMods.remove(nmod);
		NModDataLoader dataloader=new NModDataLoader(mContext);
		dataloader.removeByName(nmod.getPackageName());
		if (nmod.getNModType() == NMod.NMOD_TYPE_ZIPPED)
		{
			String zippedNModPath = new NModFilePathManager(mContext).getNModsDir() + File.separator + nmod.getPackageName();
			File file = new File(zippedNModPath);
			if (file.exists())
			{
				file.delete();
			}
		}
	}

	private void forEachItemToAddNMod(ArrayList<String> list, boolean enabled)
	{
		for (String packageName:list)
		{
			try
			{
				String zippedNModPath = new NModFilePathManager(mContext).getNModsDir() + File.separator + packageName;
				ZippedNMod zippedNMod = new ZippedNMod(packageName, mContext, new File(zippedNModPath));
				importNMod(zippedNMod, enabled);
				continue;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			try
			{
				NModExtractor extractor = new NModExtractor(mContext);
				PackagedNMod packagedNMod = extractor.archiveFromInstalledPackage(packageName);
				importNMod(packagedNMod, enabled);
			}
			catch (ExtractFailedException e)
			{
				e.printStackTrace();
			}
		}
	}

	boolean importNMod(NMod newNMod, boolean enabled)
	{
		boolean replaced = false;
		Iterator<NMod> iterator = mAllNMods.iterator();
		while (iterator.hasNext())
		{
			NMod nmod = iterator.next();
			if (nmod.equals(newNMod))
			{
				iterator.remove();
				mEnabledNMods.remove(nmod);
				mDisabledNMods.remove(nmod);
				replaced = true;
			}
		}

		mAllNMods.add(newNMod);
		if (enabled)
			setEnabled(newNMod);
		else
			setDisable(newNMod);
		return replaced;
	}



	private void refreshDatas()
	{
		NModDataLoader dataloader=new NModDataLoader(mContext);

		for (String item:dataloader.getAllList())
		{
			if (getImportedNMod(item) == null)
			{
				dataloader.removeByName(item);
			}
		}
	}

	private NMod getImportedNMod(String pkgname)
	{
		for (NMod nmod : mAllNMods)
			if (nmod.getPackageName().equals(pkgname))
				return nmod;
		return null;
	}

	void makeUp(NMod nmod)
	{
		NModDataLoader dataloader=new NModDataLoader(mContext);
		dataloader.upNMod(nmod);
		refreshEnabledOrderList();
	}

	void makeDown(NMod nmod)
	{
		NModDataLoader dataloader=new NModDataLoader(mContext);
		dataloader.downNMod(nmod);
		refreshEnabledOrderList();
	}

	private void refreshEnabledOrderList()
	{
		NModDataLoader dataloader=new NModDataLoader(mContext);
		ArrayList<String> enabledList = dataloader.getEnabledList();
		mEnabledNMods.clear();
		for (String pkgName : enabledList)
		{
			NMod nmod = getImportedNMod(pkgName);
			if (nmod != null)
			{
				mEnabledNMods.add(nmod);
			}
		}
	}

	void setEnabled(NMod nmod)
	{
		if (nmod.isBugPack())
			return;
		NModDataLoader dataloader=new NModDataLoader(mContext);
		dataloader.setIsEnabled(nmod, true);
		mEnabledNMods.add(nmod);
		mDisabledNMods.remove(nmod);
	}

	void setDisable(NMod nmod)
	{
		NModDataLoader dataloader=new NModDataLoader(mContext);
		dataloader.setIsEnabled(nmod, false);
		mDisabledNMods.add(nmod);
		mEnabledNMods.remove(nmod);
	}

	ArrayList<NMod> getDisabledNMods()
	{
		return mDisabledNMods;
	}
}
