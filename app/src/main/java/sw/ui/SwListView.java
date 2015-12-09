package sw.ui;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SwListView extends ListView {

	public interface SwDelegate
	{
		public void OnItemSelect(int pos);
		/*当绘制每行内容时调用方法*/
		public View OnGetView(int position, View convertView, ViewGroup parent);
	}

	private static final String TAG = SwListView.class.getSimpleName();	
	private SwDelegate mEvent;
	private Context ctx;
	private ArrayList<Object> lst=new ArrayList<Object>();
    private LayoutInflater layoutInflater = null;


	@SuppressWarnings("unused")
	private void l(String s)
	{
		Log.e(TAG,s);
	}


    public SwListView(Context context) 
    {
        this(context, null);
        ctx=context;
        init();
    }

    public SwListView(Context context, AttributeSet attrs) 
    {
        this(context, attrs, android.R.attr.listViewStyle);
        ctx=context;
        init();
    }

    public SwListView(Context context, AttributeSet attrs, int defStyle) 
    {
        super(context, attrs, defStyle);
        ctx=context;
        init();
    }

    public View inflate(int resID)
    {
    	return layoutInflater.inflate(resID, null);
    }
    
    public void init()
    {
    	layoutInflater=LayoutInflater.from(ctx);
    	this.setDividerHeight(0);
		MyAdapter mad=new MyAdapter(ctx,lst);
		this.setAdapter(mad);		
		this.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				mEvent.OnItemSelect(position);				
			}});
    }
    
	public void setSwDelegate(SwDelegate e)
	{
		mEvent=e;
	}
	
	public void clear()
	{	
		lst.clear();
	}

    public void reset_ds()
    {
        this.setAdapter(null);
        this.setAdapter(new MyAdapter(ctx,lst));
    }

	public void add(Object obj)
	{
		lst.add(obj);
	}
	
	public Object get(int idx)
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
	    private List<Object> mList;
	  
	    public MyAdapter(Context context, List<Object> list) {  
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
	    public long getItemId(int index) {  
	        return index;  
	    }
	  
		@Override  
	    public View getView(int position, View convertView, ViewGroup parent) 
	    {  
			if(mEvent!=null)
			{
				View t=mEvent.OnGetView(position, convertView, parent);
				if(t!=null)return t;
			}
	    	TextView t=new TextView(mContext);
	    	t.setText(mList.get(position).toString());
	        return t;  
	    }
	  
	}  
}
