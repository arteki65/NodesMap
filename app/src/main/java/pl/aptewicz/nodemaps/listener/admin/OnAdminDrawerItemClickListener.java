package pl.aptewicz.nodemaps.listener.admin;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import pl.aptewicz.nodemaps.FindAddressActivity;
import pl.aptewicz.nodemaps.MapResult;
import pl.aptewicz.nodemaps.model.FtthCheckerUser;

public class OnAdminDrawerItemClickListener implements OnItemClickListener {

	private final MapResult mapResult;
	private final FtthCheckerUser ftthCheckerUser;

	public OnAdminDrawerItemClickListener(MapResult mapResult, FtthCheckerUser ftthCheckerUser) {
		this.mapResult = mapResult;
		this.ftthCheckerUser = ftthCheckerUser;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		mapResult.drawerLayout.closeDrawer(mapResult.drawerList);
		if (position == 0) {
			Intent intent = new Intent(mapResult, FindAddressActivity.class);
			intent.putExtra(FtthCheckerUser.FTTH_CHECKER_USER_KEY, ftthCheckerUser);
			intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
			mapResult.startActivity(intent);
		}
	}
}
