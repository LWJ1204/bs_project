package com.example.finalapp.Callback;


import com.example.finalapp.enity.MsgInfo;

import java.util.List;

public interface IMsgFragmentCallback {
    List<MsgInfo> getMsgFromActivity();
    void updataReadFlag(MsgInfo data);
}
