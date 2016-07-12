package com.hom.junghan.demohom;

/**
 * Created by JungHan on 2016-07-03.
 */
public class ListItem {
    private String[] mData;
    final int columnCnt = 1;

    public ListItem(String[] data) {
        mData = data;
    }

    public ListItem(String id) {
        mData = new String[columnCnt];
        mData[0] = id;
    }


    public String[] getmData() {
        return mData;
    }

    public String getData(int index) {
        return mData[index];
    }

    public void setData(String[] data) {
        mData = data;
    }
}
