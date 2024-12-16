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

package org.activiti.spring.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("activiti.spring.cache-manager")
public class ActivitiSpringCacheManagerProperties {

    public enum CacheProvider {
        simple,
        caffeine,
    }

    private boolean enabled = true;

    private CacheProvider provider;

    private CaffeineCacheProviderProperties caffeine;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public CacheProvider getProvider() {
        return provider;
    }

    public void setProvider(CacheProvider provider) {
        this.provider = provider;
    }

    public CaffeineCacheProviderProperties getCaffeine() {
        return caffeine;
    }

    public void setCaffeine(CaffeineCacheProviderProperties caffeine) {
        this.caffeine = caffeine;
    }

    public static class CaffeineCacheProviderProperties {
        private boolean allowNullValues = false;

        private String defaultSpec = "";

        private Map<String, CaffeineCacheSpec> caches = new LinkedHashMap<>();

        public static class CaffeineCacheSpec {
            private boolean enabled = true;
            private String spec = "";

            public String getSpec() {
                return spec;
            }

            public void setSpec(String spec) {
                this.spec = spec;
            }

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }
        }

        public boolean isAllowNullValues() {
            return allowNullValues;
        }

        public void setAllowNullValues(boolean allowNullValues) {
            this.allowNullValues = allowNullValues;
        }

        public Map<String, CaffeineCacheSpec> getCaches() {
            return caches;
        }

        public void setCaches(Map<String, CaffeineCacheSpec> caches) {
            this.caches = caches;
        }

        public String getDefaultSpec() {
            return defaultSpec;
        }

        public void setDefaultSpec(String defaultSpec) {
            this.defaultSpec = defaultSpec;
        }
    }
}
