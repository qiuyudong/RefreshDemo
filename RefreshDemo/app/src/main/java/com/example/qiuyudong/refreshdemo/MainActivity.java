package com.example.qiuyudong.refreshdemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView mRecycler;
    private List<String> mList;
    private MyRecyclerAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayoutManager linearLayoutManager;
    private Handler handler = new Handler();
    //最后一个item的位置
    int lastVisibleItem;
    int page = 0;
    boolean isLoading = false;//用来控制进入getdata()的次数
    int totlePage = 3;//模拟请求的一共的页数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiprefresh);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(linearLayoutManager);
        mList = new ArrayList<>();
        mAdapter = new MyRecyclerAdapter(this, mList);
        mRecycler.setAdapter(mAdapter);

        //设置下拉刷新的颜色
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        //第一次加载刷新
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        //设置下拉刷新
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mList.clear();
                initData();
                //改变foot_item的状态，这里可以根据自身的业务需求相应修改
                mAdapter.changeState(1);
                page = 0;
            }
        });

        //给recyclerView添加滑动监听
        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断是否在执行下拉刷新
                boolean isRefreshing = mSwipeRefreshLayout.isRefreshing();
                Log.d(TAG, "onScrollStateChanged: "+isRefreshing);

                /*
                到达底部了,如果不加!isLoading的话到达底部如果还一滑动的话就会一直进入这个方法
                就一直去做请求网络的操作,这样的用户体验肯定不好.添加一个判断,每次滑倒底只进行一次网络请求去请求数据
                当请求完成后,在把isLoading赋值为false,下次滑倒底又能进入这个方法了
                 */
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mAdapter.getItemCount()
                        && !isLoading && !isRefreshing) {
                    //到达底部之后如果footView的状态不是正在加载的状态,就将 他切换成正在加载的状态
                    if (page < totlePage) {
                        Log.e("duanlian", "onScrollStateChanged: " + "进来了");
                        isLoading = true;
                        mAdapter.changeState(1);
                        //延迟2秒，模拟网络请求过程
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getData();
                                page++;
                            }
                        }, 2000);
                    } else {
                        mAdapter.changeState(2);

                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //拿到最后一个出现的item的位置
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });

    }


    private void initData() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 20; i++) {
                    mList.add("初始化的第" + i + "条数据");
                }
                isLoading = false;
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1500);



    }


    /**
     * 模拟请求数据
     */
    private void getData() {
        for (int i = 0; i < 5; i++) {
            mList.add("加载的第" + i + "条数据");
        }
        isLoading = false;
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}