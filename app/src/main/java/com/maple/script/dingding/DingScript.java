package com.maple.script.dingding;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.maple.script.App;
import com.maple.script.Callback;
import com.maple.script.MeituanAddress;
import com.maple.script.MeituanScript;
import com.maple.script.ShellUtils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by maple on 2020/1/7 14:04
 */
public class DingScript {
    public static final String TAG = "DingScript";
    private Callback callback;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int hasTitle(){
        ShellUtils.execCommand(" uiautomator dump /sdcard/ui.xml", true);
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = null;
        try {
            sp = spf.newSAXParser();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        TitHandler myhandler = new TitHandler();
        File file = new File(Environment.getExternalStorageDirectory() + "/ui.xml");
        try {
            sp.parse(file, myhandler);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return myhandler.hasTitle?myhandler.posY:250;
    }

    public void load(final Callback<List<DingInfo>> callback) {
        this.callback = callback;
        new Thread(new Runnable() {
            @Override
            public void run() {

                for(int i=0;i<4;i++)ShellUtils.execCommand(" input swipe 500 1800 500 400", true);
                int y=2010;
                int min=250;
                for(;;) {
                    min=hasTitle();
                    Log.i(TAG, "run: "+min);
                    y=2010;
                    while (true) {
                        if (y < min) {
                            break;
                        }
                        ShellUtils.execCommand(" input tap 500 " + y, true);
                        //显示号码
                        ShellUtils.execCommand(" input tap 565 2000", true);
                        ShellUtils.execCommand(" input tap 600 2250", true);
                        ShellUtils.execCommand(" uiautomator dump /sdcard/ui.xml", true);
                        DingInfo ad = parseDingInfo();
                        Log.i(TAG, ad.toString());
                        ShellUtils.execCommand("input tap 100 180", true);//回列表
                        y -= 224;
                    }
                    if(min>250)break;
                    ShellUtils.execCommand(" input swipe 500 320 500 2400", true);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinished(null);
                    }
                });
            }
        }).start();
    }

    private DingInfo parseDingInfo() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = null;
        try {
            sp = spf.newSAXParser();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        Myhandler myhandler = new Myhandler();
        File file = new File(Environment.getExternalStorageDirectory() + "/ui.xml");
        try {
            sp.parse(file, myhandler);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return myhandler.getRes();
    }
    private static class TitHandler extends DefaultHandler{
        boolean hasTitle;//存在常用联系人文本
        int posY;
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if(hasTitle)return;
            String id = attributes.getValue("resource-id");
            if(id!=null&&id.equals("com.alibaba.android.rimet:id/section_title")){
                hasTitle=true;
                String bounds = attributes.getValue("bounds");
                bounds = bounds.replace("][", ",");
                bounds = bounds.replace("]", "");
                bounds = bounds.replace("[", "");
                String[] ints = bounds.split(",");
                posY= Integer.parseInt(ints[3]);
                return;
            }
            super.startElement(uri, localName, qName, attributes);
        }
    }
    private static class Myhandler extends DefaultHandler {
        private DingInfo info;
        private static HashMap<String, Field> map = new HashMap<>();

        static {
            Field[] fields = DingInfo.class.getDeclaredFields();
            for (Field field : fields) {
                App app = field.getAnnotation(App.class);
                if (app == null) continue;
                map.put(app.value(), field);
            }
        }

        public Myhandler() {
            info = new DingInfo();
        }

        public DingInfo getRes() {
            return info;
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
            Field f;
            if ((f = map.get(id)) != null) {
                try {
                    f.setAccessible(true);
                    f.set(info, attributes.getValue("text"));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            super.startElement(uri, localName, qName, attributes);
        }
    }
}
