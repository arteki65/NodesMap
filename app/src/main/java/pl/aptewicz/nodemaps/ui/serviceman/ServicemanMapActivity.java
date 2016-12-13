package pl.aptewicz.nodemaps.ui.serviceman;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.widget.ListView;

import pl.aptewicz.nodemaps.R;
import pl.aptewicz.nodemaps.listener.serviceman.OnFtthJobClickListener;
import pl.aptewicz.nodemaps.model.FtthCheckerUserRole;
import pl.aptewicz.nodemaps.model.FtthJob;
import pl.aptewicz.nodemaps.ui.AbstractMapActivity;
import pl.aptewicz.nodemaps.ui.adapter.FtthJobAdapter;

public class ServicemanMapActivity extends AbstractMapActivity {

    public String appTitle;

    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private FtthJob[] ftthJobs;
    private ServicemanActionBarDrawerToogle drawerToggle;

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.serviceman_map_activity);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appTitle = getTitle().toString();
        createDrawerList();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        createDrawerToogle();
    }

    private void createDrawerList() {
        drawerList = (ListView) findViewById(R.id.left_drawer);
        if (ftthCheckerUser != null && ftthCheckerUser.getFtthJobs() != null) {
            ftthJobs = new FtthJob[ftthCheckerUser.getFtthJobs().size()];
            ftthJobs = ftthCheckerUser.getFtthJobs().toArray(ftthJobs);
            drawerList.setAdapter(new FtthJobAdapter(this, R.layout.ftth_job_list_item, ftthJobs));
            //drawerList.setOnItemClickListener(new OnFtthJobClickListener(this));
        }
    }

    private void createDrawerToogle() {
        drawerToggle = new ServicemanActionBarDrawerToogle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_closed);
        drawerLayout.addDrawerListener(drawerToggle);
    }
}
