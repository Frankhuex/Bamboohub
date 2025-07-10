cd bbh-front
npm install
rm -rf dist
npm run build | grep -v 'no-unused-vars'
cd ..