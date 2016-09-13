package com.chanven.cptr.demo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.loadmore.SwipeRefreshHelper;
import com.chanven.lib.cptr.loadmore.SwipeRefreshHelper.OnSwipeRefreshListener;

import java.util.ArrayList;
import java.util.List;

/**
 * listView with load more in swipeRefreshLayout
 * Created by Chanven on 2015-11-5.
 */
public class SwipeListViewActivity extends AppCompatActivity{
    private SwipeRefreshLayout mSryt;
    private ListView mListView;

    private List<String> mDatas = new ArrayList<>();
    private ListViewAdapter mAdapter;
    private SwipeRefreshHelper mSwipeRefreshHelper;

    private int page = 0;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_listview_layout);
        initView();
        initData();
    }

    private void initView() {
        mSryt = (SwipeRefreshLayout) this.findViewById(R.id.sryt_swipe_listview);
        mListView = (ListView) findViewById(R.id.lv_swipe_listview);
        mSryt.setColorSchemeColors(Color.BLUE);
    }

    private void initData() {
        mAdapter = new ListViewAdapter(this, mDatas);
        mListView.setAdapter(mAdapter);
        mSwipeRefreshHelper = new SwipeRefreshHelper(mSryt);

        mSryt.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshHelper.autoRefresh();
            }
        });

        mSwipeRefreshHelper.setOnSwipeRefreshListener(new OnSwipeRefreshListener() {
            @Override
            public void onfresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDatas.clear();
                        page = 0;
                        for (int i = 0; i < 17; i++) {
                            mDatas.add(new String("  SwipeListView item  -" + i));
                        }
                        mAdapter.notifyDataSetChanged();
                        mSwipeRefreshHelper.refreshComplete();
                        mSwipeRefreshHelper.setLoadMoreEnable(true);
                    }
                }, 1500);
            }
        });

        mSwipeRefreshHelper.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDatas.add(new String("  SwipeListView item  - add " + page));
                        mAdapter.notifyDataSetChanged();
                        mSwipeRefreshHelper.loadMoreComplete(true);
                        page++;
                        Toast.makeText(SwipeListViewActivity.this, "load more complete", Toast.LENGTH_SHORT).show();
                    }
                }, 1000);
            }
        });

    }

    OnSwipeRefreshListener mOnSwipeRefreshListener = new OnSwipeRefreshListener() {
        @Override
        public void onfresh() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDatas.clear();
                    page = 0;
                    for (int i = 0; i < 17; i++) {
                        mDatas.add(new String("  SwipeListView item  -" + i));
                    }
                    mAdapter.notifyDataSetChanged();
                    mSwipeRefreshHelper.refreshComplete();
                    mSwipeRefreshHelper.setLoadMoreEnable(true);
                }
            }, 1000);
        }
    };


    public class ListViewAdapter extends BaseAdapter {
        private List<String> datas;
        private LayoutInflater inflater;

        public ListViewAdapter(Context context, List<String> data) {
            super();
            inflater = LayoutInflater.from(context);
            datas = data;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.listitem_layout, parent, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(datas.get(position));
            return convertView;
        }

        public List<String> getData() {
            return datas;
        }

    }
}
