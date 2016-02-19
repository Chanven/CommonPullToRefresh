/*
Copyright 2015 Chanven

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
package com.chanven.lib.cptr.loadmore;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chanven.lib.cptr.R;

/**
 * default load more view
 */
public class DefaultLoadMoreFooter implements ILoadViewMoreFactory {

    @Override
    public ILoadMoreView madeLoadMoreView() {
        return new LoadMoreHelper();
    }

    private class LoadMoreHelper implements ILoadMoreView {

        protected TextView footerTv;
        protected ProgressBar footerBar;

        protected OnClickListener onClickRefreshListener;

        @Override
        public void init(FootViewAdder footViewHolder, OnClickListener onClickRefreshListener) {
            View view = footViewHolder.addFootView(R.layout.loadmore_default_footer);
            footerTv = (TextView) view.findViewById(R.id.loadmore_default_footer_tv);
            footerBar = (ProgressBar) view.findViewById(R.id.loadmore_default_footer_progressbar);
            this.onClickRefreshListener = onClickRefreshListener;
            showNormal();
        }

        @Override
        public void showNormal() {
            footerTv.setText("点击加载更多");
            footerBar.setVisibility(View.GONE);
            footerTv.setOnClickListener(onClickRefreshListener);
        }

        @Override
        public void showLoading() {
            footerTv.setText("正在加载中...");
            footerBar.setVisibility(View.VISIBLE);
            footerTv.setOnClickListener(null);
        }

        @Override
        public void showFail(Exception exception) {
            footerTv.setText("加载失败，点击重新");
            footerBar.setVisibility(View.GONE);
            footerTv.setOnClickListener(onClickRefreshListener);
        }

        @Override
        public void showNomore() {
            footerTv.setText("已经加载完毕");
            footerBar.setVisibility(View.GONE);
            footerTv.setOnClickListener(null);
        }

    }

}
