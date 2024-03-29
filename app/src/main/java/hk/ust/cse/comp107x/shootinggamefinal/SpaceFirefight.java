package hk.ust.cse.comp107x.shootinggamefinal;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class SpaceFirefight extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener{

    Toolbar toolbar;

    private ImageButton restartButton, shootButton;
    private DrawView drawView;
    MediaPlayer player;
    boolean play_music = true;
    Menu menu;
Score score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shooting_game);

        // Get a reference to the Custom View
        drawView = (DrawView) findViewById(R.id.drawView);
        drawView.setOnTouchListener(this);


        // Get reference to the buttons and set their onClickListeners

        restartButton = (ImageButton) findViewById(R.id.restartButton);
        restartButton.setOnClickListener(this);
        shootButton = (ImageButton) findViewById(R.id.shootButton);
        shootButton.setOnClickListener(this);

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call

        player = MediaPlayer.create(this, R.raw.braincandy);
        player.setLooping(true);
        play_music = true;

        // Set the context fo the SoundEffects singleton class
        SoundEffects.INSTANCE.setContext(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shooting_game, menu);

        this.menu = menu;
        if (play_music) {
            menu.findItem(R.id.action_sound).setIcon(R.drawable.ic_volume_off_white_24dp);
        }
        else {
            menu.findItem(R.id.action_sound).setIcon(R.drawable.ic_volume_up_white_24dp);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_sound) {

            if (play_music) {
                player.pause();
                play_music=false;
                menu.findItem(R.id.action_sound).setIcon(R.drawable.ic_volume_up_white_24dp);

            }
            else {
                player.start();
                play_music=true;
                menu.findItem(R.id.action_sound).setIcon(R.drawable.ic_volume_off_white_24dp);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        drawView.stopGame();

        if (play_music)
            player.pause();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawView.resumeGame();

        if (play_music)
            player.start();

    }

    @Override
    protected void onDestroy() {

        player.stop();
        player.reset();
        player.release();
        player = null;
        play_music = false;

        super.onDestroy();
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        drawView.cannon.update(event.getX());



        return true;
    }

    @Override
    public void onClick(View v) {

        // Using the View's ID to distinguish which button was clicked
        switch(v.getId()) {


            case R.id.restartButton:

                    Intent i1 = new Intent(this, SpaceFirefight.class);
                    startActivity(i1);

                break;
            case R.id.shootButton:
                drawView.shootCannon();
                break;
            default:
                break;
        }

    }

}
