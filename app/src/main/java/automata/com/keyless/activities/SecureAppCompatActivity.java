package automata.com.keyless.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Protects Activity with a password if user configured one
 */
public class SecureAppCompatActivity extends AppCompatActivity {

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppLockActivity.REQUEST_CODE) {
            AppLockActivity.handleLockResponse(this, resultCode);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AppLockActivity.protectWithLock(this, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppLockActivity.protectWithLock(this, false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLockActivity.protectWithLock(this,true);
    }
}
