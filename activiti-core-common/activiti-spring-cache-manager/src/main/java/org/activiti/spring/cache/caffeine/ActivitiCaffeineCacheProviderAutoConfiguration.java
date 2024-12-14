/*
 * Copyright 2010-2020 Alfresco Software, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.spring.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import org.activiti.spring.cache.config.ActivitiSpringCacheManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(before = CacheAutoConfiguration.class, after = ActivitiSpringCacheManagerAutoConfiguration.class)
@EnableConfigurationProperties(ActivitiSpringCaffeineCacheProviderProperties.class)
@ConditionalOnProperty(value = "activiti.spring.cache-manager.provider", havingValue = "caffeine")
@ConditionalOnClass(CaffeineCacheManager.class)
public class ActivitiCaffeineCacheProviderAutoConfiguration {

    @Bean
    public CacheManager actvitiSpringCacheManager(ActivitiSpringCaffeineCacheProviderProperties properties) {
        final CaffeineCacheManager manager = new CaffeineCacheManager();

        manager.setCaffeineSpec(CaffeineSpec.parse(properties.getSpec()));
        manager.setAllowNullValues(properties.isAllowNullValues());

        properties.getCaches()
            .entrySet()
            .stream()
            .filter(it -> it.getValue().isEnabled())
            .forEach(cacheEntry -> {
                Cache<Object, Object> cache = Caffeine.from(cacheEntry.getValue().getSpec()).build();
                manager.registerCustomCache(cacheEntry.getKey(), cache);
            });

        return manager;
    }
}
