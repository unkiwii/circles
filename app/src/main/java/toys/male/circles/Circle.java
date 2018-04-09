package toys.male.circles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class Circle extends View {
    private Paint paint;

    public Circle(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setARGB(0xff, (int)(Math.random() * 255f), (int)(Math.random() * 255f), (int)(Math.random() * 255f));
        paint.setStyle(Paint.Style.FILL);
    }

    public int getColor() {
        return paint.getColor() - 0xff000000;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getHeight() / 2, getHeight() / 2, getHeight() / 2, paint);
    }
}
