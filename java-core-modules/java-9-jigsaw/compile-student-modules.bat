javac -d mods/cn.tuyucheng.taketoday.student.model src/modules/cn.tuyucheng.taketoday.student.model/module-info.java^
	src/modules/cn.tuyucheng.taketoday.student.model/cn/tuyucheng/taketoday/student/model/Student.java

javac --module-path mods -d mods/cn.tuyucheng.taketoday.student.service^
 src/modules/cn.tuyucheng.taketoday.student.service/module-info.java^
 src/modules/cn.tuyucheng.taketoday.student.service/cn/tuyucheng/taketoday/student/service/StudentService.java

javac --module-path mods -d mods/cn.tuyucheng.taketoday.student.service.dbimpl^
 src/modules/cn.tuyucheng.taketoday.student.service.dbimpl/module-info.java^
 src/modules/cn.tuyucheng.taketoday.student.service.dbimpl/cn/tuyucheng/taketoday/student/service/dbimpl/StudentDbService.java

javac --module-path mods -d mods/cn.tuyucheng.taketoday.student.client^
 src/modules/cn.tuyucheng.taketoday.student.client/module-info.java^
 src/modules/cn.tuyucheng.taketoday.student.client/cn/tuyucheng/taketoday/student/client/StudentClient.java