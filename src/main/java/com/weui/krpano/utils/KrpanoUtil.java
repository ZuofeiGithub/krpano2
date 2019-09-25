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
    Element scene = null;
    String imageName;

    private KrpanoUtil() {
    }

    /**
     * 获取实例句柄并初始化document
     * @return
     */
    public static KrpanoUtil getInstance() {
        factory = DocumentBuilderFactory.newInstance();
        try {
            documentBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        document = documentBuilder.newDocument();
        document.setXmlStandalone(true);
        if (ObjectUtils.isEmpty(instance)) {
            instance = new KrpanoUtil();
        }
        return instance;
    }


    /**
     * 创建krpano根布局
     *
     * @param version 版本
     * @param startAction 启动时的动作
     * @return
     */
    public KrpanoUtil createKrpano(String version, String startAction) {
        if (!ObjectUtils.isEmpty(document)) {
            krpano = document.createElement("krpano");
            if (!ObjectUtils.isEmpty(version)) {
                krpano.setAttribute("version", version);
            }
            if(!ObjectUtils.isEmpty(startAction)){
                krpano.setAttribute("onstart",startAction);
            }
        }
        return instance;
    }


    /**
     * 添加场景
     * @param name 场景名字
     * @param startAction 场景开始动作
     * @param autoLoad 是否自动加载场景
     * @param content 场景未能解析的元素，当作文本字符串
     * @return
     */
    public KrpanoUtil addScene(String name, String startAction, boolean autoLoad, String content){
        if(!ObjectUtils.isEmpty(document)){
            scene = document.createElement("scene");
            if(!ObjectUtils.isEmpty(name)){
                scene.setAttribute("name",name);
            }
            if(!ObjectUtils.isEmpty(startAction)){
                scene.setAttribute("onstart",startAction);
            }
            if(!ObjectUtils.isEmpty(autoLoad)){
                scene.setAttribute("autoload",String.valueOf(autoLoad));
            }
            if(!ObjectUtils.isEmpty(content)){
                scene.setAttribute("content",content);
            }
            krpano.appendChild(scene);
        }

        return instance;
    }



    public KrpanoUtil addRootPreview(String type, String imageUrl, String striporder, Integer details) {
        createPreview(type, imageUrl, striporder, details, krpano);
        return instance;
    }

    public KrpanoUtil addScenePreview(String type, String imageUrl, String striporder, Integer details) {
        createPreview(type, imageUrl, striporder, details, scene);
        if(!ObjectUtils.isEmpty(scene)) {
            krpano.appendChild(scene);
        }else{
            throw new RuntimeException("您还没有创建场景");
        }
        return instance;
    }



    private void createPreview(String type, String imageUrl, String striporder, Integer details, Element e) {
        Element preview = null;
        if (!ObjectUtils.isEmpty(document)) {
            preview = document.createElement("preview");
            if (!ObjectUtils.isEmpty(type)) {
                preview.setAttribute("type", type);
            }
            if (!ObjectUtils.isEmpty(imageUrl)) {
                imageName = imageUrl.substring(imageUrl.lastIndexOf("/images/")+7);
                preview.setAttribute("url",  imageUrl);
            }
            if (!ObjectUtils.isEmpty(striporder)) {
                preview.setAttribute("striporder", striporder);
            }
            if (!ObjectUtils.isEmpty(details)) {
                preview.setAttribute("details", String.valueOf(details));
            }
        }
        if(!ObjectUtils.isEmpty(e)) {
            e.appendChild(preview);
        }
    }

    private void addRootGridPreview(){
        addRootPreview("grid(CUBE,16,16,512,0xCCCCCC,0xFFFFFF,0x999999);",null,null,null);
    }

    private void addSceneGridPreview(){
        addScenePreview("grid(CUBE,16,16,512,0xCCCCCC,0xFFFFFF,0x999999);",null,null,null);
    }

    /**
     * 创建球体图片视图
     *
     * @param imagePath 图片路径
     * @return
     */
    public KrpanoUtil addRootSphereImage(String imagePath) {
        addSphereImage(imagePath, krpano);
        return instance;
    }

    public KrpanoUtil addSceneSphereImage(String imagePath) {
        addSphereImage(imagePath, scene);
        if(!ObjectUtils.isEmpty(scene)) {
            krpano.appendChild(scene);
        }else{
            throw new RuntimeException("您还没有创建场景");
        }
        return instance;
    }

    private void addSphereImage(String imagePath, Element e) {
        Element image = null;
        if (!ObjectUtils.isEmpty(document)) {
            image = document.createElement("image");

            Element sphere = document.createElement("sphere");
            sphere.setAttribute("url", imagePath);
            System.out.println(imagePath);
            imageName = imagePath.substring(imagePath.lastIndexOf("/images/") + 7);
            image.appendChild(sphere);
        }
        e.appendChild(image);
    }

    /**
     *
     * @param hlookat 水平视角
     * @param vlookat 垂直视角
     * @param fov     默认视角的缩放
     * @param fovmin  默认视角缩放最小值
     * @param fovmax  默认视角缩放最大值
     * @param mfovratio 最大的屏幕尺寸的宽高比例
     * @param distortion    鱼眼视角失真设置0.0至1.0之间(扭曲程度)
     * @param fisheye 鱼眼视角设置0.0至1.0之间
     * @return
     */
    public KrpanoUtil addRootView(Double hlookat, Double vlookat, Double fov, Double fovmin, Double fovmax, Double mfovratio, Double distortion, Double fisheye) {
        addView(hlookat, vlookat, fov, fovmin, fovmax, mfovratio, distortion, fisheye, krpano);
        return this;
    }

    public KrpanoUtil addSceneView(Double hlookat, Double vlookat, Double fov, Double fovmin, Double fovmax, Double mfovratio, Double distortion, Double fisheye) {
        addView(hlookat, vlookat, fov, fovmin, fovmax, mfovratio, distortion, fisheye, scene);
        if(!ObjectUtils.isEmpty(scene)) {
            krpano.appendChild(scene);
        }else{
            throw new RuntimeException("您还没有创建场景");
        }
        return instance;
    }

    private void addView(Double hlookat, Double vlookat, Double fov, Double fovmin, Double fovmax, Double mfovratio, Double distortion, Double fisheye, Element e) {
        Element view = null;
        if (!ObjectUtils.isEmpty(document)) {
            view = document.createElement("view");
            if (!ObjectUtils.isEmpty(hlookat))
                view.setAttribute("hlookat", String.valueOf(hlookat));
            if (!ObjectUtils.isEmpty(vlookat))
                view.setAttribute("vlookat", String.valueOf(vlookat));
            if (!ObjectUtils.isEmpty(fov)) {
                view.setAttribute("fov", String.valueOf(fov));
            }
            if (!ObjectUtils.isEmpty(fovmin)) {
                view.setAttribute("fovmin", String.valueOf(fovmin));
            }
            if (!ObjectUtils.isEmpty(fovmax)) {
                view.setAttribute("fovmax", String.valueOf(fovmax));
            }
            if (!ObjectUtils.isEmpty(mfovratio)) {
                view.setAttribute("mfovration", String.valueOf(mfovratio));
            }
            if (!ObjectUtils.isEmpty(distortion)) {
                view.setAttribute("distortion", String.valueOf(distortion));
            }
            if (!ObjectUtils.isEmpty(fisheye)) {
                view.setAttribute("fisheye", String.valueOf(fisheye));
            }
        }
        e.appendChild(view);
    }


    /**
     * 创建文本框  * <layer name="hotspot_pos_info" url="viewer/plugins/textfield.swf" visible="false" html="" css="font-family:Courier;" padding="8"  align="lefttop" x="10" y="10" width="200"/>
     *
     * @param name    唯一表示名
     * @param visible 是否可见
     * @param text    文本内容
     * @param css     样式
     * @param padding 内边距
     * @param align   对齐方式
     * @param x       x坐标
     * @param y       y坐标
     * @param width   宽度
     * @return
     */
    public KrpanoUtil addRootTextView(String name, Boolean visible, String text, String css, Integer padding, String align, Integer x, Integer y, Integer width) {
        addTextView(name, visible, text, css, padding, align, x, y, width, krpano);
        return instance;
    }

    public KrpanoUtil addSceneTextView(String name, Boolean visible, String text, String css, Integer padding, String align, Integer x, Integer y, Integer width) {
        addTextView(name, visible, text, css, padding, align, x, y, width, scene);
        if(!ObjectUtils.isEmpty(scene)) {
            krpano.appendChild(scene);
        }else{
            throw new RuntimeException("您还没有创建场景");
        }
        return instance;
    }

    private void addTextView(String name, Boolean visible, String text, String css, Integer padding, String align, Integer x, Integer y, Integer width, Element e) {
        Element textView = document.createElement("layer");
        if (!ObjectUtils.isEmpty(name))
            textView.setAttribute("name", name);
        textView.setAttribute("url", "viewer/plugins/textfield.swf");
        if (!ObjectUtils.isEmpty(visible))
            textView.setAttribute("visible", String.valueOf(visible));
        if (!ObjectUtils.isEmpty(text))
            textView.setAttribute("html", text);
        if (!ObjectUtils.isEmpty(css))
            textView.setAttribute("css", css);
        if (!ObjectUtils.isEmpty(padding))
            textView.setAttribute("padding", String.valueOf(padding));
        if (!ObjectUtils.isEmpty(align))
            textView.setAttribute("align", align);
        if (!ObjectUtils.isEmpty(x))
            textView.setAttribute("x", String.valueOf(x));
        if (!ObjectUtils.isEmpty(y))
            textView.setAttribute("y", String.valueOf(y));
        if (!ObjectUtils.isEmpty(width))
            textView.setAttribute("width", String.valueOf(width));
        e.appendChild(textView);
    }


    /**
     *
     * @param name 热点名称
     * @param type 热点类型 image,text(textfield)
     * @param url 热点图像路径
     * @param alturl html5状态下显示
     * @param width 热点宽度
     * @param height 热点高度
     * @param scale 热点缩放
     * @param alpha 透明度(0.0-1.0)
     * @param keep 是否在下一个场景跳转后保持显示
     * @param renderer 渲染方式 webgl css3d
     * @param devices 支持设备类型
     * @param visible 是否可见
     * @param enabled 设置热点是否接受鼠标事件
     * @param handcursor 设置是否鼠标移动到上面显示小手
     * @param zoom 是否场景缩放时，热点跟随缩放
     * @param clickAction 热点被点击时执行的动作
     * @return
     */
    public KrpanoUtil addRootHotpot(String name, String type, String url, String alturl, Integer width, Integer height, Double scale, Double alpha, Boolean keep, String renderer, String devices, boolean visible, boolean enabled, boolean handcursor, boolean zoom, String clickAction){
        addHotpot(name, type, url, alturl, width, height, scale, alpha, keep, renderer, devices, visible, enabled, handcursor, zoom, clickAction, krpano);
        return instance;
    }

    public KrpanoUtil addSceneHotpot(String name, String type, String url, String alturl, Integer width, Integer height, Double scale, Double alpha, Boolean keep, String renderer, String devices, boolean visible, boolean enabled, boolean handcursor, boolean zoom, String clickAction){
        addHotpot(name, type, url, alturl, width, height, scale, alpha, keep, renderer, devices, visible, enabled, handcursor, zoom, clickAction, scene);
        if(!ObjectUtils.isEmpty(scene)) {
            krpano.appendChild(scene);
        }else{
            throw new RuntimeException("您还没有创建场景");
        }
        return instance;
    }

    private void addHotpot(String name, String type, String url, String alturl, Integer width, Integer height, Double scale, Double alpha, Boolean keep, String renderer, String devices, boolean visible, boolean enabled, boolean handcursor, boolean zoom, String clickAction, Element e) {
        Element hotspot = document.createElement("hotspot");
        if (!ObjectUtils.isEmpty(name)) {
            hotspot.setAttribute("name", name);
        }
        if (!ObjectUtils.isEmpty(type)) {
            hotspot.setAttribute("url", url);
        }
        if (!ObjectUtils.isEmpty(alturl)) {
            hotspot.setAttribute("alturl", alturl);
        }
        if (!ObjectUtils.isEmpty(width)) {
            hotspot.setAttribute("width", String.valueOf(width));
        }
        if (!ObjectUtils.isEmpty(height)) {
            hotspot.setAttribute("height", String.valueOf(height));
        }
        if (!ObjectUtils.isEmpty(scale)) {
            hotspot.setAttribute("scale", String.valueOf(scale));
        }
        if (!ObjectUtils.isEmpty(alpha)) {
            hotspot.setAttribute("alpha", String.valueOf(alpha));
        }
        if (!ObjectUtils.isEmpty(keep)) {
            hotspot.setAttribute("keep", alturl);
        }
        if (!ObjectUtils.isEmpty(renderer)) {
            hotspot.setAttribute("renderer", renderer);
        }
        if (!ObjectUtils.isEmpty(devices)) {
            hotspot.setAttribute("devices", devices);
        }
        if (!ObjectUtils.isEmpty(visible)) {
            hotspot.setAttribute("visible", String.valueOf(visible));
        }
        if (!ObjectUtils.isEmpty(enabled)) {
            hotspot.setAttribute("enabled", String.valueOf(enabled));
        }
        if (!ObjectUtils.isEmpty(handcursor)) {
            hotspot.setAttribute("handcursor", String.valueOf(handcursor));
        }
        if (!ObjectUtils.isEmpty(zoom)) {
            hotspot.setAttribute("zoom", String.valueOf(zoom));
        }
        if (!ObjectUtils.isEmpty(clickAction)) {
            hotspot.setAttribute("onclick", clickAction);
        }
        e.appendChild(hotspot);
    }

    /**
     * 创建动作
     * @param name 动作的名字 调用动作时调用方式 name(arg1,arg2,arg3,...)
     * @param autoRun 是否自动运行
     * @param scope 作用域 global,local,parent
     * @param secure 为true时行动将由Javascript调用
     * @param protect 为true时变量将无法访问
     * @param type 是否支持Javascript接口
     * @param args 参数
     * @param code action代码或javascript代码
     * @return
     */
    public KrpanoUtil addRootAction(String name, boolean autoRun, String scope, boolean secure, boolean protect, String type, String args, String code){
        addAction(name, autoRun, scope, secure, protect, type, args, code, krpano);
        return instance;
    }

    public KrpanoUtil addSceneAction(String name, boolean autoRun, String scope, boolean secure, boolean protect, String type, String args, String code){
        addAction(name, autoRun, scope, secure, protect, type, args, code, scene);
        if(!ObjectUtils.isEmpty(scene)) {
            krpano.appendChild(scene);
        }else{
            throw new RuntimeException("您还没有创建场景");
        }
        return instance;
    }

    private void addAction(String name, boolean autoRun, String scope, boolean secure, boolean protect, String type, String args, String code, Element e) {
        Element action = document.createElement("action");
        if (!ObjectUtils.isEmpty(name)) {
            action.setAttribute("name", name);
        }
        if (!ObjectUtils.isEmpty(autoRun)) {
            action.setAttribute("autorun", String.valueOf(autoRun));
        }
        if (!ObjectUtils.isEmpty(scope)) {
            action.setAttribute("scope", scope);
        }
        if (!ObjectUtils.isEmpty(secure)) {
            action.setAttribute("secure", String.valueOf(secure));
        }
        if (!ObjectUtils.isEmpty(protect)) {
            action.setAttribute("protect", String.valueOf(protect));
        }
        if (!ObjectUtils.isEmpty(type)) {
            if (type.equals("Javascript")) {
                action.setTextContent("<![CDATA[" + code + "]]>");
            } else {
                action.setTextContent(code);
            }
        }
        if (!ObjectUtils.isEmpty(args)) {
            action.setAttribute("args", args);
        }
        e.appendChild(action);
    }


    public KrpanoUtil build(){
        document.appendChild(krpano);
        return instance;
    }
    /**
     * 最后会根据图片名生成xml
     */
    public KrpanoUtil generateXml() {
        try {
            // 创建TransformerFactory对象
            TransformerFactory tff = TransformerFactory.newInstance();
            // 创建 Transformer对象
            Transformer tf = tff.newTransformer();
            // 输出内容是否使用换行
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            String path = ClassUtils.getDefaultClassLoader().getResource("static/xml/").getPath();
            String xmlName = imageName.substring(0, imageName.lastIndexOf("."));
            // 创建xml文件并写入内容
            if (!ObjectUtils.isEmpty(document)) {
                System.out.println(xmlName);
                tf.transform(new DOMSource(document), new StreamResult(new File(path + xmlName + ".xml")));
            }
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return instance;
    }

    /**
     * Document转字符串
     *
     * @return
     */
    public String docToString() {
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
     *
     * @param xmlStr
     * @return
     */
    public Document stringToDoc(String xmlStr) {
        //字符串转XML
        Document doc = null;
        try {
            xmlStr = new String(xmlStr.getBytes(), "UTF-8");
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
