rm -rf dist
npm run build | grep -v 'no-unused-vars'
scp -r ./dist frank@106.13.161.72:./bbh-0.2/bbh-front-0.2