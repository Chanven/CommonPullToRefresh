/*
Copyright 2015 chanven

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.chanven.cptr.demo;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.GridViewWithHeaderAndFooter;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 * GridView with loadmore
 *
 * @author Chanven
 * @date 2015-10-13
 */
public class GridViewAtivity extends AppCompatActivity {
    PtrClassicFrameLayout ptrClassicFrameLayout;
    GridViewWithHeaderAndFooter mGridView;
    GridViewAdapter mAdapter;
    private List<String> mData = new ArrayList<>();
    Handler handler = new Handler();

    int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.girdview_layout);
        ptrClassicFrameLayout = (PtrClassicFrameLayout) this.findViewById(R.id.test_grid_view_frame);
        mGridView = (GridViewWithHeaderAndFooter) this.findViewById(R.id.test_grid_view);
        initData();
    }

    private void initData() {
        mAdapter = new GridViewAdapter(this, mData);
        mGridView.setAdapter(mAdapter);
        ptrClassicFrameLayout.postDelayed(new Runnable() {

            @Override
            public void run() {
                ptrClassicFrameLayout.autoRefresh(true);
            }
        }, 150);

        ptrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page = 0;
                        mData.clear();
                        for (int i = 0; i < 40; i++) {
                            mData.add(new String("GridView item  -" + i));
                        }
                        mAdapter.notifyDataSetChanged();
                        ptrClassicFrameLayout.refreshComplete();
                        ptrClassicFrameLayout.setLoadMoreEnable(true);
                    }
                }, 1500);
            }
        });

        ptrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void loadMore() {
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 0; i < 4; i++) {
                            mData.add(new String("GridView item -- add" + page));
                        }
                        mAdapter.notifyDataSetChanged();
                        ptrClassicFrameLayout.loadMoreComplete(true);
                        page++;
                        Toast.makeText(GridViewAtivity.this, "load more complete", Toast.LENGTH_SHORT).show();
                    }
                }, 1000);
            }
        });
    }


    public class GridViewAdapter extends BaseAdapter {
        private List<String> datas;
        private LayoutInflater inflater;

        public GridViewAdapter(Context context, List<String> data) {
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
