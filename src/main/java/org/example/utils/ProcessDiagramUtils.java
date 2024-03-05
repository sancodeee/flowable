package org.example.utils;

import jakarta.servlet.http.HttpServletResponse;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 流程图相关工具类
 *
 * @author wangsen_a
 * @date 2024/02/22
 */
@Component
public class ProcessDiagramUtils {

    private static RuntimeService runtimeService;
    private static RepositoryService repositoryService;
    private static ProcessEngine processEngine;

    // 通过构造方法注入
    @Autowired
    public ProcessDiagramUtils(RuntimeService runtimeService, RepositoryService repositoryService, ProcessEngine processEngine) {
        ProcessDiagramUtils.runtimeService = runtimeService;
        ProcessDiagramUtils.repositoryService = repositoryService;
        ProcessDiagramUtils.processEngine = processEngine;
    }

    public static void generateProcessDiagram(HttpServletResponse resp, String processId) throws IOException {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
        if (pi == null) {
            return;
        }
        List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(processId).list();

        List<String> activityIds = new ArrayList<>();
        List<String> flows = new ArrayList<>();
        for (Execution exe : executions) {
            List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
            activityIds.addAll(ids);
        }

        // 生成流程图
        BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
        ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();
        ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
        byte[] buf = new byte[1024];
        int length;
        try (InputStream in = diagramGenerator.generateDiagram(bpmnModel, "png", activityIds, flows,
                processEngineConfiguration.getActivityFontName(), processEngineConfiguration.getLabelFontName(),
                processEngineConfiguration.getAnnotationFontName(),
                processEngineConfiguration.getClassLoader(), 1.0, false);
             OutputStream out = resp.getOutputStream()) {
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
        }
    }
}
