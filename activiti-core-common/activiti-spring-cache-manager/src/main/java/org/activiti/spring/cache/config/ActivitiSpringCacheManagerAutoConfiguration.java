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

package org.activiti.spring.cache.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import org.activiti.spring.cache.ActivitiSpringCacheManagerProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@AutoConfiguration(before = { CacheAutoConfiguration.class })
@EnableCaching
@EnableConfigurationProperties({ActivitiSpringCacheManagerProperties.class})
@PropertySource("classpath:config/activiti-spring-cache-manager.properties")
public class ActivitiSpringCacheManagerAutoConfiguration {

    @Bean
    @ConditionalOnClass(CaffeineCacheManager.class)
    @ConditionalOnProperty(value = "activiti.spring.cache-manager.provider", havingValue = "caffeine")
    public CacheManagerCustomizer<CaffeineCacheManager> activitiSpringCaffeineCacheManagerCustomizer(ActivitiSpringCacheManagerProperties properties) {
        return cacheManager -> {
            var caffeineCacheProperties = properties.getCaffeine();

            cacheManager.setCaffeineSpec(CaffeineSpec.parse(caffeineCacheProperties.getDefaultSpec()));
            cacheManager.setAllowNullValues(caffeineCacheProperties.isAllowNullValues());

            caffeineCacheProperties.getCaches()
                .entrySet()
                .stream()
                .filter(it -> it.getValue().isEnabled())
                .forEach(cacheEntry -> {
                    Cache<Object, Object> cache = Caffeine.from(cacheEntry.getValue()
                            .getSpec())
                        .build();
                    cacheManager.registerCustomCache(cacheEntry.getKey(),
                        cache);
                });
        };
    }

}
