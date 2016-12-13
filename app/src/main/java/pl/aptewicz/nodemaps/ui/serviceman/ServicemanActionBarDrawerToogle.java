package pl.aptewicz.nodemaps.ui.serviceman;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;

import pl.aptewicz.nodemaps.R;

public class ServicemanActionBarDrawerToogle extends ActionBarDrawerToggle {

    private final ServicemanMapActivity servicemanMapActivity;
    private final DrawerLayout drawerLayout;

    public ServicemanActionBarDrawerToogle(ServicemanMapActivity servicemanMapActivity, final DrawerLayout drawerLayout,
                                           int drawer_open, int drawer_closed) {
        super(servicemanMapActivity, drawerLayout, servicemanMapActivity.toolbar, drawer_open, drawer_closed);
        this.servicemanMapActivity = servicemanMapActivity;
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
        if (servicemanMapActivity.getSupportActionBar() != null) {
            servicemanMapActivity.getSupportActionBar()
                    .setTitle(servicemanMapActivity.getString(R.string.ftth_jobs_list_header));
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
        if (servicemanMapActivity.getSupportActionBar() != null) {
            servicemanMapActivity.getSupportActionBar().setTitle(servicemanMapActivity.appTitle);
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
