package org.example.processservice;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.*;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * flowable测试
 *
 * @author wangsen_a
 * @date 2024/02/22
 */
@SpringBootTest
@Slf4j
class ProcessServiceApplicationTests {

    private ProcessEngine processEngine;

    /**
     * 初始化流程引擎配置
     */
    @BeforeEach
    public void init() {
        ProcessEngineConfiguration processEngineConfiguration = new StandaloneProcessEngineConfiguration();
        // 数据库连接配置
        processEngineConfiguration.setJdbcUrl("jdbc:mysql://localhost:3306/flowable?serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true")
                .setJdbcUsername("root")
                .setJdbcPassword("169167866Spl.")
                .setJdbcDriver("com.mysql.cj.jdbc.Driver")
                // 如果数据库中表结构不存在就创建
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        processEngine = processEngineConfiguration.buildProcessEngine();
    }

    /**
     * 部署流程定义
     */
    @Test
    void deploy() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                // bpmn文件类路径
                .addClasspathResource("processes/ask_for_leave.bpmn20.xml").name("员工请假流程").deploy();
        log.info("deploy.getId():" + deploy.getId());
        log.info("deploy.getName():" + deploy.getName());
        log.info("deploy.getKey():" + deploy.getKey());
        log.info("deploy.getCategory():" + deploy.getCategory());
        log.info("deploy.getTenantId():" + deploy.getTenantId());
    }

    /**
     * 启动流程实例
     */
    @Test
    void startProcess() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 初始化流程变量
        Map<String, Object> map = new HashMap<>();
        map.put("employee", "赵六");
        map.put("day", 5);
        // 通过流程定义的key启动实例，可通过act_re_procdef表KEY_字段查询
        ProcessInstance askForLeave = runtimeService.startProcessInstanceByKey("leave", map);
        log.info("流程定义ID{}", askForLeave.getProcessDefinitionId());
    }

    /**
     * 查询任务
     */
    @Test
    void queryTask() {
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery()
                // 根据流程定义查询
                .processDefinitionKey("leave")
                // 根据任务处理人查询,查询赵六的任务
                .taskAssignee("赵六").list();

        // 遍历任务
        list.forEach(task -> {
            log.info("流程定义ID：{} ", task.getProcessDefinitionId());
            log.info("流程实例ID：{}", task.getProcessInstanceId());
            log.info("任务ID：{} ", task.getId());
            log.info("任务处理人：{}", task.getAssignee());
            log.info("任务名称：{} ", task.getName());
        });
    }

    /**
     * 完成流程任务
     */
    @Test
    void completeTask() {
        TaskService taskService = processEngine.getTaskService();
        // 查询任务
        Task task = taskService.createTaskQuery()
                // .processDefinitionKey("leave")
                // .taskAssignee("赵六")
                .processInstanceId("37501").singleResult();
        Map<String, Object> map = new HashMap<>();
        map.put("outcome", "通过");
        map.put("day", 3);
        // 完成该任务
        taskService.complete(task.getId(), map);
    }

    /**
     * 历史任务
     */
    @Test
    void historyTask() {
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricTaskInstance> historyList = historyService.createHistoricTaskInstanceQuery()
                // 张三历史已完成的历史任务
                .taskAssignee("zhangsan")
                // 查询已完成的
                .finished()
                // 按照结束时间排序
                .orderByHistoricTaskInstanceEndTime().asc()
                .list();
        // 遍历已完成的历史任务
        historyList.forEach(historyTask -> {
            log.info("流程定义id：{} ", historyTask.getProcessDefinitionId());
            log.info("任务id：{} ", historyTask.getId());
            log.info("任务处理人：{}", historyTask.getAssignee());
            log.info("任务名称：{} ", historyTask.getName());
        });
    }

    /**
     * 删除任务
     */
    @Test
    void deleteTask() {
        TaskService taskService = processEngine.getTaskService();
        taskService.deleteTask("");
    }

    /**
     * 删除流程定义
     */
    @Test
    void deleteProcess() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //删除流程定义
        //根据流程部署ID删除，如果流程未启动可以直接通过流程部署ID删除，否则流程启动后该删除会报异常
        //根据流程部署ID删除，同时设置级联删除,true就是表示级联删除，即使流程已经启动也会删除成功及所关联的数据
        repositoryService.deleteDeployment("2501", true);
    }

    /**
     * 流程挂起和激活
     */
    @Test
    void suspendedProcess() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                // 根据流程定义id查询流程定义
                .processDefinitionId("ask_for_leave:2:2504").singleResult();
        // 判断是否被挂起
        if (processDefinition.isSuspended()) {
            // 流程被挂起
            log.info("流程定义ID：{}, 流程定义名称：{}", processDefinition.getId(), processDefinition.getName());
            // 激活流程定义
            repositoryService.activateProcessDefinitionById("ask_for_leave:2:2504");
        } else {
            // 挂起流程定义
            log.info("流程定义ID：{}, 流程定义名称：{}", processDefinition.getId(), processDefinition.getName());
            repositoryService.suspendProcessDefinitionById("ask_for_leave:2:2504");
        }
    }

}
