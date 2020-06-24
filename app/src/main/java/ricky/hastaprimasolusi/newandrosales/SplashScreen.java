package ricky.hastaprimasolusi.newandrosales;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    private TextView iv;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        session = new SessionManager(getApplicationContext());

        /*iv = findViewById(R.id.iv);
        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.mytransition);
        iv.startAnimation(myanim);
        */
        //final Intent i = new Intent(this, LoginActivity.class);
        Thread timer = new Thread(){
            public void run(){
                try {
                    sleep(3000) ;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    //startActivity(i);
                    session.checkLogin();

                    finish();
                }
            }
        };
        timer.start();
    }
}
