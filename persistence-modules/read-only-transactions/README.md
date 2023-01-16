## 相关文章

+ [使用事务进行只读操作](docs/使用事务进行只读操作.md)

## 指示

To run the `com.baeldung.read_only_transactions.TransactionSetupIntegrationTest` first follow the steps described next:

- run the command `docker-compose -f docker-compose-mysql.yml up`
- Open a SQL client of your preference and execute the `create.sql` script.
- You can check the mysql logs using `tail -f mysql/${name of de log file created}.log`