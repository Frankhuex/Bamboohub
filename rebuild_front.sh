cd ../bbh-front
npm install
rm -rf dist
npm run build | grep -v 'no-unused-vars'

cd ..
docker-compose -p bbh-test up -d --no-deps --build frontend