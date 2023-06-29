## 相关文章

+ [将事务用于只读操作](https://www.baeldung.com/spring-transactions-read-only)

## 介绍

要运行`cn.tuyucheng.taketoday.read_only_transactions.TransactionSetupIntegrationTest`，请按照下面描述的步骤操作：

- 运行命令`docker-compose -f docker-compose-mysql.yml up`
- 打开你喜欢的SQL客户端并执行`create.sql`脚本
- 你可以使用`tail -f mysql/${name of de log file created}.log`检查mysql日志