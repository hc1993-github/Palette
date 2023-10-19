package com.example.palette.adapter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DiffCallBack extends DiffUtil.Callback {
    private List<String> mOldDatas;
    private List<String> mNewDatas;

    public DiffCallBack(List<String> mOldDatas, List<String> mNewDatas) {
        this.mOldDatas = mOldDatas;
        this.mNewDatas = mNewDatas;
    }

    @Override
    public int getOldListSize() {
        return mOldDatas !=null?mOldDatas.size() :0;
    }

    @Override
    public int getNewListSize() {
        return mNewDatas !=null?mNewDatas.size() :0;
    }

    /**
     * 判断是否是相同的Item
     * 通常判断唯一属性
     * @param oldItemPosition
     * @param newItemPosition
     * @return
     */
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        //演示
        return mOldDatas.get(oldItemPosition).equals(mNewDatas.get(newItemPosition));
    }

    /**
     * 仅在areItemsTheSame返回true时调用
     * 判断是否是相同的数据
     * 通常判断其他属性
     * @param oldItemPosition
     * @param newItemPosition
     * @return
     */
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        //演示
        return mOldDatas.get(oldItemPosition).equals(mNewDatas.get(newItemPosition));
    }

    /**
     * 高级用法
     * 仅在areItemsTheSame返回true,且areContentsTheSame返回false时调用
     * 返回Item发生改变的部分
     * @param oldItemPosition
     * @param newItemPosition
     * @return
     */
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        //演示
        return mNewDatas.get(newItemPosition);
    }

//    private void onBindViewHolder(RecyclerView.ViewHolder viewHolder,int position,List<Object> payloads){
//        if(payloads.isEmpty()){
//            onBindViewHolder(viewHolder,position);
//        }else {
//            Object o = payloads.get(0);
//
//        }
//    }

    //    private void test(List<String> mOldDatas, List<String> mNewDatas){
        //建议子线程执行配合Handler使用
//        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallBack(mOldDatas, mNewDatas), true);
//        RecyclerView.Adapter adapter = null;
//        diffResult.dispatchUpdatesTo(adapter);
//        mOldDatas = mNewDatas;
//        adapter.setData(mOldDatas);
//    }

}
