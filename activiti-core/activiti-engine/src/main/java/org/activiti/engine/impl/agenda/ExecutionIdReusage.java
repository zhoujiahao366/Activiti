package org.activiti.engine.impl.agenda;

import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.HistoricVariableInstanceQueryImpl;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.HistoricVariableInstanceEntityManager;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ExecutionIdReusage {

    public static final String EXECUTION_ID_REUSAGE_VARIABLE_NAME = ".execution_id.reusage";

    public static void copyExecutionIdReusage(ExecutionEntity fromExecution, ExecutionEntity toExecution) {
        if( fromExecution==null || toExecution==null ) {
            return;
        }
        final String executionId = getExecutionIdReusage(fromExecution);
        if( executionId!=null ) {
            toExecution.setVariableLocal( EXECUTION_ID_REUSAGE_VARIABLE_NAME, executionId );
        }
    }

    public static void setExecutionIdReusage(ExecutionEntity execution, String executionId) {
        if( execution==null || StringUtils.isEmpty(executionId) ) {
            return;
        }
        execution.setVariableLocal( EXECUTION_ID_REUSAGE_VARIABLE_NAME, executionId);
    }

    public static String getExecutionIdReusage(ExecutionEntity execution) {
        if( execution==null ) {
            return null;
        }
        return (String) execution.getVariableLocal( ExecutionIdReusage.EXECUTION_ID_REUSAGE_VARIABLE_NAME );
    }

    public static Map<String, Object> getHistoricVariableInstances(CommandContext commandContext, String executionIdReusage) {
        if( commandContext==null || StringUtils.isEmpty(executionIdReusage) ) {
            return Collections.EMPTY_MAP;
        }
        final HistoricVariableInstanceEntityManager historicVariableInstanceEntityManager = commandContext.getHistoricVariableInstanceEntityManager();
        HistoricVariableInstanceQueryImpl criteria = new HistoricVariableInstanceQueryImpl().executionId( executionIdReusage );
        final List<HistoricVariableInstance> variables = historicVariableInstanceEntityManager.findHistoricVariableInstancesByQueryCriteria(criteria, null);

        return variables.stream().collect(Collectors.toMap(HistoricVariableInstance::getVariableName, HistoricVariableInstance::getValue));
    }
}
