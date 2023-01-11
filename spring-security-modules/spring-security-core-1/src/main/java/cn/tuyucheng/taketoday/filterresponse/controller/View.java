package cn.tuyucheng.taketoday.filterresponse.controller;


import cn.tuyucheng.taketoday.filterresponse.config.AppConfig;

import java.util.HashMap;
import java.util.Map;

public class View {

    public static final Map<AppConfig.Role, Class> MAPPING = new HashMap<>();

    static {
        MAPPING.put(AppConfig.Role.ROLE_ADMIN, Admin.class);
        MAPPING.put(AppConfig.Role.ROLE_USER, User.class);
    }

    public static class User {

    }

    public static class Admin extends User {

    }
}
