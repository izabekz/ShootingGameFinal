package hk.ust.cse.comp107x.shootinggamefinal;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Space;

import java.util.ArrayList;


public class DrawView extends SurfaceView implements SurfaceHolder.Callback {

    private int width, height;
    private DrawViewThread drawviewthread;

   private Bitmap mBackgroundImage;

    Context mContext;

    // We can have multiple bullets and explosions
    // keep track of them in ArrayList
    ArrayList<Bullet> bullets;
    ArrayList<Explosion> explosions;
    Cannon cannon;
    AndroidGuy androidGuy;
    Score score;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);


        mContext = context;

        getHolder().addCallback(this);

        setFocusable(true);
        this.requestFocus();
        // taking background image from resources and decoding
        Resources res = context.getResources();
        mBackgroundImage = BitmapFactory.decodeResource(res,
                R.drawable.back);
        // creating a cannon object
        cannon = new Cannon(Color.BLUE,mContext);

        // creating arraylists to keep track of bullets and explosions
        bullets = new ArrayList<Bullet> ();
        explosions = new ArrayList<Explosion>();

        // creating the falling Android Guy
        androidGuy = new AndroidGuy(Color.RED, mContext);
        score = new Score(Color.WHITE);


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        drawviewthread = new DrawViewThread(holder);
        drawviewthread.setRunning(true);
        drawviewthread.start();
        //creating scaled bitmap
        mBackgroundImage = Bitmap.createScaledBitmap(
                mBackgroundImage, width, height, true);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        boolean retry = true;
        drawviewthread.setRunning(false);

        while (retry){
            try {
                drawviewthread.join();
                retry = false;
            }
            catch (InterruptedException e){

            }
        }

    }

    public class DrawViewThread extends Thread{
        private SurfaceHolder surfaceHolder;
        private boolean threadIsRunning = true;

        public DrawViewThread(SurfaceHolder holder){
            surfaceHolder = holder;
            setName("DrawViewThread");
        }

        public void setRunning (boolean running){
            threadIsRunning = running;
        }

        public void run() {
            Canvas canvas = null;

            while (threadIsRunning) {

                try {
                    canvas = surfaceHolder.lockCanvas(null);

                    synchronized(surfaceHolder){
                        drawGameBoard(canvas);
                    }
                    sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    if (canvas != null)
                        surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w;
        height = h;

        cannon.setBounds(0,0,width, height);
        androidGuy.setBounds(0, 0, width, height);
        for (int i = 0; i < bullets.size(); i++ ) {
            bullets.get(i).setBounds(0,0,width,height);
        }

    }

    public void drawGameBoard(Canvas canvas) {
        //draw background with image from res
        canvas.drawBitmap(mBackgroundImage, 0, 0, null);
        //canvas.drawColor(Color.WHITE);     //if you want another background color
        // Draw the cannon
        cannon.draw(canvas);

        // Draw all the bullets
        for (int i = 0; i < bullets.size(); i++ ) {
            if (bullets.get(i) != null) {
                bullets.get(i).draw(canvas);

                if (bullets.get(i).move() == false) {
                    bullets.remove(i);
                }
            }
        }

        // Draw all the explosions, at those locations where the bullet
        // hits the Android Guy
        for (int i = 0; i < explosions.size(); i++ ) {
            if (explosions.get(i) != null) {
                if (explosions.get(i).draw(canvas) == false) {
                    explosions.remove(i);
                }
            }
        }

        // If the Android Guy is falling, check to see if any of the bullets
        // hit the Guy
        if (androidGuy != null) {
            androidGuy.draw(canvas);

            RectF guyRect = androidGuy.getRect();


            for (int i = 0; i < bullets.size(); i++ ) {

                // The rectangle surrounding the Guy and Bullet intersect, then it's a collision
                // Generate an explosion at that location and delete the Guy and bullet. Generate
                // a new Android Guy to fall from the top.
                if (RectF.intersects(guyRect, bullets.get(i).getRect())) {
                    explosions.add(new Explosion(Color.RED,mContext, androidGuy.getX(), androidGuy.getY()));
                    androidGuy.reset();
                    bullets.remove(i);
                    // Play the explosion sound by calling the SoundEffects class
                    SoundEffects.INSTANCE.playSound(SoundEffects.SOUND_EXPLOSION);
                    score.incrementScore();
                    break;
                }

            }

            if (androidGuy.move() == false) {

                androidGuy = null;
            }
            if(androidGuy.getY() == 710) {
                score.decrementMiss();
            }

            if(score.getMiss()==0) {
                 drawviewthread.setRunning(false);
                score.gameOver(canvas);

            }
            if(score.getScore()!=0 && score.getScore()%3 ==0) {
                androidGuy.stepY = androidGuy.stepY + 1;
                score.incrementScore();

            }
        }
        score.draw(canvas);

    }


    // Move the cannon left or right
    public void moveCannonLeft() {
        cannon.moveLeft();
    }

    public void moveCannonRight() {
        cannon.moveRight();
    }

    // Whenever the user shoots a bullet, create a new bullet moving upwards
    public void shootCannon() {

        bullets.add(new Bullet(Color.RED, mContext, cannon.getPosition(), (float) (height - 40)));

    }

    public void stopGame(){
        if (drawviewthread != null){
            drawviewthread.setRunning(false);
        }
    }

    public void resumeGame(){
        if (drawviewthread != null){
            drawviewthread.setRunning(true);
        }
    }

    public void releaseResources(){

    }


}
