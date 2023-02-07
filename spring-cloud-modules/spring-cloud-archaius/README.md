# Spring Cloud Archaius

本模块包含有关Spring Cloud与Netflix Archaius的文章

## 相关文章

+ [Spring Cloud与Netflix Archaius简介](docs/SpringCloud与Netflix-Archaius简介.md)
+ [具有各种数据库配置的Netflix Archaius](docs/具有各种数据库配置的Netflix-Archaius.md)

## Basic Config

此服务具有基本的、开箱即用的Spring Cloud Netflix Archaius配置。

## Extra Configs

此服务自定义Archaius支持的某些属性。

这些属性是在main方法上设置的，因为Archaius使用系统属性，但它们可以添加为启动应用时的命令行参数。

## Additional Sources

在此服务中，我们创建一个新的AbstractConfiguration Bean，设置一个新的配置属性源。

这些属性优先于Archaius复合配置中的所有其他属性。