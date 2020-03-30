package com.ldm.async;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
@Service
public class AsyncServiceImpl implements AsyncService {

    private static Logger logger = LogManager.getLogger(AsyncServiceImpl.class.getName());
    @Override
    @Async("asyncServiceExecutor")
    public void mysqlToEs() {
    }

    @Override
    @Async("asyncServiceExecutor")
    public void createLog() {
    }

    @Override
    @Async("asyncServiceExecutor")
    public void updateLog() {
    }
}
