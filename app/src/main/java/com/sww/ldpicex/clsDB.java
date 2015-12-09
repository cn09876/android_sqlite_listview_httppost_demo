package com.sww.ldpicex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class clsDB 
{
	private static final String TAG = clsDB.class.getSimpleName();
	private boolean eof_=true;
	public int colCount=0;
	public String[] cols;
	private Hashtable<String, String> ht;
	public int RecordCount=0;
	private String sDbName="";
	private final static byte[] _lock = new byte[0];
	public String lastSQL="";
	private final String key="";
	public Cursor cc;
	private SQLiteDatabase db,db_;
	private String sLogFileName="";

	

	public clsDB(String sDb)
	{		
		sDbName=sDb;
		db_ = SQLiteDatabase.openOrCreateDatabase(sDbName, null);
		ht=new Hashtable<String, String>();
	}
	
	public byte[] sv_blob(String sql)
	{
		byte[] b=null;
		SQLiteDatabase db = null;
		Cursor c=null;
		synchronized(_lock)
		{
			try
			{
				db = SQLiteDatabase.openOrCreateDatabase(sDbName,null);
				c = db.rawQuery(sql, null);
				if (c.moveToNext())b = c.getBlob(0);
				c.close();
			}
			catch(Exception e)
			{
				try{c.close();}catch(Exception e1){}
			}
			finally
			{			
				try{c.close();}catch(Exception e){}
				try{db.close();}catch(Exception e){}
			}
		}
		return b;
	}
	
	public int recordCount()
	{
		return this.RecordCount;
	}
	
	public boolean eof()
	{
		return this.eof_;
	}
	
	public void ini_set(String k,String v)
	{
		try
		{
			this.create("reg", "k,v");
		}
		catch(Exception e)
		{
		}
		
		this.q("delete from reg where k='"+k+"' ");
		this.q("insert into reg (k,v) values ('"+k+"','"+v+"')");
		
	}
	
	public String ini_get(String k)
	{		
		String s="";
		try
		{
			s=sv("select v from reg where k='"+k+"' ");
		}
		catch(Exception e)
		{

		}
		return s;
	}
	
	public void field(String k,String v)
	{
		ht.put(k, v);
	}
	
	public void clear()
	{
		ht.clear();
	}
	
	public boolean isint(String s)
	{	
		s=s.trim();   
		try
		{   
			Integer.parseInt(s);   
		}   
		catch(NumberFormatException   e)   
		{   
			return   false;   
		}   
		return   true;   
	}
	
	public void add(String table)
	{
		String _val="",_fld="",s="",k="",v="";
		_val="";
		_fld="";
		
		try
		{
			for(Iterator<String> it=ht.keySet().iterator();it.hasNext();)   
			{	
				k   =   it.next(); 
				v   =   ht.get(k); 
				_fld+=" "+k+", ";
				_val+=" '"+v+"', ";
			}
			_fld=_fld.substring(0,_fld.length()-2);
			_val=_val.substring(0,_val.length()-2);
		}
		catch(Exception e)
		{
			Log.e(TAG,"clsdb.add.error: "+e.toString());
			return;
		}
		
		s="insert into "+table+" ("+_fld+") values ("+_val+") ";	//gen insert sql statement
		this.q(s);
			
	}
	
	public void update(String table,String where)
	{
		
		if(isint(where))where=" id='"+where+"' ";

		String _fld="",s="",k="",v="";
		_fld="";
		for(Iterator<String>   it   =   ht.keySet().iterator();it.hasNext();   )   
		{ 
			k   =   it.next(); 
			v   =   ht.get(k); 
			_fld+=k+"='"+v+"', ";
		}	
		_fld=_fld.substring(0,_fld.length()-2);

		s="update "+table+" set "+_fld+" where "+where;			//gen insert sql statement
		
		this.q(s);
	}

	public void create(String tbl,String flds)
	{
		String s="";
		if(flds.length()==0)return;
		s="create table "+tbl+" (id integer primary key,"+flds+",ptime datetime,utime datetime);";
		s+="create trigger "+tbl+"_insert after insert on "+tbl+" begin update "+tbl+" set ptime=datetime('now','localtime'),utime=datetime('now','localtime') where id=new.id; end;";
		s+="create trigger "+tbl+"_update after update on "+tbl+" begin update "+tbl+" set utime=datetime('now','localtime') where id=new.id; end;";
		this.q_(s);
	}
	
	public void q(String sql) 
	{
		q_(sql);
	}

	public void q_(String sql) 
	{
		this.lastSQL=sql;
		l("begin sql="+sql);
		//synchronized(_lock)
		{
			//SQLiteDatabase db = null;
			try
			{
				//db = SQLiteDatabase.openOrCreateDatabase(sDbName, key, null);
				db_.execSQL(sql);
			}
			catch(Exception e)
			{
				l("-------------------------------");
				e.printStackTrace();
				l("SQLITE严重错误:"+e.getMessage());
			}
			finally
			{
				//try{db.close();}catch(Exception e){}
			}
			
		}
		l("sql.done");
	}

	public void l(String s)
	{
		Log.e(TAG,s);
	}

	public void query(String sql)
	{
		this.close();
		cc=null;
		synchronized(_lock)
		{
			try
			{
				db = SQLiteDatabase.openOrCreateDatabase(sDbName,null);
				cc=db.rawQuery(sql, null);
				this.colCount=cc.getColumnCount();
				this.cols=cc.getColumnNames();
				this.eof_=cc.isLast();
				this.RecordCount=cc.getCount();
				if(this.RecordCount<=0)
				{
					this.eof_=true;
					return;
				}
				next();
			}
			catch(Exception e)
			{
				Log.e(TAG,"clsdb.query.error:  "+e.getMessage());
				try{cc.close();}catch(Exception e1){}
			}
			finally
			{	
			}
		}
	}
	
	public void log_file(String s)
	{
		try
		{
			File file = new File(sLogFileName+".log"); 
			SimpleDateFormat sDateFormat=new SimpleDateFormat("MM-dd hh:mm:ss",Locale.CHINA);     
			String date=sDateFormat.format(new java.util.Date());  

	    	try 
	    	{
	    		OutputStream outstream = new FileOutputStream(file,true);
	    		OutputStreamWriter out = new OutputStreamWriter(outstream);
	    		out.write(date+" "+s+"\r\n");
	    		out.close();
	    	}
	    	catch (Exception e) 
	    	{
	    		
	    	}
		}
		catch(Exception e)
		{
			
		}
	}
	// 返回一个byte数组
    public static byte[] file2bytes(String f) 
    {	
    	try
    	{
    		File file=new File(f);
	        InputStream is = new FileInputStream(file);
	        // 获取文件大小
	        long length = file.length();
	        if (length > Integer.MAX_VALUE) return null;
	        // 创建一个数据来保存文件数据
	        byte[] bytes = new byte[(int)length];
	 
	        // 读取数据到byte数组中
	        int offset = 0;
	        int numRead = 0;
	        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) 
	        {
	            offset += numRead;
	        }
	 
	        // 确保所有数据均被读取
	        if (offset < bytes.length)return null;
	 
	        is.close();
	        return bytes;
    	}
        catch(Exception e)
        {
        	return null;
        }
    }
	
	public void next()
	{
		if(this.eof_)return;
		this.eof_=cc.isLast();
		cc.moveToNext();
	}
	
	public void first()
	{
		cc.moveToFirst();
		this.eof_=cc.isLast();
	}

	
	public void close()
	{
		try{cc.close();}catch(Exception e){}
		try{db.close();}catch(Exception e){}
	}
	
	public String value(String fld)
	{
		int idx=cc.getColumnIndex(fld);
		if(idx<0)
		{
			l("字段["+fld+"]不存在");
			return "";
		}
		return cc.getString(idx);
	}
	
	public String sv(String sql)  
	{
		SQLiteDatabase db = null;
		String ret = "";
		Cursor c=null;
		synchronized(_lock)
		{
			try
			{
				db = SQLiteDatabase.openOrCreateDatabase(sDbName, null);
				c = db.rawQuery(sql, null);
				if (c.moveToNext())ret = c.getString(0);
				c.close();
			}
			catch(Exception e)
			{
				try{c.close();}catch(Exception e1){}
			}
			finally
			{
				try{db.close();}catch(Exception e){}
			}
		}
		if(ret==null)ret="";
		return ret;
	}

	public byte[] blob(String string) 
	{
		return cc.getBlob(cc.getColumnIndex(string));
	}
}
