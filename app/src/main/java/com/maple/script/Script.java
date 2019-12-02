package com.maple.script;

import android.os.Environment;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by maple on 2019/12/2 11:25
 * 脚本入口库.
 */
public class Script {
    public static List<MeituanAddress> loadMeituanAddrs(){
        ShellUtils.execCommand(" uiautomator dump /sdcard/ui.xml", true);
        return null;
    }

}
