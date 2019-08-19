/*
 * FileName：PropertiesUtil.java 
 * <p>
 * Copyright (c) 2017-2020, <a href="http://www.webcsn.com">hermit (794890569@qq.com)</a>.
 * <p>
 * Licensed under the GNU General Public License, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/gpl-3.0.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.wxmp.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;

import java.util.MissingResourceException;

/**
 * hermit
 */
@Slf4j
public class PropertiesUtil {
    private static final byte[] KEY = {9, -1, 0, 5, 39, 8, 6, 19};

    /**
     * 配置文件
     */
    private static Configuration config;

    static {
        try {
            config = new PropertiesConfiguration("property/smartwx.properties" );
        } catch (ConfigurationException e) {
            log.error("获取配置文件失败，", e);
        }
    }

    /**
     * Get a value based on key , if key does not exist , null is returned
     *
     * @param key
     * @return
     */
    public static String getString(String key) {
        try {
            return config.getString(key);
        } catch (MissingResourceException e) {
            return null;
        }
    }

    /**
     * 根据key获取值
     *
     * @param key
     * @return
     */
    public static int getInt(String key) {
        return config.getInt(key);
    }

    /**
     * 根据key获取值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getInt(String key, int defaultValue) {
        String value = config.getString(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    /**
     * 根据key获取值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = config.getString(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return new Boolean(value);
    }

    public static void main(String[] args) {
        String encrypt = SecurityUtil.encryptDes("buzhidao", KEY);
        System.out.println(encrypt);
        System.out.println(SecurityUtil.decryptDes(encrypt, KEY));
    }
}
