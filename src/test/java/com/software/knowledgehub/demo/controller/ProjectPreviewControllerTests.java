package com.software.knowledgehub.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectPreviewControllerTests {

    private final MockMvc mockMvc;

    @Autowired
    ProjectPreviewControllerTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void shouldReturnNormalizedPreviewWhenRequestIsValid() throws Exception {
        mockMvc.perform(post("/api/demo/knowledge-bases/preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "tech_docs",
                                  "name": " 技术知识库 ",
                                  "description": " 开发规范与故障记录 "
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data.code").value("TECH_DOCS"))
                .andExpect(jsonPath("$.data.name").value("技术知识库"))
                .andExpect(jsonPath("$.data.description").value("开发规范与故障记录"));
    }

    @Test
    void shouldReturnFieldErrorsWhenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/demo/knowledge-bases/preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "1-invalid-code",
                                  "name": " "
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("请求参数校验失败"))
                .andExpect(jsonPath("$.data.code").exists())
                .andExpect(jsonPath("$.data.name").value("知识库名称不能为空"));
    }

    @Test
    void shouldReturnConflictWhenCodeIsReserved() throws Exception {
        mockMvc.perform(post("/api/demo/knowledge-bases/preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "system",
                                  "name": "系统知识库"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("该知识库编码为系统保留编码"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
