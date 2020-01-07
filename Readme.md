# Shell脚本(需ROOT权限)
> 本质上android可以直接执行shell,但用app的身份登上去,大部分shell操作涉及需要各种权限,尤其是一些权限只
给系统app开放,所以建议root后,在root账号下运行

## 当前功能
* sdk 下复制一份有手机号的xls文件,打开apk,开始自动搜索,手机号,将信息保存到另一份xls文件中
* 限制:搜索过多号码后,会被微信限制使用该功能.

```
# 微信搜索脚本

1. 查看当前activity `dumpsys activity top | grep ACTIVITY`
2. 打开搜索页 `am start -n com.tencent.mm/.plugin.fts.ui.FTSMainUI`
	* 需要intent传入参数. 1100 210
3. 输入内容 `input text '号码'`
4. 点击号码搜索 `intput tap 500 360`
5. 进入页面 获取
	* 性别 com.tencent.mm:id/b7s content-desc
	* 地区 com.tencent.mm:id/b82
	* 个性签名 android:id/summary
6. `input tap 500 400`
	* 昵称 com.tencent.mm:id/b8p

```

# 美团收货地址脚本
1.收货地址

| resource-id | text |
| :--: | :--: |
| com.sankuai.meituan.takeoutnew:id/waimai_addrsdk_address_name| 地址名称|
| com.sankuai.meituan.takeoutnew:id/waimai_addrsdk_address_detail | 详细地址 |
| com.sankuai.meituan.takeoutnew:id/waimai_addrsdk_address_house_number | 门牌号 |
| com.sankuai.meituan.takeoutnew:id/waimai_addrsdk_uname | 姓名 |
| com.sankuai.meituan.takeoutnew:id/waimai_addrsdk_phonenumber | 手机号 |
2. TODO: 无法判断滚动到底,只能实现半自动.

# 钉钉
1. 通信录(常用联系人)

| resource-id | text |
| :--: | :--: | 
| com.alibaba.android.rimet:id/profile_tv_org | 企业|
| com.alibaba.android.rimet:id/user_header_full_name | 姓名|
|com.alibaba.android.rimet:id/user_mobile_info_content_tv|手机号|
