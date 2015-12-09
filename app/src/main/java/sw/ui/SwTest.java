package sw.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class SwTest extends View 
{
	private static final String TAG = SwTest.class.getSimpleName();
	private String mText="";
	private Paint p;
	private Context ctx;
	private int r=0,g=0,b=0;
	private MyThread thdMain;
	private InputMethodManager inputMethodManager;
	
	class MyThread extends Thread 
	{
        private boolean mActive = true;

        public void run() {
            while (mActive) {
                doThings();
            }
        }

        public void terminate() 
        {
            mActive = false;
        }

        private void doThings() 
        {
			try
			{
				Thread.sleep(5);
			}
			catch(Exception e){}
			b++;
			if(b>255)b=0;
			SwTest.this.postInvalidate();
        }
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		this.mText+=String.valueOf(keyCode);
		this.invalidate();
		
		return super.onKeyDown(keyCode, event);
	}

	private void l(String s)
	{
		Log.e(TAG,s);
	}
	
	public SwTest(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		ctx=context;
		init();
	}

	public SwTest(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		ctx=context;
		init();
	}

	public SwTest(Context context) 
	{
		super(context);
		ctx=context;
		init();
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
            
        }
        return true;
    }
	public void init()
	{
		setFocusable(true);
        setFocusableInTouchMode(true);
        setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if((keyCode >= KeyEvent.KEYCODE_A) && (keyCode <= KeyEvent.KEYCODE_Z)) {
                        mText = mText + (char) event.getUnicodeChar();
                        SwTest.this.postInvalidate();
                        return true;
                    }
                    else if(keyCode >= KeyEvent.KEYCODE_ENTER){  
                    	mText="";
                        Toast.makeText(getContext(), "The text is: " + mText , Toast.LENGTH_LONG).show();
                         return true;
                    }
                }
                return false;
            }
        });
        
		thdMain=new MyThread();
	}
	
	public void setText1(String s)
	{
		
		mText=s;
		this.invalidate();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		thdMain.start();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		thdMain.terminate();
		thdMain=null;
	}

	@Override
	public void draw(Canvas c) 
	{
		if(p==null)
		{
			p=new Paint();
			p.setColor(Color.BLUE);
			p.setTextSize(40);
		}
		c.drawColor(Color.rgb(r, g, b));
		
		c.drawText(mText, 10, 30, p);
		super.draw(c);
	}

}
