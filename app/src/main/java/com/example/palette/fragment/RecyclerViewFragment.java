package com.example.palette.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palette.R;
import com.example.palette.adapter.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewFragment extends Fragment {
    private RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview,container,false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(getData());
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private static final int LOAD_MORE = 3;
            private boolean hasLoadMore;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(newState==RecyclerView.SCROLL_STATE_DRAGGING){
                    hasLoadMore = false;
                }
                if(newState !=RecyclerView.SCROLL_STATE_DRAGGING && !hasLoadMore){
                    int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                    int offset = recyclerView.getAdapter().getItemCount() - lastVisibleItemPosition;
                    if(offset<=LOAD_MORE){
                        hasLoadMore = true;
                        recyclerAdapter.data.addAll(getData());
                        recyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        return view;
    }

    private List<String> getData(){
        List<String> data = new ArrayList<>();
        for (int i=0; i < 10; i++) {
            data.add("ChildView item"+i);
        }
        return data;
    }

}
