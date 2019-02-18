package com.jm.news.view;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.PopupMenu;

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
import com.jm.news.viewmodel.FragmentNewsMainViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

public class FragmentNewsMain extends MFragmentBase {

    private static final String TAG = "FragmentNewsMain";
    private Banner mBanner;
    private SmartRefreshLayout mSmartRefreshLayout;
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter mViewAdapter;
    private int mFragmentID;
    private FragmentNewsMainViewModel mViewModel = null;
    public RequestOptions mGlideOptions = new RequestOptions()
//            .skipMemoryCache(true)  // 设备配置较低的可以关闭内存缓存
            .placeholder(R.mipmap.loading_static)//图片加载出来前，显示的图片 Pictures displayed before they are loaded
            .fallback(R.mipmap.load_error) //url为空的时候,显示的图片 Pictures displayed when URL is empty
            .error(R.mipmap.load_error);//图片加载失败后，显示的图片 Pictures displayed after failed loading

    public FragmentNewsMain() {

    }

    @SuppressLint("ValidFragment")
    public FragmentNewsMain(int fragmentID) {
        this.mFragmentID = fragmentID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreateView: ");
        // view recycler
        View view = getInflaterView(R.layout.layout_fragment_news_main, inflater, container, savedInstanceState);
        mBanner = view.findViewById(R.id.banner_main_news);
        mSmartRefreshLayout = view.findViewById(R.id.sfl_viewblock);
        mRecyclerView = view.findViewById(R.id.rv_viewblock);
        if (!isRecreate()) {
            initData();
            initView();
        }

        return view;
    }

    @Override
    public void onStart() {
        LogUtils.d(TAG, "onStart: ");
        super.onStart();
        setBannerAutoPaly(true);
    }

    @Override
    public void onStop() {
        LogUtils.d(TAG, "onStop: ");
        setBannerAutoPaly(false);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        LogUtils.d(TAG, "onDestroy: ");
        if (null != mViewAdapter) {
            mViewAdapter.removeListener();
        }
        mViewAdapter = null;
        mViewModel = null;
        mBanner = null;
        mSmartRefreshLayout = null;
        mRecyclerView = null;
        mGlideOptions = null;
        super.onDestroy();
    }


    private void initData() {
        LogUtils.d(TAG, "initData: ");
        mViewModel = ViewModelProviders.of(this).get(FragmentNewsMainViewModel.class);
        mViewModel.updatetChannelID(mFragmentID);
    }

    private void initView() {
        mSmartRefreshLayout.setRefreshHeader(new MClassicsHeaderView(getContext()));
        mSmartRefreshLayout.setRefreshFooter(new BallPulseFooter(getContext()).setSpinnerStyle(SpinnerStyle.FixedBehind));
        mSmartRefreshLayout.setOnRefreshListener(new MyRefreshListener());
        mSmartRefreshLayout.setOnLoadMoreListener(new MyLoadMoreListener());

        mViewAdapter = new MyRecyclerViewAdapter();
        mViewAdapter.setmOnItemClickListener(new MyItemClickListener());

        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mViewAdapter);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        // bind observer
        NewsDataStatusObserver newsDataStatusObserver = new NewsDataStatusObserver();
        mViewModel.getNewsDataStatus().observe(this, newsDataStatusObserver);
        NewsDataCountStatusObserver newsDataCountStatusObserver = new NewsDataCountStatusObserver();
        mViewModel.getNewsDataCountStatus().observe(this, newsDataCountStatusObserver);
        NewsBannerDataStatusObserver newsBannerDataStatusObserver = new NewsBannerDataStatusObserver();
        mViewModel.getNewsBannerDataStatus().observe(this, newsBannerDataStatusObserver);

        mSmartRefreshLayout.autoRefresh();
    }

    /*************************************** observer function *******************************************/
    private class NewsDataStatusObserver implements Observer<Integer> {
        @Override
        public void onChanged(@Nullable Integer integer) {
            LogUtils.d(TAG, "onChanged: getNewsDataStatus status = " + integer);
            if (null != integer && null != mSmartRefreshLayout) {
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
    }

    private class NewsDataCountStatusObserver implements Observer<Boolean> {
        @Override
        public void onChanged(@Nullable Boolean b) {
            LogUtils.d(TAG, "onChanged: b = " + b);
            if (null != b && null != mViewAdapter && null != mSmartRefreshLayout) {
                if (b.booleanValue()) {
                    mViewAdapter.notifyDataSetChanged();
                    mSmartRefreshLayout.finishRefresh();
                } else {
                    mViewAdapter.notifyDataSetChanged();
                    mSmartRefreshLayout.finishLoadMore();
                }
            }
        }
    }

    private class NewsBannerDataStatusObserver implements Observer<Integer> {
        @Override
        public void onChanged(@Nullable Integer integer) {
            LogUtils.d(TAG, "onChanged: integer = " + integer);
            if (null != integer && null != mBanner && null != mViewModel) {
                if (integer == DataDef.RequestStatusType.DATA_STATUS_REQUEST_OK) {
                    // screen orientation optimization
//                    int orientation = getResources().getConfiguration().orientation;
//                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    mBanner.setVisibility(View.VISIBLE);
                    mBanner.setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE);
                    mBanner.setImageLoader(new MyBannerImageLoader());
                    mBanner.setBannerAnimation(Transformer.BackgroundToForeground);
                    mBanner.setBannerTitles(mViewModel.getBannerTitles());
                    mBanner.setDelayTime(5000);
                    mBanner.isAutoPlay(true);
                    mBanner.setImages(mViewModel.getBannerImgUrls());
                    mBanner.setOnBannerListener(new MyBannerListener());
                    mBanner.start();
//                    } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                        Toast.makeText(getContext(), "已针对屏幕状态优化显示", Toast.LENGTH_SHORT).show();
//                        mBanner.setVisibility(View.GONE);
//                    }
                } else {
                    mBanner.setVisibility(View.GONE);
                    CommonUtils.getInstance().showToastView(R.string.app_data_request_failed);
                }
            }
        }
    }

    /*************************************** public function *******************************************/
    public void setBannerAutoPaly(boolean isAutoPlay) {
        LogUtils.d(TAG, "setBannerAutoPaly: isAutoPlay = " + isAutoPlay);
        if (null != mBanner) {
            if (isAutoPlay) {
                mBanner.startAutoPlay();
            } else {
                mBanner.stopAutoPlay();
            }
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
            PopupMenu popupMenu = CommonUtils.getInstance().showNewsItemPopupMenu(getActivity(), view);
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
            mViewModel.requestRefreshData();
        }
    }

    private class MyLoadMoreListener implements OnLoadMoreListener {
        @Override
        public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            LogUtils.d(TAG, "onLoadMore: ");
            mViewModel.requestLoadMoreData();
        }
    }

    private class MyBannerListener implements OnBannerListener {
        @Override
        public void OnBannerClick(int position) {
            LogUtils.d(TAG, "OnBannerClick: position = " + position);
            String newsLink = mViewModel.getBannerLink(position);
            if (!TextUtils.isEmpty(newsLink)) {
                JumpUtils.jumpWebView(getActivity(), newsLink, true);
            }
        }
    }


    /*************************************** inner class *******************************************/
    private class MyBannerImageLoader extends com.youth.banner.loader.ImageLoader {

        @Override
        public void displayImage(final Context context, Object path, final ImageView imageView) {
//            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            LogUtils.d(TAG, "displayImage: path" + (String) path);
            Glide.with(imageView).load((String) path).apply(mGlideOptions).into(imageView);
        }
    }

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
