package cn.tuyucheng.taketoday.typeconversion.converter.controller;

import cn.tuyucheng.taketoday.typeconversion.Application;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = Application.class)
@AutoConfigureMockMvc
class StringToEmployeeConverterControllerIntegrationTest {

   @Autowired
   private MockMvc mockMvc;

   @Test
   void getStringToEmployeeTest() throws Exception {
      mockMvc.perform(get("/string-to-employee?employee=1,2000"))
            .andDo(print())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.salary", is(2000.0)))
            .andExpect(status().isOk());
   }
}