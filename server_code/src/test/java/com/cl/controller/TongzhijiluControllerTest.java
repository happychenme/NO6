package com.cl.controller;

import com.cl.entity.TongzhijiluEntity;
import com.cl.service.TongzhijiluService;
import com.cl.utils.R;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 通知记录控制器测试类
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TongzhijiluControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TongzhijiluService tongzhijiluService;

    private MockHttpServletRequest request;

    @BeforeEach
    public void setUp() {
        request = new MockHttpServletRequest();
        request.getSession().setAttribute("username", "admin");
    }

    /**
     * 测试分页查询接口
     */
    @Test
    public void testPage() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/tongzhijilu/page")
                .param("page", "1")
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertNotNull(content);
        assertTrue(content.contains("code"));
    }

    /**
     * 测试获取详情接口
     */
    @Test
    public void testInfo() throws Exception {
        // 先创建一条测试数据
        TongzhijiluEntity notification = createTestNotification();
        tongzhijiluService.save(notification);

        try {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/tongzhijilu/info/" + notification.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String content = result.getResponse().getContentAsString();
            assertNotNull(content);
            assertTrue(content.contains("code"));
            assertTrue(content.contains("data"));
        } finally {
            // 清理测试数据
            tongzhijiluService.deleteById(notification.getId());
        }
    }

    /**
     * 测试保存接口
     */
    @Test
    public void testSave() throws Exception {
        String json = "{" +
                "\"yuyuebianhao\":\"YY202503270100\"," +
                "\"yishengzhanghao\":\"doctor100\"," +
                "\"zhanghao\":\"user100\"," +
                "\"yonghuleixing\":1," +
                "\"tongzhileixing\":1," +
                "\"tongzhineirong\":\"测试通知内容\"," +
                "\"fasongzhuangtai\":0," +
                "\"jieshourenshouji\":\"13300133000\"" +
                "}";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/tongzhijilu/save")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertNotNull(content);
        assertTrue(content.contains("code"));

        // 清理测试数据
        // 注意：实际应该根据条件查询并删除
    }

    /**
     * 测试手动重试接口
     */
    @Test
    public void testManualRetry() throws Exception {
        // 创建测试数据
        TongzhijiluEntity notification = createTestNotification();
        notification.setFasongzhuangtai(2); // 设置为失败状态
        tongzhijiluService.save(notification);

        try {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/tongzhijilu/manualRetry/" + notification.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String content = result.getResponse().getContentAsString();
            assertNotNull(content);
            assertTrue(content.contains("code"));
        } finally {
            // 清理测试数据
            tongzhijiluService.deleteById(notification.getId());
        }
    }

    /**
     * 测试批量重试接口
     */
    @Test
    public void testBatchRetry() throws Exception {
        // 创建测试数据
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            TongzhijiluEntity notification = createTestNotification();
            notification.setYuyuebianhao("YY20250327020" + i);
            notification.setFasongzhuangtai(2);
            tongzhijiluService.save(notification);
            ids.add(notification.getId());
        }

        String json = "[" + ids.get(0) + "," + ids.get(1) + "]";

        try {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/tongzhijilu/batchRetry")
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String content = result.getResponse().getContentAsString();
            assertNotNull(content);
            assertTrue(content.contains("code"));
            assertTrue(content.contains("data"));
        } finally {
            // 清理测试数据
            tongzhijiluService.deleteBatchIds(ids);
        }
    }

    /**
     * 测试标记已处理接口
     */
    @Test
    public void testMarkAsProcessed() throws Exception {
        // 创建测试数据
        TongzhijiluEntity notification = createTestNotification();
        notification.setFasongzhuangtai(2);
        tongzhijiluService.save(notification);

        try {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/tongzhijilu/markAsProcessed/" + notification.getId())
                    .param("remark", "测试处理备注")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String content = result.getResponse().getContentAsString();
            assertNotNull(content);
            assertTrue(content.contains("code"));
        } finally {
            // 清理测试数据
            tongzhijiluService.deleteById(notification.getId());
        }
    }

    /**
     * 测试获取统计信息接口
     */
    @Test
    public void testGetStatistics() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/tongzhijilu/statistics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertNotNull(content);
        assertTrue(content.contains("code"));
        assertTrue(content.contains("data"));
    }

    /**
     * 测试导出失败记录接口
     */
    @Test
    public void testExportFailed() throws Exception {
        // 创建失败记录
        TongzhijiluEntity notification = createTestNotification();
        notification.setFasongzhuangtai(2);
        notification.setShibaiyuanyin("测试失败原因");
        tongzhijiluService.save(notification);

        try {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/tongzhijilu/exportFailed")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String content = result.getResponse().getContentAsString();
            assertNotNull(content);
            assertTrue(content.contains("code"));
            assertTrue(content.contains("data"));
        } finally {
            // 清理测试数据
            tongzhijiluService.deleteById(notification.getId());
        }
    }

    /**
     * 测试触发批量发送接口
     */
    @Test
    public void testTriggerBatchSend() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/tongzhijilu/triggerBatchSend")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertNotNull(content);
        assertTrue(content.contains("code"));
    }

    /**
     * 测试触发批量重试接口
     */
    @Test
    public void testTriggerBatchRetry() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/tongzhijilu/triggerBatchRetry")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertNotNull(content);
        assertTrue(content.contains("code"));
    }

    /**
     * 创建测试通知实体
     */
    private TongzhijiluEntity createTestNotification() {
        TongzhijiluEntity notification = new TongzhijiluEntity();
        notification.setYuyuebianhao("YY202503270" + System.currentTimeMillis());
        notification.setYishengzhanghao("doctor" + System.currentTimeMillis());
        notification.setZhanghao("user" + System.currentTimeMillis());
        notification.setYonghuleixing(1);
        notification.setTongzhileixing(1);
        notification.setTongzhineirong("测试通知内容");
        notification.setFasongzhuangtai(0);
        notification.setJieshourenshouji("13200132000");
        notification.setChulizhuangtai(0);
        notification.setAddtime(new Date());
        return notification;
    }
}
