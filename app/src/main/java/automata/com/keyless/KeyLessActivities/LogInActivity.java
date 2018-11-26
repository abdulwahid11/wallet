package automata.com.keyless.KeyLessActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import automata.com.keyless.R;

public class LogInActivity extends AppCompatActivity {

    Button create_id_btn;
    TextView signInTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        create_id_btn=(Button)findViewById(R.id.createID);
        signInTxt=(TextView)findViewById(R.id.logInTxt);

        create_id_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this,FaceEnrollmentActivity.class));

            }
        });
        signInTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LogInActivity.this, "signIn clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
