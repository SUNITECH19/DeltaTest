package com.wackycodes.sleepapneatest.model;

public class ResultItem {


    private int length;
    private String videoName;

    public ResultItem( int sleepTimeLength, String sleepVideoName){

        length = sleepTimeLength;
        videoName = sleepVideoName;
    }



    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }
}
