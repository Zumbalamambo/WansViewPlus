package net.ajcloud.wansviewplus.main.history.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.history.entity.ImageInfo;
import net.ajcloud.wansviewplus.main.history.image.ui.ImagePagerActivity;

import java.util.ArrayList;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class VideoListBaseAdapter
        extends BaseAdapter
        implements StickyGridHeadersSimpleAdapter
{
    private final Activity mContext;
    private LayoutInflater mInflater;
    private ArrayList<ImageInfo> mList;

    private boolean hasEdit = false;

    public VideoListBaseAdapter(Activity context, ArrayList<ImageInfo> list) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mList = list;
    }
    
    @Override
    public int getCount() {
        return getList().size();
    }

    @Override
    public Object getItem(int position) {
        return getList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        String path = getList().get(position).getImagePath();
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.history_video_item, parent, false);
            mViewHolder = new ViewHolder();
            mViewHolder.mImageView = convertView.findViewById(R.id.grid_item);
            mViewHolder.mEditBox  = convertView.findViewById(R.id.checkBox);
            mViewHolder.mTvTime = convertView.findViewById(R.id.tv_time);
            mViewHolder.mVideoTime = convertView.findViewById(R.id.video_time);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        //截取视频图片
        if (TextUtils.isEmpty(mViewHolder.path) ||  !mViewHolder.path.equalsIgnoreCase(path)) {
            Flowable.just(path)
                    .observeOn(Schedulers.io())
                    .map(new Function<String, Bitmap>() {
                        @Override public Bitmap apply(@NonNull String path) throws Exception {
                            Bitmap thumbnail = null;
                            try {
                                Bitmap videoThumbnail = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
                                thumbnail = ThumbnailUtils.extractThumbnail(videoThumbnail, 800, 450, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                            } catch (OutOfMemoryError e) {
                                e.printStackTrace();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                            if (null == thumbnail) {
                                thumbnail = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.realtime_picture);
                            }
                            return thumbnail;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Bitmap>() {
                        @Override public void accept(@NonNull Bitmap bitmap) throws Exception {
                            try {
                                mViewHolder.mImageView.setImageBitmap(bitmap);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
        }

        mViewHolder.path = path;
        mViewHolder.mImageView.setTag(mViewHolder);
        mViewHolder.mImageView.setOnClickListener(view -> {
            if (isEdit()) {
                ViewHolder viewHolder = (ViewHolder)view.getTag();
                boolean isCheck = !getList().get(position).isCheck();
                getList().get(position).setCheck(isCheck);
                viewHolder.mEditBox.setChecked(isCheck);
            } else {
                int section = getList().get(position).getSection();
            }
        });

        if (isEdit()) {
//            编辑模式
            mViewHolder.mEditBox.setVisibility(View.VISIBLE);
            mViewHolder.mEditBox.setChecked(getList().get(position).isCheck());
        } else {
//            普通模式
            mViewHolder.mEditBox.setVisibility(View.INVISIBLE);
            mViewHolder.mEditBox.setChecked(false);
        }

        if (!TextUtils.isEmpty(getList().get(position).getImageName())) {
            mViewHolder.mTvTime.setText(getList().get(position).getImageName());
        }

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent, boolean floatHead) {
        HeaderViewHolder mHeaderHolder;
        int section = getList().get(position).getSection();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.history_image_header, null, false);
            mHeaderHolder = new HeaderViewHolder();
            mHeaderHolder.mTextView = convertView.findViewById(R.id.header);
            mHeaderHolder.mTimeView = convertView.findViewById(R.id.timePic);
            mHeaderHolder.section = section;
            convertView.setTag(mHeaderHolder);
        } else {
            mHeaderHolder = (HeaderViewHolder) convertView.getTag();
        }

        mHeaderHolder.mTextView.setText(getList().get(position).getHeadTime());

        if (floatHead || position == 0) {
            mHeaderHolder.mTimeView.setImageResource(R.mipmap.dot_blue);
        } else {
            mHeaderHolder.mTimeView.setImageResource(R.mipmap.dot_gray);
        }

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return getList().get(position).getSection();
    }

    public boolean isEdit() {
        return hasEdit;
    }

    public void setEdit(boolean hasEdit) {
        this.hasEdit = hasEdit;
    }

    public ArrayList<ImageInfo> getList() {
        return this.mList;
    }

    public void setList(ArrayList<ImageInfo> list) {
        this.mList = list;
    }

    public static class ViewHolder {
        public ImageView mImageView;
        public CheckBox mEditBox;
        public TextView mTvTime;
        public TextView mVideoTime;
        //缓存视频路径
        public String path;
    }

    public static class HeaderViewHolder {
        public int section;
        public TextView mTextView;
        public ImageView mTimeView;
    }
}
