cd bbh-back
mvn clean install

cd ../bbh-front
npm install
rm -rf dist
npm run build | grep -v 'no-unused-vars'

docker-compose -p bbh-test up -d --build