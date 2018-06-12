package net.ajcloud.wansviewplus.main.download.ui;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.download.VideoDownloadManager;
import net.ajcloud.wansviewplus.main.download.adapter.VideoDownloadAdapter;
import net.ajcloud.wansviewplus.support.customview.dialog.AlignBottomDialog;
import net.ajcloud.wansviewplus.support.event.DelVideoMessage;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.videolan.libvlc.VideoDownloadBean;

import java.util.ArrayList;

/**
 * Created
 */
public class VideoDownloadActivity extends BaseActivity {

    private TextView downloaded;
    private TextView downloading;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private ListView listView;
    private LinearLayout delLayout;
    private TextView selectAll;
    private TextView delSel;

    private VideoDownloadAdapter adapter;
    // 判断当前是否已下载页面
    private boolean isDownloadedPage;
    // 是否处于编辑状态
    private boolean isEditor = false;
    private Handler uiHandler;
    private AlignBottomDialog myPopupWindow;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_download;
    }

    @Override
    protected boolean hasTittle() {
        return false;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar_portrait);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.mipmap.icon_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        isDownloadedPage = getIntent().getBooleanExtra("isDownloadedPage", true);
        downloaded = (TextView) findViewById(R.id.downloaded);
        downloading = (TextView) findViewById(R.id.downloading);
        listView = (ListView) findViewById(R.id.video_download_listview);
        delLayout = (LinearLayout) findViewById(R.id.ly_bottom);
        selectAll = (TextView) findViewById(R.id.tv_select_all);
        delSel = (TextView) findViewById(R.id.tv_delete);
        initPopupWindow();
    }

    @Override
    protected void initData() {
        adapter = new VideoDownloadAdapter(this);
        listView.setAdapter(adapter);
    }

    @Override
    protected void initListener() {
        uiHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case VideoDownloadManager.REFRESH_ITEM:
                        refreshItem((VideoDownloadBean) msg.obj);
                        break;
                    case VideoDownloadManager.REFRESH_LIST:
                        pickPage(isDownloadedPage);
                        break;
                    default:
                        break;
                }
            }
        };

        downloaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDownloadedPage) {
                    isDownloadedPage = true;
                    pickPage(isDownloadedPage);
                }
            }
        });

        downloading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDownloadedPage) {
                    isDownloadedPage = false;
                    pickPage(isDownloadedPage);
                }
            }
        });

        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.selectAll();
            }
        });

        delSel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getSelectList().isEmpty()) {
                    ToastUtil.show(R.string.wv_choose_delete_video);
                } else {
                    myPopupWindow.show();
                }
            }
        });
    }

    @Override
    protected boolean hasStateBar() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        pickPage(isDownloadedPage);
        VideoDownloadManager.getInstance().setUiHandler(uiHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        VideoDownloadManager.getInstance().setUiHandler(null);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_video, menu);
        menu.findItem(R.id.history_menu_edit).setVisible(true);
        menu.findItem(R.id.history_menu_cancel).setVisible(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isEditor) {
            menu.findItem(R.id.history_menu_edit).setVisible(false);
            menu.findItem(R.id.history_menu_cancel).setVisible(true);
        } else {
            menu.findItem(R.id.history_menu_edit).setVisible(true);
            menu.findItem(R.id.history_menu_cancel).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.history_menu_edit:
                isEditor = true;
                adapter.setEditor(true);
                delLayout.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
                supportInvalidateOptionsMenu();
                break;
            case R.id.history_menu_cancel:
                isEditor = false;
                adapter.setEditor(false);
                delLayout.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                supportInvalidateOptionsMenu();
                break;
        }
        return true;
    }

    public void pickPage(boolean isDownloadedPage) {
        if (isEditor) {
            adapter.getSelectList().clear();
        }
        adapter.getList().clear();
        changeView(isDownloadedPage);
        ArrayList<VideoDownloadBean> tmp = new ArrayList<VideoDownloadBean>();
        for (int i = 0; i < VideoDownloadManager.getInstance().getList().size(); i++) {
            VideoDownloadBean bean = VideoDownloadManager.getInstance().getList().valueAt(i);
            bean.setCheckable(false);
            if (isDownloadedPage && VideoDownloadManager.DOWNLOAD_SUCCEED == bean.getDownloadState()) {
                tmp.add(bean);
            } else if (!isDownloadedPage && VideoDownloadManager.DOWNLOAD_SUCCEED != bean.getDownloadState()) {
                tmp.add(bean);
            }
        }
        adapter.getList().addAll(tmp);
        adapter.notifyDataSetChanged();
    }

    private void changeView(boolean isDownloadedPage) {
        if (isDownloadedPage) {
            downloaded.setBackgroundResource(R.drawable.video_download_select_left_bg);
            downloaded.setTextColor(getResources().getColor(R.color.style_actionbar_actionbar_background_color));
            downloading.setBackgroundResource(R.color.transparent);
            downloading.setTextColor(getResources().getColor(R.color.white));
        } else {
            downloading.setBackgroundResource(R.drawable.video_download_select_right_bg);
            downloading.setTextColor(getResources().getColor(R.color.style_actionbar_actionbar_background_color));
            downloaded.setBackgroundResource(R.color.transparent);
            downloaded.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void refreshItem(VideoDownloadBean bean) {
        for (VideoDownloadBean value : adapter.getList()) {
            if (bean.getId() == value.getId()) {
                value.setDownloadProgress(bean.getDownloadProgress());
                value.setDownloadState(bean.getDownloadState());
                int index = adapter.getList().indexOf(value);
                int firstVisiblePosition = listView.getFirstVisiblePosition();
                int lastVisiblePosition = listView.getLastVisiblePosition();
                if (index >= firstVisiblePosition && index <= lastVisiblePosition) {
                    View view = listView.getChildAt(index - firstVisiblePosition);
                    adapter.updateItem(view, index);
                }
            }
        }
    }

    public void initPopupWindow() {
        myPopupWindow = new AlignBottomDialog(this, R.layout.history_delete_dlg);
        if (myPopupWindow.getContentView() != null) {
            Button delete = (Button) myPopupWindow.getContentView().findViewById(R.id.history_pop_delete);
            if (delete != null) {
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.deleteSelect();
                        isEditor = false;
                        adapter.setEditor(isEditor);
                        delLayout.setVisibility(View.GONE);
                        pickPage(isDownloadedPage);
                        myPopupWindow.dismiss();
                        invalidateOptionsMenu();
                    }
                });
            }

            Button cancel = (Button) myPopupWindow.getContentView().findViewById(R.id.history_pop_cancel);
            if (cancel != null) {
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            myPopupWindow.dismiss();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    public void onEvent(DelVideoMessage msg) {
        if (msg != null && !TextUtils.isEmpty(msg.videoPath)) {
            int len = VideoDownloadManager.getInstance().getList().size();
            if (len == 0) {
                return;
            }
            VideoDownloadBean bean;
            for (int i = 0; i < len; i++) {
                bean = VideoDownloadManager.getInstance().getList().valueAt(i);
                if (bean.getDownloadFilePath().equals(msg.videoPath)) {
                    VideoDownloadManager.getInstance().delete(bean);
                    return;
                }
            }
        }
    }

}
