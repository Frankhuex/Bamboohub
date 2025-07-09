cd bbh-back
mvn clean install
cd ..
docker-compose -p bbh-test up -d --no-deps --build backend