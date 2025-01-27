package cn.magicalsheep.csunoticeapi.controller;

import cn.magicalsheep.csunoticeapi.Factory;
import cn.magicalsheep.csunoticeapi.service.impl.http.CSEHttpService;
import cn.magicalsheep.csunoticeapi.service.impl.http.SchoolHttpService;
import cn.magicalsheep.csunoticeapi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    public AdminController(SchoolHttpService schoolHttpService, CSEHttpService cseHttpService) {
        if (Factory.isFirstRun()) {
            logger.error("Please finish the settings.json to run API");
            return;
        }
        boolean flag = Factory.getConfiguration().isInit_db();
        int page_num = (flag) ?
                0x3f3f3f3f : Factory.getConfiguration().getUpdate_num_per_pages();
        if (Factory.getConfiguration().isSchool()) {
            new Thread(() -> {
                try {
                    schoolHttpService.update(page_num);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }).start();
        }
        if (Factory.getConfiguration().isCse()) {
            new Thread(() -> {
                try {
                    cseHttpService.update(page_num);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }).start();
        }
        if (flag) {
            Factory.getConfiguration().setInit_db(false);
            logger.info("Setting field init_db has been changed to false");
            try {
                IOUtils.saveConf(Factory.getConfiguration());
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }
}
