package sw.ui;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

//自定义的下拉框控件
public class SwDropDownList extends Spinner 
{

	public interface SwDelegate
	{
		public void OnItemSelect(String s);
		public View OnGetView(int position, View convertView, ViewGroup parent);
	}

	private static final String TAG = SwDropDownList.class.getSimpleName();	
	private SwDelegate mEvent;
	private Context ctx;
	private ArrayList<String> lst=new ArrayList<String>();
	public int textSize=30;
	public int textColor=Color.BLACK;
	private LayoutInflater layoutInflater = null;
	
	@SuppressWarnings("unused")
	private void l(String s)
	{
		Log.e(TAG,s);
	}
	
    public View inflate(int resID)
    {
    	return layoutInflater.inflate(resID, null);
    }

	public SwDropDownList(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		ctx=context;
		init();
	}

	public SwDropDownList(Context context, AttributeSet attrs) {
		super(context, attrs,android.R.attr.dropDownSpinnerStyle,Spinner.MODE_DROPDOWN);
		ctx=context;
		init();
		
	}

	public SwDropDownList(Context context, int mode) {
		super(context, Spinner.MODE_DROPDOWN);
		ctx=context;
		init();
	}

	public SwDropDownList(Context context) {
		super(context);
		ctx=context;
		init();
	}

	public SwDropDownList(Context context, AttributeSet attrs, int defStyle,int mode) 
	{
		super(context, attrs, defStyle, Spinner.MODE_DROPDOWN);
		ctx=context;
		init();
	}
	
	@SuppressLint("NewApi") 
	private void init()
	{
		this.setDropDownWidth(300);
		lst.add("");
		MyAdapter mad=new MyAdapter(ctx,lst);
		this.setAdapter(mad);
		this.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) 
			{
				if(lst.size()==0)return;
				mEvent.OnItemSelect(lst.get(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) 
			{
			}});
		this.clear();
		
	}
	
	public void setSwDelegate(SwDelegate e)
	{
		mEvent=e;
	}
	
	public void clear()
	{
		
		lst.clear();
	}
	
	public void add(String s)
	{
		lst.add(s);
	}
	
	public String get(int idx)
	{
		return lst.get(idx);
	}
	
	public int count()
	{
		return lst.size();
	}
	
	
	class MyAdapter extends BaseAdapter
	{  
		  
	    private Context mContext;  
	    private List<String> mList;
	  
	    public MyAdapter(Context context, List<String> list) {  
	        this.mContext = context;  
	        this.mList = list;  
	    }  
	  
		@Override  
	    public int getCount() {  
	        return mList.size();  
	    }  
	  
	    @Override  
	    public Object getItem(int position) {  
	        return mList.get(position);  
	    }  
	  
		@Override  
	    public long getItemId(int index) 
		{  
	        return index;  
	    }
	  
		@Override  
	    public View getView(int position, View convertView, ViewGroup parent) 
	    {  
			if(mList.size()==0)
			{
				TextView t=new TextView(mContext);
				t.setText("---");
				return t;
			}
			
			if(mEvent!=null)
			{
				View t=mEvent.OnGetView(position, convertView, parent);
				if(t!=null)return t;
			}
			
	    	TextView t=new TextView(mContext);
	    	try
	    	{
	    		t.setText(mList.get(position));
	    	}
	    	catch(Exception e)
	    	{
	    		t.setText("err@"+position);
	    	}
	    	t.setTextSize(textSize);
	    	t.setTextColor(textColor);
	        return t;  
	    }
	  
	}  
	

}
