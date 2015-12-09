package com.sww.ldpicex;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.message.BasicNameValuePair;
import zrc.widget.SimpleHeader;
import zrc.widget.ZrcListView;
import zrc.widget.ZrcListView.OnItemClickListener;
import zrc.widget.ZrcListView.OnStartListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FrmCRM extends Activity {

	private static final String TAG="frmcrm";
    private ZrcListView listView;
    private List<CrmItem> crms=new ArrayList<CrmItem>();
    private MyAdapter adapter;
    private clsDB db=null;
    private Handler hh=new Handler();
    private Appl app;
	private final Context self=FrmCRM.this;

	public void l(String s)
	{
		Log.e("frmcrm",s);
	}
	public void msgbox(String msg) 
	{
		new AlertDialog.Builder(this).setTitle("提示").setMessage(msg)
				.setNeutralButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_frm_crm);


		app=(Appl)this.getApplication();
		db=new clsDB("/sdcard/_crm.db");
		db.create("crm", "name,tel,xq");
		

		this.findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				String name=((EditText)findViewById(R.id.edt_name)).getText().toString();
				String tel=((EditText)findViewById(R.id.edt_tel)).getText().toString();
				String xq=((EditText)findViewById(R.id.edt_xq)).getText().toString();
				if(name.equals(""))
				{
					msgbox("请输入 姓名");
					return;
				}
				if(tel.equals(""))
				{
					msgbox("请输入 电话");
					return;
				}
				if(xq.equals(""))
				{
					msgbox("请输入 小区");
					return;
				}
				
				db.clear();
				db.field("name", name);
				db.field("tel", tel);
				db.field("xq", xq);
				db.add("crm");
				
				String id=db.sv("select max(id) from crm");
				final String dt=String.format("<xml><id>%s</id><XiaoQu>%s</XiaoQu><Name>%s</Name><Tel>%s</Tel></xml>", id,xq,name,tel);
				
				new Thread(new Runnable(){
					@Override
					public void run() {
						
						List<BasicNameValuePair> lst=new ArrayList<BasicNameValuePair>();
						lst.add(new BasicNameValuePair("type","add"));
						lst.add(new BasicNameValuePair("Data",dt));
						app.sendHttpPost("http://www.ruiyingplan.com:81/crm.aspx",lst);
					}}).start();
				
				((EditText)findViewById(R.id.edt_name)).setText("");
				((EditText)findViewById(R.id.edt_tel)).setText("");
				((EditText)findViewById(R.id.edt_xq)).setText("");
				
				listView.refresh();
			}
		});
		


        listView = (ZrcListView) findViewById(R.id.lst_crm);
        listView.setDividerHeight(-5);
        
        // 设置下拉刷新的样式（可选，但如果没有Header则无法下拉刷新）
        SimpleHeader header = new SimpleHeader(this);
        header.setTextColor(0xff0066aa);
        header.setCircleColor(0xff33bbee);
        listView.setHeadable(header);

        // 下拉刷新事件回调（可选）
        listView.setOnRefreshStartListener(new OnStartListener() {
            @Override
            public void onStart() {
                refresh();
            }
        });


        adapter = new MyAdapter(this);
        listView.setAdapter(adapter);
        listView.refresh(); // 主动下拉刷新
        
        listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(ZrcListView parent, View view,int position, long id) {
				//TextView t=(TextView)view.findViewById(R.id.name);
			}});
        
        
        
	}
            
    private void refresh(){
        hh.postDelayed(new Runnable() {
            @Override
            public void run() 
            {
            	crms.clear();
            	
            	db.query("select * from crm order by id desc");
            	while(!db.eof())
            	{
                	CrmItem x=new CrmItem();
                	x.str姓名=db.value("name");
                	x.str小区=db.value("xq");
                	x.str电话=db.value("tel");
                	x.strID=db.value("id");
                	crms.add(x);
            		db.next();
            	}
            	db.close();
            	
            	
                adapter.notifyDataSetChanged();
                listView.setRefreshSuccess("加载完成"); // 通知加载成功
                listView.stopLoadMore(); // 开启LoadingMore功能
            }
        }, 0);
    }


    private class MyAdapter extends BaseAdapter{
    	private LayoutInflater layoutInflater = null;
    	public int iSelectPos=-1;
    	
        public MyAdapter(Context ctx) 
        {
        	layoutInflater=LayoutInflater.from(ctx);
		}

        @Override
        public int getCount() {
            return crms.size();
        }
        @Override
        public Object getItem(int position) {
            return crms.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) 
        {
        	final CrmItem_Holder item;
            if(convertView == null)
            {
            	item=new CrmItem_Holder(); 
                convertView = layoutInflater.inflate(R.layout.layout_crm_listitem, null);
                item.str姓名=(TextView)convertView.findViewById(R.id.name);
                item.str电话=(TextView)convertView.findViewById(R.id.tel); 
                item.str小区=(TextView)convertView.findViewById(R.id.xq);
                item.strID=(TextView)convertView.findViewById(R.id.sid);
                item.btnDel=(Button)convertView.findViewById(R.id.btn_del);
                convertView.setTag(item);
            }
            else
            {
                item = (CrmItem_Holder)convertView.getTag();
            }

            item.str姓名.setText(crms.get(position).str姓名);
            item.str电话.setText(crms.get(position).str电话);
            item.str小区.setText(crms.get(position).str小区);
            item.strID.setText(crms.get(position).strID);
            
            item.btnDel.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					db.q("delete from crm where id='"+item.strID.getText()+"' ");
					listView.refresh();
					
					final String dt=String.format("<xml><id>%s</id></xml>", item.strID.getText().toString());
					hh.post(new Runnable(){
						@Override
						public void run() {
							
							List<BasicNameValuePair> lst=new ArrayList<BasicNameValuePair>();
							lst.add(new BasicNameValuePair("type","del"));
							lst.add(new BasicNameValuePair("Data",dt));
							app.sendHttpPost("http://www.ruiyingplan.com:81/crm.aspx",lst);
						}}
					);

				}
			});
            
            
            return convertView;
            
        }
        
    }
    static class CrmItem_Holder {
    	public TextView strID;
        public TextView str姓名;
        public TextView str电话;
        public TextView str小区;
        public Button btnDel;
    }

	private void show_frm(Class<?> cls)
    {
    	self.startActivity(new Intent(self,cls));
    }

}
