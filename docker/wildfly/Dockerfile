FROM jboss/wildfly:10.0.0.Final
ADD ROOT.war /opt/jboss/wildfly/standalone/deployments/
ENV JAVA_OPTS -Xms1g -Xmx2g -XX:MetaspaceSize=96M -XX:MaxMetaspaceSize=256m -Djava.net.preferIPv4Stack=true