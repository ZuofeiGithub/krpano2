package com.weui.krpano.utils;


import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

/**
 * @content:
 * @auther: 左飞
 * @date: 2019/9/23 13:45
 */
public class KrpanoUtil {
    private static KrpanoUtil instance;
    private static DocumentBuilderFactory factory;
    private static DocumentBuilder documentBuilder;
    private static Document document;
    Element krpano = null;
    String imageName;
    private KrpanoUtil() {
    }

    public static KrpanoUtil getInstance(){
        factory = DocumentBuilderFactory.newInstance();
        try {
            documentBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        document = documentBuilder.newDocument();
        document.setXmlStandalone(true);
        if(ObjectUtils.isEmpty(instance)){
           instance = new KrpanoUtil();
        }
        return instance;
    }


    /**
     * 创建根布局
     * @param version
     * @return
     */
    public KrpanoUtil createKrapano(String version){
        if(!ObjectUtils.isEmpty(document)) {
             krpano = document.createElement("krpano");
            if (!ObjectUtils.isEmpty(version)) {
                krpano.setAttribute("version", version);
            }
        }
        return instance;
    }

    /**
     *
     * @param type 全景图的类型(SPHERE,CYLINDER,CUBESTRIP,grid(type,xsteps,ysteps,res,linecol,bgcol,pntcol))
     * @param imageUrl 全景图路径
     * @param striporder 定义图像顺序
     * @param details //图像细节质量调整，越大越清晰
     * @return
     */
    public KrpanoUtil createPreView(String type,String imageUrl,String striporder,Integer details){
        Element preview = null;
        if(!ObjectUtils.isEmpty(document)) {
            preview = document.createElement("preview");
            if (!ObjectUtils.isEmpty(type)) {
                preview.setAttribute("type", type);
            }
            if (!ObjectUtils.isEmpty(imageUrl)) {
                imageName = imageUrl;
                preview.setAttribute("url", "/images/" + imageUrl);
            }
            if (!ObjectUtils.isEmpty(striporder)) {
                preview.setAttribute("striporder", striporder);
            }
            if (!ObjectUtils.isEmpty(details)) {
                preview.setAttribute("details", String.valueOf(details));
            }
        }
        krpano.appendChild(preview);
        return instance;
    }


    /**
     * 创建文本框  * <layer name="hotspot_pos_info" url="viewer/plugins/textfield.swf" visible="false" html="" css="font-family:Courier;" padding="8"  align="lefttop" x="10" y="10" width="200"/>
     * @param name 唯一表示名
     * @param visible 是否可见
     * @param text 文本内容
     * @param css 样式
     * @param padding 内边距
     * @param align 对齐方式
     * @param x x坐标
     * @param y y坐标
     * @param width 宽度
     * @return
     */
    public KrpanoUtil createTextView(String name,Boolean visible,String text,String css,Integer padding,String align,Integer x,Integer y,Integer width)
    {
        Element textView = document.createElement("layer");
        if(!ObjectUtils.isEmpty(name))
        textView.setAttribute("name",name);
        textView.setAttribute("url","viewer/plugins/textfield.swf");
        if(!ObjectUtils.isEmpty(visible))
        textView.setAttribute("visible",String.valueOf(visible));
        if(!ObjectUtils.isEmpty(text))
        textView.setAttribute("html",text);
        if(!ObjectUtils.isEmpty(css))
        textView.setAttribute("css",css);
        if(!ObjectUtils.isEmpty(padding))
        textView.setAttribute("padding",String.valueOf(padding));
        if(!ObjectUtils.isEmpty(align))
        textView.setAttribute("align",align);
        if(!ObjectUtils.isEmpty(x))
        textView.setAttribute("x",String.valueOf(x));
        if(!ObjectUtils.isEmpty(y))
        textView.setAttribute("y",String.valueOf(y));
        if(!ObjectUtils.isEmpty(width))
        textView.setAttribute("width",String.valueOf(width));
        krpano.appendChild(textView);
        return instance;
    }

    /**
     * 最后会根据图片名生成xml
     */
    public  KrpanoUtil generateXml(){
        document.appendChild(krpano);
        try {
            // 创建TransformerFactory对象
            TransformerFactory tff = TransformerFactory.newInstance();
            // 创建 Transformer对象
            Transformer tf = tff.newTransformer();
            // 输出内容是否使用换行
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            String path = ClassUtils.getDefaultClassLoader().getResource("static/xml/").getPath();
            String xmlName = imageName.substring(0,imageName.lastIndexOf("."));
            // 创建xml文件并写入内容
            if(!ObjectUtils.isEmpty(document)) {
                tf.transform(new DOMSource(document), new StreamResult(new File(path + xmlName + ".xml")));
            }
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return instance;
    }

    /**
     * Document转字符串
     * @return
     */
    public  String docToString() {
        // XML转字符串
        String xmlStr = "";
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty("encoding", "UTF-8");// 解决中文问题，试过用GBK不行
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            t.transform(new DOMSource(document), new StreamResult(bos));
            xmlStr = bos.toString();
        } catch (TransformerConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(xmlStr);
        return xmlStr;
    }

    /**
     * 字符串转document
     * @param xmlStr
     * @return
     */
    public  Document stringToDoc(String xmlStr) {
        //字符串转XML
        Document doc = null;
        try {
            xmlStr = new String(xmlStr.getBytes(),"UTF-8");
            StringReader sr = new StringReader(xmlStr);
            InputSource is = new InputSource(sr);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            doc = builder.parse(is);

        } catch (ParserConfigurationException e) {
            System.err.println(xmlStr);
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            System.err.println(xmlStr);
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println(xmlStr);
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return doc;
    }


}
