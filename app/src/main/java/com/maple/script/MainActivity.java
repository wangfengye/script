package com.maple.script;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String sourceFileName = "/hzasd.xls";
    public static final String outFileName = "/hzasd_search_result.xls";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_search_wechat)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 微信搜索
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i(TAG, "run: start");
                                File f = new File(Environment.getExternalStorageDirectory() + sourceFileName);
                                ArrayList<Person> people = (ArrayList<Person>) ExcelUtils.readExcel(f);
                                for (int i = 31; i < 38; i++) {
                                    seacrchOnWechat(people.get(i));
                                    try {
                                        long sleepTime = 1000 +(long) (Math.random()*1000*60);
                                        Thread.sleep(sleepTime);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                ExcelUtils.initExcel(Environment.getExternalStorageDirectory() +outFileName, "search_result", new String[]{"name", "phoneNumber", "mac", "nickName", "sex", "area", "sign"});
                                ExcelUtils.writeObjListToExcel(people, Environment.getExternalStorageDirectory() +outFileName, MainActivity.this);
                                Log.i(TAG, "run: finished");
                            }
                        }).start();

                    }
                });
        String[] reqs = new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(reqs, 0);
        }
    }

    private Person seacrchOnWechat(Person p) {

        ShellUtils.execCommand("am start -n com.tencent.mm/.ui.LauncherUI", true);
        ShellUtils.execCommand("input tap 1100 210", true);
        ShellUtils.execCommand("input tap 1100 210", true);
        ShellUtils.execCommand("input text " + p.getPhoneNumber(), true);
        ShellUtils.execCommand("input tap 500 400 ", true);
        // 获取当前界面信息.
        ShellUtils.execCommand(" uiautomator dump /sdcard/ui.xml", true);
        xmlFac(p);
        ShellUtils.execCommand("input tap 200 750 ", true);
        ShellUtils.execCommand(" uiautomator dump /sdcard/ui.xml", true);
        xmlFac(p);
        ShellUtils.execCommand("input tap 100 200", true);
        ShellUtils.execCommand("input tap 100 200", true);
        ShellUtils.execCommand("input tap 100 200", true);
        Log.i(TAG, "seacrchOnWechat: " + p.toString());
        return p;
    }

    private void xmlFac(Person p) {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = null;
        try {
            sp = spf.newSAXParser();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        Myhandler myhandler = new Myhandler(p);
        File file = new File(Environment.getExternalStorageDirectory() + "/ui.xml");
        try {
            sp.parse(file, myhandler);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    private static class Myhandler extends DefaultHandler {
        private Person p;

        public Myhandler(Person p) {
            this.p = p;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

            String id = attributes.getValue("resource-id");
            if (id == null) {
                super.startElement(uri, localName, qName, attributes);
                return;
            }
            if (id.equals("com.tencent.mm:id/b7o")) {//地区
                p.setArea(attributes.getValue("text"));
            } else if (id.equals("android:id/summary")) {//个性签名,存在重复id节点,取第一个
                if (p.getSign() == null || p.getSign().length() <= 0) {
                    p.setSign(attributes.getValue("text"));
                }

            } else if (id.equals("com.tencent.mm:id/b7e")) {
                p.setSex(attributes.getValue("content-desc"));
            } else if (id.equals("com.tencent.mm:id/b8b")) {
                p.setNickName(attributes.getValue("text"));
            }
            super.startElement(uri, localName, qName, attributes);
        }
    }


}
