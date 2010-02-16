package ti.modules.titanium.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.appcelerator.titanium.TiContext;
import org.appcelerator.titanium.TiDict;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.proxy.TiWindowProxy;
import org.appcelerator.titanium.util.Log;
import org.appcelerator.titanium.util.TiConfig;
import org.appcelerator.titanium.view.TiUIView;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.os.Messenger;

public class WindowProxy extends TiWindowProxy
{
	private static final String LCAT = "WindowProxy";
	private static final boolean DBG = TiConfig.LOGD;

	private static final int MSG_FIRST_ID = TiWindowProxy.MSG_LAST_ID + 1;

	private static final int MSG_FINISH_OPEN = MSG_FIRST_ID + 100;
	private static final int MSG_TAB_OPEN = MSG_FIRST_ID + 101;

	protected static final int MSG_LAST_ID = MSG_FIRST_ID + 999;

	ArrayList<TiViewProxy> views;
	WeakReference<Activity> weakActivity;
	String windowId;

	public WindowProxy(TiContext tiContext, Object[] args)
	{
		super(tiContext, args);
	}

	@Override
	public TiUIView getView(Activity activity) {
		throw new IllegalStateException("call to getView on a Window");
	}

	@Override
	public boolean handleMessage(Message msg)
	{
		switch(msg.what) {
			case MSG_FINISH_OPEN: {
				realizeViews(null, view);
				if (tab == null) {
					//TODO attach window
				}
				opened = true;
				return true;
			}
			case MSG_TAB_OPEN : {
				view = new TiUIWindow(this, (Activity) msg.obj);
				realizeViews(null, view);
				opened = true;
				return true;
			}
			default : {
				return super.handleMessage(msg);
			}
		}
	}

	@Override
	protected void handleOpen(TiDict options)
	{
		Log.i(LCAT, "handleOpen");

		Messenger messenger = new Messenger(getUIHandler());
		view = new TiUIWindow(this, options, messenger, MSG_FINISH_OPEN);
	}

	public void fillIntentForTab(Intent intent) {
		Messenger messenger = new Messenger(getUIHandler());
		intent.putExtra("messenger", messenger);
		intent.putExtra("messageId", MSG_TAB_OPEN);
	}

	@Override
	protected void handleClose(TiDict options)
	{
		Log.i(LCAT, "handleClose");
		if (view != null) {
			((TiUIWindow) view).close();
		}
		opened = false;
	}

	public void addView(TiViewProxy view)
	{
		if (views == null) {
			views = new ArrayList<TiViewProxy>();
		}
		synchronized(views) {
			views.add(view);
		}
	}

	public void removeView(TiViewProxy view)
	{
		if (views != null) {
			synchronized(views) {
				views.remove(view);
			}
		}
	}

	public void showView(TiViewProxy view)
	{

	}

	public void showView(TiViewProxy view, JSONObject options)
	{

	}
	
	public TiViewProxy getTab()
	{
		return tab;
	}
}
