package hk.ust.cse.comp107x.shootinggamefinal;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;


public class Score {

    private Paint paint;
    private int score;
    private int miss;


    public Score(int color) {
        paint = new Paint();
        // Set the font face and size of drawing text
        paint.setTypeface(Typeface.MONOSPACE);
        paint.setTextSize(24);
        paint.setColor(color);
        miss = 2;
        score = 0;

    }

    public void incrementScore() {
        score++;

    }

    public void decrementScore() {
        score--;
    }

    public int getScore() { return score; }




    public void decrementMiss() {
        miss--;
    }

    public int getMiss() { return miss; }
    public void gameOver(Canvas canvas) {
        canvas.drawText("Game Over!", 210, 350, paint);
        canvas.drawText("Score: " + score, 210, 370, paint);
        canvas.drawText("Press restart button to play again", 30, 410, paint);
    }

    public void draw(Canvas canvas) {


        canvas.drawText("Score: " + score, 10, 30, paint);
        canvas.drawText("Misses: " + miss, 10, 50, paint);
    }
}
