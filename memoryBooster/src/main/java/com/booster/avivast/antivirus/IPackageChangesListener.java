package com.booster.avivast.antivirus;

import android.content.Intent;

public interface IPackageChangesListener
{
	public void OnPackageAdded(Intent intent);
	public void OnPackageRemoved(Intent intent);
}
