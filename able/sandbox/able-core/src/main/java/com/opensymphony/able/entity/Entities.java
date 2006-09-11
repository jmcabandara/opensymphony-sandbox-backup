/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.able.entity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A collection of {@link EntityInfo} instances which can be auto-discovered by
 * finding the file <b>/META-INF/services/able/entities.properties</b> on the
 * classpath. This file is automatically generated by the able-apt module if you
 * build with it on the classpath.
 * 
 * 
 * @version $Revision: 1.1 $
 */
public class Entities {
    private static final Log log = LogFactory.getLog(Entities.class);
    private static final Entities instance = new Entities();

    private Map<String, EntityInfo> entityMap;

    public static Entities getInstance() {
        return instance;
    }

    public synchronized Map<String, EntityInfo> getEntityMap() {
        if (entityMap == null) {
            entityMap = new ConcurrentHashMap<String, EntityInfo>();
            populateEntityMap(entityMap);
        }
        return Collections.unmodifiableMap(entityMap);
    }

    public EntityInfo getEntity(String name) {
        return getEntityMap().get(name);
    }

    public void addEntity(String shortName, String className) {
        Class type = loadClass(className);
        entityMap.put(shortName, new EntityInfo(type));
    }

    protected void populateEntityMap(Map<String, EntityInfo> map) {
        String file = "META-INF/services/able/entities.properties";
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
        if (in == null) {
            in = getClass().getClassLoader().getResourceAsStream(file);
            if (in == null) {
                log.warn("No file: " + file + " found on the classpath so cannot discover entities");
                return;
            }
        }
        Properties properties = new Properties();
        try {
            properties.load(in);
            
            System.out.println("Loaded: " + properties);
            
            Set<Entry<Object, Object>> entrySet = properties.entrySet();
            for (Entry<Object, Object> entry : entrySet) {
                String className = (String) entry.getKey();
                String alias = (String) entry.getValue();
                addEntity(alias, className);
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to load properties file: " + file + ". Reason: " + e, e);
        }
    }

    protected Class loadClass(String className) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException e) {
            try {
                return getClass().getClassLoader().loadClass(className);
            }
            catch (ClassNotFoundException e1) {
                throw new RuntimeException("Failed to load type: " + className + ". Reason: " + e, e);
            }
        }
    }

}
