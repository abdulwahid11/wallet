package automata.com.keyless.KeyLessActivities;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import okhttp3.Response;
import automata.com.keyless.R;
import automata.com.keyless.fragments.FragmentGraph;
import automata.com.keyless.fragments.FragmentTransactionsAll;
import automata.com.keyless.fragments.FragmentWallets;
import automata.com.keyless.interfaces.NetworkUpdateListener;

public class GraphActivity extends AppCompatActivity implements NetworkUpdateListener {
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private ActionBar actionBar;

    public Fragment[] fragments;

    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        fragments = new Fragment[3];
        fragments[0] = new FragmentGraph();
        fragments[1] = new FragmentWallets();
        fragments[2] = new FragmentTransactionsAll();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
// Replace the contents of the container with the new fragment
        ft.replace(R.id.your_placeholder, new FragmentGraph());
// or ft.add(R.id.your_placeholder, new FooFragment());
// Complete the changes added above
        ft.commit();


        //  actionBar=getActionBar();
       // actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        dl = (DrawerLayout)findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl,R.string.open, R.string.close);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    /*
                    case R.id.account:
                        Toast.makeText(GraphActivity.this, "My Account",Toast.LENGTH_SHORT).show();
                    case R.id.settings:
                        Toast.makeText(GraphActivity.this, "Settings",Toast.LENGTH_SHORT).show();
                    case R.id.mycart:
                        Toast.makeText(GraphActivity.this, "My Cart",Toast.LENGTH_SHORT).show();
                    default:

                    return true;
                        */
                }

                return true;




            }
        });


    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUpdate(Response s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                broadCastDataSetChanged();
                if (fragments != null && fragments[0] != null) {
                    ((FragmentGraph) fragments[0]).update(true);
                }
            }
        });
    }
    public void broadCastDataSetChanged() {
        if (fragments != null && fragments[1] != null && fragments[2] != null) {
            ((FragmentWallets) fragments[1]).notifyDataSetChanged();
            ((FragmentTransactionsAll) fragments[2]).notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed(){

        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }
}

