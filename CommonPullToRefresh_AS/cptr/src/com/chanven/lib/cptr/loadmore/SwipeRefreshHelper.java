package com.chanven.lib.cptr.loadmore;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;

import java.lang.reflect.Field;

/**
 * SwipeRefreshHelper
 * Created by Chanven on 2015-11-4.
 */
public class SwipeRefreshHelper {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mContentView;

    private OnSwipeRefreshListener mOnSwipeRefreshListener;

    private boolean isLoading = false;
    private boolean isAutoLoadMore = true;
    private boolean isLoadMoreEnable = false;
    private boolean hasInitLoadMoreView = false;
    private ILoadViewMoreFactory loadViewFactory = new DefaultLoadMoreFooter();
    private ListViewHandler listViewHandler = new ListViewHandler();
    private RecyclerViewHandler recyclerViewHandler = new RecyclerViewHandler();

    private OnLoadMoreListener mOnLoadMoreListener;
    private ILoadViewMoreFactory.ILoadMoreView mLoadMoreView;

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (null != mOnSwipeRefreshListener) {
                mOnSwipeRefreshListener.onfresh();
            }
        }
    };

    public void autoRefresh() {
        if(null != mOnSwipeRefreshListener) {
            mSwipeRefreshLayout.setRefreshing(true);
            mOnSwipeRefreshListener.onfresh();
        }
    }

    public SwipeRefreshHelper(SwipeRefreshLayout refreshLayout) {
        this.mSwipeRefreshLayout = refreshLayout;
        init();
    }

    private void init() {
        if (mSwipeRefreshLayout.getChildCount() <= 0) {
            throw new RuntimeException("SwipRefreshLayout has no child view");
        }
        try {
            Field field = mSwipeRefreshLayout.getClass().getDeclaredField("mTarget");
            field.setAccessible(true);
            mContentView = (View) field.get(mSwipeRefreshLayout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnSwipeRefreshListener(OnSwipeRefreshListener onSwipeRefreshListener) {
        this.mOnSwipeRefreshListener = onSwipeRefreshListener;
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.mOnLoadMoreListener = onLoadMoreListener;
    }

    public void refreshComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void setLoadMoreEnable(boolean enable) {
        if (this.isLoadMoreEnable == enable) {
            return;
        }
        this.isLoadMoreEnable = enable;
        if (!hasInitLoadMoreView && isLoadMoreEnable) {
            mLoadMoreView = loadViewFactory.madeLoadMoreView();
            if (mContentView instanceof AbsListView) {
                hasInitLoadMoreView = listViewHandler.handleSetAdapter(mContentView, mLoadMoreView, onClickLoadMoreListener);
                listViewHandler.setOnScrollBottomListener(mContentView, onScrollBottomListener);
            } else if (mContentView instanceof RecyclerView) {
                hasInitLoadMoreView = recyclerViewHandler.handleSetAdapter(mContentView, mLoadMoreView, onClickLoadMoreListener);
                recyclerViewHandler.setOnScrollBottomListener(mContentView, onScrollBottomListener);
            }
        }
    }

    private OnScrollBottomListener onScrollBottomListener = new OnScrollBottomListener() {
        @Override
        public void onScorllBootom() {
            if (isAutoLoadMore && isLoadMoreEnable && !isLoading()) {
                // 此处可加入网络是否可用的判断
                loadMore();
            }
        }
    };

    private View.OnClickListener onClickLoadMoreListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            loadMore();
        }
    };

    private void loadMore() {
        isLoading = true;
        mLoadMoreView.showLoading();
        if (null != mOnLoadMoreListener) {
            mOnLoadMoreListener.loadMore();
        }
    }

    public void loadMoreComplete(boolean hasMore) {
        isLoading = false;
        if (hasMore) {
            mLoadMoreView.showNormal();
        } else {
            setNoMoreData();
        }
    }

    public void setNoMoreData() {
        isLoading = false;
        mLoadMoreView.showNomore();
    }

    public boolean isLoading() {
        return isLoading;
    }

    public static interface OnSwipeRefreshListener {
        public void onfresh();
    }
}
