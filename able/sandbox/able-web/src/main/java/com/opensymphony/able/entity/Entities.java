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
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A collection of {@link EntityInfo} instances which can be auto-discovered by
 * finding the file <b>/META-INF/services/able/entities.properties</b> on the
 * classpath. This file is automatically generated by the able-apt module if you
 * build with it on the classpath.
 *
 * @version $Revision: 1.1 $
 */
public class Entities {
    private static final Log log = LogFactory.getLog(Entities.class);
    private static final Entities instance = new Entities();

    private SortedMap<String, EntityInfo> entityMap;
    private SortedMap<String, EntityInfo> persistentTypes = new TreeMap<String, EntityInfo>();

    public static Entities getInstance() {
        return instance;
    }

    public static EntityInfo info(Object value) {
        Class type = Object.class;
        if (value != null) {
            type = value.getClass();
        }
        return getInstance().getEntityByClass(type.getName());
    }

    public synchronized Map<String, EntityInfo> getEntityMap() {
        if (entityMap == null) {
            entityMap = new TreeMap<String, EntityInfo>();
            populateEntityMap(entityMap);
        }
        return Collections.unmodifiableMap(entityMap);
    }

    public EntityInfo getEntity(String name) {
        EntityInfo answer = getEntityByName(name);
        if (answer == null) {
            return getEntityByClass(name);
        }
        return answer;
    }

    public EntityInfo getEntityByClass(Class type) {
        Collection<EntityInfo> entities = getEntities();
        for (EntityInfo info : entities) {
            if (info.getEntityClass().equals(type)) {
                return info;
            }
        }

        EntityInfo entityInfo = new EntityInfo(type);
        addEntity(entityInfo);
        return entityInfo;
    }

    public EntityInfo getEntityByClass(String name) {
        Collection<EntityInfo> entities = getEntities();
        for (EntityInfo info : entities) {
            if (info.getEntityClass().getName().equals(name)) {
                return info;
            }
        }
        // lets try load the entity dynamically
        return addEntity(name);
    }

    public EntityInfo getEntityByName(String name) {
        return getEntityMap().get(name);
    }

    public Collection<EntityInfo> getEntities() {
        // force lazy load
        getEntityMap();
        return persistentTypes.values();
    }

    public EntityInfo addEntity(String className) {
        Class type = loadClass(className);
        EntityInfo entityInfo = new EntityInfo(type);
        addEntity(entityInfo);
        return entityInfo;
    }

    public EntityInfo addEntity(String shortName, String className) {
        Class type = loadClass(className);
        EntityInfo entityInfo = new EntityInfo(type);
        entityMap.put(shortName, entityInfo);
        if (isPersistentEntity(entityInfo)) {
            persistentTypes.put(shortName, entityInfo);
        }
        return entityInfo;
    }

    protected void addEntity(EntityInfo entityInfo) {
        entityMap.put(entityInfo.getEntityName(), entityInfo);
        if (isPersistentEntity(entityInfo)) {
            persistentTypes.put(entityInfo.getEntityName(), entityInfo);
        }
    }

    protected boolean isPersistentEntity(EntityInfo entityInfo) {
        Class type = entityInfo.getEntityClass();
        if (type.isPrimitive() || type.getPackage().getName().startsWith("java.lang") || type.isEnum()) {
            return false;
        }
        return true;
    }

    protected void populateEntityMap(Map<String, EntityInfo> map) {
        String file = "META-INF/services/able/entities.properties";
        URL url = Thread.currentThread().getContextClassLoader().getResource(file);
        if (url == null) {
            url = getClass().getClassLoader().getResource(file);
            if (url == null) {
                log.warn("No file: " + file + " found on the classpath so cannot discover entities");
                return;
            }
        }
        Properties properties = new Properties();
        try {
            log.info("Loading entities from: " + url);
            properties.load(url.openStream());

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
