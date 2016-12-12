package pl.aptewicz.nodemaps.ui.admin;
import android.support.annotation.StringRes;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.View;

import pl.aptewicz.nodemaps.MapResult;
import pl.aptewicz.nodemaps.R;

public class AdminActionBarDrawerToogle extends ActionBarDrawerToggle {

	private final MapResult mapResult;
	private final DrawerLayout drawerLayout;

	public AdminActionBarDrawerToogle(MapResult mapResult, final DrawerLayout drawerLayout,
									  @StringRes
					int openDrawerContentDescRes,
									  @StringRes
					int closeDrawerContentDescRes) {
		super(mapResult, drawerLayout, mapResult.toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
		this.mapResult = mapResult;

		this.drawerLayout = drawerLayout;

		setDrawerIndicatorEnabled(false);
		setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
		setToolbarNavigationClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				drawerLayout.openDrawer(GravityCompat.START);
			}
		});
	}

	@Override
	public void onDrawerOpened(final View drawerView) {
		if (mapResult.getSupportActionBar() != null) {
			mapResult.getSupportActionBar()
					.setTitle(mapResult.getString(R.string.admin_menu_header));
		}
		setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
		setToolbarNavigationClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				drawerLayout.closeDrawer(GravityCompat.START);
			}
		});
		super.onDrawerOpened(drawerView);
	}

	@Override
	public void onDrawerClosed(View drawerView) {
		if (mapResult.getSupportActionBar() != null) {
			mapResult.getSupportActionBar().setTitle(mapResult.appTitle);
		}
		setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
		setToolbarNavigationClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				drawerLayout.openDrawer(GravityCompat.START);
			}
		});
		super.onDrawerClosed(drawerView);
	}
}
