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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jm.news.R;
import com.jm.news.customview.MFragmentBase;
import com.jm.news.define.BaseViewClickListener;
import com.jm.news.define.DataDef;
import com.jm.news.entry.MClassicsHeader;
import com.jm.news.entry.ViewHolderOneImage;
import com.jm.news.entry.ViewHolderThreeImage;
import com.jm.news.util.CommonUtils;
import com.jm.news.viewmodel.FragmentNewsItemViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

public class FragmentNewsItem extends MFragmentBase {

    private static final String TAG = "FragmentNewsItem";
    private int fragmentID;
    private SmartRefreshLayout mSmartRefreshLayout;
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter myViewAdapter;
    private FragmentNewsItemViewModel mViewModel;
    private RequestOptions options = new RequestOptions()
//            .skipMemoryCache(true)  // 设备配置较低的可以关闭内存缓存
            .placeholder(R.mipmap.loading_static)//图片加载出来前，显示的图片
            .fallback(R.mipmap.load_error) //url为空的时候,显示的图片
            .error(R.mipmap.load_error);//图片加载失败后，显示的图片

    public FragmentNewsItem() {

    }

    @SuppressLint("ValidFragment")
    public FragmentNewsItem(int fragmentID) {
        this.fragmentID = fragmentID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = getInflaterView(R.layout.layout_fragment_news_item, inflater, container, savedInstanceState);
        mSmartRefreshLayout = view.findViewById(R.id.viewblock_smartrefresh_layout);
        mRecyclerView = view.findViewById(R.id.viewblock_recyclerView);
        if (!isRecreate()) {
            initData();
            initView();
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initData() {
        mViewModel = ViewModelProviders.of(this).get(FragmentNewsItemViewModel.class);
        mViewModel.updatetChannelID(fragmentID);

    }

    private void initView() {
//        mSmartRefreshLayout.setRefreshHeader(new BezierRadarHeader(getContext()).setEnableHorizontalDrag(true));
        mSmartRefreshLayout.setRefreshHeader(new MClassicsHeader(getContext()));
        mSmartRefreshLayout.setRefreshFooter(new BallPulseFooter(getContext()).setSpinnerStyle(SpinnerStyle.FixedBehind));
        mSmartRefreshLayout.setOnRefreshListener(new MyRefreshListener());
        mSmartRefreshLayout.setOnLoadMoreListener(new MyLoadMoreListener());

        myViewAdapter = new MyRecyclerViewAdapter();
        myViewAdapter.setmOnItemClickListener(new MyItemClickListener());

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(myViewAdapter);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        NewsDataStatusObserver newsDataStatusObserver = new NewsDataStatusObserver();
        mViewModel.getNewsDataStatus().observe(this, newsDataStatusObserver);
        NewsDataCountStatusObserver newsDataCountStatusObserver = new NewsDataCountStatusObserver();
        mViewModel.getNewsDataCountStatus().observe(this, newsDataCountStatusObserver);

        mSmartRefreshLayout.autoRefresh();
    }

    /*************************************** inner class *******************************************/
    private class NewsDataStatusObserver implements Observer<Integer> {
        @Override
        public void onChanged(@Nullable Integer integer) {
            Log.d(TAG, "onChanged: getNewsDataStatus status = " + integer);
            if (integer == DataDef.RequestStatusType.DATA_STATUS_REQUEST_FAILED) {
                mSmartRefreshLayout.finishRefresh();
                mSmartRefreshLayout.finishLoadMore();
                CommonUtils.getInstance().showToastView(R.string.app_data_request_failed);
            } else if (integer == DataDef.RequestStatusType.DATA_STATUS_NO_MOREDATA) {
                mSmartRefreshLayout.finishLoadMore();
                CommonUtils.getInstance().showToastView(R.string.app_data_request_no_more);
            }
        }
    }

    private class NewsDataCountStatusObserver implements Observer<Boolean> {
        @Override
        public void onChanged(@Nullable Boolean b) {
            if (b.booleanValue()) {
                myViewAdapter.notifyDataSetChanged();
                mSmartRefreshLayout.finishRefresh();
            } else {
                myViewAdapter.notifyDataSetChanged();
                mSmartRefreshLayout.finishLoadMore();
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
            Log.d(TAG, "onCreateViewHolder: i=" + i);
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
                holder.getmTitle().setText(mViewModel.getNewsTitle(i));
                holder.getmSource().setText(mViewModel.getNewsSource(i));
                holder.getmPubData().setText(mViewModel.getPubDate(i));
                if (mViewModel.getItemImgCount(i) > 0) {
                    holder.getmImageLatout().setVisibility(View.VISIBLE);
                    Glide.with(holder.getmImage())
                            .load(mViewModel.getImgUrls(i, 0)).apply(options).into(holder.getmImage());
                } else {
                    holder.getmImageLatout().setVisibility(View.GONE);
                }
            } else if (viewHolder instanceof ViewHolderThreeImage) {
                ViewHolderThreeImage holder = (ViewHolderThreeImage) viewHolder;
                holder.getmTitle().setText(mViewModel.getNewsTitle(i));
                holder.getmSource().setText(mViewModel.getNewsSource(i));
                holder.getmPubData().setText(mViewModel.getPubDate(i));
                Glide.with(holder.getmImageLeft())
                        .load(mViewModel.getImgUrls(i, 0)).apply(options).into(holder.getmImageLeft());
                Glide.with(holder.getmImageMiddle())
                        .load(mViewModel.getImgUrls(i, 1)).apply(options).into(holder.getmImageMiddle());
                Glide.with(holder.getmImageRight())
                        .load(mViewModel.getImgUrls(i, 2)).apply(options).into(holder.getmImageRight());
            } else {

            }
        }

        @Override
        public int getItemViewType(int position) {
            int itemImgCount = mViewModel.getItemImgCount(position);
            Log.d(TAG, "getItemViewType: count=" + itemImgCount);
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

    /*************************************** listener function *******************************************/
    private class MyItemClickListener extends BaseViewClickListener {
        @Override
        public void onItemClick(View view, int position) {
            Log.d(TAG, "onItemClick: position=" + position);
            String newsLink = mViewModel.getNewsLink(position);
            if (!TextUtils.isEmpty(newsLink)) {
                CommonUtils.getInstance().jumpWebView(getActivity(), newsLink, true);
            }
        }

        @Override
        public void onItemLongClick(View view, final int position) {
            PopupMenu popupMenu = CommonUtils.getInstance().showNewsItemPopupMenu(getActivity(), view);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_news_item_open_detail:
                            String newsLink = mViewModel.getNewsLink(position);
                            if (!TextUtils.isEmpty(newsLink)) {
                                CommonUtils.getInstance().jumpWebView(getActivity(), newsLink, true);
                            }
                            break;
                        case R.id.menu_news_item_open_other:
                            String jumpURl = mViewModel.getNewsLink(position);
                            if (!TextUtils.isEmpty(jumpURl)) {
                                CommonUtils.getInstance().jumpOtherApp(getActivity(), jumpURl);
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
            Log.d(TAG, "onRefresh: ");
            mViewModel.requestRefreshData();
        }
    }

    private class MyLoadMoreListener implements OnLoadMoreListener {
        @Override
        public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            Log.d(TAG, "onLoadMore: ");
            mViewModel.requestLoadMoreData();
        }
    }
}
