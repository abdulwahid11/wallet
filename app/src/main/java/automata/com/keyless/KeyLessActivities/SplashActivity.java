package automata.com.keyless.KeyLessActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import automata.com.keyless.R;

public class SplashActivity extends AppCompatActivity {
    private Handler mWaitHandler = new Handler();

    SharedPreferences myPrefs;
    public static final String FILE_NAME="keyLessPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        myPrefs=getSharedPreferences(FILE_NAME,MODE_PRIVATE);


        mWaitHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                //The following code will execute after the 5 seconds.

                try {

                    //Go to next page i.e, start the next activity.
                    String pin=myPrefs.getString("pin","");
                    if(pin.length()!=0)
                    {

                        Intent intent = new Intent(getApplicationContext(), GraphActivity.class);
                        startActivity(intent);


                    }
                    else{

                        Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                        startActivity(intent);

                    }
                    //Let's Finish Splash Activity since we don't want to show this when user press back button.
                    finish();
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        }, 3000);  // Give a 5 seconds delay.

    }

}
