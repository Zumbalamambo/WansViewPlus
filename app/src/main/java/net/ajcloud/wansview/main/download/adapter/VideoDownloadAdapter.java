package net.ajcloud.wansview.main.download.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.entity.camera.PhoneImageListData;
import net.ajcloud.wansview.main.download.VideoDownloadManager;
import net.ajcloud.wansview.main.home.HomeActivity;
import net.ajcloud.wansview.support.utils.ToastUtil;

import org.videolan.libvlc.VideoDownloadBean;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created
 */
public class VideoDownloadAdapter extends BaseAdapter {

    private ArrayList<VideoDownloadBean> list = new ArrayList<VideoDownloadBean>();
    private Context mContext;
    private ViewHolder holders;
    private boolean isEditor;
    private ArrayList<VideoDownloadBean> selectList = new ArrayList<VideoDownloadBean>();

    public VideoDownloadAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.adapter_video_download_item, null);
            holder.name = (TextView) convertView.findViewById(R.id.video_download_name);
            holder.from = (TextView) convertView.findViewById(R.id.video_download_source);
            holder.startTime = (TextView) convertView.findViewById(R.id.video_download_date);
            holder.duration = (TextView) convertView.findViewById(R.id.video_download_duration);
            holder.pic = (ImageView) convertView.findViewById(R.id.video_download_img);
            holder.failImg = (ImageView) convertView.findViewById(R.id.video_download_fail_img);
            holder.cb = (CheckBox) convertView.findViewById(R.id.video_download_cb);
            holder.sl = (SwipeLayout) convertView.findViewById(R.id.video_download_layout);
            holder.delLayout = (LinearLayout) convertView.findViewById(R.id.video_download_delete_layout);
            holder.downloadLayout = (LinearLayout) convertView.findViewById(R.id.video_download_item_layout);
            holder.cbLayout = (RelativeLayout) convertView.findViewById(R.id.video_download_check_layout);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.video_download_progress);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final VideoDownloadBean data = list.get(position);
//        holder.name.setText(mContext.getString(R.string.video_download_item_name));
        holder.startTime.setText(data.getStartTime());
        if (data.getType() == 0) {
            holder.from.setText(mContext.getString(R.string.wv_video_download_item_download_from_cloud));
        } else if (data.getType() == 1) {
            holder.from.setText(mContext.getString(R.string.wv_video_download_item_download_from_tfcard));
        } else if (data.getType() == 2) {
            holder.from.setText(mContext.getString(R.string.wv_video_download_item_download_from_nas));
        }

        updateState(holder, data);

        if (isEditor) {
            holder.cbLayout.setVisibility(View.VISIBLE);
        } else {
            holder.cbLayout.setVisibility(View.GONE);
            data.setCheckable(false);
        }

        holder.delLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.sl.close(false);
                list.remove(position);
                VideoDownloadManager.getInstance().delete(data);
                notifyDataSetChanged();
            }
        });

        if (!TextUtils.isEmpty(data.getPicFilePath())) {
            File imgFile = new File(data.getPicFilePath());
            if (imgFile.exists()) {
                Glide.with(mContext).load(Uri.fromFile(imgFile)).placeholder(R.mipmap.figure_big).into(holder.pic);
            } else if (!TextUtils.isEmpty(data.getPicUrl())) {
                Glide.with(mContext).load(data.getPicUrl()).placeholder(R.mipmap.figure_big).into(holder.pic);
            } else {
                Glide.with(mContext).load(R.mipmap.figure_big).into(holder.pic);
            }
        } else {
            Glide.with(mContext).load(data.getPicUrl()).placeholder(R.mipmap.figure_big).into(holder.pic);
        }

        holder.cb.setChecked(data.isCheckable());
        holder.cbLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = !holder.cb.isChecked();
                holder.cb.setChecked(isCheck);
                data.setCheckable(isCheck);
                if (isCheck) {
                    selectList.add(data);
                } else {
                    selectList.remove(data);
                }
            }
        });

        holder.downloadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
                if (VideoDownloadManager.DOWNLOAD_SUCCEED == data.getDownloadState()) {
                    File file = new File(data.getDownloadFilePath());
                    if (file.exists()) {
                        PhoneImageListData convertData = new PhoneImageListData(data.getDownloadFilePath(), "", data.getStartTime(), 1);
                        convertData.setVideoStream(data.getPicFilePath());
                        convertData.setEmid(data.getOid());
                        Intent intent = new Intent(mContext, HomeActivity.class);
                        intent.putExtra("type", "file");
                        intent.putExtra("index", 1);
                        ArrayList<PhoneImageListData> list = new ArrayList<>();
                        list.add(convertData);
                        intent.putExtra("sources", list);
                        mContext.startActivity(intent);

                    } else {
                        ToastUtil.show(R.string.wv_video_download_player_file_empty);
                    }
                } else if (VideoDownloadManager.DOWNLOAD_FAILED == data.getDownloadState() || VideoDownloadManager.DOWNLOAD_PAUSE == data.getDownloadState()
                        || VideoDownloadManager.DOWNLOAD_TERMINATED == data.getDownloadState()) {
                    holder.failImg.setVisibility(View.GONE);
                    if (data.getType() == 0) {
                        data.setDownloadUrl(getPlayUrl(data));
                    }
                    VideoDownloadManager.getInstance().download(data);
                } else if (VideoDownloadManager.DOWNLOAD_NOTFOUND == data.getDownloadState()) {
                    ToastUtil.show(R.string.wv_video_download_item_download_notfound);
                }
            }
        });

        close();
        holder.sl.setShowMode(SwipeLayout.ShowMode.PullOut);
        holder.sl.addDrag(SwipeLayout.DragEdge.Right, holder.delLayout);
        holder.sl.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
                close();
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                holders = holder;
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
            }

            @Override
            public void onClose(SwipeLayout layout) {
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
            }
        });
        return convertView;
    }

    private void close() {
        if (null != holders) {
            if (holders.sl.getOpenStatus() == SwipeLayout.Status.Open) {
                holders.sl.close(true);
            }
            holders = null;
        }
    }

    public ArrayList<VideoDownloadBean> getList() {
        return list;
    }

    public void setList(ArrayList<VideoDownloadBean> list) {
        this.list = list;
    }

    public void updateItem(View view, int index) {
        if (null == view) {
            return;
        }
        ViewHolder hold = (ViewHolder) view.getTag();
        VideoDownloadBean data = list.get(index);
        updateState(hold, data);
    }

    private void updateState(ViewHolder hold, VideoDownloadBean data) {
        if (VideoDownloadManager.DOWNLOAD_RAW == data.getDownloadState()) {
            hold.failImg.setVisibility(View.GONE);
            hold.progressBar.setVisibility(View.GONE);
            hold.duration.setVisibility(View.VISIBLE);
            hold.duration.setText(mContext.getString(R.string.wv_video_download_item_download_wait));
        } else if (VideoDownloadManager.DOWNLOAD_INIT == data.getDownloadState()
                || VideoDownloadManager.DOWNLOAD_DOWNLOADING == data.getDownloadState()
                || VideoDownloadManager.DOWNLOAD_CODING == data.getDownloadState()) {
            hold.failImg.setVisibility(View.GONE);
            hold.duration.setVisibility(View.GONE);
            hold.progressBar.setVisibility(View.VISIBLE);
            hold.progressBar.setProgress(data.getDownloadProgress());
        } else if (VideoDownloadManager.DOWNLOAD_NOTFOUND == data.getDownloadState()) {
            hold.failImg.setVisibility(View.VISIBLE);
            hold.progressBar.setVisibility(View.GONE);
            hold.duration.setVisibility(View.VISIBLE);
            hold.duration.setText(mContext.getString(R.string.wv_video_download_item_download_notfound));
        } else if (VideoDownloadManager.DOWNLOAD_SUCCEED == data.getDownloadState()) {
            hold.failImg.setVisibility(View.GONE);
            hold.duration.setVisibility(View.VISIBLE);
            hold.progressBar.setVisibility(View.GONE);
            hold.duration.setText(data.getVideoDurationTime());
        } else {
            hold.failImg.setVisibility(View.VISIBLE);
            hold.progressBar.setVisibility(View.GONE);
            hold.duration.setVisibility(View.VISIBLE);
            hold.duration.setText(mContext.getString(R.string.wv_video_download_item_download_fail));
        }
    }

    public void setEditor(boolean editor) {
        isEditor = editor;
        selectList.clear();
    }

    public void deleteSelect() {
        VideoDownloadManager.getInstance().delete(selectList);
        selectList.clear();
    }

    public ArrayList<VideoDownloadBean> getSelectList() {
        return selectList;
    }

    public void selectAll() {
        for (VideoDownloadBean bean : list) {
            bean.setCheckable(true);
        }
        selectList.clear();
        selectList.addAll(list);
        notifyDataSetChanged();
    }

    private String getPlayUrl(VideoDownloadBean bean) {
        Map<String, String> params = new HashMap<>();
        params.put("oid", bean.getOid());
        params.put("stream", bean.getStream());
        return "";
        /* todo  获取url地址
        String url = AppApplication.getInstance().getServerInfo().getM3u8url();

        return url + CameraRequest.GetTsInfo + "?" + ZTELib.getInstence().getBusinessQuery(CameraRequest.GetTsInfo,
                AppApplication.devHostPresenter.getDevHost(bean.getOid()) != null ? AppApplication.devHostPresenter.getDevHost(bean.getOid()).getAk() : "",
                params); */

    }

    static class ViewHolder {
        TextView name;
        TextView from;
        TextView startTime;
        TextView duration;
        ImageView pic;
        ImageView failImg;
        CheckBox cb;
        SwipeLayout sl;
        LinearLayout delLayout;
        LinearLayout downloadLayout;
        RelativeLayout cbLayout;
        ProgressBar progressBar;
    }
}
