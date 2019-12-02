package com.maple.script;

/**
 * Created by maple on 2019/12/2 11:26
 * 美团地址信息
 */
public class MeituanAddress {
    @App("com.sankuai.meituan.takeoutnew:id/waimai_addrsdk_address_name")
    private String poi;
    @App("com.sankuai.meituan.takeoutnew:id/waimai_addrsdk_address_detail")
    private String address;
    @App("com.sankuai.meituan.takeoutnew:id/waimai_addrsdk_address_house_number")
    private String homeCount;
    @App("com.sankuai.meituan.takeoutnew:id/waimai_addrsdk_uname")
    private String name;
    @App("com.sankuai.meituan.takeoutnew:id/waimai_addrsdk_phonenumber")
    private String phoneNumber;

    public String getPoi() {
        return poi;
    }

    public void setPoi(String poi) {
        this.poi = poi;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHomeCount() {
        return homeCount;
    }

    public void setHomeCount(String homeCount) {
        this.homeCount = homeCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "MeituanAddress{" +
                "poi='" + poi + '\'' +
                ", address='" + address + '\'' +
                ", homeCount='" + homeCount + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
