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
package org.activiti.bpmn.converter;

import org.activiti.bpmn.model.Event;
import org.activiti.bpmn.model.LinkEventDefinition;
import org.apache.commons.lang3.StringUtils;

import javax.xml.stream.XMLStreamWriter;

import static org.activiti.bpmn.constants.BpmnXMLConstants.ATTRIBUTE_ID;
import static org.activiti.bpmn.constants.BpmnXMLConstants.ATTRIBUTE_NAME;


public class LinkEventDefinitionXMLConverter {
    public static final String ELEMENT_EVENT_LINK_DEFINITION = "linkEventDefinition";
    public static final String ATTRIBUTE_LINK_SOURCE = "source";
    public static final String ATTRIBUTE_LINK_TARGET = "target";

    protected void writeLinkDefinition(Event parentEvent, LinkEventDefinition eventDefinition, XMLStreamWriter xtw) throws Exception {
        xtw.writeStartElement(ELEMENT_EVENT_LINK_DEFINITION);
        if(StringUtils.isNotEmpty(eventDefinition.getId())) {
            xtw.writeAttribute(ATTRIBUTE_ID, eventDefinition.getId());
        }
        if(StringUtils.isNotEmpty(eventDefinition.getName())) {
            xtw.writeAttribute(ATTRIBUTE_NAME, eventDefinition.getName());
        }
        if (StringUtils.isNotEmpty(eventDefinition.getTarget())) {
            xtw.writeStartElement(ATTRIBUTE_LINK_TARGET);
            xtw.writeCharacters(eventDefinition.getTarget());
            xtw.writeEndElement();
        }
        if(eventDefinition.getSources()!=null) {
            for (int i = 0; i < eventDefinition.getSources().size(); i++) {
                xtw.writeStartElement(ATTRIBUTE_LINK_SOURCE);
                xtw.writeCharacters(eventDefinition.getSources().get(i));
                xtw.writeEndElement();
            }
        }
        xtw.writeEndElement();
    }
}
