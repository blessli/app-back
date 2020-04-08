package com.ldm;


import com.ldm.dao.UserDao;
import com.ldm.service.CommonService;
import com.ldm.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BackApplicationTests.class)
public class BackApplicationTests {
    UserService userService=new UserService();
    CommonService commonService=new CommonService();
    @Test
    public void test() throws Exception {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.println(sdf.format(new Date()));
        commonService.msgSecCheck(userService.getAccessToken().getAccess_token(),
                "特3456书yuuo莞6543李zxcz蒜7782法fgnv级");

    }
}
