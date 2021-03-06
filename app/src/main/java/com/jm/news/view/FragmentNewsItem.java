package com.jm.news.view;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jm.news.R;
import com.jm.news.customview.MClassicsHeaderView;
import com.jm.news.customview.MFragmentBase;
import com.jm.news.define.BaseViewClickListener;
import com.jm.news.define.DataDef;
import com.jm.news.entry.ViewHolderOneImage;
import com.jm.news.entry.ViewHolderThreeImage;
import com.jm.news.util.CommonUtils;
import com.jm.news.util.JumpUtils;
import com.jm.news.util.LogUtils;
import com.jm.news.viewmodel.FragmentNewsItemViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

public class FragmentNewsItem extends MFragmentBase {

    // static field
    private static final String TAG = "FragmentNewsItem";
    // Fragment ID
    private int mFragmentID;
    // control field
    private SmartRefreshLayout mSmartRefreshLayout;
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter mViewAdapter;
    private TextView mTvErrorTips;
    // function related field
    private RequestOptions mGlideOptions = new RequestOptions()
//            .skipMemoryCache(true)                // 设备配置较低的可以关闭内存缓存
            .placeholder(R.mipmap.loading_static)   // 图片加载出来前，显示的图片
            .fallback(R.mipmap.load_error)          // url为空的时候,显示的图片
            .error(R.mipmap.load_error);            // 图片加载失败后，显示的图片
    // viewmodel related field
    private FragmentNewsItemViewModel mViewModel;


    public FragmentNewsItem() {
        super();
    }

    @SuppressLint("ValidFragment")
    public FragmentNewsItem(int fragmentID) {
        this.mFragmentID = fragmentID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreateView: ");
        View view = getInflaterView(R.layout.layout_fragment_news_item, inflater, container, savedInstanceState);
        mSmartRefreshLayout = view.findViewById(R.id.sfl_viewblock);
        mRecyclerView = view.findViewById(R.id.rv_viewblock);
        mTvErrorTips = view.findViewById(R.id.tv_error_tips);
        if (!isRecreate()) {
            initData();
            initView();
        }
        return view;
    }

    @Override
    public void onStart() {
        if (mTvErrorTips.getVisibility() == View.VISIBLE && CommonUtils.getInstance().isNetworkAvailable()) {
            updateStatus(true);
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        LogUtils.d(TAG, "onDestroy: ");
        if (null != mViewAdapter) {
            mViewAdapter.removeListener();
        }
        mViewModel = null;
        mViewAdapter = null;
        mSmartRefreshLayout = null;
        mRecyclerView = null;
        mGlideOptions = null;
        mTvErrorTips = null;
        super.onDestroy();
    }

    /******************************* private function *****************************/
    private void initData() {
        mViewModel = ViewModelProviders.of(this).get(FragmentNewsItemViewModel.class);
        mViewModel.updatetChannelID(mFragmentID);

    }

    private void initView() {
//        mSmartRefreshLayout.setRefreshHeader(new BezierRadarHeader(getContext()).setEnableHorizontalDrag(true));
        mSmartRefreshLayout.setRefreshHeader(new MClassicsHeaderView(getContext()));
        mSmartRefreshLayout.setRefreshFooter(new BallPulseFooter(getContext()).setSpinnerStyle(SpinnerStyle.Translate));
        mSmartRefreshLayout.setOnRefreshListener(new MyRefreshListener());
        mSmartRefreshLayout.setOnLoadMoreListener(new MyLoadMoreListener());

        mViewAdapter = new MyRecyclerViewAdapter();
        mViewAdapter.setmOnItemClickListener(new MyItemClickListener());
        mTvErrorTips.setOnClickListener(new MyOnClickListener());

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mViewAdapter);

        NewsDataStatusObserver newsDataStatusObserver = new NewsDataStatusObserver();
        mViewModel.getNewsDataStatus().observe(this, newsDataStatusObserver);
        NewsDataCountStatusObserver newsDataCountStatusObserver = new NewsDataCountStatusObserver();
        mViewModel.getNewsDataCountStatus().observe(this, newsDataCountStatusObserver);

        updateStatus(true);
    }

    private void updateStatus(boolean isRefresh) {
        if (CommonUtils.getInstance().isNetworkAvailable()) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mTvErrorTips.setVisibility(View.GONE);
            if (isRefresh) {
                mSmartRefreshLayout.autoRefresh();
            }
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mTvErrorTips.setVisibility(View.VISIBLE);
            mTvErrorTips.setText(R.string.tips_net_invisible);
        }
    }

    /*************************************** observer function *******************************************/
    private class NewsDataStatusObserver implements Observer<Integer> {
        @Override
        public void onChanged(@Nullable Integer integer) {
            LogUtils.d(TAG, "onChanged: getNewsDataStatus status = " + integer);
            if (integer == DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED) {
                mSmartRefreshLayout.finishRefresh(false);
                mSmartRefreshLayout.finishLoadMore(false);
                mRecyclerView.setVisibility(View.GONE);
                mTvErrorTips.setVisibility(View.VISIBLE);
                mTvErrorTips.setText(R.string.tips_request_failed);
            } else if (integer == DataDef.RequestStatusType.DATA_STATUS_NETWORK_DISCONNECTED) {
                mSmartRefreshLayout.finishRefresh(false);
                mSmartRefreshLayout.finishLoadMore(false);
                mRecyclerView.setVisibility(View.GONE);
                mTvErrorTips.setVisibility(View.VISIBLE);
                mTvErrorTips.setText(R.string.tips_net_invisible);
            } else if (integer == DataDef.RequestStatusType.DATA_STATUS_NO_MORE_DATA) {
                mSmartRefreshLayout.finishLoadMore(true);
                CommonUtils.getInstance().showToastView(R.string.toast_app_data_request_no_more);
            }
        }
    }

    private class NewsDataCountStatusObserver implements Observer<Boolean> {
        @Override
        public void onChanged(@Nullable Boolean b) {
            mViewAdapter.notifyDataSetChanged();
            if (b.booleanValue()) {
                mSmartRefreshLayout.finishRefresh(true);
            } else {
                mSmartRefreshLayout.finishLoadMore(true);
            }
            mRecyclerView.setVisibility(View.VISIBLE);
            mTvErrorTips.setVisibility(View.GONE);
        }
    }

    /*************************************** listener function *******************************************/
    private class MyItemClickListener extends BaseViewClickListener {
        @Override
        public void onItemClick(View view, int position) {
            LogUtils.d(TAG, "onItemClick: position=" + position);
            String newsLink = mViewModel.getNewsLink(position);
            if (!TextUtils.isEmpty(newsLink)) {
                JumpUtils.jumpWebView(getActivity(), newsLink, true);
            }
        }

        @Override
        public void onItemLongClick(View view, final int position) {
            PopupMenu popupMenu = CommonUtils.getPopupMenu(getActivity(), view, R.menu.menu_popup_news_item, false);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_news_item_open_detail:
                            String newsLink = mViewModel.getNewsLink(position);
                            if (!TextUtils.isEmpty(newsLink)) {
                                JumpUtils.jumpWebView(getActivity(), newsLink, true);
                            }
                            break;
                        case R.id.menu_news_item_open_other:
                            String jumpURl = mViewModel.getNewsLink(position);
                            if (!TextUtils.isEmpty(jumpURl)) {
                                JumpUtils.jumpOtherApp(getActivity(), jumpURl);
                            }
                            break;
                        case R.id.menu_news_item_delete:
                            mViewModel.removeNewsItem(position);
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
            popupMenu.show();
        }
    }

    private class MyRefreshListener implements OnRefreshListener {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            LogUtils.d(TAG, "onRefresh: ");
            if (CommonUtils.getInstance().isNetworkAvailable()) {
                mViewModel.requestRefreshData();
            } else {
                mSmartRefreshLayout.finishRefresh(false);
            }
            updateStatus(false);
        }
    }

    private class MyLoadMoreListener implements OnLoadMoreListener {
        @Override
        public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            LogUtils.d(TAG, "onLoadMore: ");
            if (CommonUtils.getInstance().isNetworkAvailable()) {
                mViewModel.requestLoadMoreData();
            } else {
                mSmartRefreshLayout.finishLoadMore(false);
            }
            updateStatus(false);
        }
    }

    private class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (CommonUtils.getInstance().isNetworkAvailable()) {
                updateStatus(true);
                LogUtils.d(TAG, "onClick: net is visible");
            } else {
                LogUtils.d(TAG, "onClick: net is invisible");
            }
        }
    }

    /*************************************** inner class *******************************************/
    private class MyRecyclerViewAdapter extends RecyclerView.Adapter implements View.OnClickListener, View.OnLongClickListener {

        private LayoutInflater inflater = LayoutInflater.from(getContext());
        private BaseViewClickListener mItemClickListener;

        public void setmOnItemClickListener(BaseViewClickListener mItemClickListener) {
            this.mItemClickListener = mItemClickListener;
        }

        public void removeListener() {
            this.mItemClickListener = null;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
            LogUtils.d(TAG, "onCreateViewHolder: i=" + i);
            View view = null;
            RecyclerView.ViewHolder holder = null;
            if (i >= 3) {
                view = inflater.inflate(R.layout.layout_listitem_three_image, viewGroup, false);
                holder = new ViewHolderThreeImage(view);
            } else {
                view = inflater.inflate(R.layout.layout_listitem_one_image, viewGroup, false);
                holder = new ViewHolderOneImage(view);
            }
            holder.itemView.setOnClickListener(this);
            holder.itemView.setOnLongClickListener(this);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder instanceof ViewHolderOneImage) {
                ViewHolderOneImage holder = (ViewHolderOneImage) viewHolder;
                holder.getTvTitle().setText(mViewModel.getNewsTitle(i));
                holder.getTvSource().setText(mViewModel.getNewsSource(i));
                holder.getTvPubData().setText(mViewModel.getPubDate(i));
                if (mViewModel.getItemImgCount(i) > 0) {
                    holder.getLlImageLatout().setVisibility(View.VISIBLE);
                    Glide.with(holder.getIvImage())
                            .load(mViewModel.getImgUrls(i, 0)).apply(mGlideOptions).into(holder.getIvImage());
                } else {
                    holder.getLlImageLatout().setVisibility(View.GONE);
                }
            } else if (viewHolder instanceof ViewHolderThreeImage) {
                ViewHolderThreeImage holder = (ViewHolderThreeImage) viewHolder;
                holder.getTvTitle().setText(mViewModel.getNewsTitle(i));
                holder.getTvSource().setText(mViewModel.getNewsSource(i));
                holder.getTvPubData().setText(mViewModel.getPubDate(i));
                Glide.with(holder.getIvImageLeft())
                        .load(mViewModel.getImgUrls(i, 0)).apply(mGlideOptions).into(holder.getIvImageLeft());
                Glide.with(holder.getIvImageMiddle())
                        .load(mViewModel.getImgUrls(i, 1)).apply(mGlideOptions).into(holder.getIvImageMiddle());
                Glide.with(holder.getIvImageRight())
                        .load(mViewModel.getImgUrls(i, 2)).apply(mGlideOptions).into(holder.getIvImageRight());
            } else {

            }
        }

        @Override
        public int getItemViewType(int position) {
            int itemImgCount = mViewModel.getItemImgCount(position);
            LogUtils.d(TAG, "getItemViewType: count=" + itemImgCount);
            return itemImgCount;
        }

        @Override
        public int getItemCount() {
            return mViewModel.getListCount();
        }

        @Override
        public void onClick(View v) {
            int position = mRecyclerView.getChildAdapterPosition(v);
            if (null != mItemClickListener) {
                mItemClickListener.onItemClick(v, position);
            }
        }


        @Override
        public boolean onLongClick(View v) {
            int position = mRecyclerView.getChildAdapterPosition(v);
            if (null != mItemClickListener) {
                mItemClickListener.onItemLongClick(v, position);
            }
            return true;
        }
    }
}
