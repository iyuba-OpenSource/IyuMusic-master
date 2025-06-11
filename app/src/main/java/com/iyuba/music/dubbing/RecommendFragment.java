package com.iyuba.music.dubbing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.music.R;
import com.iyuba.music.entity.MusicVoa;

import java.util.ArrayList;
import java.util.List;


/**
 * 推荐 fragment
 */
public class RecommendFragment extends Fragment {

    private static final String VOA_LIST = "voa_list";
    private static final String VOA_ID = "voa_id";
    private static final int SPAN_COUNT = 2;

    public static RecommendFragment newInstance(ArrayList<MusicVoa> list, int voaId) {
        RecommendFragment fragment = new RecommendFragment();
        Bundle args = new Bundle();
        args.putSerializable(VOA_LIST, list);
        args.putInt(VOA_ID, voaId);
        fragment.setArguments(args);
        return fragment;
    }

    RecyclerView mRecyclerView;

    private ArrayList<MusicVoa> mList;
    private int mVoaId;
    private RecommendAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mList = (ArrayList<MusicVoa>) getArguments().getSerializable(VOA_LIST);
            mVoaId = getArguments().getInt(VOA_ID);
        }
        mAdapter = new RecommendAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), SPAN_COUNT);
        NormalGridItemDivider divider = new NormalGridItemDivider(getContext());
        divider.setDivider(getResources().getDrawable(R.drawable.voa_activity_divider));
        mRecyclerView.addItemDecoration(divider);
        mRecyclerView.setLayoutManager(layoutManager);
//        mRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。

        //mAdapter.setmVoaCallback(callback);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mList != null) {
            mAdapter.setVoaList(mList, mVoaId);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void upData(ArrayList<MusicVoa> list, int voaId) {
        mAdapter.setVoaList(list, voaId);
        mAdapter.notifyDataSetChanged();
    }
}
