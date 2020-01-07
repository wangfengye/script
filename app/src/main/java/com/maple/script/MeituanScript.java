package com.maple.script;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.yhao.floatwindow.Screen;

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
 * Created by maple on 2019/12/2 11:30
 */
public class MeituanScript {
    private static final String TAG = MeituanScript.class.getSimpleName();
    private Callback callback;
    private Handler handler = new Handler(Looper.getMainLooper());

    public void loadMeituanAddrs(final Callback<List<MeituanAddress>> callback) {
        this.callback = callback;
        new Thread(new Runnable() {
            @Override
            public void run() {
                //前置:进入地址列表
                ShellUtils.execCommand(" uiautomator dump /sdcard/ui.xml", true);
                List<int[]> points = getPoints();
                final ArrayList<MeituanAddress> data = new ArrayList<MeituanAddress>();
                for (int[] p : points) {
                    ShellUtils.execCommand("input tap " + p[0] + " " + p[1], true);
                    ShellUtils.execCommand(" uiautomator dump /sdcard/ui.xml", true);
                    MeituanAddress ad = parseMeituan();
                    Log.i(TAG, "loadMeituanAddrs: " + ad.toString());
                    data.add(ad);
                    ShellUtils.execCommand("input tap 100 180", true);//回列表
                    ShellUtils.execCommand("input tap 940 1360", true);//确认返回
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinished(data);
                    }
                });
            }
        }).start();
    }

    private List<int[]> getPoints() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = null;
        try {
            sp = spf.newSAXParser();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        Listhandler myhandler = new Listhandler();
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

    private MeituanAddress parseMeituan() {
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

    private static class Myhandler extends DefaultHandler {
        private MeituanAddress address;
        private static HashMap<String, Field> map = new HashMap<>();

        static {
            Field[] fields = MeituanAddress.class.getDeclaredFields();
            for (Field field : fields) {
                App app = field.getAnnotation(App.class);
                if (app == null) continue;
                map.put(app.value(), field);
            }
        }

        public Myhandler() {
            address = new MeituanAddress();
        }

        public MeituanAddress getRes() {
            return address;
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
                    f.set(address, attributes.getValue("text"));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            super.startElement(uri, localName, qName, attributes);
        }
    }

    private static class Listhandler extends DefaultHandler {
        private List<int[]> points;

        public Listhandler() {
            points = new ArrayList<>();
        }

        public List<int[]> getRes() {
            return points;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            String clazz = attributes.getValue("class");
            if ("android.widget.ImageView".equals(clazz)) {
                String bounds = attributes.getValue("bounds");
                bounds = bounds.replace("][", ",");
                bounds = bounds.replace("]", "");
                bounds = bounds.replace("[", "");
                String[] ints = bounds.split(",");
                int x = (Integer.parseInt(ints[0]) + Integer.parseInt(ints[2])) / 2;
                int y = (Integer.parseInt(ints[1]) + Integer.parseInt(ints[3])) / 2;
                if (x > 500) {
                    points.add(new int[]{x, y});
                }

            }

            super.startElement(uri, localName, qName, attributes);
        }
    }
}
