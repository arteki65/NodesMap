package pl.aptewicz.nodemaps.ui.admin;
import android.support.annotation.StringRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;

import pl.aptewicz.nodemaps.MapResult;
import pl.aptewicz.nodemaps.R;

public class AdminActionBarDrawerToogle extends ActionBarDrawerToggle {

	private final MapResult mapResult;

	public AdminActionBarDrawerToogle(MapResult mapResult, DrawerLayout drawerLayout,
			@StringRes
					int openDrawerContentDescRes,
			@StringRes
					int closeDrawerContentDescRes) {
		super(mapResult, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
		this.mapResult = mapResult;
	}

	@Override
	public void onDrawerOpened(View drawerView) {
		if (mapResult.getSupportActionBar() != null) {
			mapResult.getSupportActionBar()
					.setTitle(mapResult.getString(R.string.admin_menu_header));
		}
		super.onDrawerOpened(drawerView);
	}

	@Override
	public void onDrawerClosed(View drawerView) {
		if (mapResult.getSupportActionBar() != null) {
			mapResult.getSupportActionBar().setTitle(mapResult.appTitle);
		}
		super.onDrawerClosed(drawerView);
	}
}
