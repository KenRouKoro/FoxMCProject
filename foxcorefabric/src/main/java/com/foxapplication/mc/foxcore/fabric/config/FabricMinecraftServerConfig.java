package com.foxapplication.mc.foxcore.fabric.config;

import com.foxapplication.embed.hutool.core.io.resource.ClassPathResource;
import com.foxapplication.embed.hutool.core.text.UnicodeUtil;
import com.foxapplication.embed.hutool.json.JSONObject;
import com.foxapplication.embed.hutool.log.Log;
import com.foxapplication.embed.hutool.log.LogFactory;
import com.foxapplication.embed.hutool.setting.dialect.Props;
import com.foxapplication.mc.core.config.FoxConfig;
import com.foxapplication.mc.core.config.interfaces.FieldAnnotationData;
import com.foxapplication.mc.foxcore.fabric.FoxCoreFabricServer;
import com.google.common.base.MoreObjects;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.level.GameType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * 用于修改MinecraftServer的配置文件
 */
public class FabricMinecraftServerConfig implements FoxConfig {
    private final Props props;
    private final Path filePath;
    private static JSONObject lang = null;
    private static Log log ;


    public FabricMinecraftServerConfig(Path filePath) {
        this.filePath = filePath;
        props = new Props(filePath.toFile());
        props.autoLoad(true);
        log = LogFactory.get();

        if (lang==null){
            ClassPathResource resource = new ClassPathResource("assets/foxcore/minecraft_properties_lang.json");
            lang = new JSONObject(resource.readUtf8Str());
        }
    }

    @Override
    public List<String> getList() {
        Set<Object> keys = props.keySet();
        return keys.stream()
                .map(Object::toString)
                .toList();
    }

    @Override
    public Object getValue(String s) {
        // 获取原始值
        Object value = props.get(s);

        if (value instanceof String stringValue) {
            if ("true".equalsIgnoreCase(stringValue) || "false".equalsIgnoreCase(stringValue)) {
                // 字符串符合布尔值，则转换为布尔型并返回
                return Boolean.parseBoolean(stringValue);
            } else if ("max-tick-time".equalsIgnoreCase(stringValue)) {
                // 特殊字符串max-tick-time，转换为long型
                try {
                    // 尝试将字符串转换为Long类型
                    return Long.parseLong(stringValue);
                } catch (NumberFormatException e) {
                    // 如果转换失败，说明字符串不是一个有效的long，返回原始字符串
                    return value;
                }
            } else {
                try {
                    // 尝试将字符串转换为int类型
                    return Integer.parseInt(stringValue);
                } catch (NumberFormatException e) {
                    // 如果转换失败，说明字符串不是一个有效的int，返回原始字符串
                    return value;
                }
            }
        }

        return value;
    }


    @Override
    public void setValue(String s, Object o) {
        String valueAsString = o instanceof String ? (String) o : String.valueOf(o);
        valueAsString = UnicodeUtil.toUnicode(valueAsString,true);
        props.put(s, valueAsString);
        DedicatedServer server = FoxCoreFabricServer.getServer();
        setServerConfig(server,s,o);
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.ISO_8859_1, new OpenOption[0]);){
            props.store(writer, "Minecraft server properties");
        } catch (IOException iOException) {
            log.error("Failed to store properties to file: {}", filePath);
        }

    }

    @Override
    public FieldAnnotationData getAnnotation(String s) {
        return new FieldAnnotationData(lang.getStr(s,s),s);
    }

    @Override
    public String configName() {
        return "MinecraftServer";
    }

    @Override
    public void setConfigName(String s) {

    }

    private void setServerConfig(DedicatedServer server,String s, Object o){
        switch (s){
            case "pvp":
                server.setPvpAllowed((Boolean) o);
                break;
            case "allow-flight":
                server.setFlightAllowed((Boolean) o);
                break;
            case "motd":
                server.setMotd(UnicodeUtil.toString((String) o));
                break;
            case "player-idle-timeout":
                server.setPlayerIdleTimeout((Integer) o);
                break;
            case "enforce-whitelist":
                server.setEnforceWhitelist((Boolean) o);
                break;
            case "gamemode":
                server.getWorldData().setGameType(get(dispatchNumberOrString(GameType::byId, GameType::byName),o.toString()));
                break;
        }
    }
    protected GameType get(Function<String, Object> function, String row) {
        return (GameType) MoreObjects.firstNonNull(row != null ? function.apply(row) : null, GameType.SURVIVAL);
    }
    protected static <V> Function<String, V> dispatchNumberOrString(IntFunction<V> intFunction, Function<String, V> function) {
        return string -> {
            try {
                return intFunction.apply(Integer.parseInt(string));
            } catch (NumberFormatException numberFormatException) {
                return function.apply(string);
            }
        };
    }
}
