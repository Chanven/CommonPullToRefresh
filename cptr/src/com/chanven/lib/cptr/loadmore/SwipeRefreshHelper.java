package com.chanven.lib.cptr.loadmore;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;

import java.lang.reflect.Field;

/**
 * SwipeRefreshHelper
 * Created by Chanven on 2015-11-4.
 */
public class SwipeRefreshHelper {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mContentView;

    private OnSwipeRefreshListener mOnSwipeRefreshListener;

    private LoadMoreHandler mLoadMoreHandler;

    private boolean isLoadingMore = false;
    private boolean isAutoLoadMoreEnable = true;
    private boolean isLoadMoreEnable = false;
    private boolean hasInitLoadMoreView = false;
    private ILoadMoreViewFactory loadMoreViewFactory = new DefaultLoadMoreViewFooter();

    private OnLoadMoreListener mOnLoadMoreListener;
    private ILoadMoreViewFactory.ILoadMoreView mLoadMoreView;

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

    public void setFooterView(ILoadMoreViewFactory factory) {
        if (null == factory || (null != loadMoreViewFactory && loadMoreViewFactory == factory)) {
            return;
        }

        loadMoreViewFactory = factory;

        if (hasInitLoadMoreView) {
            mLoadMoreHandler.removeFooter();
            mLoadMoreView = loadMoreViewFactory.madeLoadMoreView();
            hasInitLoadMoreView = mLoadMoreHandler.handleSetAdapter(mContentView, mLoadMoreView,
                    onClickLoadMoreListener);
            if (!isLoadMoreEnable) {
                mLoadMoreHandler.removeFooter();
            }
        }
    }

    public void setLoadMoreEnable(boolean enable) {
        if (this.isLoadMoreEnable == enable) {
            return;
        }
        this.isLoadMoreEnable = enable;
        if (!hasInitLoadMoreView && isLoadMoreEnable) {
            mLoadMoreView = loadMoreViewFactory.madeLoadMoreView();

            if (null == mLoadMoreHandler) {
                if (mContentView instanceof GridView) {
                    mLoadMoreHandler = new GridViewHandler();
                } else if (mContentView instanceof AbsListView) {
                    mLoadMoreHandler = new ListViewHandler();
                } else if (mContentView instanceof RecyclerView) {
                    mLoadMoreHandler = new RecyclerViewHandler();
                }
            }

            if (null == mLoadMoreHandler) {
                throw new IllegalStateException("unSupported contentView !");
            }

            hasInitLoadMoreView = mLoadMoreHandler.handleSetAdapter(mContentView, mLoadMoreView,
                    onClickLoadMoreListener);
            mLoadMoreHandler.setOnScrollBottomListener(mContentView, onScrollBottomListener);
            return;
        }

        if (hasInitLoadMoreView) {
            if (isLoadMoreEnable) {
                mLoadMoreHandler.addFooter();
            } else {
                mLoadMoreHandler.removeFooter();
            }
        }
    }

    public boolean isLoadMoreEnable() {
        return isLoadMoreEnable;
    }

    public void setAutoLoadMoreEnable(boolean isAutoLoadMoreEnable) {
        this.isAutoLoadMoreEnable = isAutoLoadMoreEnable;
    }

    private OnScrollBottomListener onScrollBottomListener = new OnScrollBottomListener() {
        @Override
        public void onScorllBootom() {
            if (isAutoLoadMoreEnable && isLoadMoreEnable && !isLoadingMore()) {
                // can check network here
                loadMore();
            }
        }
    };

    private View.OnClickListener onClickLoadMoreListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (isLoadMoreEnable && !isLoadingMore()) {
                loadMore();
            }
        }
    };

    private void loadMore() {
        isLoadingMore = true;
        mLoadMoreView.showLoading();
        if (null != mOnLoadMoreListener) {
            mOnLoadMoreListener.loadMore();
        }
    }

    public void loadMoreComplete(boolean hasMore) {
        isLoadingMore = false;
        if (hasMore) {
            mLoadMoreView.showNormal();
        } else {
            setNoMoreData();
        }
    }

    public void setNoMoreData() {
        isLoadingMore = false;
        mLoadMoreView.showNomore();
    }

    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    public static interface OnSwipeRefreshListener {
        public void onfresh();
    }
}
