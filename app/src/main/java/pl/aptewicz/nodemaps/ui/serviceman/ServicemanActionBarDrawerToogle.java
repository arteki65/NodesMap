package pl.aptewicz.nodemaps.ui.serviceman;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;

import pl.aptewicz.nodemaps.MapResult;
import pl.aptewicz.nodemaps.R;

public class ServicemanActionBarDrawerToogle extends ActionBarDrawerToggle {

	private final MapResult mapResult;

	public ServicemanActionBarDrawerToogle(MapResult mapResult, DrawerLayout drawerLayout,
			int drawer_open, int drawer_closed) {
		super(mapResult, drawerLayout, drawer_open, drawer_closed);
		this.mapResult = mapResult;
	}

	@Override
	public void onDrawerOpened(View drawerView) {
		if (mapResult.getSupportActionBar() != null) {
			mapResult.getSupportActionBar()
					.setTitle(mapResult.getString(R.string.ftth_jobs_list_header));
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
