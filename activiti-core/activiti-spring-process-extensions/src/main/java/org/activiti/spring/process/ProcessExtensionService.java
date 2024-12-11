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


package org.activiti.spring.process;

import static java.util.Collections.synchronizedMap;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.spring.process.model.Extension;
import org.activiti.spring.process.model.ProcessExtensionModel;
import org.activiti.spring.resources.DeploymentResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessExtensionService {

    private static final Logger logger = LoggerFactory.getLogger(ProcessExtensionService.class);

    private final DeploymentResourceLoader<ProcessExtensionModel> processExtensionLoader;
    private final ProcessExtensionResourceReader processExtensionReader;
    private RepositoryService repositoryService;

    private static final Extension EMPTY_EXTENSIONS = new Extension();
    private final Map<String, Map<String, Extension>> processExtensionModelDeploymentMap;

    public ProcessExtensionService(DeploymentResourceLoader<ProcessExtensionModel> processExtensionLoader,
        ProcessExtensionResourceReader processExtensionReader) {

        this.processExtensionLoader = processExtensionLoader;
        this.processExtensionReader = processExtensionReader;

        processExtensionModelDeploymentMap = synchronizedMap(new HashMap<>());
    }

    public ProcessExtensionService(
        DeploymentResourceLoader<ProcessExtensionModel> processExtensionLoader,
        ProcessExtensionResourceReader processExtensionReader,
        int cacheLimit
    ) {
        this.processExtensionLoader = processExtensionLoader;
        this.processExtensionReader = processExtensionReader;

        processExtensionModelDeploymentMap = synchronizedMap(new LinkedHashMap<>(cacheLimit + 1, 0.75f, true) {
            // +1 is needed, because the entry is inserted first, before it is removed
            // 0.75 is the default (see javadocs)
            // true will keep the 'access-order', which is needed to have a real LRU cache
            private static final long serialVersionUID = 1L;

            protected boolean removeEldestEntry(Map.Entry<String, Map<String, Extension>> eldest) {
                boolean removeEldest = size() > cacheLimit;
                if (removeEldest && logger.isDebugEnabled()) {
                    logger.debug("Cache limit of {} entries has been reached, eldest key {} will be evicted", cacheLimit, eldest.getKey());
                }
                return removeEldest;
            }
        });
    }

    private Map<String, Extension> getProcessExtensionsForDeploymentId(String deploymentId) {
        Map<String, Extension> processExtensionModelMap = processExtensionModelDeploymentMap.get(deploymentId);
        if (processExtensionModelMap != null) {
            return processExtensionModelMap;
        }

        List<ProcessExtensionModel> processExtensionModels = processExtensionLoader.loadResourcesForDeployment(deploymentId,
                processExtensionReader);

        processExtensionModelMap = buildProcessDefinitionAndExtensionMap(processExtensionModels);
        processExtensionModelDeploymentMap.put(deploymentId, processExtensionModelMap);
        return processExtensionModelMap;
    }

    private Map<String, Extension> buildProcessDefinitionAndExtensionMap(List<ProcessExtensionModel> processExtensionModels) {
        Map<String, Extension> buildProcessExtensionMap = new HashMap<>();
        for (ProcessExtensionModel processExtensionModel:processExtensionModels ) {
            buildProcessExtensionMap.putAll(processExtensionModel.getAllExtensions());
        }

        return buildProcessExtensionMap;
    }

    public boolean hasExtensionsFor(ProcessDefinition processDefinition) {
        return !EMPTY_EXTENSIONS.equals(getExtensionsFor(processDefinition));
    }

    public boolean hasExtensionsFor(String processDefinitionId) {
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(processDefinitionId);
        return hasExtensionsFor(processDefinition);
    }

    public Extension getExtensionsFor(ProcessDefinition processDefinition) {
        Map<String, Extension> processExtensionModelMap = getProcessExtensionsForDeploymentId(processDefinition.getDeploymentId());
        Extension extension = processExtensionModelMap.get(processDefinition.getKey());

        return extension != null ? extension : EMPTY_EXTENSIONS;
    }

    public Extension getExtensionsForId(String processDefinitionId) {
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(processDefinitionId);

        Extension processExtension = getExtensionsFor(processDefinition);
        return processExtension != null ? processExtension : EMPTY_EXTENSIONS;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }
}
