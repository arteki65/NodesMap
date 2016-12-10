package pl.aptewicz.nodemaps.listener;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import pl.aptewicz.nodemaps.FindAddressActivity;
import pl.aptewicz.nodemaps.model.FtthCheckerUser;

public class OnAdminDrawerItemClickListener implements OnItemClickListener {

	private final Context context;
	private final FtthCheckerUser ftthCheckerUser;

	public OnAdminDrawerItemClickListener(Context context, FtthCheckerUser ftthCheckerUser) {
		this.context = context;
		this.ftthCheckerUser = ftthCheckerUser;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(position == 0) {
			Intent intent = new Intent(context, FindAddressActivity.class);
			intent.putExtra(FtthCheckerUser.FTTH_CHECKER_USER_KEY, ftthCheckerUser);
			context.startActivity(intent);
		}
	}
}
