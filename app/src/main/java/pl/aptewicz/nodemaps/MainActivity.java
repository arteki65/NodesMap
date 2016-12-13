package pl.aptewicz.nodemaps;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;

import pl.aptewicz.nodemaps.model.FtthCheckerUser;
import pl.aptewicz.nodemaps.model.FtthCheckerUserRole;
import pl.aptewicz.nodemaps.network.RequestQueueSingleton;
import pl.aptewicz.nodemaps.ui.admin.AdminMapActivity;
import pl.aptewicz.nodemaps.ui.serviceman.ServicemanMapActivity;

public class MainActivity extends AppCompatActivity {

	private Intent mapResultIntent;
	private RequestQueueSingleton requestQueueSingleton;
	private FtthCheckerUser ftthCheckerUser;
	private EditText usernameEditText;
	private EditText passwordEditText;
	private View progressView;
	private View loginFormView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));

		mapResultIntent = new Intent(this, MapResult.class);

		usernameEditText = (EditText) findViewById(R.id.usernameEditText);

		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					try {
						attemptLogin();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					return true;
				}
				return false;
			}
		});

		Button signInButton = (Button) findViewById(R.id.sign_in_button);
		signInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					attemptLogin();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		loginFormView = findViewById(R.id.login_form);
		progressView = findViewById(R.id.login_progress);

		requestQueueSingleton = RequestQueueSingleton.getInstance(this);

		showProgress(false);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		showProgress(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.settings:
				Intent settingsActivity = new Intent(getBaseContext(), SettingsActivity.class);
				startActivity(settingsActivity);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void attemptLogin() throws JSONException {
		resetErrors();

		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(password)) {
			passwordEditText.setError(getString(R.string.error_invalid_password));
			focusView = passwordEditText;
			cancel = true;
		}

		if (TextUtils.isEmpty(username)) {
			passwordEditText.setError(getString(R.string.error_field_required));
			focusView = passwordEditText;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			InputMethodManager inputManager = (InputMethodManager) getSystemService(
					Context.INPUT_METHOD_SERVICE);
			View v = getCurrentFocus();
			if (v != null) {
				inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}

			usernameEditText.setText("");
			passwordEditText.setText("");
			showProgress(true);
			authenticateUser(username, password);
		}
	}
	private void authenticateUser(String username, final String password) {
		//TODO: uncomment
		/*ftthCheckerUser = new FtthCheckerUser();
		1ftthCheckerUser.setUsername(username);
		ftthCheckerUser.setPassword(password);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String serverAddress = sharedPreferences.getString("server_address", "default");

		FtthCheckerRestApiRequest ftthCheckerRestApiRequest = new FtthCheckerRestApiRequest(
				Request.Method.GET, "http://" + serverAddress + "/PracaInzRest/user", null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						FtthCheckerUser ftthCheckerUserFromResponse = new Gson()
								.fromJson(response.toString(), FtthCheckerUser.class);
						ftthCheckerUserFromResponse.setPassword(ftthCheckerUser.getPassword());

						mapResultIntent.putExtra(FtthCheckerUser.FTTH_CHECKER_USER,
								ftthCheckerUserFromResponse);
						Toast.makeText(MainActivity.this, "Authorization succes",
								Toast.LENGTH_SHORT).show();
						startActivity(mapResultIntent);
						//showProgress(false);
					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				showProgress(false);
				if (error instanceof AuthFailureError) {
					Toast.makeText(MainActivity.this, "Authorization failed", Toast.LENGTH_SHORT)
							.show();
				}
			}
		}, ftthCheckerUser);

		requestQueueSingleton.addToRequestQueue(ftthCheckerRestApiRequest);*/

		ftthCheckerUser = new FtthCheckerUser();
		ftthCheckerUser.setUsername(username);
		ftthCheckerUser.setPassword(password);
		ftthCheckerUser.setFtthCheckerUserRole(FtthCheckerUserRole.SERVICEMAN);

		Intent intent = new Intent(this, ServicemanMapActivity.class);
		intent.putExtra(FtthCheckerUser.FTTH_CHECKER_USER, ftthCheckerUser);
		startActivity(intent);
	}

	private void resetErrors() {
		usernameEditText.setError(null);
		passwordEditText.setError(null);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			loginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
			loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
}
