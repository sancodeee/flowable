package org.example.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.example.utils.ProcessDiagramUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 流程图控制器
 *
 * @author wangsen_a
 * @date 2024/02/22
 */
@RestController
@RequestMapping("/utils")
public class ProcessDiagramController {

    /**
     * 当前流程执行位置
     *
     * @param resp          响应体
     * @param processInstId 流程实例id
     * @throws IOException ioexception
     */
    @GetMapping("/pic")
    public void showPic(HttpServletResponse resp, @RequestParam String processInstId) throws IOException {
        ProcessDiagramUtils.generateProcessDiagram(resp, processInstId);
    }

}
