# activiti learning
1. 来源：[bilibili activiti7](https://www.bilibili.com/video/BV1za4y1u7r6/?spm_id_from=333.999.0.0)
2. 环境配置
   - idea plugin: activiti bpmn visualizer
   - docker 部署tomcat(tomcat7, 其它版本tomcat的jdk版本不对应, 部署war包可以使用docker cp命令)
     - tomcat7下安装activiti5: activiti-explorer
     - tomcat7下安装activiti6: activiti-app、activiti-admin
   - docker 部署mysql(version: latest, port: 3306, password: 123456)
3. 阅读顺序
   1. ProcessEngineTest
   2. SimpleLeaveTest
   3. ProcessUtilTest
   4. ExpressionTest
   5. ListenerTest
   6. ...