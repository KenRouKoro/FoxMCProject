package com.foxapplication.mc.core.config;

import cn.korostudio.ctoml.Location;
import cn.korostudio.ctoml.OutputAnnotationData;
import com.foxapplication.embed.hutool.core.io.FileUtil;
import com.foxapplication.embed.hutool.core.io.file.FileReader;
import com.foxapplication.embed.hutool.core.io.file.FileWriter;
import com.foxapplication.embed.hutool.core.io.watch.WatchMonitor;
import com.foxapplication.embed.hutool.core.io.watch.Watcher;
import com.foxapplication.embed.hutool.core.io.watch.watchers.DelayWatcher;
import com.foxapplication.embed.hutool.core.util.CharsetUtil;
import com.foxapplication.embed.hutool.json.JSONObject;
import com.foxapplication.embed.hutool.json.JSONUtil;
import com.foxapplication.embed.hutool.setting.Setting;
import com.foxapplication.mc.core.config.interfaces.FileType;
import com.foxapplication.mc.core.config.interfaces.FileTypeInterface;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.HashMap;

/**
 * 本地持久化Fox配置类
 */
public class LocalFoxConfig {
    @Getter
    private final FoxConfig config;
    @Getter
    @Setter
    private FileType fileType;
    @Getter
    @Setter
    private String filePath ;
    private FileWriter fileWriter;
    private FileReader fileReader;
    private WatchMonitor watchMonitor = null;
    /**
     * 如果是BaseSetting就调用hutool的Setting模块
     */
    private Setting setting = null;

    /**
     * 构造函数
     * @param config Fox配置接口
     * @param fileType 文件类型
     * @param filePath 文件路径
     */
    public LocalFoxConfig(FoxConfig config,FileType fileType, String filePath){
        this.config = config;
        this.fileType = fileType;
        this.filePath = filePath;
        init();

    }

    /**
     * 构造函数
     * @param config Fox配置接口
     * @param fileType 文件类型
     */
    public LocalFoxConfig(FoxConfig config, FileType fileType) {
        this(config,fileType, new File(System.getProperty("user.dir"), "config").getPath());
    }

    /**
     * 构造函数
     * @param beanClass Bean类
     */
    public LocalFoxConfig(Class<?> beanClass) {
        this(new BeanFoxConfig(beanClass), beanClass.getAnnotation(FileTypeInterface.class) != null ? beanClass.getAnnotation(FileTypeInterface.class).type() : FileType.TOML);
    }

    /**
     * 构造函数
     * @param bean Bean对象
     */
    public LocalFoxConfig(Object bean) {
        this(new BeanFoxConfig(bean), bean.getClass().getAnnotation(FileTypeInterface.class) != null ?  bean.getClass().getAnnotation(FileTypeInterface.class).type() : FileType.TOML);
    }

    /**
     * 设置配置名称
     * @param name 配置名称
     */
    public void setName(String name){
        config.setConfigName(name);
    }

    /**
     * 设置是否自动热重载
     * @param autoLoad 是否自动热重载
     */
    public void setAutoLoad(boolean autoLoad){
        if (autoLoad&&(watchMonitor==null)){
            watchMonitor = WatchMonitor.create(filePath+"/"+config.configName()+getSuffix(fileType), WatchMonitor.ENTRY_MODIFY);
            watchMonitor.setWatcher(new DelayWatcher(new Watcher() {
                @Override
                public void onCreate(WatchEvent<?> event, Path currentPath) {

                }

                @Override
                public void onModify(WatchEvent<?> event, Path currentPath) {
                    load();
                }

                @Override
                public void onDelete(WatchEvent<?> event, Path currentPath) {

                }

                @Override
                public void onOverflow(WatchEvent<?> event, Path currentPath) {

                }
            }, 500));
            watchMonitor.start();
        }else {
            if (watchMonitor!=null){
                watchMonitor.close();
            }
        }
    }

    public BeanFoxConfig getBeanFoxConfig(){
        if (config instanceof BeanFoxConfig beanFoxConfig){
            return beanFoxConfig;
        }else {
            throw new RuntimeException("正在获取非BeanFoxConfig类");
        }
    }

    /**
     * 初始化配置
     */
    private void init(){
        boolean initFlag = !FileUtil.isFile(filePath + "/" + config.configName() + getSuffix(fileType));
        FileUtil.touch(filePath+"/"+config.configName()+getSuffix(fileType));
        fileReader = new FileReader(filePath+"/"+config.configName()+getSuffix(fileType));
        fileWriter = new FileWriter(filePath+"/"+config.configName()+getSuffix(fileType));
        if (initFlag){
            initFile();
        }
        load();

        if (config instanceof BeanFoxConfig beanFoxConfig){
            beanFoxConfig.addFieldChangedListener(event -> {
                save();
            });
        }
    }

    /**
     * 保存配置
     */
    public void save(){
        switch (fileType){
            case JSON:
                saveJSONFile();
                break;
            case TOML:
                saveTomlFile();
                break;
            case BaseSetting:
            default:
                saveBaseSettingFile();
        }
    }

    /**
     * 加载配置
     */
    public void load(){
        switch (fileType){
            case JSON:
                loadJSONFile();
                break;
            case TOML:
                loadTomlFile();
                break;
            case BaseSetting:
            default:
                loadBaseSettingFile();
        }
    }

    /**
     * 初始化文件
     */
    private void initFile(){
        switch (fileType){
            case JSON:
                initJSONFile();
                break;
            case TOML:
                initTomlFile();
                break;
            case BaseSetting:
            default:
                initBaseSettingFile();
        }
    }

    /**
     * 初始化JSON文件
     */
    private void initJSONFile(){
        HashMap<String, Object> map = new HashMap<>();
        config.getList().forEach(key -> map.put(key, config.getValue(key)));
        fileWriter.write(new JSONObject(map,false).toStringPretty());
    }

    /**
     * 加载JSON文件
     */
    private void loadJSONFile(){
        String jsonStr = fileReader.readString();
        JSONUtil.parseObj(jsonStr).forEach(config::setValue);
    }

    /**
     * 保存JSON文件
     */
    private void saveJSONFile(){
        HashMap<String, Object> map = new HashMap<>();
        config.getList().forEach(key -> map.put(key, config.getValue(key)));
        fileWriter.write(new JSONObject(map,false).toStringPretty());
    }

    /**
     * 初始化BaseSetting文件
     */
    private void initBaseSettingFile(){
        StringBuilder sb = new StringBuilder();
        config.getList().forEach(key -> {
            if (config.getAnnotation(key)!=null) sb.append("#").append(config.getAnnotation(key)).append('\n');
            sb.append(key).append(" = ").append(config.getValue(key)).append('\n');
        });
        fileWriter.write(sb.toString());
        setting = new Setting(filePath+"/"+config.configName()+getSuffix(fileType),CharsetUtil.CHARSET_UTF_8,false);
    }

    /**
     * 加载BaseSetting文件
     */
    private void loadBaseSettingFile(){
        setting.load();
        setting.forEach(config::setValue);
    }

    /**
     * 保存BaseSetting文件
     */
    private void saveBaseSettingFile(){
        StringBuilder sb = new StringBuilder();
        config.getList().forEach(key -> {
            if (config.getAnnotation(key)!=null) sb.append("#").append(config.getAnnotation(key)).append('\n');
            sb.append(key).append(" = ").append(config.getValue(key)).append('\n');
        });
        fileWriter.write(sb.toString());
    }

    /**
     * 初始化Toml文件
     */
    private void initTomlFile(){
        HashMap<String,Object> map = new HashMap<>();
        config.getList().forEach(key -> map.put(key, new OutputAnnotationData(config.getAnnotation(key).getAnnotation(), Location.Top,config.getValue(key))));
        TomlWriter writer = new TomlWriter();
        fileWriter.write(writer.write(map));
    }

    /**
     * 加载Toml文件
     */
    private void loadTomlFile(){
        String tomlStr = fileReader.readString();
        Toml toml = new Toml();
        toml.read(tomlStr);
        toml.toMap().forEach(config::setValue);

    }

    /**
     * 保存Toml文件
     */
    private void saveTomlFile(){
        HashMap<String,Object> map = new HashMap<>();
        config.getList().forEach(key -> map.put(key, new OutputAnnotationData(config.getAnnotation(key).getAnnotation(), Location.Top,config.getValue(key))));
        TomlWriter writer = new TomlWriter();
        fileWriter.write(writer.write(map));
    }

    /**
     * 根据文件类型返回后缀名
     * @param type 文件类型
     * @return 文件后缀名
     */
    public static String getSuffix(FileType type){
        return switch (type) {
            case JSON -> ".json";
            case TOML -> ".toml";
            case YAML -> ".yaml";
            case BaseSetting -> ".setting";
        };
    }
}
