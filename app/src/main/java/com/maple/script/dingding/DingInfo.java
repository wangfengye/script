package com.maple.script.dingding;

import com.maple.script.App;

/**
 * Created by maple on 2020/1/7 14:02
 */
public class DingInfo {
    @App("com.alibaba.android.rimet:id/profile_tv_org")
    public String company;
    @App("com.alibaba.android.rimet:id/user_header_full_name")
    public String name;
    @App("com.alibaba.android.rimet:id/user_mobile_info_content_tv")
    public String phone;

    @Override
    public String toString() {
        return "DingInfo{" +
                "company='" + company + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
