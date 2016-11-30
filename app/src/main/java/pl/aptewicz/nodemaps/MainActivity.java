package pl.aptewicz.nodemaps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

    private Button startButton;

    private Intent mapResultIntent;

    private EditText serverIpTextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapResultIntent = new Intent(MainActivity.this, MapResult.class);

        serverIpTextField = (EditText) findViewById(R.id.serverIpTextField);
        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapResultIntent.putExtra("serverIp", serverIpTextField.getText().toString());
                startActivity(mapResultIntent);
            }
        });
    }
}
