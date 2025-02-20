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
package org.activiti.engine.impl.bpmn.behavior;

import org.activiti.bpmn.model.ThrowEvent;

public class IntermediateThrowLinkEventActivityBehavior extends FlowNodeActivityBehavior {
    private static final long serialVersionUID = 1L;

    private final ThrowEvent throwEvent;

    public IntermediateThrowLinkEventActivityBehavior(ThrowEvent throwEvent) {
        this.throwEvent = throwEvent;
    }


    public ThrowEvent getThrowEvent() {
        return throwEvent;
    }
}
