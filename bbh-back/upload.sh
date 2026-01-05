export JAVA_HOME=`/usr/libexec/java_home -v 17`
mvn clean install
scp ./target/bbh-back-0.2.jar frank@106.13.161.72:bbh-0.2/bbh-back-0.2.jar