package net.ajcloud.wansviewplus.main.history;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.main.history.adapter.ImageListBaseAdapter;
import net.ajcloud.wansviewplus.main.history.entity.ImageInfo;
import net.ajcloud.wansviewplus.main.history.image.adapter.ImagePagerAdapter;
import net.ajcloud.wansviewplus.support.utils.SizeUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by mamengchao on 2018/07/06.
 * Function:    图片
 */
public class ImageListFragment extends Fragment {
    private ImageListBaseAdapter mAdapter;
    private StickyGridHeadersGridView mGridView;
    private ArrayList<ImageInfo> mlist = new ArrayList<>();
    private ArrayList<ImageInfo> mtmplist = new ArrayList<>();
    private HashMap<String, Boolean> mhasRecord = new HashMap<>();
    private LinearLayout imageLy;
    private String[] imagesandvideos;
    private String oid = "HN1A005FAC00362";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_list, container, false);
        mGridView = view.findViewById(R.id.asset_grid);
        imageLy = view.findViewById(R.id.image_ly);
        mAdapter = new ImageListBaseAdapter(getActivity(), mlist);
        mGridView.setHeadersIgnorePadding(true);
        mGridView.setAdapter(mAdapter);
        mGridView.setCanHeadDispach(false);
        mGridView.setLinePaddingLeft((int)SizeUtil.dp2px(getActivity(), 15));
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //滚动到底部自动加载(很重要)
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    int aa = view.getLastVisiblePosition();
                    int count = (view.getCount() - 1);
                    if (aa == count) {

                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        initData();
        showResult();
        return view;
    }

    private void getDirData() {
        File directory = new File(MainApplication.fileIO.getImageFileDirectory(oid));
        List<String> filepaths = new ArrayList<>();
        if (directory.isDirectory()) {
            if (!directory.exists())
                directory.mkdirs();
            if (directory.listFiles().length > 0) {
                for (File file : directory.listFiles()) {
                    filepaths.add(file.getAbsolutePath());
                }
            }
        }

        if (filepaths != null && filepaths.size() > 0) {
            imagesandvideos = filepaths.toArray(new String[0]);
            Arrays.sort(imagesandvideos, (lhs, rhs) -> {
                String first = getFileName(lhs);
                String second = getFileName(rhs);
                return second.compareToIgnoreCase(first);
            });
        } else {
            imagesandvideos = null;
        }
    }

    private String getFileName(String path) {
        String fileName;
        int start = path.lastIndexOf("/");
        if (start != -1) {
            fileName = path.substring(start + 1, path.length());
            if (fileName.startsWith("vlcsnap-")) {
                fileName = fileName.substring(8);
            }
            return fileName;
        }
        return "";
    }

    private void initData() {
        String fileName;
        getDirData();
        mlist.clear();
        if (imagesandvideos == null || imagesandvideos.length == 0) {
            return;
        }
        int section = 1;
        mtmplist.clear();
        mhasRecord.clear();
        Map<String, Integer> sectionMap = new HashMap<String, Integer>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //vlcsnap-2018-07-07-15h36m26s872.png
        for (int i = 0; i < imagesandvideos.length; i++) {
            String tmp = "";
            String pattern = "-|\\.";
            Pattern pat = Pattern.compile(pattern);
            int start = imagesandvideos[i].lastIndexOf("/");
            if (start != -1) {
                fileName = imagesandvideos[i].substring(start + 1, imagesandvideos[i].length());
                if (fileName.startsWith("vlcsnap-")) {
                    fileName = fileName.substring(11);
                }
            } else {
                continue;
            }

            final String[] rs = pat.split(fileName);

            if (rs.length == 5) { //2015-09-28-14h01m10s934.jpg
                long time = 0;
                String timeStr;
                tmp = rs[0] + "-" + rs[1] + "-" + rs[2];
                String hour = rs[3].substring(0, 2);
                String minute = rs[3].substring(3, 5);
                String second = rs[3].substring(6, 8);
                timeStr = tmp + " " + hour + ":" + minute + ":" + second;
                try {
                    time = dateFormat.parse(timeStr).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String imageName = "";
                String iamgeHead = "";

                ImageInfo listData = new ImageInfo();
                listData.setHeadTime(iamgeHead);
                listData.setCheck(false);
                listData.setImagePath(imagesandvideos[i]);
                listData.setImageName(imageName);

                if (!sectionMap.containsKey(iamgeHead)) {
                    listData.setSection(section);
                    sectionMap.put(iamgeHead, section);
                    section++;
                } else {
                    listData.setSection(sectionMap.get(iamgeHead));
                }
                mtmplist.add(listData);
            }
        }
        if (mtmplist.size() > 0) {
            mlist = new ArrayList<>(mtmplist);
        } else {
            mlist.clear();
        }
        mAdapter.setList(mlist);
        mAdapter.notifyDataSetChanged();
    }

    private void showResult() {
        if (mlist.size() == 0) {
            imageLy.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.GONE);
        } else {
            imageLy.setVisibility(View.GONE);
            mGridView.setVisibility(View.VISIBLE);
        }
    }
}
