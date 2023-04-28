## Spring Quartz

本模块包含有关Spring集成Quartz的文章

## 相关文章

+ [使用Quartz在Spring中进行调度](docs/使用Quartz在Spring中进行调度.md)

## #Scheduling in Spring with Quartz Example Project

This is the first example where we configure a basic scheduler.

## Spring boot application, Main class

`cn.tuyucheng.taketoday.springquartz.SpringQuartzApp`

## Configuration in *application.properties*

- Default: configures scheduler using Spring convenience classes:

  `using.spring.schedulerFactory=true`

- To configure scheduler using Quartz API:

  `using.spring.schedulerFactory=false`