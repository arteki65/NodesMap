package pl.aptewicz.nodemaps;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import pl.aptewicz.nodemaps.model.FtthCheckerUser;
import pl.aptewicz.nodemaps.service.FetchLocationConstants;
import pl.aptewicz.nodemaps.service.FetchLocationIntentService;

public class FindAddressActivity extends AppCompatActivity {

	private LocationResultReceiver locationResultReceiver;
	private EditText addressToFindEditText;
	private View findAddressView;
	private View progressView;
	private FtthCheckerUser ftthCheckerUser;

	@Override
	protected void onCreate(
			@Nullable
					Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_address);

		ftthCheckerUser = (FtthCheckerUser) getIntent()
				.getSerializableExtra(FtthCheckerUser.FTTH_CHECKER_USER_KEY);
		locationResultReceiver = new LocationResultReceiver(new Handler());

		addressToFindEditText = (EditText) findViewById(R.id.addressToFindEditText);
		findAddressView = findViewById(R.id.find_address_form);
		progressView = findViewById(R.id.find_address_progress);
	}

	public void findAddress(View view) {
		Intent intent = new Intent(this, FetchLocationIntentService.class);
		intent.putExtra(FetchLocationConstants.LOCATION_DATA_EXTRA,
				addressToFindEditText.getText().toString());
		intent.putExtra(FetchLocationConstants.RECEIVER, locationResultReceiver);
		startService(intent);
		showProgress(true);
	}

	private class LocationResultReceiver extends ResultReceiver {

		private LocationResultReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			String address = resultData.getString(FetchLocationConstants.RESULT_DATA_KEY);
			String[] splitedAddress = TextUtils.split(address, "\n");

			Intent intent = new Intent(FindAddressActivity.this, MapResult.class);
			intent.putExtra(FetchLocationConstants.LAT_LNG,
					splitedAddress[splitedAddress.length - 2] + " " + splitedAddress[
							splitedAddress.length - 1]);
			intent.putExtra(FtthCheckerUser.FTTH_CHECKER_USER_KEY, ftthCheckerUser);
			intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			FindAddressActivity.this.startActivity(intent);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			findAddressView.setVisibility(show ? View.GONE : View.VISIBLE);
			findAddressView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							findAddressView.setVisibility(show ? View.GONE : View.VISIBLE);
						}
					});

			progressView.setVisibility(show ? View.VISIBLE : View.GONE);
			progressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							progressView.setVisibility(show ? View.VISIBLE : View.GONE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			progressView.setVisibility(show ? View.VISIBLE : View.GONE);
			findAddressView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
}
