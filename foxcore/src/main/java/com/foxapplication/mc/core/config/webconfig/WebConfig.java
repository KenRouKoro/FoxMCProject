package com.foxapplication.mc.core.config.webconfig;

import cn.korostudio.ctoml.OutputAnnotation;
import com.foxapplication.embed.hutool.core.bean.BeanUtil;
import com.foxapplication.embed.hutool.core.io.FileUtil;
import com.foxapplication.embed.hutool.core.io.file.FileReader;
import com.foxapplication.embed.hutool.core.io.file.FileWriter;
import com.foxapplication.embed.hutool.core.io.resource.ClassPathResource;
import com.foxapplication.embed.hutool.core.lang.Console;
import com.foxapplication.embed.hutool.core.map.SafeConcurrentHashMap;
import com.foxapplication.embed.hutool.core.thread.ThreadUtil;
import com.foxapplication.embed.hutool.core.util.CharsetUtil;
import com.foxapplication.embed.hutool.core.util.StrUtil;
import com.foxapplication.embed.hutool.core.util.TypeUtil;
import com.foxapplication.embed.hutool.http.ContentType;
import com.foxapplication.embed.hutool.http.HttpUtil;
import com.foxapplication.embed.hutool.http.server.SimpleServer;
import com.foxapplication.embed.hutool.json.JSONArray;
import com.foxapplication.embed.hutool.json.JSONObject;
import com.foxapplication.embed.hutool.json.JSONUtil;
import com.foxapplication.embed.hutool.log.Log;
import com.foxapplication.embed.hutool.log.LogFactory;
import com.foxapplication.mc.core.FoxCore;
import com.foxapplication.mc.core.config.FoxConfig;
import com.foxapplication.mc.core.config.LocalFoxConfig;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WebConfig {
    @Getter
    private static SimpleServer server;
    @Getter
    @Setter
    private static String filePath = "";
    private static final Log log = LogFactory.get();
    private static final Map<String, FoxConfig> configMap = new SafeConcurrentHashMap<>();
    private static final ClassPathResource resource = new ClassPathResource("filelist.json",WebConfig.class);
    private static final JSONArray files = JSONUtil.parseArray(resource.readUtf8Str());
    public static FoxConfig removeConfig(String name){
        return configMap.remove(name);
    }
    public static FoxConfig addConfig(FoxConfig config){
        configMap.put(config.configName(),config);
        return config;
    }


    public static void init() {
        server = HttpUtil.createServer(FoxCore.getConfig().getWebPort());
        filePath = new File(System.getProperty("user.dir"), "config/foxcorewebconfig").getPath();

        if (!FileUtil.isFile(filePath+"/index.html")){
            log.info("正在释放前端文件......");
            copyFile();
            log.info("完成。");
        }

        createFileLink();
        createAPI();

        ThreadUtil.execAsync(()->{
            final InetSocketAddress address = server.getAddress();
            log.info("FoxCoreWebConfig服务正在【{}:{}】上监听", address.getHostName(), address.getPort());
            server.start();
        });
    }





    private static void createFileLink() {
        files.forEach((file) -> {
            server.addAction(file.toString(), (request, response) ->{
                String fileType = "";
                String fileName = file.toString(); // 文件名
                if (fileName.endsWith(".js")) {
                    fileType = "application/javascript";
                } else if (fileName.endsWith(".json")) {
                    fileType = "application/json";
                } else if (fileName.endsWith(".html")) {
                    fileType = "text/html";
                } else if (fileName.endsWith(".svg")) {
                    fileType = "image/svg+xml";
                } else if (fileName.endsWith(".ttf")) {
                    fileType = "application/x-font-ttf";
                } else if (fileName.endsWith(".css")) {
                    fileType = "text/css";
                } else if (fileName.endsWith(".md")) {
                    fileType = "text/markdown";
                }
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.write(new FileReader(filePath+"/"+file.toString()).readBytes(),fileType);
            });
        });

    }

    private static void createAPI() {
        server.addAction("/api/index.md", (request, response) -> {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.write(new FileReader(filePath + "/index.md", CharsetUtil.CHARSET_UTF_8).readString(), ContentType.TEXT_PLAIN.toString());
        });

        server.addAction("/api/list", (request, response) -> {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.write(getList(), ContentType.JSON.toString());
        });

        server.addAction("/api/metadata", (request, response) -> {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.write(getConfig(request.getParam("id")), ContentType.JSON.toString());
        });

        server.addAction("/api/save", (request, response) -> {
            try {
                response.setHeader("Access-Control-Allow-Origin", "*");

                String data = request.getBody(CharsetUtil.CHARSET_UTF_8);
                JSONObject jsonObject = JSONUtil.parseObj(data);

                String id = jsonObject.getStr("configID");
                String fieldID = jsonObject.getStr("fieldID");

                save(id,fieldID,jsonObject);


            }catch (Exception e){
                log.error(e);
            }finally {
                response.write("{\"status\":\"ok\"}",ContentType.JSON.toString());
            }

        });

        server.addAction("/", (request, response) -> {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.write(new FileReader(filePath + "/index.html", CharsetUtil.CHARSET_UTF_8).readString(), ContentType.TEXT_HTML.toString());
        });
    }


    private static String getList(){
        HashMap<String, List<Map<String, String>>> reMap =  new HashMap<>();
        ArrayList<Map<String, String>> list = new ArrayList<>();
        reMap.put("configs",list);
        configMap.forEach((k,v)->{
            HashMap<String,String> map = new HashMap<>();
            map.put("name", v.configName());
            map.put("id", v.configName());
            list.add(map);
        });
        return JSONUtil.toJsonStr(reMap);
    }

    private static String getConfig(String id){
        return parseConfig(configMap.get(id));
    }

    private static String parseConfig(FoxConfig config){
        if (config == null){
            return "{}";
        }
        JSONObject object = new JSONObject();
        List<String> idList = config.getList();

        object.putOnce("id",config.configName());
        object.putOnce("name",config.configName());


        idList.forEach((id) -> {
            JSONObject object1 = new JSONObject();
            Object value = config.getValue(id);
            String annotation = config.getAnnotation(id).getAnnotation();
            String name = config.getAnnotation(id).getName();
            String type;
            if (StrUtil.isBlank(name)){
                name = id;
            }
            object1.putOnce("name",name);
            object1.putOnce("id",id);
            object1.putOnce("annotation",annotation);
            if (value instanceof String){
                object1.putOnce("data",value);
                type = "string";

            }else if (value instanceof List<?>){
                object1.putOnce("data",value);
                Type objType = value.getClass().getGenericSuperclass();
                Type typeArgument = TypeUtil.getTypeArgument(objType);
                type = switch (typeArgument.getTypeName()) {
                    case "java.lang.String" -> "stringarray";
                    case "java.lang.Integer", "java.lang.Long" -> "numberarray";
                    case "java.lang.Float", "java.lang.Double" -> "decimalsarray";
                    default -> throw new RuntimeException("错误的类型： "+typeArgument.getTypeName());
                };
            } else if(value instanceof Integer || value instanceof Long){
                object1.putOnce("data",value);
                type = "number";
            } else if(value instanceof Float || value instanceof Double){
                object1.putOnce("data",value);
                type = "decimals";
            } else if(value instanceof Boolean){
                object1.putOnce("data",value);
                type = "bool";
            } else {
                type = "object";
                object1.putOnce("data",value);
                Object obj = value;
                Set<Field> fields = getFields(obj.getClass());
                Map<String, Object> to = new LinkedHashMap<>();

                for (Field field : fields) {
                    OutputAnnotation ann = field.getAnnotation(OutputAnnotation.class);
                    field.setAccessible(true);
                    if(isValidAnnotation(ann)){
                        handleAnnotation(obj, to, field, ann);
                    }else {
                        handleNonAnnotatedField(obj, to, field);
                    }
                }
                object1.putOnce("object_annotation",to);
            }
            object1.putOnce("type",type);

            object.append("config",object1);
        });


        return object.toString();
    }


    private static void save(String id,String fieldID,JSONObject data){
        Object object = configMap.get(id).getValue(fieldID);
        FoxConfig config = configMap.get(id);
        if (object instanceof String){
            config.setValue(fieldID,data.get("data"));
        }else if (object instanceof List<?>){
            config.setValue(fieldID,data.getJSONArray("data").toList(TypeUtil.getTypeArgument(object.getClass().getGenericSuperclass()).getClass()));
        } else if(object instanceof Integer || object instanceof Long){
            config.setValue(fieldID,data.get("data"));
        } else if(object instanceof Float || object instanceof Double){
            config.setValue(fieldID,data.get("data"));
        } else if(object instanceof Boolean){
            config.setValue(fieldID,data.get("data"));
        }else{
            config.setValue(fieldID,data.getJSONObject("data").toBean(object.getClass()));
        }
    }
    private static void copyFile(){
        files.forEach((file) -> {
            ClassPathResource cpyResource =  new ClassPathResource(file.toString(),WebConfig.class);
            FileWriter fileWriter = FileWriter.create(new File(filePath + "/" + file.toString()));
            FileUtil.touch(filePath + "/" + file.toString());
            fileWriter.writeFromStream(cpyResource.getStream(),true);
            log.info("正在复制："+filePath + "/" + file.toString());
        });
    }

    private static Set<Field> getFields(Class<?> cls) {
        Set<Field> fields = new LinkedHashSet<Field>(Arrays.asList(cls.getDeclaredFields()));
        while (cls != Object.class) {
            fields.addAll(Arrays.asList(cls.getDeclaredFields()));
            cls = cls.getSuperclass();
        }
        removeConstantsAndSyntheticFields(fields);

        return fields;
    }

    private static void removeConstantsAndSyntheticFields(Set<Field> fields) {
        fields.removeIf(field -> (Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) || field.isSynthetic() || Modifier.isTransient(field.getModifiers()));
    }

    private static void handleAnnotation(Object obj, Map<String, Object> to, Field field, OutputAnnotation annotation){
        if (BeanUtil.isBean(field.getType())){
            to.put(field.getName(),getBeanAnn(field.getType()));
        }else if(Map.class.isAssignableFrom(field.getType())){
            handleMapField(obj, to, field);
        }
        else {
            to.put(field.getName(), annotation.value());
        }
    }

    private static void handleNonAnnotatedField(Object obj, Map<String, Object> to, Field field){
        if(Map.class.isAssignableFrom(field.getType())){
            handleMapField(obj, to, field);
        }else {
            to.put(field.getName(), null);
        }
    }
    private static void handleMapField(Object obj, Map<String, Object> to, Field field){
        try {
            Map<String,Object> mapObj = (Map<String, Object>) field.get(obj);
            Map<String, Object> to1 = new LinkedHashMap<>();
            for(String key: mapObj.keySet()){
                Object value = mapObj.get(key);
                if (BeanUtil.isBean(value.getClass())){
                    to1.put(key,getBeanAnn(value.getClass()));
                }
            }
            to.put(field.getName(),to1);
        } catch (Exception e) {
            log.error("转换Map对象失败",e);
        }
    }

    public static Map<String,Object> getBeanAnn(Class<?> target){
        return getBeanAnn(target, new HashSet<>());
    }
    private static Map<String,Object> getBeanAnn(Class<?> target, Set<Class<?>> visited){
        if (visited.contains(target)) {
            return Collections.emptyMap();
        }

        visited.add(target);

        Set<Field> fields = getFields(target);
        Map<String, Object> to = new LinkedHashMap<>();
        OutputAnnotation classAnn = target.getAnnotation(OutputAnnotation.class);
        if (isValidAnnotation(classAnn)){
            to.put("CLASS_ANN",classAnn.value());
        }
        for (Field field : fields) {
            OutputAnnotation annotation = field.getAnnotation(OutputAnnotation.class);
            field.setAccessible(true);
            if(isValidAnnotation(annotation)){
                if (BeanUtil.isBean(field.getType())){
                    to.put(field.getName(),getBeanAnn(field.getType(), visited));
                }else if(Map.class.isAssignableFrom(field.getType())){
                    // Handle Map type field, you need to implement this method
                    handleMapFieldInBeanAnn(field, to, visited);
                }
                else {
                    to.put(field.getName(), annotation.value());
                }
            }else {
                to.put(field.getName(), null);
            }
        }
        return to;
    }

    private static void handleMapFieldInBeanAnn(Field field, Map<String, Object> to, Set<Class<?>> visited) {
        try {
            Map<String, Object> mapValue = (Map<String, Object>) field.get(null);
            Map<String, Object> annotationMap = new LinkedHashMap<>();
            for(Map.Entry<String, Object> entry : mapValue.entrySet()) {
                if (BeanUtil.isBean(entry.getValue().getClass())) {
                    annotationMap.put(entry.getKey(), getBeanAnn(entry.getValue().getClass(), visited));
                }
            }
            to.put(field.getName(), annotationMap);
        } catch (IllegalAccessException e) {
            // Log the exception here
            log.error("Accessing field failed", e);
        }
    }
    private static boolean isValidAnnotation(OutputAnnotation annotation){
        return annotation != null && annotation.at() != null && annotation.value() != null;
    }



}
