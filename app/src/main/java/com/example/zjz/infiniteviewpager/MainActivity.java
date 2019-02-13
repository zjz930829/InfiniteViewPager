package com.example.zjz.infiniteviewpager;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private InfiniteViewPagerAdapter adapter;
    private List<String> lists;
    private int num = 50;// viewpager中缓存条数  [可以改变]
    private boolean flag = true;
    private int currentCnt = 50;//当前完成了多少关卡
    private int totalCnt = 80;//总关卡数
    private int startId = 0;//开始关卡ID
    private int endId;//结束关卡的ID
    private int pageSize = 20;//每次加载的条数  [可以改变]
    private int currentId;//当前关卡ID
    private int getStartId;//起始获取开始关卡ID
    private int getEndId;//起始获取结束关卡ID
    private boolean canLoadLeft;//可否加载前面关卡
    private boolean canLoadRight;//可否加载后面关卡
    private int tempLoadSize = 0;//暂存每次加载多少条数据
    private boolean resetAdapter;//是否重新添加adapter
    private int loadWay = 0;//往左加载还是往右加载数据 1:往左  2:往右
    private int currentPosition;
    private int currentItem = 0;
    boolean isRight = false;// 向右
    private String ids;//用于正式请求接口的ids

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewpager);
        initData();
    }

    private void initData() {
        currentCnt = 10;//用户已完成的关卡数
        totalCnt = 100;//总共的关卡数
        startId = 1;//开始的关卡id
        currentId = currentCnt + startId;
        endId = startId + totalCnt - 1;
        if (currentCnt > 5) {//判断用户已完成关卡是否大于5关，否则直接从startId开始加载
            getStartId = currentId - 5;
            getEndId = currentId + 5;
        } else {
            getStartId = startId;
            getEndId = startId + 10;
        }
        if (getStartId == startId) {
            canLoadLeft = false;
        } else {
            canLoadLeft = true;
        }
        if (getEndId < endId) {
            canLoadRight = true;
        } else {
            canLoadRight = false;
        }

        int start = getStartId;
        int total = totalCnt > 11 ? 11 : totalCnt;
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < total; i++) {
            buffer.append(start);
            if (start == currentId) {
                currentItem = i;
            }
            if (i != total - 1) {
                buffer.append(",");
            }
            start++;
        }
        ids = buffer.toString();//目前ids没用上
        //TODO 以下，可以进行网络请求，请求最初的list数目
        initList(ids);
    }

    private void initList(String ids) {
        String[] idList = ids.split(",");
        ArrayList<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(idList));

        lists = list;
        initViewPager();
    }

    private int lastValue = -1;

    private void initViewPager() {

        viewPager.setPageMargin(30);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setPageTransformer(true, new ScaleInTransformer(0.75f));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                Log.d("zjz", "isRight=" + isRight);
                int position = viewPager.getCurrentItem();
                currentPosition = position;
                Log.d("zjz", "arg0=" + arg0 + ",position=" + position);
                if (canLoadLeft && !isRight && position == 1) {
                    //往左滑动,加载前面关卡
                    isRight = true;
                    int start = getStartId - 1;
                    loadMoreLeftData(start, position);
                } else if (canLoadRight && isRight && position == lists.size() - 2) {
                    //往右滑动,加载后面关卡
//                    isRight=false
                    int end = getEndId + 1;
                    loadMoreRightData(end, position);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset != 0) {
                    if (lastValue >= positionOffsetPixels) {
                        //左
                        isRight = false;
                    } else if (lastValue < positionOffsetPixels) {
                        //右
                        isRight = true;
                    }
                }
                lastValue = positionOffsetPixels;
//                LogUtils.i("zjz","arg0="+arg0+",arg1="+arg1+",arg2="+arg2);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        adapter = new InfiniteViewPagerAdapter(this, currentId);
        adapter.setLists(lists);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentItem);
//        setCurrentStage(currentItem);
    }

    /**
     * 加载前面数据
     *
     * @param start
     * @param position
     */
    private void loadMoreLeftData(int start, int position) {
        Toast.makeText(MainActivity.this, "加载前面数据", Toast.LENGTH_SHORT).show();
        StringBuilder buffer = new StringBuilder();
        tempLoadSize = 0;
        int i = 0;
        loadWay = 1;
        //以下算法是算出加载前面数据需要到的ids
        for (; i < pageSize; i++) {
            if (start >= startId) {
                canLoadLeft = true;
                buffer.append(start + ",");
                start--;
//                if (getStartId > start) {
//                    getStartId--;
//                    Log.d("zjz", "getStartId=" + getStartId);
//                }
            } else {
                canLoadLeft = false;
                break;
            }
        }

        tempLoadSize = i;
        if (buffer.length() != 0) {
            String ids = buffer.delete(buffer.length() - 1, buffer.length()).toString();//ids也是用于接口的请求，目前没用上
            //TODO 以下，可以进行网络请求，加载前面的list数目
            loadMoreData(ids);
        }
    }


    /**
     * 加载后面数据
     *
     * @param end
     * @param position
     */
    private void loadMoreRightData(int end, int position) {
        Toast.makeText(MainActivity.this, "加载后面数据", Toast.LENGTH_SHORT).show();
        loadWay = 2;
        tempLoadSize = 0;
        resetAdapter = false;
        int i = 0;
        StringBuilder buffer = new StringBuilder();
        //以下算法是算出加载后面数据需要到的ids
        for (; i < pageSize; i++) {
            if (end <= endId) {
                canLoadRight = true;
                buffer.append(end + ",");
                end++;
//                if (end > getEndId) {
//                    getEndId++;
//                    Log.d("zjz", "getEndId=" + getEndId);
//                }
            } else {
                canLoadRight = false;
                break;
            }

        }

        tempLoadSize = i;

        if (buffer.length() != 0) {
            String ids = buffer.delete(buffer.length() - 1, buffer.length()).toString();//ids也是用于接口的请求，目前没用上
            //TODO 以下，可以进行网络请求，加载后面的list数目
            loadMoreData(ids);
        }

    }

    private void loadMoreData(String ids) {

        ArrayList<String> list = new ArrayList<String>();
        String[] idList = ids.split(",");
        list.addAll(Arrays.asList(idList));

        if (loadWay == 1) {
            initMoreLeftData(list);
        } else if (loadWay == 2) {
            initMoreRightData(list);
        }
    }

    /**
     * 添加前面的数据
     *
     * @param list
     */
    private void initMoreLeftData(List<String> list) {
        int start = getStartId - 1;
        int listSize = list.size();
        Log.i("zjz", "initMoreLeftData====listSize=" + listSize + ",tempLoadSize=" + tempLoadSize);
        for (int i = 0; i < listSize; i++) {
            lists.add(0, list.get(i));
            if (lists.size() > num) {
                lists.remove(num);
                if ((getEndId - start) >= num) {
                    getEndId--;
                    canLoadRight = true;
                    Log.d("zjz", "getEndId=" + getEndId);
                }
            }
            start--;
            if (getStartId > start) {
                getStartId--;
                Log.d("zjz", "getStartId=" + getStartId);
            }
        }
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPosition + tempLoadSize);
    }


    /**
     * 添加后面的数据
     *
     * @param list
     */
    private void initMoreRightData(List<String> list) {
        int end = getEndId + 1;
        int listSize = list.size();
        Log.i("zjz", "initMoreRightData====listSize=" + listSize + ",tempLoadSize=" + tempLoadSize);
        for (int i = 0; i < listSize; i++) {
            lists.add(list.get(i));
            if (lists.size() > num) {
                resetAdapter = true;
                lists.remove(0);
                if ((end - getStartId) >= num) {
                    getStartId++;
                    canLoadLeft = true;
                    Log.d("zjz", "getStartId=" + getStartId);
                }
            } else {
                resetAdapter = false;
            }
            end++;
            if (end > getEndId) {
                getEndId++;
                Log.d("zjz", "getEndId=" + getEndId);
            }
        }
        if (resetAdapter) {
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(lists.size() - tempLoadSize - 2);
        } else {
            adapter.notifyDataSetChanged();
        }
    }
}
